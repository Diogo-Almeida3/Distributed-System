package data.serv2cli;

import Constants.Notification;

import java.io.Serializable;
import java.util.ArrayList;

public class Serv2Cli implements Serializable {

    private Notification notification = null;
    private ArrayList<String> users = new ArrayList<>();

    public Serv2Cli(Notification request){
        this.notification = request;
    }

    public void addUsers(String ... users2add) {
        for (String user : users2add)
            users.add(user);
    }

    public Notification getNotification() {
        return notification;
    }
}
