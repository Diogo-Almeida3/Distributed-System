package data.cli2serv;

public class Cli2ServDel extends Cli2Serv {
    private String username;
    private String usernameDel;

    public Cli2ServDel(String username,String usernameDel) {
        super(RequestType.DELETE_CONTACT);
        this.username = username;
        this.usernameDel = usernameDel;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernameDel() {
        return usernameDel;
    }
}
