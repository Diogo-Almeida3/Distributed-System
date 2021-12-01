package grds.data.InitData;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class InitData extends InetSocketAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    private typeInitData type;

    public typeInitData getType() {
        return type;
    }

    public InitData(InetAddress addr, int port, typeInitData type) {
        super(addr, port);
        this.type = type;
    }
}
