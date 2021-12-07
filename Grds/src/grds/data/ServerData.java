package grds.data;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerData extends InetSocketAddress {
    private static int identifierCount = 0;

    private int identifier = identifierCount++;
    private int numClientsAtri = 0;
    private ThreadServData threadTimeout;

    public ServerData(InetAddress addr, int port) {
        super(addr, port);
        threadTimeout = new ThreadServData();
        threadTimeout.start();
    }

    public int getIdentifier() {
        return identifier;
    }

    public boolean isTimeout() { return threadTimeout.isTimeout(); }

    public int getNumTimeouts() {
        return threadTimeout.getNumTimeouts();
    }

    public void newClient() {numClientsAtri++;}

    public void removeClient() {
        if (numClientsAtri < 1)
            throw new UnsupportedOperationException();
        numClientsAtri--;
    }

    public void pinged() {
        System.err.println("Server " + identifier + " has pinged");
        threadTimeout.interrupt();
    }

    public void stop() {
        threadTimeout.setExit(true);
        threadTimeout.interrupt();
    }

    public int getNumCli() {
        return numClientsAtri;
    }
}
