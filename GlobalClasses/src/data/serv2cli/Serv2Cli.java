package data.serv2cli;

import Constants.Notification;

import java.io.Serializable;

public class Serv2Cli implements Serializable {

    private Notification notification;
    private String message;

    public Serv2Cli(Notification notification, String message) {
        this.notification = notification;
        this.message = message;
    }

    public Serv2Cli(Notification notification) {
        this.notification = notification;
    }

    public String getMessage() {
        return message;
    }

    public Notification getNotification() {
        return notification;
    }
}
