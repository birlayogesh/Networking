import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;

class NewClientThread extends Thread {
	
	
	public void run() {
		String request;
		Socket a;
		BufferedReader br=null;
		br = new BufferedReader(new InputStreamReader(System.in));
		int val=0;
		String hostName=null;
		String osName=null;
		String ipAddress=null;
		System.out.println("Enter the IP Address of the Registration Server");
		
		
			
			try {
				ipAddress = br.readLine();
				hostName = InetAddress.getLocalHost().getHostAddress();
				osName = System.getProperty("os.name");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (true) {
				try {
				System.out.println("\n1. Communication with Server");
				System.out.println("\n2. Communication with Peers");
				System.out.println("\n3. Exit");
				System.out.println("\nEnter your choice");
		        int value=Integer.parseInt(br.readLine());
		        
				if(value==1){	
					System.out.println("\nChoose Server Side Operations");
				System.out.println("\n1. Register:");
				System.out.println("\n2. PQuery:");
				System.out.println("\n3. Leave:");
				System.out.println("\n4. Keep Alive:");
				System.out.println("Enter your choice");
				val = Integer.parseInt(br.readLine());
				
				}else if(value==2){
					System.out.println("\nChoose Client Side Operations");
				  System.out.println("\n1. GET RFC");
				  System.out.println("\nEnter your choice");
				   val = Integer.parseInt(br.readLine());
				   if(val==1)
					 val=5;
				   else
					   System.out.println("Wrong Choice");
				}else{
					 System.out.println("Thank You For Using Our Application. See You soon.");
					 System.exit(1);
 				}
				switch (val) {
				case 1:
					a = new Socket(ipAddress, 65423);
					
					//Read cookie from file
					File file = new File("cookie.ser");
					if(file.exists()){
						FileInputStream fileIn = new FileInputStream("cookie.ser");
						ObjectInputStream fileInStream = new ObjectInputStream(fileIn);
						try {
							Peer.cookie = Integer.parseInt((String)fileInStream.readObject());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							System.out.println("File Not Found...");
						}
						fileInStream.close();
						fileIn.close();
					}
					PrintWriter out = new PrintWriter(a.getOutputStream(), true);
					request = Utility.createRequest("REGISTER","N/A", "P2P-DI/1.0","N/A","N/A",String.valueOf(Peer.cookie), hostName, osName, "65450");
					out.println(request.toString());
					out.close();
					a.close();
					break;
				case 2:
					ClientRequest cr = new ClientRequest(ipAddress);
					cr.start();
					//cr.join();
					break;
				case 3:
					a = new Socket(ipAddress, 65423);
					out = new PrintWriter(a.getOutputStream(), true);
					request = Utility.createRequest("LEAVE","N/A", "P2P-DI/1.0","N/A","N/A",String.valueOf(Peer.cookie), hostName, osName, "65450");
					out.println(request.toString());
					out.close();
					a.close();
					break;
				case 4:	
					a = new Socket(ipAddress, 65423);
					out = new PrintWriter(a.getOutputStream(), true);
					request = Utility.createRequest("KEEPALIVE","N/A", "P2P-DI/1.0","N/A","N/A",String.valueOf(Peer.cookie), hostName, osName, "65450");
					out.println(request.toString());
					out.close();
					a.close();
					break;
				case 5:
					long startTime = System.currentTimeMillis();
					HandleGETRfc grfc=null;
					System.out.println("Which RFC : ");
					grfc = new HandleGETRfc(Integer.parseInt(br.readLine()));
					grfc.start();
					grfc.join();
					//long endTime = System.currentTimeMillis();
					//System.out.println("Total time taken for transfer :: " + (startTime-endTime));
					break;
				
				default:
					System.out.println("Wrong Choice");
				}
			}catch(SocketException s){
				System.out.println("Problem Connecting Host. Check IP Address of Server");
			} catch (NumberFormatException e) {
				System.out.println("Please Enter Your Choice as Numericals(1,2...) Contine..");			
			}catch(Exception e){
				System.out.println("Problem in Connection...");
			}
		

	}
}
}



class ClientRequest extends Thread implements Serializable {
	String ipAddress = null;
	
	
	public ClientRequest(String ipAddress) {
		super();
		this.ipAddress = ipAddress;
	}



	public void run() {
		ServerSocket listener;
		Socket socketServer, socketClient;
		String hostName = null;
		//LinkedList peerIndexNew;
		RegistrationServer rs, rs1 = null;
		try {
			hostName = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String osName = System.getProperty("os.name");
		try {
			listener = new ServerSocket(0);
			socketClient = new Socket(this.ipAddress, 65423);

			PrintWriter out = new PrintWriter(socketClient.getOutputStream(),true);

			String request = Utility.createRequest("PQUERY", "N/A","P2P-DI/1.0","N/A","N/A",String.valueOf(Peer.cookie), hostName, osName,String.valueOf(listener.getLocalPort()));
			out.println(request.toString());

			socketServer = listener.accept();
			ObjectInputStream in = new ObjectInputStream(socketServer.getInputStream());
			Peer.peerIndex = (LinkedList) in.readObject();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
			String[] lines = { br.readLine(), br.readLine(), br.readLine(),br.readLine() };
//			System.out.println(lines);

			System.out.println("\nResponse:");
			System.out.println(lines[0]);
			System.out.println(lines[1]);
			System.out.println(lines[2]);
			System.out.println();
						
			
			/*if(peerIndexNew.isEmpty()){
				Peer.peerIndex.removeAll(Peer.peerIndex);
			}
			Iterator i = peerIndexNew.iterator();
			int flag = 1;
			while (i.hasNext()) {
				flag = 1;
				rs = (RegistrationServer) i.next();

				if (Peer.peerIndex.size() == 0)
					Peer.peerIndex.add(rs);

				for (int i1 = 0; i1 < Peer.peerIndex.size(); i1++) {
					rs1 = (RegistrationServer) Peer.peerIndex.get(i1);
					if (rs1.getPeer().getHostName().equalsIgnoreCase(rs.getPeer().getHostName())){
						Peer.peerIndex.remove(i1);
						Peer.peerIndex.add(rs);
						flag=0;
					}
				}
				if (flag == 1){
					Peer.peerIndex.add(rs);
				}	
			}*/		
			System.out.println(Peer.peerIndex);
			in.close();
			socketServer.close();
			socketClient.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}	
	


class HandlePerformRFCQuery extends Thread implements Serializable {
	String hostName;
	int portNumber;
	
	
	public HandlePerformRFCQuery(String hostName, int portNumber) {
		super();
		this.hostName = hostName;
		this.portNumber = portNumber;
	}


	public void run() {
	ServerSocket listener;
	Socket socketServer, socketClient;
	String hostName = null;
	LinkedList rfcIndex = new LinkedList();
	RFCInfo rs, rs1 = null;
	try {
		//hostName = InetAddress.getLocalHost().getHostAddress();
		
		String osName = System.getProperty("os.name");
		LinkedList peerIndex = Peer.peerIndex;
		
		listener = new ServerSocket(0);
		socketClient = new Socket(this.hostName, this.portNumber);

		PrintWriter out = new PrintWriter(socketClient.getOutputStream(),
				true);

		String request = Utility.createRequest("RFCQuery", "N/A", "P2P-DI/1.0","N/A","N/A",String.valueOf(Peer.cookie), InetAddress.getLocalHost().getHostAddress(), osName,String.valueOf(listener.getLocalPort()));
		out.println(request.toString());

		socketServer = listener.accept();
		ObjectInputStream in = new ObjectInputStream(
				socketServer.getInputStream());
		rfcIndex = (LinkedList) in.readObject();
		Iterator i = rfcIndex.iterator();
		int flag = 1;
		while (i.hasNext()) {
			flag = 1;
			rs = (RFCInfo) i.next();

			if (Peer.rfcIndex.size() == 0)
				Peer.rfcIndex.add(rs);

			for (int i1 = 0; i1 < Peer.rfcIndex.size(); i1++) {
				rs1 = (RFCInfo) Peer.rfcIndex.get(i1);
				if (rs1.getRfcNumber() == rs.getRfcNumber())
					flag = 0;
			}
			if (flag == 1)
				Peer.rfcIndex.add(rs);
		}
		System.out.println(Peer.rfcIndex);
		BufferedReader br = new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
		String[] lines = { br.readLine(), br.readLine(), br.readLine(),br.readLine() };
//		System.out.println(lines);

		System.out.println("\nResponse:");
		System.out.println(lines[0]);
		System.out.println(lines[1]);
		System.out.println(lines[2]);
		System.out.println();
		
		in.close();
		//socketServer.close();
		socketClient.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("Error in getting RFC Information. Problem in Network. Please reconnect.");
	}

}
}


class HandleGETRfc extends Thread implements Serializable {
	int rfcNumber;


public HandleGETRfc(int rfcNumber) {
	super();
	this.rfcNumber = rfcNumber;
}
public void run() {
	LinkedList peerIndex = Peer.peerIndex;
	boolean rfcFound = false;
	RFCInfo rfcInfo = null; 
	
	try{
		String hostName = InetAddress.getLocalHost().getHostAddress();
		String osName = System.getProperty("os.name");
		
		LinkedList rfcIndex = Peer.rfcIndex;
		Iterator ifcIndexIterator = rfcIndex.iterator();
		while(ifcIndexIterator.hasNext()){
			rfcInfo = (RFCInfo)ifcIndexIterator.next();
			if(rfcInfo.getRfcNumber() == this.rfcNumber){
				rfcFound = true;
				break;
			}
		}
		Iterator i = peerIndex.iterator();
		if(!rfcFound){
			while(i.hasNext()){
				RegistrationServer rs = (RegistrationServer)i.next();
				if(!rs.getPeer().getHostName().equalsIgnoreCase(hostName)){
					if(!rfcFound){
						HandlePerformRFCQuery performRFCQuery = new HandlePerformRFCQuery(rs.getPeer().getHostName(),rs.getPeer().getPortNumber());
						performRFCQuery.start();
						performRFCQuery.join();
					}
				}
			}
				rfcIndex = Peer.rfcIndex;
				ifcIndexIterator = rfcIndex.iterator();
				while(ifcIndexIterator.hasNext()){
					rfcInfo = (RFCInfo)ifcIndexIterator.next();
					if(rfcInfo.getRfcNumber() == this.rfcNumber){
						rfcFound = true;
						break;
					}
				}
			//	if(rfcFound)
			//		break;
			//}
		}
			if(rfcFound){
				RegistrationServer rs = null;
				boolean peerAlive = false;
				i = peerIndex.iterator();
				while(i.hasNext()){
					rs = (RegistrationServer)i.next();
					if(rs.getPeer().getHostName().equalsIgnoreCase(rfcInfo.getHostName())){
						peerAlive = true;
						break;
					}
				}
			if(rs!=null && peerAlive){
				Socket s = new Socket(rs.getPeer().getHostName(),rs.getPeer().getPortNumber());
				ServerSocket listener = new ServerSocket(0);
				String request = Utility.createRequest("GETRFC", String.valueOf(this.rfcNumber) , "P2P-DI/1.0","N/A","N/A",String.valueOf(Peer.cookie), hostName, osName,String.valueOf(listener.getLocalPort()));
				PrintWriter out = new PrintWriter(s.getOutputStream(),true);
				out.write(request);
				out.flush();
				Socket s1 = listener.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(s1.getInputStream()));
				String[] lines = { br.readLine(), br.readLine(), br.readLine(),br.readLine() };
				System.out.println("\nResponse:");
				System.out.println(lines[0]);
				System.out.println(lines[1]);
				System.out.println(lines[2]);
				System.out.println();
				String filePath = Utility.saveFile(s1);
				String updateRecord = this.rfcNumber + "#" + rfcInfo.getRfcTitle() + "#" + filePath +"\n";
				Utility.updateRFCDb(updateRecord);
				s1.close();
				out.close();
				listener.close();
				s.close();
			}else{
				System.out.println("RFC is not in the network");
			}
		}else{
			System.out.println("RFC is not in the network");
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
}
}









