package data.cli2serv;

public class Cli2ServInvGroup extends Cli2Serv {

    private String nameGroup;
    public Cli2ServInvGroup(String nameGroup) {
        super(RequestType.JOIN_GROUP);
        this.nameGroup = nameGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }
    
}
