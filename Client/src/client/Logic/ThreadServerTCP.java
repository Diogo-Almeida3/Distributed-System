package client.Logic;

import client.UI.Text.UIClient;
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
    private UIClient ui;

    public ThreadServerTCP(Client logic, UIClient ui) throws IOException {
            if (logic==null||ui==null)
                throw new IllegalArgumentException();
            ss = new ServerSocket(0);
            this.logic = logic;
            this.ui = ui;
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
                ui.notification(serv2Cli.getNotification());
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
