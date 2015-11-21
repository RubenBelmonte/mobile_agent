import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



public class Server extends UnicastRemoteObject implements Server_Interface{
    private final String serverName;
    
    /**
     * Constructor of the class. Request the server name at runtime.
     * @throws RemoteException 
     */
    public Server() throws RemoteException{
        super();
        serverName = getName();
    }
    
    /**
     * Shows a welcome message, the list of servers by which the agent has 
     * passed and runs the execute method of the agent.
     * @param agent
     * @return the list of the servers visited when the agent ends.
     * @throws RemoteException 
     */
    @Override
    public List<String> receive(Agent agent) throws RemoteException {
        System.out.println("[INFO] ("+serverName+"): Welcome " + agent.getName()
                 +" to the Server " + serverName+"!!");
        System.out.println("[INFO] ("+serverName+"): The previous servers of:\n" + 
                Arrays.toString(agent.getPreviousHosts().toArray()));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException  ex) {
            System.out.println("[ERROR] ("+serverName+"): Something has happened"
                    + " and the thread has interrupted");
        }
        return agent.execute();
    }

    private String getName() {
        Scanner scanIn = new Scanner(System.in);
        System.out.println("[REQUEST] (Server): Enter the name associate to this Server");
        return scanIn.nextLine();
    }

}
