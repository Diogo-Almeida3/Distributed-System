package server.threads;

import Constants.Multicast;
import Constants.Notification;
import data.serv2cli.Serv2Cli;
import data.serv2grds.Serv2GrdsDBup;

import javax.print.attribute.standard.MediaSize;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        MulticastSocket ms;

        /* GRDS INFORMS YOU THAT YOU HAVE TO UPDATE */
        try {
            ms = new MulticastSocket(Multicast.MULTICAST_GRDS_PORT_DIFFUSION);

            InetAddress mulIP = InetAddress.getByName(Multicast.MULTICAST_GRDS_IP_DIFFUSION);
            InetSocketAddress isa = new InetSocketAddress(mulIP,Multicast.MULTICAST_GRDS_PORT_DIFFUSION);
            NetworkInterface ni = NetworkInterface.getByName("wlan1");
            ms.joinGroup(isa,ni);

            while (true) {
                dp = new DatagramPacket(new byte[3000], 3000);
                ms.receive(dp);

                ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Serv2GrdsDBup data = (Serv2GrdsDBup) ois.readObject();

                if(data.getType() == Notification.NEW_FILE_AVAILABLE){
                    ThreadReceivedFiles threadReceivedFiles =  new ThreadReceivedFiles(data.getServerIp(),data.getServerPort(),data.getFileId());
                    threadReceivedFiles.run();
                }

                for (ThreadClient client : clients) {
                    for (String user : data.getUsers())
                        if (user.equals(client.getCliUsername()))
                            client.notification(data.getType());
                }
            }
            /* Lançar thread para receber o ficheiro quando acaba fecha*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

