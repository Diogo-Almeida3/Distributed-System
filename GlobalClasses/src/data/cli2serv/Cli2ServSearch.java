package data.cli2serv;

public class Cli2ServSearch extends Cli2Serv{

    private String username;

    public Cli2ServSearch(String username){
        super(RequestType.SEARCH_USER);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
