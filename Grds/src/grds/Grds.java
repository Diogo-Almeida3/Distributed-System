package grds;

import data.Cli2Grds;
import data.Serv2Grds;
import grds.data.ServerData;
import grds.threads.ThreadMulticast;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.net.*;
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
    private int myport;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Invalid arguments! <GRDS_PORT>");
            return;
        }
        new Grds(args);
    }

    public Grds(String[] args) {
        try {
            myport = Integer.parseInt(args[0]);
            datagramSocket = new DatagramSocket(myport); // If it is not possible to open on this port it could mean that there is already an instance of grds running
        } catch (SocketException e) {
            System.err.println("You cannot run more than one GRDS");
            return;
        } catch (NumberFormatException e) {
            System.err.println("Invalid arguments! <GRDS_PORT>");
            return;
        }
        servers = new ArrayList<>();


        // ========================== Threads ==========================
        ThreadReceivedServers threadReceivedServers = new ThreadReceivedServers();
        threadReceivedServers.start();
        ThreadMulticast threadMulticast = new ThreadMulticast(myport);
        threadMulticast.start();
        System.out.println("============================== GRDS Ready ==============================");
        try {
            synchronized (threadReceivedServers) {
                threadReceivedServers.wait();
            }
            synchronized (threadMulticast) {
                threadMulticast.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ===============================================================

    }


    class ThreadReceivedServers extends Thread {
        private boolean run = true;

        public void setRun(boolean run) {
            this.run = run;
        }

        @Override
        public void run() {
            while (run) {
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[5000], 5000);
                    datagramSocket.receive(datagramPacket);

                    ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData(),0 ,datagramPacket.getLength());
                    ObjectInputStream oin = new ObjectInputStream(bais);
                    Object systemReq = oin.readObject();

                    // ======================== Server Communication ========================
                    if (systemReq instanceof Serv2Grds) {
                        Serv2Grds data = (Serv2Grds) systemReq;

                        switch (data.getRequest()) {
                            case REGISTER -> {
                                synchronized (servers) {
                                    servers.add(new ServerData(datagramPacket.getAddress(), data.getPort())); // Add server to list of active servers
                                }
                                System.out.println("New Server Registered! --> " + datagramPacket.getAddress().getHostAddress() + ":" + data.getPort());
                            }
                            case PING -> {
                                System.out.println("Ping! --> " + datagramPacket.getAddress().getHostAddress());
                            }
                            default -> System.err.println("Request of server "+datagramPacket.getAddress().getHostAddress()+"is not possible");
                        }
                    }
                    // ====================== End Server Communication =======================


                    // ======================== Client Communication ========================
                    else if (systemReq instanceof Cli2Grds) {
                        Cli2Grds data = (Cli2Grds) systemReq;
                        DatagramSocket ds = new DatagramSocket();
                        try {
                            ServerData serv = null;
                            synchronized (servers) {
                                serv = getServer();
                                serv.newClient();
                            }
                            Cli2Grds send2cli = new Cli2Grds(serv.getAddress(),serv.getPort());

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);

                            oos.writeObject(send2cli);
                            oos.flush();

                            datagramPacket.setData(baos.toByteArray());
                            datagramPacket.setLength(baos.size());
                            java.lang.System.out.println("Server "+serv.getAddress().getHostAddress()+" assigned to client " + datagramPacket.getAddress().getHostAddress());
                             ds.send(datagramPacket);

                        } catch (OperationNotSupportedException e) { // No active servers, informs client to terminate
                            datagramPacket.setData(new byte[0]);
                            datagramPacket.setLength(0);
                            ds.send(datagramPacket);
                            System.err.println("No active servers!");
                        }
                    }
                    // ======================= END Client Communication ======================


                    else {
                        System.err.println("Not Client or Server connect....");
                    }
                } catch (IOException e) {
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
        ServerData servMin = servers.get(0);
        int minCli = servMin.getNumCli();
        for (ServerData serv : servers) {
            if (serv.getNumCli() < minCli) {
                minCli = serv.getNumCli();
                servMin = serv;
            }
        }
        return servMin;
    }
}
