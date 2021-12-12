package data.serv2grds;

import Constants.Notification;

import java.util.ArrayList;
import java.util.Collection;

public class Serv2GrdsDBup extends Serv2Grds {
    private ArrayList<String> users = new ArrayList<>();
    private Notification type;

    private String ServerIp = null;
    private int ServerPort = 0;

    public Serv2GrdsDBup() {
        super(Request.BD_UPDATE);
    }

    public Serv2GrdsDBup(Notification type, String ... users2add) {
        super(Request.BD_UPDATE);
        if (users2add != null)
            addUsers(users2add);
        this.type = type;
    }

    public Serv2GrdsDBup(Notification type, Collection<String> groupUsers) {
        super(Request.BD_UPDATE);
        if (groupUsers != null)
            users.addAll(groupUsers);
        this.type = type;
    }

    public Serv2GrdsDBup(Notification type, String serverIp, int serverPort, String ... users2add) {
        super(Request.BD_UPDATE);
        if (users2add != null)
            addUsers(users2add);
        this.ServerIp = serverIp;
        this.ServerPort = serverPort;
        this.type = type;
    }

    public void setIp(String ip, int port) { ServerIp = ip; ServerPort = port; }

    public void addUsers(String ... users2add) {
        for (String user : users2add)
            users.add(user);
    }

    public Notification getType() {
        return type;
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
}
