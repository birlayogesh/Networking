import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;

class Receiver
{
   public static void main(String args[]) throws Exception
   {
	   	
	   if(args.length<3){
           System.out.println("Arguments should be in following sequence::::port# file-name p");
               System.exit(0);
       }
	   String fileName = args[1];
	   int portNo = Integer.parseInt(args[0]);
	   
	   double probability = Double.parseDouble(args[2]);
	   
	   //System.out.println(args[0]+""+args[1]);
	   DatagramSocket serverSocket = new DatagramSocket(portNo);
	   HashMap headerInfo=new HashMap();
	   int lastACKedPacket=-1;
	   long packetCounter = 0;
	   
	   
	   //Sender will send control info to the receivers first.
	   byte[] controlData = new byte[200];
	   DatagramPacket controlPacket = new DatagramPacket(controlData, controlData.length);
       serverSocket.receive(controlPacket);
       String[] token = new String(controlPacket.getData()).split(":");
       long fileLength = Long.parseLong(token[0]);
       int mss_size = Integer.parseInt(token[1]);
       InetAddress senderIPAddress = controlPacket.getAddress();
       int senderPort = controlPacket.getPort();
	   int lastPacketSize = (int)fileLength % mss_size; 
	  
	   //Find out the number of packets receiver is going to receive.
       long totalPackets;
       if(fileLength%mss_size == 0)
    	   totalPackets = (fileLength / mss_size);
       else
    	   totalPackets = ((fileLength / mss_size)+1);
       
        boolean isCorrect = false;
       
       //Open the File on which the ouput has to be written
       File file = new File(fileName);
       if(file.exists()){
    	   file.delete();
           file = new File(fileName);
       }
       
       //long startTime = System.currentTimeMillis();
       FileOutputStream fos = new FileOutputStream(file,true);
       while(true){
    	   
    	   if(packetCounter == totalPackets-1){
    		   mss_size = lastPacketSize;
    	   }
    	   if(packetCounter == totalPackets){
    		   mss_size = 5;
    	   }
    	     byte[] receiveData = new byte[mss_size+8]; 
             DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
             serverSocket.receive(receivePacket);
             
             //Extract header from packet
             byte[] header = new byte[8];
             byte[] ACKheader = new byte[8];
             byte[] receivedByte =  receivePacket.getData();
             System.arraycopy(receivedByte, 0, header, 0, 8);
             headerInfo = parseHeader(header);
             
             //Extract data from packet
             byte[] data = new byte[mss_size];
             System.arraycopy(receivedByte, 8, data, 0, mss_size);
             
             if(new String(data).equalsIgnoreCase("close")){
            	 break;
             }
             
             //Check the loss probability and accept/discard a packet.
             Random rn = new Random();
             double d = rn.nextDouble();
             if(d <=probability){
            	 System.out.println("Packet Loss, Sequence Number = "+headerInfo.get("packetNo"));
            	 continue;
             }
             
             //Check for errors by calculating checksum
			 if(packetCounter < totalPackets-2){
             isCorrect = checkForErrors(headerInfo,data,header);
             if(!isCorrect){
            	 System.out.println("Packet Loss, Checksum Error, Sequence Number = "+headerInfo.get("packetNo"));
            	 continue;
             }	 
			 }
              
             
             //Send ACK to sender.
             ACKheader = generateACKHeader(headerInfo);
             DatagramPacket ACKPacket = new DatagramPacket(ACKheader,ACKheader.length, senderIPAddress, senderPort);
             serverSocket.send(ACKPacket);
             
             if(lastACKedPacket >= Integer.parseInt(headerInfo.get("packetNo").toString()))
            	 continue;
             else
            	 lastACKedPacket = Integer.parseInt(headerInfo.get("packetNo").toString());
            
             //Write data to file
            
             fos.write(data);
             fos.flush();
             
             
             //System.out.println("Received Packet "+packetCounter);
             packetCounter++;
        }
       fos.close();
       
       //long endTime = System.currentTimeMillis();
       //System.out.println("Time taken : "+(endTime - startTime));
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
	   headerInfo.put(new String("packetNo"),new Integer(bb.getInt()));
	   
	   
	   //Parse checkSum
	   tempByte[0] = 0;
	   tempByte[1] = 0;
	   tempByte[2] = header[4];
	   tempByte[3] = header[5];
	   bb = ByteBuffer.wrap(tempByte);
	   headerInfo.put(new String("checkSum"),new Integer(bb.getInt()));
	   
	 //Parse Indicator field
	   tempByte[0] = 0;
	   tempByte[1] = 0;
	   tempByte[2] = header[6];
	   tempByte[3] = header[7];
	   bb = ByteBuffer.wrap(tempByte);
	   headerInfo.put(new String("indicator"),new Integer(bb.getInt()));
	   
	   return headerInfo;
   }
   
  
   public static byte[] generateACKHeader(HashMap headerInfo){
		byte header[] = new byte[8];
		
		int packetNo = Integer.parseInt(headerInfo.get("packetNo").toString());
		//Generate and insert Indication Field
		byte[] tempBytes = ByteBuffer.allocate(4).putInt(packetNo).array();
		for(int i=0;i<4;i++)
			header[i] = tempBytes[i];
		
		header[4] = 0;
		header[5] = 0;
				
		//Generate and insert Indication Field
		String indicationField = "1010101010101010";
		tempBytes = ByteBuffer.allocate(4).putInt(Integer.parseInt(indicationField, 2)).array();
		header[6] = tempBytes[2];
		header[7] = tempBytes[3];
		
		return header;
	}
	
   
   public static boolean checkForErrors(HashMap headerInfo,byte[] receivedByte,byte[] header){
	   byte[] tempBytes; 
	   boolean isCorrect = false;
	   int checksum = crc16(receivedByte);
	   tempBytes = ByteBuffer.allocate(4).putInt(checksum).array();
	   if(tempBytes[2] == header[4] && tempBytes[3] == header[5]){
		   isCorrect = true;
	   }
	   return isCorrect;
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
}