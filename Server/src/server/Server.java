package server;


import server.threads.ThreadClient;
import server.utils.DB;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import server.threads.ThreadPing;

import static server.utils.Constants.*;

public class Server {

    MulticastSocket ms;
    private int grdsPort;
    private String grdsIp;
    private String sgbdIP;
    private ServerSocket socketReceiveConnections;


    /*
    * 1 -> Ligar o Servidor ✔
    * 2 -> Tratar das coordenadas ✔
    * 3 -> Ligar-se a um socket udp com o Grds ✔
    * 4 -> Enviar o porto TCP para o grds conseguir informar os clientes ✔️
    * 5 -> ...
    */
    public Server(String[] args) throws SQLException {
        int tries = 0;
        Thread threadPing;
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
                    ms = new MulticastSocket(MULTICAST_PORT);
                    InetAddress ipBroadCast = InetAddress.getByName(MULTICAST_IP);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(ds.getLocalPort());
                    oos.flush();

                    DatagramPacket dpReq = new DatagramPacket(baos.toByteArray(), baos.size(), ipBroadCast, MULTICAST_PORT);

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
                    System.err.println("erro pesadao");
                    e.printStackTrace();
                }
            }
        }

        DB db = new DB();
        threadPing = new ThreadPing(socketReceiveConnections,grdsIp,grdsPort);
        threadPing.start();

        while(true){
            try {
                Socket sCli = socketReceiveConnections.accept();

                ThreadClient threadClient = new ThreadClient(sCli,db);
                threadClient.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Ligação à base de dados - Lança exceção para fora caso não consiga se conectar

    }

    public static void main(String[] args) {
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