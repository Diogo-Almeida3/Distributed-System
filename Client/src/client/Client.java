package client;

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

        // Contactar via udp o grds para receber coordenadas( IP E PORTO TCP de um servidor ativo)

        DatagramSocket ds = new DatagramSocket();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject("REQUEST"); // objeto com string
        out.flush();

        byte[] req = baos.toByteArray();

        DatagramPacket dpSend = new DatagramPacket(req,req.length, InetAddress.getByName(grdsIp),grdsPort);
        ds.send(dpSend);

        DatagramPacket dpReceived = new DatagramPacket(new byte[256],256);
        ds.receive(dpReceived);

        ByteArrayInputStream bais = new ByteArrayInputStream(dpReceived.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);

        try {
            String msgReceived = (String) ois.readObject();
            System.out.println("Re");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
