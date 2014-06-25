
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
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

class NewServerThread extends Thread {
	String hostName=null;
	int portNumber=0;
	String docNum=null;
	public void run() {
		ServerSocket listener = null;
		Socket socket = null;
		int choice=0;
		try {
			listener = new ServerSocket(65450);
			//socket = listener.accept();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true) {
			try {
			
				socket = listener.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
				String[] lines = { br.readLine(), br.readLine(), br.readLine(),br.readLine() };
				
				
				
				HashMap params = Utility.parse(lines);
				
				
				if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("rfcquery"))
					choice = 1;
				else if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("getrfc"))
					choice = 2;
				else if(((String) params.get("Method")).toLowerCase().equalsIgnoreCase("N/A"))
					choice = 3;
				
				switch (choice) {
				
				case 1:
					System.out.println("\nRequest At Peer Server:");
					System.out.println(lines[0]);
					System.out.println(lines[1]);
					System.out.println(lines[2]);
					System.out.println();
					hostName = (String) params.get("Host");
					portNumber = Integer.parseInt((String) params
							.get("Port"));
					HandleRFCQuery hr = new HandleRFCQuery(hostName, portNumber);
					hr.start();
					hr.join();
					break;
					
				case 2:
					System.out.println("\nRequest At Peer Server:");
					System.out.println(lines[0]);
					System.out.println(lines[1]);
					System.out.println(lines[2]);
					System.out.println();
					hostName = (String) params.get("Host");
					portNumber = Integer.parseInt((String) params
							.get("Port"));
					
					docNum=(String) params.get("Document");
					
					HashMap<Integer,String> locInfo=(HashMap<Integer,String>)Utility.generateRFCLocationInfo();
					String filePath=(String)locInfo.get(new Integer(docNum));
					
					Socket sendingsocket = new Socket(hostName, portNumber);
					PrintWriter pw = new PrintWriter(sendingsocket.getOutputStream(),true);
					String response = null;
                    response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","306","SUCCESS","N/A",Utility.getHostName(), Utility.getOsName(), "N/A");
                    pw.println(response);
                    Utility.sendFile(filePath, sendingsocket);
					pw.close();
					sendingsocket.close();
					break;
					
				case 3:

					System.out.println("\nResponse:");
					System.out.println(lines[0]);
					System.out.println(lines[1]);
					System.out.println(lines[2]);
					System.out.println();
					
					if(params.get("Cookie")!=null){
						Peer.cookie = Integer.parseInt((String)params.get("Cookie"));
						File file = new File("cookie.ser");   /// Need to change the path
						if(!file.exists())
							file.createNewFile();
						FileOutputStream fileOut = new FileOutputStream("cookie.ser");
						ObjectOutputStream fileoutStream = new ObjectOutputStream(fileOut);
						fileoutStream.writeObject(String.valueOf(Peer.cookie));
				        fileoutStream.close();
				        fileOut.close();
					}
					break;
					
				}
				socket.close();
				//listener.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

}

class HandleRFCQuery extends Thread {
	String hostName;
	int portNumber;

	public HandleRFCQuery(String hostName, int portNumber) {
		super();
		this.hostName = hostName;
		this.portNumber = portNumber;
	}

	public void run() {
		try {
			LinkedList rfcIndex = Utility.readRFCIndexFromFile();
			Socket s = new Socket(hostName, portNumber);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(rfcIndex);
			out.flush();
			PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
            String response = null;
            response = Utility.createRequest("N/A","N/A", "P2P-DI/1.0","101","OK","N/A",Utility.getHostName(), Utility.getOsName(), "N/A");
            pw.println(response);
             
             
             pw.close();
			out.close();
			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}




