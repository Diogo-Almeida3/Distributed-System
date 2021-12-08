package grds.data;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Date;

public class ServerData extends InetSocketAddress {
    private static int identifierCount = 0;

    private int identifier = identifierCount++;
    private int numClientsAtri = 0;
    private long lastPinged;

    public ServerData(InetAddress addr, int port) {
        super(addr, port);
        lastPinged = Calendar.getInstance().getTimeInMillis();
    }

    public int getIdentifier() {
        return identifier;
    }

    public boolean isTimeout() {
        return Calendar.getInstance().getTimeInMillis() - lastPinged > 60 * 1000;
    }

    public int getNumTimeouts() {
        return Math.toIntExact((Calendar.getInstance().getTimeInMillis() - lastPinged) / 20 * 1000);
    }

    public void newClient() {numClientsAtri++;}

    public void removeClient() {
        if (numClientsAtri < 1)
            throw new UnsupportedOperationException();
        numClientsAtri--;
    }

    public void pinged() {
        lastPinged = Calendar.getInstance().getTimeInMillis();
    }

    public int getNumCli() {
        return numClientsAtri;
    }
}
