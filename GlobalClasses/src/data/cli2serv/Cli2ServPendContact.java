package data.cli2serv;

public class Cli2ServPendContact extends Cli2Serv {

    private String username;
    public Cli2ServPendContact(String username) {
        super(RequestType.LIST_REQUESTS);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
