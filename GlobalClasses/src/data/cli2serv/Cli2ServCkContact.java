package data.cli2serv;

public class Cli2ServCkContact extends Cli2Serv {
    private String name1, name2;
    private int groupId;
    private boolean ckGroup;

    public Cli2ServCkContact(String name1, String name2) {
        super(RequestType.CK_CONTACT);
        this.name1 = name1;
        this.name2 = name2;
        this.ckGroup = false;
    }

    public Cli2ServCkContact(String name1, int groupId) {
        super(RequestType.CK_CONTACT);
        this.name1 = name1;
        this.groupId = groupId;
        this.ckGroup = true;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public boolean isCkGroup() {
        return ckGroup;
    }

    public int getGroupId() {
        return groupId;
    }
}
