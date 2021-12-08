package client.Logic;

import data.serv2cli.Serv2Cli;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadServerTCP extends Thread{

    private Socket sCli;
    private ObjectInputStream in2Serv;
    private boolean exit=false;

    public ThreadServerTCP(Socket sCli){
        this.sCli = sCli;
        try {
            in2Serv = new ObjectInputStream(sCli.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        Serv2Cli serv2Cli = null;
        while(!exit) {

            try {
                serv2Cli = (Serv2Cli) in2Serv.readObject();

                // tratar consoante o tipo
                switch (serv2Cli.getRequest()){

                    case NOTIFICATION_MESSAGE -> {

                    }

                    case NOTIFICATION_FILE -> {

                    }
                }

            } catch (IOException |ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            in2Serv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
