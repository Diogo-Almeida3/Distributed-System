package grds;

import grds.data.InitData.InitData;
import grds.data.ServerData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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

    /*
    DONE: Thread que recebe os servidores (unicast) e adição dos novos servidores no array servers
    TODO: Thread que recebe os servidores em multicast & Thread que recebe os clientes e encaminha para os servidores

     */

    private ArrayList<ServerData> servers; // Active Servers
    private DatagramSocket datagramSocket;

    public Grds() {
        try {
            datagramSocket = new DatagramSocket(9001); // If it is not possible to open on this port it could mean that there is already an instance of grds running
        } catch (SocketException e) {
            System.err.println("You cannot run more than one GRDS");
        }
        servers = new ArrayList<>();







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
                    datagramSocket.receive(datagramPacket);
                    ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData(),0 ,datagramPacket.getLength());
                    ObjectInputStream oin = new ObjectInputStream(bais);
                    InitData newServer = (InitData) oin.readObject();
//                    for (ServerData serv : servers) // Test if the server is already registered
//                        if (serv.getAddress().equals(newServer.getAddress())) throw new Exception("The server with ip:"+newServer.getAddress().getHostAddress()+" is already registered!");
                    switch (newServer.getType()) {
                        case CLIENT -> {

                        }
                        case SERVER -> {
                            servers.add(new ServerData(newServer.getAddress(), newServer.getPort())); // Add server to list of active servers
                        }
                        default -> {
                            System.err.println("Not Client or Server connect....");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("It was not possible to receive the information from the server with the ip: " + datagramSocket.getInetAddress().getHostAddress());
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

}
