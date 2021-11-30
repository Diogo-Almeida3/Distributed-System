package server.threads;

import java.io.IOException;
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
                DatagramSocket ds = new DatagramSocket(grdsPort);
                byte[] pingResp = ("" + socketReceiveConnections.getLocalPort()).getBytes();
                DatagramPacket dpResp = new DatagramPacket(pingResp, pingResp.length,
                        InetAddress.getByName(grdsIp), grdsPort);
                ds.send(dpResp);

                Thread.sleep(20 * 1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
