package data.cli2serv;

public class Cli2ServCreatGroup extends Cli2Serv {

    private String username;
    private String groupName;

    public Cli2ServCreatGroup(String username,String groupName) {
        super(RequestType.CREATE_GROUP);
        this.username = username;
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public String getGroupName() {
        return groupName;
    }
}
