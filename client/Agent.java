import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;


public class Agent implements Serializable{
    private final List<String> previousHosts;
    private final String name;
    private final Stack<String> destinations;
    
    /**
     * Constructor of the class
     * @param agentName
     * @param destinations: itinerary of the agent 
     */
    public Agent(String agentName, Stack<String> destinations){
        this.name = agentName;
        this.destinations = destinations;
        this.previousHosts = new ArrayList<>();
    }
    
    /**
     * The agent prints out the message of arrival and locates the next server.
     * @return the list of the servers visited when the agent ends.
     */
    public List<String> execute() {
        Server_Interface remoteObject;
        String nextHost = null;
        System.out.println("[INFO] Agent: Hello! I'm Agent " + this.name);        
        
        while (true){
            try{
                nextHost = destinations.pop();
                System.out.println("Agent----> " + nextHost);
                remoteObject = (Server_Interface)Naming.lookup(nextHost);
                
            }catch(EmptyStackException e){
                System.out.println("[INFO] Agent: There is no more hosts, I'm coming back Home!!!");
                return previousHosts;
            }catch (Exception ex){
                System.out.println("[WARNING] The agent could not get the remote "
                    + "object from " + nextHost+"\nI will try to go to the next host");
                continue;
            }

            try{
                previousHosts.add(nextHost.split("/")[3]);
                return remoteObject.receive(this);
            }catch (RemoteException ex){
                System.out.println("[WARNING] The agent has had a problem running the"
                        + " receive method of the remote object from " + nextHost +
                        "\nI will try to go to the next host");
                previousHosts.remove(previousHosts.size() - 1);
            }
        }
    }  
    
    /**
     * 
     * @return the list of visited host.
     */
    public List<String> getPreviousHosts(){
        return previousHosts;
    }
    
    /**
     * Add the host passed by parameter to the list of visited host.
     * @param host 
     */
    public void addHost(String host){
        previousHosts.add(host);
    }
    
    /**
     * 
     * @return the name of the agent
     */
    public String getName(){
        return name;
    }
}
