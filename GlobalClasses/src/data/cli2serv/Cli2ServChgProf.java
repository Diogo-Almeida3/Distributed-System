package data.cli2serv;

public class Cli2ServChgProf extends Cli2Serv {

    private typeEdit editReq = null;

    private String newName = null;
    private String newUsername = null;
    private String oldUsername = null;
    private String newPassword = null;
    private String oldPassword = null;


    // Constructor to change Name or Username
    public Cli2ServChgProf(String newName, String oldUsername,typeEdit editReq) {
        super(RequestType.EDIT_USER);
        if (editReq == typeEdit.EDIT_NAME)
            this.newName = newName;
        else
            this.newUsername = newName;
        this.oldUsername = oldUsername;
        this.editReq = editReq;
    }

    // Constructor to change Password
    public Cli2ServChgProf(String oldUsername, String newPassword, String oldPassword,typeEdit editReq) {
        super(RequestType.EDIT_USER);
        this.oldUsername = oldUsername;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.editReq = editReq;
    }

    public typeEdit getEditReq() {
        return editReq;
    }

    public String getNewName() {
        return newName;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public enum typeEdit {EDIT_NAME, EDIT_PASSWORD, EDIT_USERNAME}
}
