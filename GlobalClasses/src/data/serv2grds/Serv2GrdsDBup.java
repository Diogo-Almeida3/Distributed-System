package data.serv2grds;

import Constants.Notification;

import java.util.ArrayList;
import java.util.Collection;

public class Serv2GrdsDBup extends Serv2Grds {
    private ArrayList<String> users = new ArrayList<>();
    private Notification type;
    private String message;

    private String ServerIp = null;
    private int ServerPort = 0;
    private int fileId = -1;

    private String extra;

    public Serv2GrdsDBup(Notification type, String ... users2add) {
        super(Request.BD_UPDATE);
        if (users2add != null)
            addUsers(users2add);
        this.type = type;
    }

    public Serv2GrdsDBup(Notification type, ArrayList<String> groupUsers) {
        super(Request.BD_UPDATE);
        if (groupUsers != null)
            users.addAll(groupUsers);
        this.type = type;
    }

    public Serv2GrdsDBup(Notification type, String serverIp, int serverPort, int fileId,String ... users2add) {
        super(Request.BD_UPDATE);
        this.fileId = fileId;
        this.ServerIp = serverIp;
        if (users2add != null)
            addUsers(users2add);
        this.ServerPort = serverPort;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIp(String ip, int port) { ServerIp = ip; ServerPort = port; }

    public void addUsers(String ... users2add) {
        for (String user : users2add)
            users.add(user);
    }

    public Notification getType() {
        return type;
    }

    public int getFileId() {
        return fileId;
    }

    public String getServerIp() {
        return ServerIp;
    }

    public int getServerPort() {
        return ServerPort;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getExtra() {
        return extra;
    }

    public void putExtra(String extra) {
        this.extra = extra;
    }
}
