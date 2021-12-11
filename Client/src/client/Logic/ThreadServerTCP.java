package client.Logic;

import data.serv2cli.Serv2Cli;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ThreadServerTCP extends Thread{

    private ServerSocket ss = null;
    private InputStream isServ = null;
    private Socket sCli = null;
    private ObjectInputStream oisServ = null;
    private boolean exit=false;
    private Client logic;

    public ThreadServerTCP(Client logic){
        try {
            ss = new ServerSocket(0);
            this.logic = logic;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public ObjectInputStream getOisServ() {
        return oisServ;
    }

    public int getpPort() {return ss.getLocalPort(); }

    @Override
    public void run() {
        try {
            Socket sCli = ss.accept(); // Wait for connection with server
            isServ = sCli.getInputStream();
            oisServ = new ObjectInputStream(isServ);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Serv2Cli serv2Cli = null;
        while(!exit) {
            try {
                serv2Cli = (Serv2Cli) oisServ.readObject();

                switch (serv2Cli.getNotification()){
                    case CONTACT_REQUEST -> {
                        System.out.println("\nYou have a new contact request!");
                    }
                    case JOIN_GROUP_REQUEST -> {
                        System.out.println("\nYou have a new request to join in your group!");
                    }
                    case CONTACT_REQ_RESPONSE -> {
                        System.out.println("\nYou have a response to a contact request!");
                    }
                    case JOIN_GROUP_REQ_RESPONSE -> {
                        System.out.println("\nYou have an answer to your request to join a group!");
                    }
                    case EDIT_USER -> {
                        // Interface Gráfica Apenas
                    }
                    case MESSAGE -> {
                        System.out.println("\nYou have a new message!");
                    }
                    case MESSAGE_DELETE -> {
                        System.out.println("\nA message was deleted!");
                    }
                    case FILE -> {
                        System.out.println("\nYou have a new file to transfer!");
                    }
                    case FILE_DELETE -> {
                        System.out.println("\nOne file has been deleted!");
                    }
                    case USER_LOGIN -> {
                        // Interface Gráfica Apenas
                    }
                    case USER_LEFT -> {
                        // Interface Gráfica Apenas
                    }
                    case GROUP_DELETE -> {
                        System.out.println("\nA group you were in was deleted!");
                    }
                    case CONTACT_DELETE -> {
                        System.out.println("\nA contact of yours was deleted!");
                    }
                    case LEAVE_GROUP -> {
                        System.out.println("\nOne user has left of your group!");
                    }
                }
            } catch (SocketException e) {
                if (!exit) {
                    logic.connect2serv();
                    exit = true;
                }
                break;
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            if (ss!=null)
                ss.close();
            if (oisServ!=null)
                oisServ.close();
            if (isServ!=null)
                isServ.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
