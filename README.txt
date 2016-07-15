
Please make sure this machine has java environment by typing “java -version” in the terminal. (otherwise please install JDK).

Files for testing are provided inside each folder:
-Client side
client1.jpg (SHA1 checksum: dcf5f1a780b6518d9ddd82a8ee491f308f467b59)
client2.pdf (SHA1 checksum: 408cf42a073f5aac51404f30790daacc4ce3ba4b)
-Server side
server1.jpg (SHA1 checksum: 68b4fc87e3531f7f0c3fa487aab23f3e1b76cac3)
server2.txt (SHA1 checksum: 3abbb1e4f8c6585d259d6a1a54e97fc1bd6f2b9a)

Checksum will be printed after successfully transfer.

———————————————————————————————INSTRUCTION———————————————————————————————

0. Extract this file into some writable path.
*************************
**How to use the Client**
*************************

1. Open terminal(or CMD), change path to the Client’s src folder. e.g.“cd (path)/TFTP/TFTPClient/src”.

2. Compile all the .java files using “javac *.java”. 

3. Commands: “java TFTPClient [host] [Request Type] [Filename] (mode)”. Mode can be blank, which means using default mode “octet”.

-Based on the above provided files, the commands would be: 
Download(read) file using octet mode: “java TFTPClient 127.0.0.1 R server1.jpg”
Download(read) file using ascii mode: “java TFTPClient 127.0.0.1 R server2.txt netascii”

Upload(write) file using octet mode: “java TFTPClient 127.0.0.1 W client1.jpg”
Upload(write) file using octet mode: “java TFTPClient 127.0.0.1 W client2.pdf”

-If upload or download successfully, the terminal will print the something like following:
—————————————
Downloading

Download Finished.
Filename: server2.txt
SHA1 Checksum: 3abbb1e4f8c6585d259d6a1a54e97fc1bd6f2b9a
—————————————

Please note that client is not allowed to upload files that are already exsited(has the same name) at server side. 


4. Files are stored at the default folder which is (path)/TFTP/TFTPClient

NOTE:
-Each transfer will allow up to 5 times of time out, after which the connection will be closed.
-Checksum of each file is provided after successfully transfer.
-File name should not contain ” ”(space)
-Please limit file size less than 35MB, file lager than this may get wrong during transfer

*************************
**How to run the Server**
*************************

1. Open terminal (or CMD), change path to the Server’s src folder. e.g.“cd (path)/TFTP/TFTPServer/src”.

2. Compile all the .java files using “javac *.java”. 

3. Run the server using “java TFTP TFTPServer”， then server will start and listen to port 6969.

4. Files are stored at default folder which is (path)/TFTP/TFTPServer. 

-If upload or download successfully, the terminal will print the something like following:
—————————————
Read Request from /192.168.1.75
Transfer completed.(Client /192.168.1.75)
Filename: server2.txt
SHA1 checksum: 3abbb1e4f8c6585d259d6a1a54e97fc1bd6f2b9a
—————————————

Note:
-The server can support up to 2 clients at the same time. No guarantee if clients are more than 2.
-Checksum of each file is provided after successfully transfer.
-File name should not contain ” ”(space)
-Please limit file size less than 35MB, file lager than this may get wrong during transfer

**************************************
**How to view source code in Eclipse**
**************************************
1. Open Eclipse, create a project by choosing “File->New—>Java Project”, type the project name and use the default options. Then the project will be visible in the Package Explorer on the left.

2. Expand the project in the Package Explorer, right click “src” then choose “Import”, choose “General->File system”, browse to choose the “src” folder which contains the source code.

3. Choose all .java files then click “finish”. All the .java files should then be in the default package of the project.

