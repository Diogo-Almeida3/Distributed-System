package data.cli2serv;

public class Cli2ServGetFile extends Cli2Serv {
    private String serverIp, dir;
    private int serverPort, id;

    public Cli2ServGetFile(int id) { // Constructor for the client
        super(RequestType.GET_FILE);
        this.id = id;
    }

    public Cli2ServGetFile(String serverIp, int serverPort,String dir) { // Constructor for the server
        super(RequestType.GET_FILE);
        this.serverIp = serverIp;
        this.dir = dir;
        this.serverPort = serverPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getId() {
        return id;
    }

    public String getDir() {
        return dir;
    }
}
