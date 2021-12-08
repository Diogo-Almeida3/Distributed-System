package data.cli2serv;

public class Cli2ServListContacts extends Cli2Serv{
    private static final long serialVersionUID = 1L;
    private String username;

    public Cli2ServListContacts(String username) {
        super(RequestType.LIST_CONTACT);
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
}
