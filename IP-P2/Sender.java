import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Sender {
	static int globalMss;
	
	public static void main(String args[]) throws IOException {
		try {
			DatagramSocket clientSocket = new DatagramSocket(65499);
			int mss_size = Integer.parseInt(args[args.length-1]);
			Sender.globalMss = mss_size;
			int receiversPort = Integer.parseInt(args[args.length-3]);
			String fileCopy=args[args.length-2];
			String[] receivers=new String[args.length-3];
			for(int i=0;i<args.length-3;i++)
			receivers[i]=args[i];
			startSendingToReceivers(mss_size,receiversPort,receivers,clientSocket,fileCopy);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void startSendingToReceivers(int mss_size,int receiversPort,String[] receivers,DatagramSocket clientSocket,String fileCopy) throws Exception {
		File file = new File(fileCopy);
		byte[] data;
		byte[] header;
		long packetCount;
		byte[] fileContent =  new byte[(int)file.length()];
		FileInputStream fin = new FileInputStream(file);
		fin.read(fileContent);
		if(file.length()%mss_size == 0)
				packetCount = (file.length() / mss_size);
		else
				packetCount = ((file.length() / mss_size)+1);
		
		sendControlInformation(file.length(),mss_size,receiversPort,receivers,clientSocket);   // put this within a loop. Need to be done for all receiers.
		Thread.sleep(2000);
		int i=0;
		for(i=0;i<packetCount+1;i++){
			
			if(i%473 == 0){
				Thread.sleep(1000);
			}
			if(i == packetCount-1){
				mss_size = (int)file.length()%mss_size;
			}
			if(i == packetCount){
				data = "close".getBytes();
				mss_size = data.length;
			}else{
				data = readMssBytes(fileContent,mss_size,i);
			}
			header = generateHeader(i,data);
			sendToReceivers(header,data,mss_size,i,receivers,receiversPort,clientSocket,packetCount);
		}
		
	}

	public static void sendToReceivers(byte[] header,byte[] data,int mss_size,int packetNo,String[] receivers,int receiversPort,DatagramSocket clientSocket,long packetCount){
		List rcvArr = new LinkedList();
		
		try{
			for(int i=0;i<receivers.length;i++){
				Receivers rcv = new Receivers(receivers[i], header, data, mss_size,packetNo,receiversPort,clientSocket,packetCount);
				rcv.start();
				rcv.join();
				
			}
			//System.out.println("I am out of Join");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public static byte[] readMssBytes(byte[] fileContent,int mss_size,int packetNo){
		int j = 0;
		byte[] b=new byte[mss_size];
		int startOffset = packetNo * Sender.globalMss;
		String data;
		try{
			for(int i=0;i<mss_size;i++){
				b[i] = fileContent[startOffset];
				startOffset++;
			}
		}catch(ArrayIndexOutOfBoundsException e){
			
		}
		data = new String(b);
		return b;
	}
	
	
	
	
	public static byte[] generateHeader(int packetNo,byte[] data){
		byte header[] = new byte[8];
		
		//Generate and insert Indication Field
		byte[] tempBytes = ByteBuffer.allocate(4).putInt(packetNo).array();
		for(int i=0;i<4;i++)
			header[i] = tempBytes[i];
		
		//Generate and insert Indication Field
		int checksum = crc16(data);
		tempBytes = ByteBuffer.allocate(4).putInt(checksum).array();
		header[4] = tempBytes[2];
		header[5] = tempBytes[3];
				
		//Generate and insert Indication Field
		String indicationField = "0101010101010101";
		tempBytes = ByteBuffer.allocate(4).putInt(Integer.parseInt(indicationField, 2)).array();
		header[6] = tempBytes[2];
		header[7] = tempBytes[3];
		
		return header;
	}
	
		
	private static int crc16(final byte[] buffer) {
	    int crc = 0xFFFF;
	 
	    for (int j = 0; j < buffer.length ; j++) {
	        crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
	        crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
	        crc ^= ((crc & 0xff) >> 4);
	        crc ^= (crc << 12) & 0xffff;
	        crc ^= ((crc & 0xFF) << 5) & 0xffff;
	    }
	    crc &= 0xffff;
	    return crc;
	 
	  }
	
	public static void sendControlInformation(long fileLength,int mss_size,int receiversPort,String[] receivers,DatagramSocket clientSocket){
		String controlInfo = String.valueOf(fileLength) +":"+ String.valueOf(mss_size)+":";
		try{	
			for(int i=0;i<receivers.length;i++){
				InetAddress IPAddress = InetAddress.getByName(receivers[i]);
				DatagramPacket destinationPacket = new DatagramPacket(controlInfo.getBytes(),controlInfo.getBytes().length, IPAddress, receiversPort);
				clientSocket.send(destinationPacket);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}



class Receivers extends Thread{
	String receiver;
	byte[] header;
	byte[] data;
	int mss_size;
	int packetNo;
	int receiversPort;
	long packetCount;
	DatagramSocket clientSocket;
	
	

	public Receivers(String receiver, byte[] header, byte[] data,int mss_size, int packetNo,int receiversPort,DatagramSocket clientSocket,long packetCount) {
		super();
		this.receiver = receiver;
		this.header = header;
		this.data = data;
		this.mss_size = mss_size;
		this.packetNo = packetNo;
		this.receiversPort = receiversPort;
		this.clientSocket = clientSocket;
		this.packetCount = packetCount;
	}


	public void run(){
		try {
			
			HashMap headerInfo=new HashMap();
			
			//Form the byte array that has to be sent to the receivers.
			byte[] destination = new byte[mss_size+8];
			System.arraycopy(header, 0, destination, 0, 8);
			byte[] sendpacket = new byte[mss_size];
			sendpacket = data;
			System.arraycopy(sendpacket, 0, destination, 8, mss_size);
			int noOfTries = 0;
			
			byte[] ACK = new byte[8]; 
            DatagramPacket ACKPacket = new DatagramPacket(ACK, ACK.length);
			 
			if(packetNo == packetCount-1){
				System.out.print(".");
				Thread.sleep(2000);
			}            
			InetAddress IPAddress = InetAddress.getByName(this.receiver);
			DatagramPacket destinationPacket = new DatagramPacket(destination,destination.length, IPAddress, this.receiversPort);
			while(true){
				clientSocket.send(destinationPacket);
				if(packetNo == packetCount)
					break;
				clientSocket.setSoTimeout(200);
				try{
					 clientSocket.receive(ACKPacket);
				}catch(SocketTimeoutException e){
					System.out.println("Timeout, Sequence Number = "+packetNo);
					continue;
				}
				headerInfo = parseHeader(header);
				if((Integer)headerInfo.get("packetNo") == packetNo){
					break;
				}
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static HashMap parseHeader(byte[] header){
		   byte[] tempByte=new byte[4];
		   ByteBuffer bb;
		   HashMap headerInfo = new HashMap();
		   
		   //Parse Sequence Number
		   tempByte[0] = header[0];
		   tempByte[1] = header[1];
		   tempByte[2] = header[2];
		   tempByte[3] = header[3];
		   bb = ByteBuffer.wrap(tempByte);
		   headerInfo.put("packetNo", bb.getInt());
		   
		   
		   //Parse checkSum
		   tempByte[0] = 0;
		   tempByte[1] = 0;
		   tempByte[2] = header[4];
		   tempByte[3] = header[5];
		   bb = ByteBuffer.wrap(tempByte);
		   headerInfo.put("checkSum", bb.getInt());
		   
		 //Parse Indicator field
		   tempByte[0] = 0;
		   tempByte[1] = 0;
		   tempByte[2] = header[6];
		   tempByte[3] = header[7];
		   bb = ByteBuffer.wrap(tempByte);
		   headerInfo.put("indicator", bb.getInt());
		   
		   return headerInfo;
	   }
	
}
