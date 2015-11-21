import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ClientAPP {
     
    /**
     * Creates a registry on a specified port, rebinds the registry and creates
     * a new Client process
     * @param args
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException 
     */
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException{
        if(System.getSecurityManager() == null){
            try{
               System.setSecurityManager(new RMISecurityManager());
            }
            catch (Exception ex){
               System.out.println(ex);
               System.exit(0);
            }
        }

        int hostPort = 1025;
        String hostIp = "localhost";
        String ip = "localhost";
        
        if (args.length == 3){
            try{
                ip = args[0]; 
                hostIp = args[1];
                hostPort = Integer.parseInt(args[2]);
            }catch(Exception ex){
                System.out.println("[ERROR] The second argument must be a number");
                System.exit(0);
            }
        }else if (args.length > 0){
            System.out.println("[ERROR] Invalid number of arguments. please use:"
                    + "\n java -Djava.security.policy=Client.policy ClientApp "
                    + "[<your ip> <HostRegister ip> <HostRegister port>]");
            System.exit(0);
        }
        
        
        Scanner scanIn = new Scanner(System.in);
        
        System.out.println("[REQUEST] Enter the name associate to the agent object");
        String name = scanIn.nextLine();
        String itinerary;
        while (true){
            System.out.println("[REQUEST] Enter the itinerary to sort destinations. Itineraries:\n"
                   + "- Sequencial: \"Seq\"\n- Random: \"Alea\"\n- Our itinerary: \"Own\"");
            itinerary = scanIn.nextLine();

            if(itinerary.equalsIgnoreCase("seq") || itinerary.equalsIgnoreCase("Alea")
                    || itinerary.equalsIgnoreCase("Own")){
                break;
            }
            System.out.println("[ERROR] Client: Itinerary it's not correct. Please Try again");            
        }
        scanIn.close();
        Host_Interface h = (Host_Interface)Naming.
                    lookup("rmi://"+hostIp+":"+hostPort+"/addHost");
        
        Client c = new Client(name, itinerary, h, ip); 
        c.run();
        
    }
}
