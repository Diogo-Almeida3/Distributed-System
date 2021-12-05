package data.cli2serv;

public class Cli2ServLog extends Cli2Serv {
    private static final long serialVersionUID = 1L;
    private String username,password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Cli2ServLog(String username, String password) {
        super(RequestType.LOGIN);
        this.username = username;
        this.password = password;
    }
}
