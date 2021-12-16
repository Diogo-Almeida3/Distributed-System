package data.cli2serv;

import java.util.Locale;

public class Cli2ServAdminGroup extends Cli2Serv {
    private int idGroup;
    private String nameNewGroup =null;
    private typeEdit type = null;
    private String userKick = null;
    private String username = null;
    private String acceptUser = null;
    private String refuseUser = null;

    /* Rename the group | Kick User */
    public Cli2ServAdminGroup(int idGroup,String username,typeEdit editType,String name) {
        super(RequestType.ADMIN_GROUP);
        this.idGroup = idGroup;
        this.type = editType;
        if(type == typeEdit.EDIT_NAME)
            nameNewGroup = name;
        else if(type == typeEdit.DELETE_MEMBER)
            userKick = name;
        else if(type == typeEdit.ACCEPT_MEMBER)
            acceptUser=name;
        else if(type == typeEdit.REFUSE_MEMBER)
            refuseUser=name;

        this.username = username.toLowerCase();
    }

    /* Delete Group*/
    public Cli2ServAdminGroup(int idGroup,String username,typeEdit editType) {
        super(RequestType.ADMIN_GROUP);
        this.idGroup = idGroup;
        this.type = editType;
        this.username = username.toLowerCase();
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

    public String getRefuseUser() {
        return refuseUser;
    }

    public int getIdGroup(){
        return idGroup;
    }

    public typeEdit getTypeEdit(){
        return type;
    }

    public enum typeEdit{EDIT_NAME, DELETE_MEMBER, DELETE_GROUP,ACCEPT_MEMBER,REFUSE_MEMBER,WAITING_MEMBERS}

    public String getAcceptUser() {return acceptUser;}
}
