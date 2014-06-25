import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


public class Utility {
	public static HashMap<String,String> parse(String[] request){
		HashMap<String,String> params = new HashMap<String,String>();
		boolean header = false;
		for(String s:request){
			String[] fields = s.split(" ");
			if(header==false){
				params.put("Method", fields[0]);
				params.put("Document", fields[1]);
				params.put("Version", fields[2]);
				params.put("StatusCode",fields[3]);
				params.put("Phrase",fields[4]);
				params.put("Cookie",fields[5]);
				header = true;
			}else{
				params.put(fields[0], fields[1]);
			}
			
		}
		return params;
	}
	
	
	public static LinkedList readRFCIndexFromFile() throws IOException{
		LinkedList rfcIndex = new LinkedList();	
		FileInputStream fstream = new FileInputStream("RFCDb.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String str;
		String hostName = InetAddress.getLocalHost().getHostAddress();
		while((str = br.readLine())!=null){
			String[] tokens = str.split("#");
			RFCInfo ri = new RFCInfo(Integer.parseInt(tokens[0]),tokens[1],hostName,7200);
			rfcIndex.add(ri);
		}
		return rfcIndex;
	}

	public static HashMap<Integer,String> generateRFCLocationInfo() throws IOException{
		FileInputStream fstream = new FileInputStream("RFCDb.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String str;
		String hostName = InetAddress.getLocalHost().getHostAddress();
		HashMap<Integer,String> rfcLoc = new HashMap<Integer,String>();
		
		while((str = br.readLine())!=null){
			String[] tokens = str.split("#");
			rfcLoc.put(Integer.parseInt(tokens[0]),tokens[2]);
			
		}
		return rfcLoc;
	}
	
	
	public static String saveFile(Socket socket) throws Exception {  
		final int BUFFER_SIZE = 1000; 
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());  
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());  
        FileOutputStream fos = null;  
        byte [] buffer = new byte[BUFFER_SIZE];  
        
     // 1. Read file name.  
        String fileName = ois.readUTF();
        File file = new File(System.getProperty("user.dir")+"/RFCDb/",fileName);
        Object o =null;
        fos = new FileOutputStream(file);  
            
        // 2. Read file to the end.  
        Integer bytesRead = (Integer)0;  
        do {  
            o = ois.readObject();  
            if (!(o instanceof Integer)) {  
                throwException("Something is wrong");  
            }  
            bytesRead = (Integer)o;  
            o = ois.readObject();  
            if (!(o instanceof byte[])) {  
                throwException("Something is wrong");  
            }  
            buffer = (byte[])o;  
  
            // 3. Write data to output file.  
            fos.write(buffer, 0, bytesRead);  
        } while (bytesRead == BUFFER_SIZE);  
          
        System.out.println("File transfer success");  
          
        fos.close();  
        ois.close();  
        oos.close();  
        return file.getAbsolutePath().replace('\\', '/');
    }  


	public static void sendFile(String file_name,Socket socket1)throws Exception{
		 
		File file = new File(file_name);   
		ObjectInputStream ois = new ObjectInputStream(socket1.getInputStream());  
		ObjectOutputStream oos = new ObjectOutputStream(socket1.getOutputStream());  
		oos.writeUTF(file.getName()); 
		
		FileInputStream fis = new FileInputStream(file);  
		byte [] buffer = new byte[1000];  
		Integer bytesRead = 0;  
		while ((bytesRead = fis.read(buffer)) > 0) {  
		     oos.writeObject(bytesRead);  
		     oos.writeObject(Arrays.copyOf(buffer, buffer.length));  
		}  
		 
		oos.close();  
		ois.close();  
	}
	
	
	public static void updateRFCDb(String record) throws IOException{
		
		//Find out if this the duplicate entry or not
		HashMap<Integer,String> locInfo=(HashMap<Integer,String>)Utility.generateRFCLocationInfo();
		String[] tokens = record.split("#");
		int rfcNumber = Integer.parseInt(tokens[0]); 
		if(!locInfo.containsKey(rfcNumber)){
			File file = new File("RFCDb.txt");
			FileWriter fw = new FileWriter(file.getName(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(record);
			bw.close();
		}
	}
	
	
	
public static void throwException(String message) throws Exception {  
    throw new Exception(message);  
}  

public static String createRequest(String operation,String document, String version,String statusCode, String phrase,String cookie,String hostName,String osName,String portNumber){
	String r1=operation+" "+document+" "+version+" "+statusCode+" "+phrase+" "+cookie+"\r\n";
	String r2="Host"+" "+hostName+"\r\n";
	String r3="OS"+" "+osName+"\r\n";
	String r4="Port"+" "+portNumber+"\r\n\r\n";
	return r1+r2+r3+r4;
}


public static String getHostName(){
    try {
            return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
        e.printStackTrace();
    }
    return null;
}

public static String getOsName(){
    return System.getProperty("os.name");
}

}


