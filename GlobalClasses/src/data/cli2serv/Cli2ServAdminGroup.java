package data.cli2serv;

public class Cli2ServAdminGroup extends Cli2Serv {
    private int idGroup;
    private String nameNewGroup =null;
    private typeEdit type = null;
    private String userKick = null;
    private String username = null;

    /* Rename the group | Kick User */
    public Cli2ServAdminGroup(int idGroup,String username,typeEdit editType,String name) {
        super(RequestType.ADMIN_GROUP);
        this.idGroup = idGroup;
        this.type = editType;
        if(type == typeEdit.EDIT_NAME)
            nameNewGroup = name;
        else
            userKick = null;
        this.username = username;
    }

    /* Delete Group*/
    public Cli2ServAdminGroup(int idGroup,String username,typeEdit editType) {
        super(RequestType.ADMIN_GROUP);
        this.idGroup = idGroup;
        this.type = editType;
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public String getNameNewGroup(){
        return nameNewGroup;
    }

    public String getUserKick(){
        return userKick;
    }

    public int getIdGroup(){
        return idGroup;
    }

    public typeEdit getTypeEdit(){
        return type;
    }

    public enum typeEdit{EDIT_NAME, DELETE_MEMBER, DELETE_GROUP}


}
