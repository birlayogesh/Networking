import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class Node implements Serializable{
	int nodeNumber;
	double table[][];
	int noOfNodes;


	public Node(int noOfNodes,int nodeNumber) {
		super();
		this.nodeNumber = nodeNumber;
		this.noOfNodes = noOfNodes;
	}


	public double[][] getTable() {
		return table;
	}

	public void setTable(double[][] table) {
		this.table = table;
	}

	
	public int getNoOfNodes() {
		return noOfNodes;
	}

	public void setNoOfNodes(int noOfNodes) {
		this.noOfNodes = noOfNodes;
	}

	
	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	

	public String toString() {
		System.out.println("Node " + nodeNumber + "table");
		double value;
		for(int i=0;i<noOfNodes;i++){
			for(int j=0;j<noOfNodes;j++){
				if(table[i][j] == 99999){
					value = -1;
				}else
					value = table[i][j];
				System.out.print(" "+value);
			}
			System.out.println();	
		}
		return "";
	}
	
	public boolean performDVCheckUp(){
		int srcNode = nodeNumber - 1;
		int flag = 0;
		double minCost;
		boolean leastCostChanged= false;
		
		for(int destNode=0;destNode<noOfNodes;destNode++){
			minCost=99999999;
			flag = 0;
			for(int j=0;j<noOfNodes;j++){
				if(destNode == srcNode){
					table[srcNode][destNode] = 0;
					flag = 1;
					break;
				}else if(srcNode == j){
					continue;
				}
				else{
					minCost = Math.min(minCost,(table[srcNode][j] + table[j][destNode]));
				}
			}
			if(flag == 0){
				if(table[srcNode][destNode] > minCost){
					table[srcNode][destNode] = minCost;
					leastCostChanged = true;
				}
			}
		}
		return leastCostChanged;
	}
}


class DistributedDVClient extends Thread implements Serializable{
	String[] hostName;
	int[] portNumber;
	String path;
	int nodeNumber;
	DatagramSocket socket;
	
	
	
public DistributedDVClient(String[] hostName, int[] portNumber,String path,int nodeNumber,DatagramSocket serverSocket) {
		super();
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.path = path;
		this.nodeNumber = nodeNumber+1;
		this.socket = serverSocket;
	
	}


public String[] getHostName() {
	return hostName;
}


public void setHostName(String[] hostName) {
	this.hostName = hostName;
}


public int[] getPortNumber() {
	return portNumber;
}


public void setPortNumber(int[] portNumber) {
	this.portNumber = portNumber;
}



public static Node initializeNodes(String filePath,int nodeNumber)throws IOException{
	int NoOfNodes = 0,row,column,flag = -999;
	double value;
	FileInputStream fis = new FileInputStream(new File(filePath));
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	NoOfNodes = Integer.parseInt(br.readLine());
	
	String str=br.readLine();
	String token[] = str.split(" ");
	row = Integer.parseInt(token[0]);

	Node node = new Node(NoOfNodes,nodeNumber);
	double[][] table = new double[NoOfNodes][NoOfNodes];
	table = initializeTable(table,NoOfNodes);
	table[row-1][row-1]=0;
	while(true){
		row = Integer.parseInt(token[0]);
		column = Integer.parseInt(token[1]);
		table[row-1][Integer.parseInt(token[1])-1] = Double.parseDouble(token[2]);
		str = br.readLine();
		if(str==null){
			flag =1;
			break;
		}
		token = str.split(" ");
	}
	
	node.setTable(table);
	node.setNoOfNodes(NoOfNodes);
	br.close();
	fis.close();
	return node;
}


public static double[][] initializeTable(double[][] table,int noOfNodes){
	for(int i=0;i<noOfNodes;i++){
		for(int j=0;j<noOfNodes;j++){
				table[i][j] = 99999;
			
		}
	}
	return table;
}



public static void displayTables(Node node){
		node.toString();
	
}



public void run(){
	
	
	int initialNode = 3;
	Node node = null;
	double[][] receivedTable = null;
	int flag = 0;
	
	try {
		node = initializeNodes(this.path,nodeNumber);
		
		
		while(true){
			if(flag == 0 && node.getNodeNumber() == initialNode){
				sendDistanceVector(node,this.hostName,this.portNumber,this.socket);
				flag =1;
			}else{
				if(node.getNodeNumber() == initialNode){
					printFormattedOutput(node);
					//node.toString();
					System.out.println();
				}
				receivedTable = receiveDistanceVector(this.portNumber[nodeNumber-1],socket);
				copyTable(receivedTable,node.getTable(),node.getNoOfNodes());
				boolean isLeastCostChanged = node.performDVCheckUp();
				if(isLeastCostChanged)
					sendDistanceVector(node,this.hostName,this.portNumber,this.socket);
				
			}
			
		}
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}


public static void printFormattedOutput(Node node){
	double[][] table = node.getTable();
	
	//REF: www.stackoverflow.com
	String leftAlignFormat = "| %-15s | %.2f |%n";

	System.out.println("NODE"+(node.nodeNumber)+" routing table");
	System.out.format("+--------------+----------+%n");
	System.out.printf("| Node name    | Distance |%n");
	System.out.format("+--------------+----------+%n");
	for (int i = 0; i < node.getTable().length; i++) {
	    System.out.format(leftAlignFormat, "Node " + (i+1),node.getTable()[node.nodeNumber-1][i]);
	}
	System.out.format("+--------------+----------+%n");
}

public static void sendDistanceVector(Node node,String[] hostName, int[] portNumber,DatagramSocket socket) throws IOException{
	
		double[][] srcTable = node.getTable();
		String srcTableStr = convertSrcTableToString(srcTable,node.noOfNodes);
		byte[] byteArr = srcTableStr.getBytes();
		
		int nodeNumber = node.getNodeNumber() - 1;
		for(int j=0;j<node.noOfNodes;j++){
			if(j == nodeNumber)
				continue;
			if(srcTable[nodeNumber][j]!=99999){
				InetAddress destAddress = InetAddress.getByName(hostName[j]);
				DatagramPacket srcTablePacket = new DatagramPacket(byteArr,byteArr.length, destAddress, portNumber[j]);
	            socket.send(srcTablePacket);
			}
		}
		
}

public static String convertSrcTableToString(double[][] srcTable,int noOfNodes){
	String str = "";
	
	double value;
	for(int i=0;i<noOfNodes;i++){
		for(int j=0;j<noOfNodes;j++){
			str = str + srcTable[i][j]+",";
		}
	}
	return str;
	
}


public static double[][] receiveDistanceVector(int portNumber,DatagramSocket socket) throws IOException{
	double[][] receivedTable = null;
	byte[] receiveData = new byte[115]; 
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    socket.receive(receivePacket);
    receivedTable = convertStringToDoubleArr(receiveData);
	return receivedTable;
}

public static double[][] convertStringToDoubleArr(byte[] receiveData){
	double[][] receivedArr = new double[4][4];
	String str = new String(receiveData);
	String[] token = str.split(",");
	int count = 0;
	for(int i=0;i<4;i++){
		for(int j=0;j<4;j++){
			receivedArr[i][j] = Double.parseDouble(token[count]);
			count++;
		}
	}
	return receivedArr;
    
}


public static void copyTable(double[][] srcTable, double[][] destTable, int noOfNodes){
	for(int i=0;i<noOfNodes;i++){
		for(int j=0;j<noOfNodes;j++){
			if(destTable[i][j] > srcTable[i][j] || (destTable[i][j]==99999 && srcTable[i][j]!=99999)){
				destTable[i][j] = srcTable[i][j];
			}
		}
	}
}



}


public class DistributedDV {
	public static void main(String args[])throws IOException, InterruptedException{
		String[] hostName = {"192.168.2.3","192.168.2.3","192.168.2.2","192.168.2.2"};
		int[] portNumber = {65401,65402,65403,65404};
		String[] filePath = {"F:/Semester 1/IP/Projects/Project3/Peer Manual Files/node1.txt","F:/Semester 1/IP/Projects/Project3/Peer Manual Files/node2.txt","F:/Semester 1/IP/Projects/Project3/Peer Manual Files/node3.txt","F:/Semester 1/IP/Projects/Project3/Peer Manual Files/node4.txt"};
		
	
		
		DatagramSocket socket0 = new DatagramSocket(portNumber[0]);
		DatagramSocket socket1 = new DatagramSocket(portNumber[1]);
		//DatagramSocket socket2 = new DatagramSocket(portNumber[2]);
		//DatagramSocket socket3 = new DatagramSocket(portNumber[3]);
		
		DistributedDVClient dvClient0 = new DistributedDVClient(hostName,portNumber,filePath[0],0,socket0);
		DistributedDVClient dvClient1 = new DistributedDVClient(hostName,portNumber,filePath[1],1,socket1);
		//DistributedDVClient dvClient2 = new DistributedDVClient(hostName,portNumber,filePath[2],2,socket2);
		//DistributedDVClient dvClient3 = new DistributedDVClient(hostName,portNumber,filePath[3],3,socket3);
		
		
		//dvClient2.start();
	//	Thread.sleep(100);
	//	dvClient3.start();
	//	Thread.sleep(100);
		
		dvClient0.start();
		Thread.sleep(100);
		dvClient1.start();
		Thread.sleep(100);
		
	}
}
