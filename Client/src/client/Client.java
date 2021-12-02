package client;

import client.threads.ThreadLoginRegist;
import data.Cli2Grds;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private int grdsPort;
    private String grdsIp;

    public Client(String args[]) throws IOException {

        this.grdsIp = args[0];
        this.grdsPort = Integer.parseInt(args[1]);

        // Contact via udp the grds to receive coordinates( IP AND TCP port of an active server)

        DatagramSocket ds = new DatagramSocket();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject("REQUEST"); // objeto com string
        out.flush();

        byte[] req = baos.toByteArray();

        DatagramPacket dpSend = new DatagramPacket(req,req.length, InetAddress.getByName(grdsIp),grdsPort);
        ds.send(dpSend);

        DatagramPacket dpReceived = new DatagramPacket(new byte[5000],5000);
        ds.receive(dpReceived);

        if (dpReceived.getLength() == 0) {
            System.err.println("No servers available...");
            ds.close();
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(dpReceived.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);


        Cli2Grds msgReceived = null;
        try {
            msgReceived = (Cli2Grds) ois.readObject();
            System.out.println("Assigned server:" + msgReceived.getServIp().getHostAddress() + ":"+msgReceived.getPortIp());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
