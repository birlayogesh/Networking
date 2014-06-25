import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


class Node{
	int nodeNumber;
	double table[][];
	int noOfNodes;
	int flag;
	int isInitialized;
	int iterations;
	
	

	public Node(int noOfNodes,int nodeNumber) {
		super();
		this.nodeNumber = nodeNumber;
		this.noOfNodes = noOfNodes;
		this.flag = 0;
		this.isInitialized = 0;
		this.iterations = 0;
	}

	
	public int getIterations() {
		return iterations;
	}


	public void setIterations(int iterations) {
		this.iterations = iterations;
	}


	public double[][] getTable() {
		return table;
	}

	public void setTable(double[][] table) {
		this.table = table;
	}

	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
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

	
	public int getIsInitialized() {
		return isInitialized;
	}

	public void setIsInitialized(int isInitialized) {
		this.isInitialized = isInitialized;
	}

	public String toString() {
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
					if(table[destNode][srcNode] < minCost)
						table[srcNode][destNode] = table[destNode][srcNode];
					else{
						table[srcNode][destNode] = minCost;
						table[destNode][srcNode] = minCost;
					}
						
					leastCostChanged = true;
				}
				this.flag = 0;
				
			}
		}
		this.iterations++;
		return leastCostChanged;
	}
}





class DistanceVector {
	
	public static void main(String args[])throws IOException{
		
		
		int initialNode = Integer.parseInt(args[0]);
		String filePath = args[1];//"F:/Semester 1/IP/Projects/Project3/samples/Input_network/large-net-a.txt";
		int n1 = Integer.parseInt(args[2]);
		int n2 = Integer.parseInt(args[3]);
		ArrayList<Node> nodeList = new ArrayList<Node>();
		int noOfNodes = initializeNodes(filePath,nodeList);
		sendDistanceVector(initialNode,nodeList,noOfNodes);
		nodeList.get(initialNode-1).setIsInitialized(1);
		//displayTables(nodeList);
		startLoop(nodeList,noOfNodes);
		
		
		//Code to print the formatted output.
		System.out.println("The cost of the least-cost path between nodes "+(n1)+" and "+(n2)+" is "+nodeList.get(n1-1).getTable()[n1-1][n2-1]);
		
		System.out.println();
		
		// REF : www.stackoverflow.com
		String leftAlignFormat = "| %-15s | %.2f |%n";

		System.out.println("NODE"+(n1)+" routing table");
		System.out.format("+--------------+----------+%n");
		System.out.printf("| Node name    | Distance |%n");
		System.out.format("+--------------+----------+%n");
		for (int i = 0; i < nodeList.get(n1-1).getTable().length; i++) {
		    System.out.format(leftAlignFormat, "Node " + (i+1),nodeList.get(n1-1).getTable()[n1-1][i]);
		}
		System.out.format("+--------------+----------+%n");
		
		System.out.println();
		
		System.out.println("NODE"+(n2)+" routing table");
		System.out.format("+--------------+----------+%n");
		System.out.printf("| Node name    | Distance |%n");
		System.out.format("+--------------+----------+%n");
		for (int i = 0; i < nodeList.get(n2-1).getTable().length; i++) {
		    System.out.format(leftAlignFormat, "Node " + (i+1), nodeList.get(n2-1).getTable()[n2-1][i]);
		}
		System.out.format("+--------------+----------+%n");
		
		int maxIterations = 0;
		Node maxIteratingNode=null;
		for(int i=0;i<nodeList.size();i++){
			Node node = nodeList.get(i);
			if(node.getIterations() > maxIterations){
				maxIterations = node.getIterations();
				maxIteratingNode = node;
			}
		}
		//System.out.println("Max Iteration " + maxIterations +" taken by Node " + maxIteratingNode.getNodeNumber() + " when starting node is" + z);
		
		}
	//}
	
	
	public static void startLoop(ArrayList<Node> nodeList,int noOfNodes){
		int flag =1;
		while(flag == 1){
			for(int i=0;i<noOfNodes;i++){
				flag = 0;
				Node node = nodeList.get(i);
				if(node.getFlag() == 1){
					if(node.getIsInitialized() == 0){
						sendDistanceVector(node.getNodeNumber(),nodeList,noOfNodes);
						node.setIsInitialized(1);
					}	
					boolean isLeastCostChanged = node.performDVCheckUp();
					if(isLeastCostChanged){
						sendDistanceVector(node.getNodeNumber(),nodeList,noOfNodes);
						flag = 1;
					}
				}
			}
			
			
		}
	}
	
	
	public static void sendDistanceVector(int initialNode,ArrayList<Node> nodeList,int noOfNodes){
		
		initialNode = initialNode - 1;
		Node srcNode = nodeList.get(initialNode);
		double[][] srcTable = srcNode.getTable();
		
		for(int j=0;j<noOfNodes;j++){
			if(j == initialNode)
				continue;
			if(srcTable[initialNode][j]!=99999){
				Node destNode = nodeList.get(j);
				copyTable(srcNode,destNode);
				destNode.setFlag(1);
			}	
		}
		
	}
	
	
	public static void copyTable(Node srcNode,Node destNode ){
		int noOfNodes = srcNode.getNoOfNodes();
		double[][] srcTable = srcNode.getTable();
		double[][] destTable = destNode.getTable();
		for(int i=0;i<noOfNodes;i++){
			for(int j=0;j<noOfNodes;j++){
				if(destTable[i][j] > srcTable[i][j] || (destTable[i][j]==99999 && srcTable[i][j]!=99999)){
					destTable[i][j] = srcTable[i][j];
				}
			}
		}
	}
	
	public static int initializeNodes(String filePath,ArrayList<Node> nodeList)throws IOException{
		int NoOfNodes = 0,row,column,flag = -999;
		double value;
		FileInputStream fis = new FileInputStream(new File(filePath));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		NoOfNodes = Integer.parseInt(br.readLine());
		//br.close();
		//fis.close();

		for(int i=1;i<NoOfNodes+1;i++){
			fis = new FileInputStream(new File(filePath));
			br = new BufferedReader(new InputStreamReader(fis));
			br.readLine();
			String str=br.readLine();
			String token[] = str.split(" ");
			row = Integer.parseInt(token[0]);
			//for(int i=1;str!=null;){
			Node node = new Node(NoOfNodes,i);
			double[][] table = new double[NoOfNodes][NoOfNodes];
			table = initializeTable(table,NoOfNodes);
			table[row-1][row-1]=0;
			while(true){
				row = Integer.parseInt(token[0]);
				column = Integer.parseInt(token[1]);
				if(row == i){
					table[row-1][Integer.parseInt(token[1])-1] = Double.parseDouble(token[2]);
					//table[Integer.parseInt(token[1])-1][row-1] = Double.parseDouble(token[2]);
				}
				str = br.readLine();
				if(str==null){
					flag =1;
					break;
				}
				token = str.split(" ");
			}
			
			node.setTable(table);
			nodeList.add(node);
			br.close();
			fis.close();
			//if(flag == 1)
			//	break;
			
			//i++;
		}
		
		fis = new FileInputStream(new File(filePath));
		br = new BufferedReader(new InputStreamReader(fis));
		br.readLine();
		
		while(true){
			

			String str=br.readLine();
			if(str == null)
				break;
			String token[] = str.split(" ");
			row = Integer.parseInt(token[0]);
			column = Integer.parseInt(token[1]);
			value = Double.parseDouble(token[2]);
			

			Node node = nodeList.get(column-1);
			double[][] table = node.getTable();
			//table[row-1][column-1] = value;
			table[column-1][row-1] = value;
			node.setTable(table);
			
		}

		
		//System.out.println("Initialization finished");
		return NoOfNodes;
	}
	
	public static void displayTables(ArrayList<Node> arrList){
		Iterator<Node> itr = arrList.iterator();
		while(itr.hasNext()){
			Node node = itr.next();
			node.toString();
			System.out.println();
		}
	}
	
	
	public static double[][] initializeTable(double[][] table,int noOfNodes){
		for(int i=0;i<noOfNodes;i++){
			for(int j=0;j<noOfNodes;j++){
					table[i][j] = 99999;
				
			}
		}
		return table;
	}

}
	
