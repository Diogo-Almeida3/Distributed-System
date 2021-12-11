package data.cli2serv;

public class Cli2ServInvGroup extends Cli2Serv {

    private int groupId;
    public Cli2ServInvGroup(int groupId) {
        super(RequestType.JOIN_GROUP);
        this.groupId = groupId;
    }

    public int getGroupID() {
        return groupId;
    }
    
}
