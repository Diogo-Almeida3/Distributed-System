package server;


import server.utils.DB;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import server.threads.ThreadPing;
import java.sql.Time;

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
            socketReceiveConnections = new ServerSocket(9001);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args.length != 1) {
            this.grdsIp = args[0];
            this.grdsPort = Integer.parseInt(args[1]);
            this.sgbdIP = args[2];
        } else { //3030 | 230.30.30.30
            this.sgbdIP = args[0];
            MulticastSocket ms = null;
            DatagramSocket ds = null;

            while (true) {
                try {

                    /* Envia por multicast um pedido ao GRDS */
                    ms = new MulticastSocket(MULTICAST_PORT);
                    byte[] udpBytes = ("REQUEST").getBytes();
                    InetAddress ipBroadCast = InetAddress.getByName(MULTICAST_IP);
                    DatagramPacket dpReq = new DatagramPacket(udpBytes, udpBytes.length, ipBroadCast, MULTICAST_PORT);
                    ms.send(dpReq);
                    System.out.println("Sending multicast request to grds...");
                    ms.close();


                    ds = new DatagramSocket(MULTICAST_PORT);
                    ds.setSoTimeout(3000);
                    DatagramPacket dpResp = new DatagramPacket(new byte[256], 256);
                    System.out.println("Waiting for response...");
                    ds.receive(dpResp);
                    ds.close();

                    String grdsPort = new String(dpResp.getData(), 0, dpResp.getLength());
                    this.grdsPort = Integer.parseInt(grdsPort);
                    this.grdsIp = String.valueOf(dpResp.getAddress());
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

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        threadPing = new ThreadPing(socketReceiveConnections,grdsIp,grdsPort);
        threadPing.start();
        try {
            synchronized (threadPing){
                threadPing.wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Ligação à base de dados - Lança exceção para fora caso não consiga se conectar
        DB db = new DB();
    }
}