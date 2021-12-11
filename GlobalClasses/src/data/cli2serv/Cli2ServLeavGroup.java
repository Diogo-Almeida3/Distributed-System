package data.cli2serv;

public class Cli2ServLeavGroup extends Cli2Serv {
    private int idGroup;
    public Cli2ServLeavGroup(int idGroup) {
        super(RequestType.LEAVE_GROUP);
        this.idGroup = idGroup;
    }

    public int getIdGroup() {
        return idGroup;
    }
}
