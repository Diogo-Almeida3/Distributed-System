package server.threads;


import data.Serv2Grds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


public class ThreadPing extends Thread {
    private int grdsPort;
    private String grdsIp;
    private ServerSocket socketReceiveConnections;
    private boolean isRegisted = false;

    public ThreadPing(ServerSocket socketReceiveConnections, String grdsIp, int grdsPort) {
        this.grdsIp = grdsIp;
        this.grdsPort = grdsPort;
        this.socketReceiveConnections = socketReceiveConnections;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Serv2Grds tcpPort = new Serv2Grds(isRegisted ? Serv2Grds.Request.PING : Serv2Grds.Request.REGISTER,socketReceiveConnections.getLocalPort());
                isRegisted = true;
                DatagramSocket ds = new DatagramSocket();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeObject(tcpPort);
                oos.flush();

                DatagramPacket dpResp = new DatagramPacket(baos.toByteArray(), baos.size(),
                        InetAddress.getByName(grdsIp), grdsPort);
                java.lang.System.out.println("Sending my tcp port to the GRDS...");
                ds.send(dpResp);

                Thread.sleep(20 * 1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
