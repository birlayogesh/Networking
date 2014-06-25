import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class LSR {
	static HashSet<Integer> originArray1 = new HashSet<Integer>();
	static HashSet<Integer> originArray2 = new HashSet<Integer>();

	
	public static void main(String args[]) {
		try {
			String node1=args[0];
			String node2=args[1];
			//FileInputStream file = new FileInputStream("large-net-a.txt");
			//FileInputStream file = new FileInputStream("large-net-b.txt");
			FileInputStream file = new FileInputStream("small-net.txt");
			//FileInputStream file = new FileInputStream("small-net1.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(file));
			String fileContent[] = readFile(br);
			int numNode = Integer.parseInt(fileContent[0]);
			double adjMatrix[][] = new double[numNode + 1][numNode + 1];
			adjMatrix = initializeMatrix(adjMatrix, fileContent, numNode);
			List finalOutput=linkStateRouting(adjMatrix, numNode);
			
			int n1=Integer.parseInt(node1);
			int n2=Integer.parseInt(node2);
			printOutput(n1,n2,finalOutput);
			System.out.println();
			//printAllValues(finalOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printOutput(int n1,int n2,List finalOutptut){
		int l=0;
		double abc[],def[];
		
		
		abc = (double[])finalOutptut.get(n1-1);
		def=  (double[])finalOutptut.get(n2-1);
		
		
		System.out.println("The cost of the least-cost path between nodes "+(n1)+" and "+(n2)+" is "+abc[n2]);
		System.out.println();
		
		//REF: www.stackoverflow.com -- START (for printing format)
		String leftAlignFormat = "| %-15s | %.2f |%n";

		System.out.println("NODE"+(n1)+" routing table");
		System.out.format("+--------------+----------+%n");
		System.out.printf("| Node name    | Distance |%n");
		System.out.format("+--------------+----------+%n");
		for (int i = 1; i < abc.length; i++) {
		    System.out.format(leftAlignFormat, "Node " + i, (abc[i]));
		}
		System.out.format("+--------------+----------+%n");
		
		System.out.println();

		System.out.println("NODE"+(n2)+" routing table");
		System.out.format("+--------------+----------+%n");
		System.out.printf("| Node name    | Distance |%n");
		System.out.format("+--------------+----------+%n");
		for (int i = 1; i < def.length; i++) {
		    System.out.format(leftAlignFormat, "Node " + i, (def[i]));
		}
		System.out.format("+--------------+----------+%n");
		////REF: www.stackoverflow.com -- END (for printing format)
		
	}
	public static List linkStateRouting(double adjMatrix[][],int numN){
		         
		int i1=1,j1=2;
		
		boolean flag=true,flag1=true;
		int minIndex=0;
		List distVectList=new ArrayList();
		double dval[][]=new double[numN+1][numN+1];
		
		for(int i=1;i<=numN;i++)
			for(int j=1;j<=numN;j++)
				dval[i][j]=999;
		long currTime=System.currentTimeMillis();
	    long exectime=0;
		while(i1<=numN){
		
			
				
		originArray1=new HashSet<Integer>();
		originArray2=new HashSet<Integer>();
		
		originArray1.add(new Integer(i1));
		for(j1=1;j1<=numN;j1++){
			if(originArray1.contains(new Integer(j1)))
				continue;
			originArray2.add(new Integer(j1));
		}
		
		double d[]=new double[numN+1];
		for(j1=1;j1<=numN;j1++)
			if(adjMatrix[i1][j1]!=999)
				d[j1]=adjMatrix[i1][j1];
			else
				d[j1]=999;
		
		do{
		for(j1=1;j1<=numN;j1++){
			if(originArray1.contains(new Integer(j1)))
				continue;
			 if(flag){
				flag=false; 
			 }else{
				 d=update(d,minIndex,adjMatrix,j1);
			 }
			minIndex=getminIndex(d);
			originArray1.add(new Integer(minIndex));
			
		}
		System.out.println();
		}while(originArray1.equals(originArray2));
	
		
			dval[i1]=d;
		
	distVectList.add(d);
	exectime=System.currentTimeMillis();
	//System.out.println("TIME FOR NODE "+i1+" is "+(exectime-currTime));
	i1++;	
	}
	List updatedList=updateList(distVectList,numN);	
		
		return updatedList;

	}

	public static List updateList(List distVectList,int numN){
				double a[][]=new double[numN+1][numN+1];
		int j=1,i1=1;
		for(int i=0;i<numN;i++){
			   a[j]=(double[])distVectList.get(i);
			   j++;
		}
		for(int i=1;i<=numN;i++)
			for(int k=1;k<=numN;k++)
			{
				if(a[i][k]>a[k][i])
					a[i][k]=a[k][i];
			}
		
		double min=999;
		for(int i=numN-1;i>=0;i--)
			for(int l=numN-1;l>=0;l--)
			if(a[i][l]==999){
				   for(int k=numN-1;k>=0;k--)
				   {
					   if(!(a[i][k+1]==0.0 || a[k+1][l]==0.0)){
						   if( min>(a[i][k+1] + a[k+1][l]))
						   min=a[i][k+1] + a[k+1][l];
					   }
				   }
				a[i][l]=min;
				min=999;
			}
		min=999;
		for(int i=numN;i>=0;i--)
			if(a[numN][i]==999){
				   for(int k=numN-1;k>=0;k--)
				   {
					   if(!(a[numN][k+1]==0.0 || a[k+1][i]==0.0)){
					   if( min>a[numN][k+1] + a[k+1][i])
							  min=a[numN][k+1] + a[k+1][i];
				   }
				   }
				a[numN][i]=min;
				a[i][numN]=min;
				min=999;
			}
				
				
		int v=0,k=0;
		 while(v<numN){
			 distVectList.remove(k);
			 v++;
		 }
		 v=1;
		 while(v<=numN){
		 distVectList.add(a[v]);
		 v++;
		 }
		return distVectList;
	}
	public static void printAllValues(List distVectList){

		int l=0;
		double abc[];
		int len=distVectList.size();
		while(len!=0){
		abc = (double[])distVectList.get(l);
		int a=abc.length-1,k=1;
		System.out.print("NODE"+(l+1)+":  ");  
		while(a>0){
			System.out.print(abc[k]+" ");
			k++;
			a--;
		}
		System.out.println(); 
		l++;
		len--;
		}	
	}
	
	
	public static double[] update(double arr[], int index,
			double adjMatrix[][], int adjIndex) {
		
		double arr1[] = new double[arr.length];
		for (int i = 1; i < arr.length; i++)
			if (arr[i] <= (arr[index] + adjMatrix[index][i]))
				arr1[i] = arr[i];
			else
				arr1[i] = arr[index] + adjMatrix[index][i];
		return arr1;
	}

	public static int getminIndex(double arr[]) {
		int index = 0;
		double min = 999;
		for (int i = 1; i < arr.length; i++) {
			if (originArray1.contains(new Integer(i)))
				continue;
			if (arr[i] < min && arr[i] != 0) {
				min = arr[i];
				index = i;
			}
		}
		return index;
	}

	public static double[][] initializeMatrix(double adjmatrix[][],
			String content[], int numN) {

		for (int i = 1; i <= numN; i++)
			for (int j = 1; j <= numN; j++) {
				if (i == j)
					adjmatrix[i][j] = 0;
				else
					adjmatrix[i][j] = 999;
			}

		int count = 1;
		while (content[count] != null) {
			String arry[] = content[count].split("\\s");
			double firstnode = Double.parseDouble(arry[0]);
			double secondnode = Double.parseDouble(arry[1]);
			double cost = Double.parseDouble(arry[2]);
			adjmatrix[(int) firstnode][(int) secondnode] = cost;
			adjmatrix[(int) secondnode][(int) firstnode] = cost;
			count++;
		}

		return adjmatrix;
	}

	public static String[] readFile(BufferedReader br1) throws Exception {
		String str[] = new String[500];
		int lineCount = 0;
		while ((str[lineCount] = br1.readLine()) != null) {
			lineCount++;
		}
		return str;
	}
}