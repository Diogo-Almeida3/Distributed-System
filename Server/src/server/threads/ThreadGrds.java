package server.threads;

import Constants.Multicast;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ThreadGrds extends Thread {

    private ArrayList<ThreadClient> clients;

    public ThreadGrds(ArrayList<ThreadClient> clients) {
        this.clients = clients;
    }

    @Override
    public void run() {
        DatagramPacket dp;
        MulticastSocket ms = null;

        try {
            ms = new MulticastSocket(Multicast.MULTICAST_GRDS_PORT_DIFFUSION);

            InetAddress mulIP = InetAddress.getByName(Multicast.MULTICAST_GRDS_IP_DIFFUSION);
            InetSocketAddress isa = new InetSocketAddress(mulIP,Multicast.MULTICAST_GRDS_PORT_DIFFUSION);
            NetworkInterface ni = NetworkInterface.getByName("wlan0");
            ms.joinGroup(isa,ni);

            while (true) {
                dp = new DatagramPacket(new byte[512], 512);
                ms.receive(dp);

                String msg = new String(dp.getData(), 0, dp.getLength());

                if (msg.contains("BD_UPDATE")) {    // inform you that changes have occurred and send a message to your customers
                    for (ThreadClient client : clients) {
                        client.refreshDB(); //TODO: Informar só os clientes que tem de atualizar a base de dados
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

