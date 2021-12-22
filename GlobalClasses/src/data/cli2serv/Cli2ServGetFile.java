package data.cli2serv;

public class Cli2ServGetFile extends Cli2Serv {
    private String serverIp,filename;
    private int serverPort;

    public Cli2ServGetFile(String filename) { // Constructor for the client
        super(RequestType.GET_FILE);
        this.filename = filename;
    }

    public Cli2ServGetFile(String serverIp, int serverPort,String filename) { // Constructor for the server
        super(RequestType.GET_FILE);
        this.serverIp = serverIp;
        this.filename = filename;
        this.serverPort = serverPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getFilename() {
        return filename;
    }

    public int getServerPort() {
        return serverPort;
    }
}
