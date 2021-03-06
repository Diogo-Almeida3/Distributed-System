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
    private String grdsIp;
    private int grdsPort;
    private ThreadPing threadPing;
    private ThreadSendFiles threadSendFiles;

    public ThreadAcceptCli(ServerSocket socketReceiveConnections, DB db,ArrayList<ThreadClient> clients,String grdsIp,int grdsPort,ThreadPing threadPing,ThreadSendFiles threadSendFiles){
        this.socketReceiveConnections = socketReceiveConnections;
        this.db = db;
        this.clients = clients;
        this.grdsIp = grdsIp;
        this.grdsPort = grdsPort;
        this.threadPing = threadPing;
        this.threadSendFiles = threadSendFiles;
    }

    @Override
    public void run() {
        while(true){
            try {
                Socket sCli = socketReceiveConnections.accept();
                ThreadClient threadClient = new ThreadClient(sCli,db,grdsIp,grdsPort, threadPing,threadSendFiles);
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
