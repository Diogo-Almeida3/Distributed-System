package grds.threads;

import grds.data.ServerData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.rmi.ServerException;
import java.util.ArrayList;

public class ThreadReceivedServers extends Thread {
    private boolean run = true;
    private DatagramSocket datagramSocket;
    private ArrayList<ServerData> servers;

    public void setRun(boolean run) {
        this.run = run;
    }

    public ThreadReceivedServers(DatagramSocket datagramSocket, ArrayList<ServerData> servers) {
        if (datagramSocket == null || servers == null)
            throw new IllegalArgumentException();
        this.datagramSocket = datagramSocket;
        this.servers = servers;
    }

    @Override
    public void run() {
        while (run) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[256],256);
            try {
                datagramSocket.receive(datagramPacket);
                ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData(),0 ,datagramPacket.getLength());
                ObjectInputStream oin = new ObjectInputStream(bais);
                InetSocketAddress newServer = (InetSocketAddress) oin.readObject();
                for (ServerData serv : servers) // Test if the server is already registered
                    if (serv.getAddress().equals(newServer.getAddress())) throw new Exception("The server with ip:"+newServer.getAddress().getHostAddress()+" is already registered!");
                servers.add(new ServerData(newServer.getAddress(), newServer.getPort())); // Add server to list of active servers
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("It was not possible to receive the information from the server with the ip: " + datagramSocket.getInetAddress().getHostAddress());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
