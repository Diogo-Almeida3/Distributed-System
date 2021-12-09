package data.cli2serv;

public class Cli2ServAdd extends Cli2Serv {
    private String username;
    private String addUsername;

    public Cli2ServAdd(String username, String addUsername) {
        super(RequestType.ADD_CONTACT);
        this.username = username;
        this.addUsername = addUsername;
    }

    public String getAddUsername(){
        return addUsername;
    }
    public String getUsername(){
        return username;
    }
}
