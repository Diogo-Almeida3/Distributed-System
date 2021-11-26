package server;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class DataAdress extends InetSocketAddress  implements Serializable{

    private int port;


    public DataAdress(int port) {
        super(port);
    }

    public DataAdress(InetAddress addr, int port) {
        super(addr, port);
    }

    public DataAdress(String hostname, int port) {
        super(hostname, port);
    }
}
