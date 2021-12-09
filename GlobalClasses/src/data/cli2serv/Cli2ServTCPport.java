package data.cli2serv;

public class Cli2ServTCPport extends Cli2Serv {
    private int port;

    public Cli2ServTCPport(int port) {
        super(RequestType.TCP_PORT);
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
