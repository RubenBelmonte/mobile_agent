import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server_Interface extends Remote{
   
    public List<String> receive(Agent agent) throws RemoteException;
}
