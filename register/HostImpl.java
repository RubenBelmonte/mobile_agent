import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class HostImpl extends UnicastRemoteObject implements Host_Interface{
    private final List<String> urls;
    private final List<String> socketDest;
    private int portCount;
    private final String socketInfo;
    
    /**
     * Constructor of the class
     * @param socketInfo
     * @throws RemoteException 
     */
    public HostImpl(String socketInfo) throws RemoteException{
        super();
        this.urls = new ArrayList<>();
        this.socketDest = new ArrayList<>();
        this.portCount = 1025;
        this.socketInfo = socketInfo;
    }
    
    /**
     * Adds a new server.
     * @param url: The RMI url of the remote object
     * @throws RemoteException 
     */
    @Override
    public void addHost(String url) throws RemoteException {
        urls.add(url);
        System.out.println("[INFO] New server is online: " + url);
    }
    
    /**
     * 
     * @return the list of servers that participate in the itinerary
     */
    @Override
    public List<String> getHosts(){
        return urls;
    }
    
    /**
     * A port is assigned to a server and this information is saved. Then returns the port
     * @param ip of the server
     * @return the port to socket of the server
     * @throws RemoteException 
     */
    @Override
    public int getSocketPort(String ip) throws RemoteException{
        portCount++;
        socketDest.add(ip+":"+portCount);
        System.out.println("[INFO] A port for a socket("+portCount+")"
                + " has been claimed by "+ip);
        return portCount;
    }
    
    /**
     * Removes information about a port assigned to a server.
     * @param ip
     * @param port
     * @throws RemoteException 
     */
    @Override
    public void removeInfoFrom(String ip, int port) throws RemoteException{
        ListIterator<String> iter = socketDest.listIterator(socketDest.size());
        String s;
        while (iter.hasPrevious()){
            s = iter.previous();
            if(s.equalsIgnoreCase(ip+":"+port)){
                System.out.println("[INFO] The following information has been removed: "+s);
                socketDest.remove(socketDest.indexOf(s));
                return;
            }   
        }
    }
    
    /**
     * 
     * @return the list of assigned port information
     * @throws RemoteException 
     */
    @Override
    public List<String> getSocketInfo() throws RemoteException{
        List <String> res = new ArrayList<>();
        res.addAll(socketDest);
        res.add(socketInfo);
        return res;
    }
}
