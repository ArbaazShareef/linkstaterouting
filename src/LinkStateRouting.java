import java.io.*;
import java.util.Scanner;

public class LinkStateRouting{
//Start of Link State Routing	

//Start By Initializing and Declaring the Variables
	static Boolean isInputGiven = false,isSourceSelected = false,isDestinationSelected = false;
	static int DistanceArray[] = null;
	static int inputNetwork[][] = null;                  
	static int routerCount,sourceRouter=-1,destinationRouter=-1;
	static ConnectionTable []connectionTable = null;
	public static void main(String[] args) {

		printMenu();

	}
	private static void printMenu() {
		System.out.println("===========================================================\n");
		System.out.println(" Link State Routing Simulator:Finding the Shortest Path");
		System.out.println("===========================================================\n");
		System.out.println("CS542 Link State Routing Simulator");
        System.out.println("(1) Create a Network Topology");
        System.out.println("(2) Build a Forward Table");
        System.out.println("(3) Shortest Path to Destination Router");
        System.out.println("(4) Modify a Topology (Change the status of the Router)");
        System.out.println("(5) Best Router for Broadcast )");
        System.out.println("(6) Exit");
        System.out.println("Command:");
        Scanner input = new Scanner(System.in);
        String userInput = input.nextLine();
        if(userInput.equals("1")){
        	System.out.println(" Input original network topology matrix data file:");
        	Scanner fscanner = new Scanner(System.in);              
        	String fileName = fscanner.nextLine();
        	try {
				inputNetwork = createNetworkTopology(fileName);
				printNetworkTopology(inputNetwork);
				isInputGiven=true;
				printMenu();
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found, Please Enter a Valid File Name !.Select Correct file name by clicking option 1 first ");   
				printMenu();
			}
        }else if(userInput.equals("2")){
        	if(isInputGiven){
        		isSourceSelected=true;
        		PrintForwardTable(inputNetwork);
            		}else{
                		System.out.println("Enter the Input Network matrix first by selecting the 1st option");
                	}
        	printMenu();
        	
        }else if(userInput.equals("3")){
        	if(isInputGiven){
        		if(isSourceSelected){
        			
        			System.out.println(" Enter Destination Router Id< 1 -"+routerCount+">:");
                	Scanner destS = new Scanner(System.in);       //Takes from the user the Destination Router ID as input 
            		String Sdestination = destS.nextLine();
            		destinationRouter = Integer.parseInt(Sdestination);
            		destinationRouter=destinationRouter-1;   
            		isDestinationSelected = true;
            		if (!(inputNetwork[sourceRouter][sourceRouter] != 0)) {
            			if (!(inputNetwork[destinationRouter][destinationRouter] != 0)) {
            				int p= sourceRouter+1;
            				int q = destinationRouter+1;
            				System.out.print("Shortest Path from Router:["+p +"] to ["+ q + "] is: ");

            				if (connectionTable[destinationRouter].l > 0) {
            					for (int n = 0;n< connectionTable[destinationRouter].d; n++ ) {
            						if (-1 != connectionTable[destinationRouter].id[n]) System.out.print(" "+ (connectionTable[destinationRouter].id[n]+1));
            					}
            					System.out.println();
            					System.out.println("The total cost is "+ connectionTable[destinationRouter].l);
            				}  else System.out.println("Path Not Available");
            			}  else System.out.println("Destination Router is Down");    //If Destination Router is down
            		} else System.out.println("Source Router is Down");
                	printMenu();
        			
        			
        			
        		}else{
        			System.out.println("Please Select the source router First by selecting 2nd option from menu below");
        			
        		}
        		
        		
        		
        	}else{
        		System.out.println("Enter the Input Network matrix first by selecting the 1st option");
        	}
        	printMenu();
        }else if(userInput.equals("4")){
        	if(isInputGiven){
        		ChangeNetworkTopology();
            	}else{
            		System.out.println("Enter the Input Network matrix first by selecting the 1st option");
            	}
        	printMenu();
        }else if(userInput.equals("5")){
        	if(isInputGiven){
        		SelectBestRouter(inputNetwork);
            	}
        	printMenu();
        }else if(userInput.equals("6")){
        System.out.println("Program Terminated....");
        	
        }else{
        	System.out.println("Please select a valid option");
        	printMenu();
        }
		
	}
	private static void SelectBestRouter(int[][] inputNetwork2) {
		int[] totalcosts = new int[routerCount];
		
		for(int i=0;i<routerCount;i++){
			sourceRouter=i;
			runDikstraAlgorithm(inputNetwork);
			int totalcost = 0;
			for(int j=0;j<routerCount;j++){
				if (inputNetwork2[i][i] == 0) {
					if (inputNetwork2[j][j] == 0) {
						if (connectionTable[j].l > 0) {
							totalcost = connectionTable[j].l+totalcost;
						}
					}
					}	
			}
			totalcosts[i]=totalcost;
		}
		int mincost=Integer.MAX_VALUE,minRouter=0;
		for(int p=0;p<routerCount;p++){
			if(totalcosts[p]<mincost){
				mincost=totalcosts[p];
				minRouter = p;
			}
		}
			System.out.println("The Best Router for Broadcast is "+(minRouter+1));
			System.out.println("and its total cost is "+(totalcosts[minRouter]));
		
	}
	private static void ChangeNetworkTopology() {
		System.out.println("Enter Router Id< 1 - "+ (routerCount)+" > to Down:");                       
		Scanner cnt = new Scanner(System.in);          //Takes from the user the Router ID to Down as input 
		String Scnt = cnt.nextLine();
		int deleteRouter = Integer.parseInt(Scnt);
		deleteRouter=deleteRouter-1;
		for (int j =0; j < routerCount; j++ ){
			inputNetwork[j][deleteRouter] = -1 ;             //Assigns -1 to the Down Router row
		}
		for (int l =0; l < routerCount; l++ ){
			inputNetwork[deleteRouter][l] = -1 ;            //Assigns -1 to the Down Router column
		}
		
		if(isSourceSelected&&isDestinationSelected){
			runDikstraAlgorithm(inputNetwork);
		}
		else{
			System.out.println("Modified Topology:");
			System.out.println();
			System.out.print("Router|");
			for (int j =0; j < routerCount; j++ ){
				System.out.print(String.format( "%3d",j+1));
					
			}
			System.out.println();
			System.out.println("-------------------------------------------------------------------");
			for (int j =0; j < routerCount; j++ ){
				System.out.print(String.format( "%2d|    ",j+1));
				for (int k =0; k < routerCount; k++ )
					System.out.print( String.format( "%3d", inputNetwork[j][k]));
				System.out.println();
			}
			System.out.println("-------------------------------------------------------------------");
				
		}
		
	}
	private static void PrintForwardTable(int[][] inputNetwork2) {
		System.out.println("Enter Source Router Id< 1 - "+ routerCount+" >:"); 
		Scanner ftscanner = new Scanner(System.in);                            
		String inpSource = ftscanner.nextLine();
		sourceRouter = Integer.parseInt(inpSource);
		sourceRouter = sourceRouter-1 ;                                   //Decrements the Source ID by 1
		runDikstraAlgorithm(inputNetwork);
		
	}
	private static void runDikstraAlgorithm(int[][] inputNetwork2) {
		// TODO Auto-generated method stub
		if (inputNetwork2[sourceRouter][sourceRouter] == 0) {
			connectionTable = new ConnectionTable[routerCount];    //Creates a ConTable Object with the Number of Routers

			for (int j = 0;j<routerCount;j++) {	
				ConnectionTable ce = new ConnectionTable();
				ce.flag = true;
				ce.l = -1;                      //Initializing the variables to start processing            
				ce.id = new int[routerCount];
				ce.id[0] = sourceRouter ;
				ce.d = 1;
				for (int i = 1;i<routerCount;i++) ce.id[i] = -1;
				connectionTable[j] = ce;
			}
			int tmpsource = sourceRouter;
			connectionTable[tmpsource].l = 0;
			connectionTable[tmpsource].id[0]=sourceRouter;
			connectionTable[tmpsource].flag = false;
			
			for (int loopcnt = 0 ; loopcnt<routerCount; loopcnt++) {

				for (int k = 0 ;  k< routerCount ; k++)
				{  
					if (connectionTable[k].flag)
					{
						if (inputNetwork2[tmpsource][k]!= -1){

							if ((connectionTable[k].l != -1) ) {
								// smaller ( selected node length+ tableentry,previous entry path) 
								if (connectionTable[k].l > connectionTable[tmpsource].l + inputNetwork2[tmpsource][k]) {
									connectionTable[k].l = connectionTable[tmpsource].l + inputNetwork2[tmpsource][k];
									for (int idx = 0; idx< connectionTable[tmpsource].d ;idx ++)
										connectionTable[k].id[idx] = connectionTable[tmpsource].id[idx];
									connectionTable[k].d = connectionTable[tmpsource].d ;								
									connectionTable[k].id[connectionTable[k].d] = k;
									connectionTable[k].d++;
								}
							}
							else 
							{  //selected node length is added to length table entry for new length
								connectionTable[k].l = connectionTable[tmpsource].l + inputNetwork2[tmpsource][k];

								for (int idx = 0; idx< connectionTable[tmpsource].d ;idx ++){
									connectionTable[k].id[idx] = connectionTable[tmpsource].id[idx];
								}


								connectionTable[k].d = connectionTable[tmpsource].d ;	
								connectionTable[k].id[connectionTable[k].d] = k;
								connectionTable[k].d++;
							}
						}

					}
				}
				System.out.println();
				
				int small = 0;
				int indx_small = 0;

				for (int i = 0; i<routerCount; i++){

					if (connectionTable[i].flag){
						if(connectionTable[i].l !=-1 ){
							small = connectionTable[i].l;
							indx_small = i;
							break;
						}
					}				
				}
				for (int i = 0; i<routerCount; i++){
					if (connectionTable[i].flag){
						if(connectionTable[i].l != -1 ){
							if (small > connectionTable[i].l){						
								small = connectionTable[i].l;
								indx_small = i;
							}
						}
					}			
				}
				tmpsource = indx_small;
				connectionTable[tmpsource].flag = false;
			
		}
			int scr = sourceRouter+1;
			System.out.println("Router [" + scr + "] "+ "Connection Table:");
			System.out.println("============================");
			System.out.println("Destination        Interface");	         //Printing the Connection Table
			for (int i = 0; i<routerCount; i++){
				String tmp = String.valueOf(connectionTable[i].id[1]+1);
				if (connectionTable[i].id[1] == -1) tmp = "-1";                //Check the Router ID if it is -1
				if (i == sourceRouter) tmp = "-";                              //Source to Source Router will be "-"
				System.out.print("      "+  (i+1) + "                "+ tmp);
				System.out.println();
			}
			//end of if statement	
		}else {
			int src = sourceRouter-1;
			System.out.println("Router [" +src+ "] "+ "Connection Table:");
			System.out.println("============================");
			System.out.println("Destination        Interface");              //If there is no Interface to the router then assign -1 
			for (int i = 0; i<routerCount; i++){
				int m =i+1;
				System.out.print("      "+m+ "                -1");
				System.out.println();
			}

		}

		
	}
	private static void printNetworkTopology(int[][] inputNetwork2) {
		System.out.println();
		System.out.print("Router|");
		for (int j =0; j < routerCount; j++ ){
			System.out.print(String.format( "%3d",j+1));
		}
		System.out.println();
		System.out.println("-------------------------------------------------------------------");
		for (int j =0; j < routerCount; j++ ){
			System.out.print(String.format( "%2d|    ",j+1));
			for (int k =0; k < routerCount; k++ )
				System.out.print( String.format( "%3d", inputNetwork2[j][k]));
			System.out.println();
		}
		System.out.println("-------------------------------------------------------------------");
		
		
		
		
	}
	private static int[][] createNetworkTopology(String fileName) throws FileNotFoundException {
		// TODO Auto-generated method stub
		int[][] matrix = {{1}, {2}};

		File inFile = new File(fileName);
		Scanner in = new Scanner(inFile);

		 routerCount = 0;
		String[] length = in.nextLine().trim().split("\\s+");
		  for (int i = 0; i < length.length; i++) {
		    routerCount++;
		  }

		in.close();

		matrix = new int[routerCount][routerCount];
		in = new Scanner(inFile);

		int lineCount = 0;
		while (in.hasNextLine()) {
		  String[] currentLine = in.nextLine().trim().split("\\s+"); 
		     for (int i = 0; i < currentLine.length; i++) {
		        matrix[lineCount][i] = Integer.parseInt(currentLine[i]);    
		            }
		  lineCount++;
		 }                                 
		 return matrix;
		
	}
	
	
	static class ConnectionTable{
		int l,d;
		boolean flag;
		int[] id;
		
		
	}
	
	
	


	
	
	
//End of Link State Routing	
}