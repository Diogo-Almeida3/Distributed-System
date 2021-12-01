package data;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class ComData implements Serializable {
    private static final long serialVersionUID = 1L;

    private typeInitData type;
    private int port;

    public ComData(int port, typeInitData type) {
        this.port = port;
        this.type = type;
    }

    public int getPort() {
        return port;
    }

    public typeInitData getType() {
        return type;
    }

    public enum typeInitData {
        SERVER,CLIENT
    }
}