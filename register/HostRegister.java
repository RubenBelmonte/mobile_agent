import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class HostRegister {
    
    /**
     * Creates a registry on a specified port, rebinds the registry and creates
     * a new HostRegister process to register the servers.
     * @param args
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     * @throws IOException 
     */
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, IOException{
        if(System.getSecurityManager() == null){
            try{
               System.setSecurityManager(new RMISecurityManager());
            }
            catch (Exception ex){
               System.out.println(ex);
               System.exit(0);
            }
        }
        
        HostImpl h;
        int nclients = 1;
        int port = 1025;
        String ip = "localhost";
        DatagramSocket mySocket = new DatagramSocket(null);
        if (args.length == 1 || args.length > 3){
            System.out.println("[ERROR] Invalid number of arguments. please use:"
                    + "\n java -Djava.security.policy=HostRegister.policy HostRegister "
                    + "[<your ip> <your port>] [<number of clients>]");
            System.exit(0);
        }
        if (args.length >= 2){
            try{
                ip = args[0];
                port = Integer.parseInt(args[1]);
            }catch(Exception ex){
                System.out.println("[ERROR] The second argument must be a number");
                System.exit(0);
            }
        }
        if (args.length == 3) {
            try{
                nclients = Integer.parseInt(args[2]);
            }catch(Exception ex){
                System.out.println("[ERROR] The third argument must be a number");
                System.exit(0);
            }
        }
        
        try{
            mySocket.bind(new InetSocketAddress(port));
        }catch (Exception ex){
            System.out.println("[ERROR] The port "+port+" is already in use");
            System.exit(0);
        }
        System.setProperty("java.rmi.server.hostname", ip);
        h = new HostImpl(ip+":"+port);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("addHost", h);
        System.out.println("[INFO] The host register is online. You can run the servers now");
        
        byte[] buffer;
        for (int i = 0; i < nclients; i++){
            do{
                buffer = new byte[5];                                     
                DatagramPacket datagram = new DatagramPacket(buffer, 5);
                mySocket.receive(datagram);
            }while(!new String(buffer).equalsIgnoreCase("close"));
        }
        System.exit(0);
    }
}
