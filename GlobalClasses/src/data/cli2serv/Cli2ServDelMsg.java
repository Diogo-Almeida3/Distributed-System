package data.cli2serv;

public class Cli2ServDelMsg extends Cli2Serv {

    private int idMessage;
    private String username;
    public Cli2ServDelMsg(String username ,int idMessage) {
        super(RequestType.DELETE_MESSAGE);
        this.idMessage = idMessage;
        this.username = username;
    }

    public int getIdMessage() {
        return idMessage;
    }

    public String getUsername() {
        return username;
    }
}
