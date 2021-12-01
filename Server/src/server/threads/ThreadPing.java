package server.threads;


import data.ComData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


public class ThreadPing extends Thread {
    private int grdsPort;
    private String grdsIp;
    private ServerSocket socketReceiveConnections;

    public ThreadPing(ServerSocket socketReceiveConnections, String grdsIp, int grdsPort) {
        this.grdsIp = grdsIp;
        this.grdsPort = grdsPort;
        this.socketReceiveConnections = socketReceiveConnections;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ComData tcpPort = new ComData(socketReceiveConnections.getLocalPort(), ComData.typeInitData.SERVER);
                DatagramSocket ds = new DatagramSocket();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeObject(tcpPort);
                oos.flush();

                DatagramPacket dpResp = new DatagramPacket(baos.toByteArray(), baos.size(),
                        InetAddress.getByName(grdsIp), grdsPort);
                System.out.println("Sending my tcp port to the GRDS...");
                ds.send(dpResp);

                Thread.sleep(5 * 1000); //todo mudar para 20 segundos
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
