package grds.data;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerData extends InetSocketAddress {
    private int numClientsAtri = 0;

    public void newClient() {numClientsAtri++;}

    public void removeClient() {
        if (numClientsAtri < 1)
            throw new UnsupportedOperationException();
        numClientsAtri--;
    }

    public int getNumCli() {
        return numClientsAtri;
    }

    public ServerData(InetAddress addr, int port) {
        super(addr, port);
    }
}
