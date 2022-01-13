package InterfacesRMI;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteGrdsInterface extends Remote {
    void getAllServersInfo(GetRemoteMeta3AppInterface cliRef) throws IOException;

    void addObserver(GetRemoteMeta3AppInterface cliRef) throws RemoteException;
    void removeObserver(GetRemoteMeta3AppInterface cliRef) throws RemoteException;
}
