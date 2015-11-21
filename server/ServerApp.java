import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Creates a registry on a specified port, rebinds the registry and creates 
 * a new Server process
 * @throws RemoteException 
 * @throws FileNotFoundException
 * @throws IOException 
 */
public class ServerApp {
    public static void main(String[] args) throws RemoteException, FileNotFoundException, IOException, NotBoundException{
        if(System.getSecurityManager() == null){
            try{
                System.setSecurityManager(new RMISecurityManager());
            }
            catch (Exception ex){
                System.out.println(ex);
                System.exit(0);
            }
        }
        String ip = "localhost";
        String addHostIp = "localhost";
        int port = 1099;//default port for LocateRegistry.getRegistry
        int addHostPort = 1025;
        int nclients = 1;
        int socketPort = 0;
        String name;
        String []objectsName = null;
        Host_Interface hostReg = null;
        DatagramSocket mySocket = new DatagramSocket(null);
        
        if (args.length == 1 || args.length == 2 || args.length > 4){
            System.out.println("[ERROR] Invalid number of arguments. please use:"
                    + "\n java -Djava.security.policy=Server.policy ServerApp "
                    + "[<your ip> <HostRegister ip> <HostRegister port>] [<number of clients>]");
            System.exit(0);
        }
        
        if (args.length >= 3){
            try{
                ip = args[0]; 
                addHostIp = args[1];
                addHostPort = Integer.parseInt(args[2]);
            }catch(Exception ex){
                System.out.println("[ERROR] The third argument must be a number");
                System.exit(0);
            }
        }
            
        if (args.length == 4) {
            try{
                nclients = Integer.parseInt(args[3]);
            }catch(Exception ex){
                System.out.println("[ERROR] The fourth argument must be a number");
                System.exit(0);
            }
        }
        System.setProperty("java.rmi.server.hostname", ip);
        
        Scanner scanIn = new Scanner(System.in);
        
        
        Registry registry = LocateRegistry.getRegistry();
        try{
            objectsName = registry.list();
        }catch (ConnectException ex){
            try{
                System.out.println("[INFO] The registry was not created in the "
                        + "default port(1099). Creating the registry");
                registry= LocateRegistry.createRegistry(port);
                objectsName = registry.list();
            }catch(Exception e){
                System.out.println("[REQUEST] Enter the RMI port");
                while(true){
                    try{
                        port = Integer.parseInt(scanIn.nextLine());
                        break;
                    }catch(NumberFormatException Ex){
                        System.out.println("[ERROR] Port format not correct. Try again please");
                    }
                }
            }
        }
        
        System.out.println("[REQUEST] Enter the name associate to the remote object");
        while(true){
            name = scanIn.nextLine();
            if (name.contains(" ") || name.length() == 0)
                System.out.println("[ERROR] The name must not contains blanks or be empty. Try aganin please.");
            else if(itsRepited(objectsName, name))
                System.out.println("[ERROR] The name is already chosen. Try again please");
            else    
                break;
        }
        
        Server s = new Server();
        scanIn.close();
        registry.rebind(name, s);
        
        try{
            hostReg = (Host_Interface)Naming.
                        lookup("rmi://"+addHostIp+":"+addHostPort+"/addHost");
        }catch (Exception ex){
            System.out.println("[ERROR] It has not been been able to get the HostImpl remote object");
            System.exit(0);
        }
        while(true){
            try{
                socketPort = hostReg.getSocketPort(ip);
                mySocket.bind(new InetSocketAddress(socketPort));
                hostReg.addHost("rmi://"+ip+":"+port+"/"+name);
                break;
            }catch(Exception ex){
                hostReg.removeInfoFrom(ip, socketPort);
            }
        }
        System.out.println("[INFO] The socket port of this server is "+socketPort);
        System.out.println("[INFO] Server has started successfully");
        
        byte[ ] buffer;
        for (int i = 0; i < nclients; i++){
            do{
                buffer = new byte[5];                                     
                DatagramPacket datagram = new DatagramPacket(buffer, 5);
                mySocket.receive(datagram);
            }while(!new String(buffer).equalsIgnoreCase("close"));
        }
        System.exit(0);
    }
    
    private static boolean itsRepited(String[] names, String serverName){
        for (String name : names)
            if (name.equalsIgnoreCase(serverName))
                return true;
        return false;
    }
}

