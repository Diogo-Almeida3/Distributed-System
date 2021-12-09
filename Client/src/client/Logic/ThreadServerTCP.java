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

                switch (serv2Cli.getRequest()){
                    case NOTIFICATION_MESSAGE -> {
                        System.out.println("NOTIFICATION_MESSAGE");
                    }

                    case NOTIFICATION_FILE -> {
                        System.out.println("NOTIFICATION_FILE");
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
