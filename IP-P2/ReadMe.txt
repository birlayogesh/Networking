Steps to run the code.

Compile the java files as follows:

For Sender   : javac Sender.java
For Receiver : javac Receiver.java

Run the .class files as follows with following command line arguments.

For sender : java Sender server-1 server-2 server-3 server-port# file-name MSS
where where server-i is the host name where the i-th server (receiver) runs, i = 1, 2, 3, server-port# is the
port number of the server (between 65400 - 65498), file-name is the name of the file to be transferred, and MSS is the maximum segment size.

Note: Do not use port 65499 as it is used for other purpose for communication between sender and receiver.


For Receiver : java Receiver port# file-name p
where port# is the port number to which the server is listening, file-name is the name of the file where the data will be written, and p is the packet loss probability.


