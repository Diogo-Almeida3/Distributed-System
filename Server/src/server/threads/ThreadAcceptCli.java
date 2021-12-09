package server.threads;

import server.utils.DB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadAcceptCli extends Thread {

    private Socket sCli;
    private ServerSocket socketReceiveConnections;
    private DB db;
    private ArrayList<ThreadClient> clients;

    public ThreadAcceptCli(ServerSocket socketReceiveConnections, DB db,ArrayList<ThreadClient> clients){
        this.socketReceiveConnections = socketReceiveConnections;
        this.db = db;
        this.clients = clients;
    }

    @Override
    public void run() {
        while(true){
            try {
                Socket sCli = socketReceiveConnections.accept();
                ThreadClient threadClient = new ThreadClient(sCli,db);
                threadClient.start();

                synchronized (clients) {
                    clients.add(threadClient);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
