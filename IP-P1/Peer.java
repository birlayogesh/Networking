import java.io.IOException;
import java.util.LinkedList;




public class Peer {
	static LinkedList peerIndex = new LinkedList();
	static LinkedList rfcIndex = new LinkedList();
	static int cookie;
	public static void main(String args[]) throws IOException {
		Peer p = new Peer();
		NewServerThread server = new NewServerThread();
		server.start();
		NewClientThread client = new NewClientThread();		
		client.start();
	}
}
