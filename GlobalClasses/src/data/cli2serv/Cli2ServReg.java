package data.cli2serv;

public class Cli2ServReg extends Cli2Serv {
    private static final long serialVersionUID = 1L;
    private String username,name,password;

    public String getUsername() {
        return username;
    }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }

    public Cli2ServReg(String username, String name, String password) {
        super(RequestType.REGISTER);
        this.username = username;
        this.name = name;
        this.password = password;
    }
}
