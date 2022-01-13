package InterfacesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface GetRemoteMeta3AppInterface extends Remote {
    void notify(String description) throws RemoteException;
    void sendServersInfo(ArrayList<String> serves) throws RemoteException;
}
