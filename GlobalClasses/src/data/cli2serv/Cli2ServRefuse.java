package data.cli2serv;

public class Cli2ServRefuse extends Cli2Serv {

    private String username;
    private String refuseUsername;
    public Cli2ServRefuse(String username, String refuseUsername) {
        super(RequestType.REFUSE_CONTACT);
        this.username = username;
        this.refuseUsername = refuseUsername;
    }

    public String getUsername() {
        return username;
    }

    public String getRefuseUsername() {
        return refuseUsername;
    }
}
