import java.io.Serializable;
import java.util.HashMap;


public class PeerInfo implements Serializable {
	//private String id;
	private String hostName;
	private int portNumber;
	
	public PeerInfo(){
	
	}
	
	public PeerInfo(String hostName, int portNumber) {
		super();
		//this.id = id;
		this.hostName = hostName;
		this.portNumber = portNumber;
	}

	/*public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}*/

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	@Override
	public String toString() {
		return "PeerInfo [hostName=" + hostName + ", portNumber=" + portNumber
				+ "]";
	}

	
	
	
}
