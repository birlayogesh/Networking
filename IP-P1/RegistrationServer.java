import java.io.Serializable;
import java.util.Date;



public class RegistrationServer implements Serializable{

	int cookie;
	boolean isActive;
	int ttl;
	int numberOfTimesRegistered;
	boolean recentConnected;
	Date recentDateConnected;
	
	
	
	PeerInfo peer;
	private static final long serialVersionUID = 1L;
	
	
	
	public RegistrationServer( int cookie, boolean isActive,
			int ttl,PeerInfo peer,Date recentDateConnected, int numberOfTimeRegistered) {
		super();
		this.cookie = cookie;
		this.isActive = isActive;
		this.ttl = ttl;
		this.peer = peer;
		this.recentDateConnected = recentDateConnected;
		this.numberOfTimesRegistered = numberOfTimeRegistered;
		
	}
	
	public int getCookie() {
		return cookie;
	}
	public void setCookie(int cookie) {
		this.cookie = cookie;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public int getNumberOfTimesRegistered() {
		return numberOfTimesRegistered;
	}
	public void setNumberOfTimesRegistered(int numberOfTimesRegistered) {
		this.numberOfTimesRegistered = numberOfTimesRegistered;
	}
	public boolean getRecentConnected() {
		return recentConnected;
	}
	public void setRecentConnected(boolean recentConnected) {
		this.recentConnected = recentConnected;
	}

	public PeerInfo getPeer() {
		return peer;
	}

	public void setPeer(PeerInfo peer) {
		this.peer = peer;
	}

	public Date getRecentDateConnected() {
		return recentDateConnected;
	}

	public void setRecentDateConnected(Date recentDateConnected) {
		this.recentDateConnected = recentDateConnected;
	}

	
	
	public RegistrationServer(int cookie, boolean isActive, int ttl,
			int numberOfTimesRegistered, boolean recentConnected,
			Date recentDateConnected, PeerInfo peer) {
		super();
		this.cookie = cookie;
		this.isActive = isActive;
		this.ttl = ttl;
		this.numberOfTimesRegistered = numberOfTimesRegistered;
		this.recentConnected = recentConnected;
		this.recentDateConnected = recentDateConnected;
		this.peer = peer;
	}

	@Override
	public String toString() {
		return "RegistrationServer [cookie=" + cookie + ", isActive="
				+ isActive + ", ttl=" + ttl + ", numberOfTimesRegistered="
				+ numberOfTimesRegistered + ", recentConnected="
				+ recentConnected + ", recentDateConnected="
				+ recentDateConnected + ", peer=" + peer + "]";
	}

	
	
	
	
}
