import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

 class HandleRegister extends Thread{
	LinkedList peerIndex;
	String hostName;
	int portNumber;
	long counter;
	
	
	
	public HandleRegister(LinkedList peerIndex,String hostName,int portNumber){
		this.peerIndex = peerIndex;
		this.hostName = hostName;
		this.portNumber = portNumber;
	}
	
	public void run(){
		RegistrationServer rs = null;
		Iterator i = peerIndex.iterator();
		PeerInfo peer = null;
		
		boolean isAlreadyRegistered = false;
		
		while(i.hasNext()){
			rs = (RegistrationServer)i.next();
			if(rs.getPeer().getHostName().equalsIgnoreCase(hostName)){
				isAlreadyRegistered = true;
               
               rs.setTtl(7200);
               rs.recentConnected=true;
               
               try{
               	Socket s = new Socket(rs.getPeer().getHostName(),rs.getPeer().getPortNumber());
				PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
				String response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","102","Already_Registered",String.valueOf(rs.getCookie()),Utility.getHostName(), Utility.getOsName(), "N/A");
				rs.setNumberOfTimesRegistered(rs.getNumberOfTimesRegistered() + 1);
				rs.setRecentDateConnected(new Date());
				pw.println(response);
				pw.close();
				s.close();
               }catch(Exception e){
            	   e.printStackTrace();
               }
				break;
			}
		}
		
		if(!isAlreadyRegistered){
			
			peer = new PeerInfo(hostName,portNumber);
			rs = new RegistrationServer(++RegistrationServerImpl.cookie,false,7200,peer,new Date(),1);
			peerIndex.add(rs);
			Socket s;
			PrintWriter pw =null;
			try {
				s = new Socket(peer.getHostName(),peer.getPortNumber());
				pw = new PrintWriter(s.getOutputStream(),true);
				String response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","101","OK",String.valueOf(rs.getCookie()),Utility.getHostName(), Utility.getOsName(), "N/A");
				pw.println(response);
				pw.close();
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			//Timer Logicccc Starts
		if(rs.isActive()==false){
			rs.setActive(true);
			int remainingTime = 7200;
	        long timeout = System.currentTimeMillis() + (remainingTime * 1000);
	        while (System.currentTimeMillis() < timeout) {
	            try {
	            	if(rs.getRecentConnected()==true){
	            		rs.setRecentConnected(false);
	            		remainingTime = 7200;
	        	        timeout = System.currentTimeMillis() + (remainingTime * 1000);
	            	}
	            		
					Thread.sleep(1000);
					 rs.setTtl((int)(timeout - System.currentTimeMillis()) / 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	         
	        }
	        rs.setActive(false);
		}
	        //Timer Logicccc Ends
		}
}
 
 class HandleLeave extends Thread{
		LinkedList peerIndex;
		String hostName;
		public HandleLeave(LinkedList peerIndex,String hostName){
			this.peerIndex = peerIndex;
			this.hostName = hostName;
			
		}
		
		public void run(){
			RegistrationServer rs = null;
			Iterator i = peerIndex.iterator();
			boolean isAlreadyRegistered = false;
			while(i.hasNext()){
				rs = (RegistrationServer)i.next();
				if(rs.getPeer().getHostName().equalsIgnoreCase(hostName)){
					rs.setActive(false);
					break;
				}
			}
		}
	} 
 

 class HandlePQuery extends Thread{
		LinkedList peerIndex;
		String responseHostName;
		int responsePortNumber;
		
		
		
		public HandlePQuery(LinkedList peerIndex,
				String responseHostName, int responsePortNumber) {
			super();
			this.peerIndex = peerIndex;
			this.responseHostName = responseHostName;
			this.responsePortNumber = responsePortNumber;
		}



		public void run(){
			try{
				//System.out.println("Port is :: " + this.responsePortNumber);
			//	System.out.println("Hostname is :: " + this.responseHostName);
				int requestCookie =0;
				boolean isAuthorized = false;
				Socket s = new Socket(this.responseHostName,this.responsePortNumber);
				System.out.println(peerIndex);
				PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				Iterator i = peerIndex.iterator();
				LinkedList sendPeerIndex = new LinkedList();
				RegistrationServer rs = null;
				while(i.hasNext()){
					rs = (RegistrationServer)i.next();
					if(rs.getPeer().getHostName().equalsIgnoreCase(this.responseHostName)){
						requestCookie = rs.getCookie();
						if(rs.isActive())
							isAuthorized = true;
					}	
					if(rs.isActive())
						sendPeerIndex.add(rs);
				}
				out.writeObject(sendPeerIndex);
				out.flush();
				String response = null;
				if(isAuthorized)
					 response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","101","OK",String.valueOf(requestCookie),Utility.getHostName(), Utility.getOsName(), "N/A");
				else
					 response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","110","NOT_AUTHORIZED","N/A",Utility.getHostName(), Utility.getOsName(), "N/A");
				pw.println(response);
				out.close();
				pw.close();
				s.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
 }
 
 class HandleKeepAlive extends Thread{
		LinkedList peerIndex;
		String responseHostName;
		int responsePortNumber;
		
		
		
		public HandleKeepAlive(LinkedList  peerIndex,
				String responseHostName, int responsePortNumber) {
			super();
			this.peerIndex = peerIndex;
			this.responseHostName = responseHostName;
			this.responsePortNumber = responsePortNumber;
		}



		public void run(){
			try{
				Socket s = new Socket(this.responseHostName,this.responsePortNumber);
				PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
				
				Iterator i = peerIndex.iterator();
				RegistrationServer rs;
				String tempCookie="N/A";
				while(i.hasNext()){
					rs=(RegistrationServer)i.next();
					if(rs.getPeer().getHostName().equalsIgnoreCase(this.responseHostName)){
						  rs.setTtl(7200);
			              rs.recentConnected=true;
			              tempCookie=String.valueOf(rs.getCookie());
						break;
					}
					i.next();
				}
				String response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","101","OK",tempCookie,Utility.getHostName(), Utility.getOsName(), "N/A");
				pw.println(response);
				pw.close();
				s.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
}
 
 
 
 
public class RegistrationServerImpl {
	static int cookie;
	
	public static void main(String args[]) throws IOException{
		Socket socket = null;
		ServerSocket listener = new ServerSocket(65423);
		LinkedList peerIndex = new LinkedList();
		int choice =0;
		while(true){
			try{
				socket = listener.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String[] lines = {br.readLine(),br.readLine(),br.readLine(),br.readLine()};
				HashMap params = Utility.parse(lines);
			//	System.out.println(params);
				
				if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("register"))
					choice = 1;
				else if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("leave"))
					choice = 2;
				else if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("pquery"))
					choice = 3;
				else if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("keepalive"))
					choice = 4;
				
				System.out.println("\nRequest At Server:\n");
				System.out.println(params.get("Method")+ " "+params.get("Document")+" "+params.get("Version")+" "
						+params.get("StatusCode")+" "+params.get("Phrase")+" "+params.get("Cookie"));
						System.out.println("Host:"+ params.get("Host"));
						System.out.println("OS:"+params.get("OS"));
				
				switch(choice){
				case 1:
				//	System.out.println("New thread Request");
						String hostName = (String)params.get("Host");
						int portNumber = Integer.parseInt((String)params.get("Port"));
						HandleRegister hr = new HandleRegister(peerIndex,hostName,portNumber);
						hr.start();
					
					
				//	System.out.println(peerIndex.size());
					break;
					
				case 2:
					try{
						 hostName = (String)params.get("Host");
						 HandleLeave lr = new HandleLeave(peerIndex,hostName);
						 lr.start();
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				case 3:
						 hostName = (String)params.get("Host");
						 portNumber = Integer.parseInt((String)params.get("Port"));
						HandlePQuery pq = new HandlePQuery(peerIndex,hostName,portNumber);
						pq.start();
					break;
				
				case 4:
					 hostName = (String)params.get("Host");
					 portNumber = Integer.parseInt((String)params.get("Port"));
					 HandleKeepAlive ka = new HandleKeepAlive(peerIndex,hostName,portNumber);
					 ka.start();
					break;
				//listener.close();
				}
				out.close();
				socket.close();
		//		listener.close();
				
			}catch (SocketException e){
				//socket.close();
				System.out.println("Caught an exception");
				e.printStackTrace();
			}
		}
		//listener.close();
	}
}
