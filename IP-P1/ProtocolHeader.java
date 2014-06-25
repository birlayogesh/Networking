import java.net.InetAddress;
import java.net.UnknownHostException;


public class ProtocolHeader {
	String method;
	String operation;
	String version;
	String hostName;
	String operatingSystem;
	String statusCode;
	String phrase;
	String portNumber;
	
	
	public ProtocolHeader() {
		super();
		
		this.version = "P2P-DI/1.0";
		try {
			this.hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		};
		this.operatingSystem = System.getProperty("os.name");
		
	}
	
	
}
