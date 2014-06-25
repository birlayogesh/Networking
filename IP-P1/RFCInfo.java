import java.io.Serializable;


public class RFCInfo implements Serializable{
	int rfcNumber;
	String rfcTitle;
	String hostName;
	int ttl;
	
	public RFCInfo(int rfcNumber, String rfcTitle, String hostName, int ttl) {
		super();
		this.rfcNumber = rfcNumber;
		this.rfcTitle = rfcTitle;
		this.hostName = hostName;
		this.ttl = ttl;
	}
	
	public int getRfcNumber() {
		return rfcNumber;
	}
	public void setRfcNumber(int rfcNumber) {
		this.rfcNumber = rfcNumber;
	}
	public String getRfcTitle() {
		return rfcTitle;
	}
	public void setRfcTitle(String rfcTitle) {
		this.rfcTitle = rfcTitle;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	
	@Override
	public String toString() {
		return "RFCInfo [rfcNumber=" + rfcNumber + ", rfcTitle=" + rfcTitle
				+ ", hostName=" + hostName + ", ttl=" + ttl + "]";
	}
	
	
}
