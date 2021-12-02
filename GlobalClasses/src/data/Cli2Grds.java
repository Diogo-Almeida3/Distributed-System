package data;

import java.io.Serializable;
import java.net.InetAddress;

public class Cli2Grds implements Serializable {
    private InetAddress servIp = null;
    private int portIp = 0;

    public Cli2Grds(InetAddress servIp, int portIp) {
        this.servIp = servIp;
        this.portIp = portIp;
    }

    public Cli2Grds() {
    }

    public InetAddress getServIp() {
        return servIp;
    }

    public int getPortIp() {
        return portIp;
    }
}
