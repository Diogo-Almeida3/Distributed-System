package data.cli2serv;

public class Cli2ServExit extends Cli2Serv {
    private static final long serialVersionUID = 1L;
    private String username;

    public Cli2ServExit(String username) {
        super(RequestType.EXIT);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
