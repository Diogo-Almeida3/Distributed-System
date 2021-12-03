package grds.threads;

import java.io.*;
import java.net.*;

public class ThreadMulticast extends Thread {

    private int myPort;

    private final String MULTICAST_IP = "230.30.30.30";
    private final int MULTICAST_PORT = 3030;

    public ThreadMulticast(int myPort) {
        this.myPort = myPort;
    }

    @Override
    public void run() {
        MulticastSocket ms = null;
        DatagramSocket ds = null;
        try {
            // Receive request on multicast
            ms = new MulticastSocket(MULTICAST_PORT);
            InetAddress mulIP = InetAddress.getByName(MULTICAST_IP);
            InetSocketAddress isa = new InetSocketAddress(mulIP,MULTICAST_PORT);
            NetworkInterface ni = NetworkInterface.getByName("wlan1");
            ms.joinGroup(isa,ni);

            DatagramPacket dp = new DatagramPacket(new byte[256],256);
            ms.receive(dp);

            ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData(),0 ,dp.getLength());
            ObjectInputStream oin = new ObjectInputStream(bais);

            Integer port = (Integer) oin.readObject();

            // Send port to server
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(myPort);
            oos.flush();

            dp.setPort(port);
            dp.setData(baos.toByteArray());
            dp.setLength(baos.size());

            ds = new DatagramSocket(0);
            ds.send(dp);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ms != null)
                ms.close();

            if (ds != null)
                ds.close();
        }
    }
}
