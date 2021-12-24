package data.cli2serv;

public class Cli2ServDelFile extends Cli2Serv {
    private String username;
    private int idFile;

    public Cli2ServDelFile(String username,int idFile) {
        super(RequestType.DELETE_FILE);
        this.username = username;
        this.idFile = idFile;
    }

    public String getUsername() {
        return username;
    }

    public int getIdFile() {
        return idFile;
    }
}
