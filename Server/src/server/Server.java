package server;


import Constants.Multicast;
import server.threads.*;
import server.utils.DB;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {

    MulticastSocket ms;
    private int grdsPort;
    private String grdsIp;
    private String sgbdIP;
    private ServerSocket socketReceiveConnections;
    private static final String serverDirectory = "./Files/"+ ManagementFactory.getRuntimeMXBean().getName();

    public Server(String[] args) throws SQLException {
        int tries = 0;
        ThreadPing threadPing;
        try {
            socketReceiveConnections = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args.length != 1) {
            this.grdsIp = args[0];
            this.grdsPort = Integer.parseInt(args[1]);
            this.sgbdIP = args[2];
            System.out.println("GRDS Coordinates: " + grdsIp + ":" + grdsPort + " | SGDB Ip: " + sgbdIP);
        } else { //3030 | 230.30.30.30
            this.sgbdIP = args[0];
            MulticastSocket ms = null;
            DatagramSocket ds = null;

            while (true) {
                try {
                    ds = new DatagramSocket(0);
                    /* Multicast sends a request to GRDS */
                    ms = new MulticastSocket(Multicast.MULTICAST_GRDS_SEARCH_PORT);
                    InetAddress ipBroadCast = InetAddress.getByName(Multicast.MULTICAST_GRDS_SEARCH_IP);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(ds.getLocalPort());
                    oos.flush();

                    DatagramPacket dpReq = new DatagramPacket(baos.toByteArray(), baos.size(), ipBroadCast, Multicast.MULTICAST_GRDS_SEARCH_PORT);

                    ms.send(dpReq);
                    System.out.println("Sending multicast request to grds...");
                    ms.close();


                    ds.setSoTimeout(3000);
                    DatagramPacket dpResp = new DatagramPacket(new byte[256], 256);
                    System.out.println("Waiting for response...");
                    ds.receive(dpResp);
                    ds.close();

                    ByteArrayInputStream bais = new ByteArrayInputStream(dpResp.getData(),0 ,dpResp.getLength());
                    ObjectInputStream oin = new ObjectInputStream(bais);

                    Integer portReceived = (Integer) oin.readObject();

                    this.grdsPort = portReceived;
                    this.grdsIp = dpResp.getAddress().getHostAddress();
                    System.out.println("GRDS Coordinates: " + grdsIp + ":" + grdsPort + " | SGDB Ip: " + sgbdIP);
                    break;
                } catch (SocketTimeoutException e) {
                    System.err.println("Response time passed trying again...");
                    if (++tries >= 3){
                        try {
                            socketReceiveConnections.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }
                    assert ds != null;
                    ds.close();

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        DB db = new DB();
        threadPing = new ThreadPing(socketReceiveConnections,grdsIp,grdsPort);
        threadPing.start();

        ArrayList<ThreadClient> clients =  new ArrayList<>();

        ThreadActivityClient threadActivityClient = new ThreadActivityClient(clients,db);
        threadActivityClient.start();

        ThreadGrds threadGrds = new ThreadGrds(clients);
        threadGrds.start();

        ThreadSendFiles threadSendFiles = new ThreadSendFiles("./Files/"+ ManagementFactory.getRuntimeMXBean().getName());
        threadSendFiles.start();

        ThreadAcceptCli threadAcceptCli =  new ThreadAcceptCli(socketReceiveConnections,db,clients,grdsIp,grdsPort, threadPing,threadSendFiles);
        threadAcceptCli.start();


        System.out.println("Server Ready!");

        synchronized (threadActivityClient) {
            try {
                threadActivityClient.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (threadGrds) {
            try {
                threadGrds.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (threadAcceptCli) {
            try {
                threadAcceptCli.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Ligação à base de dados - Lança exceção para fora caso não consiga se conectar

    }

    public static void main(String[] args) {
//        DB db = null;
//        try {
//            db = new DB();
//            db.sendMessage("jpbp","lims","mensagem12346 654as dasda sdasd ad");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        try{
            if(args.length == 3 || args.length ==1 )
                new Server(args);
            else
                throw new IllegalArgumentException("Invalid arguments! You must use one of the following formatting: <IP_GRDS> <Port_GRDS> <IP_SBGD> OR <IP_SGBD>");
        } catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
        } catch (SQLException throwables) {
            System.err.println("Unable to connect to the database!");
        }
    }
}