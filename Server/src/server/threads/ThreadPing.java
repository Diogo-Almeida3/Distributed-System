package server.threads;


import data.serv2grds.Serv2Grds;

import java.io.*;
import java.net.*;


public class ThreadPing extends Thread {
    private int grdsPort;
    private String grdsIp;
    private ServerSocket socketReceiveConnections;
    private boolean isRegisted = false;
    private Integer id = -1;

    public ThreadPing(ServerSocket socketReceiveConnections, String grdsIp, int grdsPort) {
        this.grdsIp = grdsIp;
        this.grdsPort = grdsPort;
        this.socketReceiveConnections = socketReceiveConnections;
    }

    public int getIdServ() {
        return id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Serv2Grds tcpPort = new Serv2Grds(isRegisted ? Serv2Grds.Request.PING : Serv2Grds.Request.REGISTER,socketReceiveConnections.getLocalPort(),id);
                DatagramSocket ds = new DatagramSocket();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeObject(tcpPort);
                oos.flush();

                DatagramPacket dpResp = new DatagramPacket(baos.toByteArray(), baos.size(),
                        InetAddress.getByName(grdsIp), grdsPort);
                java.lang.System.out.println("["+id+"] Sending my tcp port to the GRDS...");
                ds.send(dpResp);

                if (!isRegisted) {
                    ds.receive(dpResp);
                    ByteArrayInputStream bais = new ByteArrayInputStream(dpResp.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    id = (Integer) ois.readObject();
                }

                isRegisted = true;

                Thread.sleep(20 * 1000);
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
