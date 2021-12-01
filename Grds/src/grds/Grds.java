package grds;

import data.ComData;
import grds.data.ServerData;

import javax.naming.OperationNotSupportedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Grds {
    /*
     * TODO:
     * Manter Registo Servidores Ativos
     * Direcionar aplicações clientes para os diversos servidores
     * Difundir informação relevante
     *
     * Distribuir os clientes segundo o escalonamento circular
     * Informa via UDP os servidores sobre as coordenadas TCP do conjunto de servidores ativos
     * Refletor de informação relevante (Pedidos de contato, envio de mensagens e disponibilização de ficheiros
     * Comunica com os clientes via UDP
     *
     * Quando recebe um contacto do cliente mal este se liga deve enviar via udp as coordenadas TCP de um servidor ativo - Thread a tratar disto
     * Recebe de 20 em 20 segundos uma mensagem dos servidores com a indicaçao do porto de escuta tcp (Três periodos sem receber esquece o servidor)
     * Recebe via UDP uma mensagem do servidor a informar que a base de dados foi alterada e rencaminha para os restantes servidores (Se forem ficheiros - ver enunciado)
     * Recebe via UDP uma mensagem a solicitar a eliminaçao dos ficheiros e esta mensagem inclui a indicação para avisar todos os outros servidores
     * Trata do encerramento organizado de um servidor
     */

    private ArrayList<ServerData> servers; // Active Servers
    private DatagramSocket datagramSocket;

    public Grds() {
        try { // TODO: Porta como argumento
            datagramSocket = new DatagramSocket(9001); // If it is not possible to open on this port it could mean that there is already an instance of grds running
        } catch (SocketException e) {
            System.err.println("You cannot run more than one GRDS");
            return;
        }
        servers = new ArrayList<>();
        ThreadReceivedServers threadReceivedServers = new ThreadReceivedServers();
        threadReceivedServers.start();
        try {
            synchronized (threadReceivedServers) {
                threadReceivedServers.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    class ThreadReceivedServers extends Thread {
        private boolean run = true;

        public void setRun(boolean run) {
            this.run = run;
        }


        @Override
        public void run() {
            while (run) {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[256],256);
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[5000], 5000);
                    datagramSocket.receive(datagramPacket);
                    ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData(),0 ,datagramPacket.getLength());
                    ObjectInputStream oin = new ObjectInputStream(bais);
                    ComData newServer = (ComData) oin.readObject();
//                    String teste = (String) oin.readObject();
//                    System.out.println(teste);
//                    for (ServerData serv : servers) // Test if the server is already registered
//                        if (serv.getAddress().equals(newServer.getAddress())) throw new Exception("The server with ip:"+newServer.getAddress().getHostAddress()+" is already registered!");
                    switch (newServer.getType()) {
                        case CLIENT -> {
                            synchronized (servers) {
                                ServerData serv = getServer();
                            }
                            // Send info to client

                        }
                        case SERVER -> {
                            synchronized (servers) {
                                System.out.println(datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort());
                                servers.add(new ServerData(datagramPacket.getAddress(), newServer.getPort())); // Add server to list of active servers
                            }
                            System.out.println("New Server Registered");
                        }
                        default -> {
                            System.err.println("Not Client or Server connect....");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("It was not possible to receive the information from the server.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private ServerData getServer() throws OperationNotSupportedException {
        if (servers.isEmpty())
            throw new OperationNotSupportedException();
        int minCli = -1;
        ServerData servMin = null;
        for (ServerData serv : servers) {
            if (serv.getNumCli() < minCli) {
                minCli = serv.getNumCli();
                servMin = serv;
            }
        }
        return servMin;
    }
}
