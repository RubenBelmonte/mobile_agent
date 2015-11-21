import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Host_Interface extends Remote{
    
    public void addHost(String url) throws RemoteException;
    public List<String> getHosts() throws RemoteException;
    public int getSocketPort(String ip) throws RemoteException;
    public void removeInfoFrom(String ip, int port) throws RemoteException;
    public List<String> getSocketInfo() throws RemoteException;
}
