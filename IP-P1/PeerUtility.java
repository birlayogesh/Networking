
import java.util.HashMap;


public class PeerUtility {

	public static String createRequest(String operation,String document, String version,String hostName,String osName,String portNumber){
		String r1=operation+" "+document+" "+version+"\r\n";
		String r2="Host"+" "+hostName+"\r\n";
		String r3="OS"+" "+osName+"\r\n";
		String r4="Port"+" "+portNumber+"\r\n\r\n";
		return r1+r2+r3+r4;
	}
	

}
