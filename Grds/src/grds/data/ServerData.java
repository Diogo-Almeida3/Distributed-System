package grds.data;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerData extends InetSocketAddress {
    private int numClientsAtri = 0;
    private Integer numTimeouts = 0;
    private ThreadServData threadTimeout;

    public ServerData(InetAddress addr, int port) {
        super(addr, port);
        threadTimeout = new ThreadServData(numTimeouts);
        threadTimeout.start();
    }

    public boolean isTimeout() { return threadTimeout.isTimeout(); }

    public int getNumTimeouts() {
        return numTimeouts;
    }

    public void newClient() {numClientsAtri++;}

    public void removeClient() {
        if (numClientsAtri < 1)
            throw new UnsupportedOperationException();
        numClientsAtri--;
    }

    public void pinged() {
        threadTimeout.interrupt();
        synchronized (numTimeouts) { numTimeouts = 0;}
    }

    public void stop() {
        threadTimeout.setExit(true);
        threadTimeout.interrupt();
    }

    public int getNumCli() {
        return numClientsAtri;
    }
}
