
Editor Used : Eclipse
JDK Version : java version "1.7.0_25"

Compilation Of the Projects.

1. Files in the projects are dependent on each other as we use the reference of one file into another.
2. For compilation use the jar file that we have attached here in the source code.
3. It is must for all the files to get compiled before running this project.

Compilation and Running If using Command Prompt:

For RegistrationServer:
javac -cp P2P.jar RegistrationServerImpl.java
java -cp P2P.jar RegistrationServerImpl

For Every Peer:
javac -cp P2P.jar Peer.java
java -cp P2P.jar Peer

If using Eclipse Please directly run as Java Project.


Conditions While Running :

1. If you are running through command prompt:
         Each peer should have RFCDb folder and RFCDb.txt in the same folder where your source code is kept.

2. If you are running through Eclipse:
         Each peer should have RFCDb folder and RFCDb.txt at the level of src folder and not inside the src folder.
		 

HERE RFCDb folder contains all the downloaded file and RFCDb.txt contains the format of the file that we have.
So the peer which has got RFC should have entry in RFCDb.txt in this format:

NumberOfRFC#NameofRFC#FilePath
Eg:
12#rfc6#F:/Workspace/Practice/RFCDb/rfc12.txt


Running the Project :

PreRequisite  : No build errors.

1. First Run RegistrationServerImpl.java , It will start the RegistrationServer.
2. Run Peer.java at each PC. This will start all the Client and Server at the Peer Process.
3. Once You start this you first need to enter IP address of Registration Server.
4. Then menu driven program will start like this.
               1. Communication with Server

			   2. Communication with Peers

			   3. Exit

			   Enter your choice

5. Here Register,PQuery,Leave and Keep Alive are performed by communicating with server.
6. Here GETRFC is performed by communicating with peers.
7. First you need to Register with the Server. So press 1. It will show following menu:
				Choose Server Side Operations

				1. Register:

				2. PQuery:

				3. Leave:

				4. Keep Alive:
				Enter your choice
8. Press 1 and you get Registered there.
9. Here Peers gets registered. Now PQuery it.Here we can find the list of peers registered on the server from which we can take the file.
   Also we get all details of the peer here.
10.Menu 3rd will make the peer leave the network and 4th will make update the TTL time of the peer.

11.Now for RFC we need to communicate with the client which is done by Pressing 2 at main menu. We get the following menu:
				Choose Client Side Operations

				1. GET RFC
	
				Enter your choice
				
12.Press 1 then it will Query
                 Which RFC : 
13.Enter the RFC number which other Peer has and you get the file on your Peer.



P.S (Please let us know If you face any problem in any of the steps mentioned above)
 
			   
 