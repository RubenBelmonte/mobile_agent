# mobile_agent
RMI aplication with the mobile agent paradigm

# HOW TO COMPILE AND EXECUTE
If you runs without parameters, the program uses the default values to execute in localhost and one client.
- Register:

To compile use the command (you have to be in the “register” directory):

$ javac Host_Interface.java HostImpl.java HostRegister.java

To runs use the command (you have to be in the “register” directory):

$ java -Djava.security.policy=HostRegister.policy HostRegister ["your ip" "your port"] ["number of clients"]


- Server

To compile use the command (you have to be in the “server” directory):

$ javac Agent.java Host_Interface.java Server.java ServerApp.java Server_Interface.java

To runs use the command (you have to be in the “server” directory):

$ java -Djava.security.policy=Server.policy ServerApp ["your ip" "HostRegister ip" "HostRegister port"] ["number of clients"]


- Client

To compile use the command (you have to be in the “client” directory):

$ javac Agent.java Host_Interface.java Server_Interface.java Client.java ClientAPP.java

To runs use the command (you have to be in the “client” directory):

$ java -Djava.security.policy=Client.policy ClientApp ["your ip" "HostRegister ip" "HostRegister port"]

