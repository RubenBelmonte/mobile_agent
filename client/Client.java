import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;




public class Client {
    private final String agentName;
    private final Stack<String> destinations;
    private final Host_Interface hr;
    private final String ip;

    /**
     * Client constructor. Initializes the parameters and make the itinerary.
     * @param agentName
     * @param itinerary
     * @param hr
     * @param ip
     * @throws RemoteException 
     */
    public Client(String agentName, String itinerary,  Host_Interface hr, String ip) {
        this.agentName = agentName;
        this.hr = hr;
        this.ip = ip;
        this.destinations = createItinerary(itinerary);
    }
    
    /**
     * Start the agent's itinerary. when the agent returns and
     * sends a signal to the servers to say that the itinerary has ended.
     * @throws RemoteException 
     */
    public void run() throws RemoteException {
        Server_Interface remoteObject;
        String nextHost = "";
        Agent myAgent;
        List <String> hostsVisited = null;
        
        while (true){
            try{
                nextHost = destinations.pop();
                myAgent = new Agent(agentName, destinations);
                remoteObject = (Server_Interface)Naming.lookup(nextHost);
                myAgent.addHost(nextHost.split("/")[3]);
            }catch(EmptyStackException e){//exception of the Stack
                System.out.println("[INFO] Client: There is no valid hosts");
                closeServers();
                return;
            }catch (Exception ex){//Exception of the lookup method
                System.out.println("[WARNING] The Client could not get "
                        + "the remote object from " + nextHost+"\nI will try to"
                        + " go to the next host");
                continue;
            }

            
            System.out.println("[INFO] Bye "+agentName+", it is time to "
                    + "leave the host "+ ip);
            

            hostsVisited = remoteObject.receive(myAgent);
            break;
        }
        
        System.out.println("[INFO] Agent: Congratulations " + agentName + "!! You've done here");
        System.out.println("[INFO] The hosts visited by the agent "+agentName +
                "\n" + Arrays.toString(hostsVisited.toArray()));
        closeServers();
        
    }
    
    private Stack<String> createItinerary(String itinerary){
        List<String> hosts = null;
        try{
            hosts = hr.getHosts();
        }catch (Exception ex){
            System.out.println("[ERROR] Probably the HostRegister is not online");
            System.out.println(ex);
            System.exit(0);
        }
        if (itinerary.equalsIgnoreCase("Seq"))
            return createSeqStack(hosts);
        
        else if (itinerary.equalsIgnoreCase("Alea"))
            return createAleaStack(hosts);
        
        else //itinerary.equalsIgnoreCase("Own")
            return createOwnStack(hosts);
    }

    private Stack<String> createSeqStack(List<String> hosts) {
        Stack<String> res = new Stack<>();
        
        ListIterator<String> iter = hosts.listIterator(hosts.size());
        while (iter.hasPrevious())
            res.push(iter.previous());
        
        return res;
    }

    private Stack<String> createAleaStack(List<String> hosts) {
        Random rnd = new Random();
        int k = hosts.size();
        int posToFlip;
        
        while (k > 1) {
            posToFlip = rnd.nextInt(k);
            String tmp = hosts.get(posToFlip);
            tmp = hosts.set(k - 1, tmp);
            hosts.set(posToFlip, tmp);
            k--;
        }
        
        return createSeqStack(hosts);
    }

    private Stack<String> createOwnStack(List<String> hosts) {
        List<String> dfsTravel = new ArrayList<>();
        dfsTravel = createlistOfTree(hosts, dfsTravel, 0);
        
        return createSeqStack(dfsTravel);
    }
    
    private List<String> createlistOfTree(List<String> hosts, List<String> l, int pos){
        l.add(hosts.get(pos));
        if (2*pos + 1 < hosts.size())
            l = createlistOfTree(hosts, l, 2*pos + 1);
        if (2*pos + 2 < hosts.size())
            l = createlistOfTree(hosts, l, 2*pos + 2);
        return l;
        
    }

    private void closeServers() throws RemoteException {
        String socketIp;
        int port;
        String message = "close";
        List <String> l = hr.getSocketInfo();
        for (String host : l){
            try{
                socketIp = host.split(":")[0];
                port = Integer.parseInt(host.split(":")[1]);
                
                DatagramSocket mySocket = new DatagramSocket();
                byte[ ] buffer = message.getBytes( );
                DatagramPacket datagram = new DatagramPacket(buffer,
                        buffer.length, InetAddress.getByName(socketIp), port);
                mySocket.send(datagram);
                System.out.println("close enviado a "+socketIp+"|"+port);
            }catch (Exception ex){// the server is close
            }
        }   
    }
    
}
