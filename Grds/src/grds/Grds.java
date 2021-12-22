package grds;

import Constants.Multicast;
import data.Cli2Grds;
import data.serv2grds.Serv2Grds;
import data.serv2grds.Serv2GrdsDBup;
import grds.data.ServerData;
import grds.threads.ThreadCheckServs;
import grds.threads.ThreadMulticast;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Grds {
    /*
     * TODO:
     * Difundir informação relevante
     *
     * Informa via UDP os servidores sobre as coordenadas TCP do conjunto de servidores ativos
     * Refletor de informação relevante (Pedidos de contato, envio de mensagens e disponibilização de ficheiros
     * Comunica com os clientes via UDP
     *
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
        ThreadCheckServs threadCheckServs = new ThreadCheckServs(servers);
        threadCheckServs.start();
        System.out.println("============================== GRDS Ready ==============================");
        try {
            synchronized (threadReceivedServers) {
                threadReceivedServers.wait();
            }
            synchronized (threadMulticast) {
                threadMulticast.wait();
            }
            synchronized (threadCheckServs) {
                threadCheckServs.wait();
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
            MulticastSocket ms = null;
            InetAddress mulIP = null;
            try {
                ms = new MulticastSocket(Multicast.MULTICAST_GRDS_PORT_DIFFUSION);
                mulIP = InetAddress.getByName(Multicast.MULTICAST_GRDS_IP_DIFFUSION);
                InetSocketAddress isa = new InetSocketAddress(mulIP, Multicast.MULTICAST_GRDS_PORT_DIFFUSION);
                NetworkInterface ni = NetworkInterface.getByName("wlan1");
                ms.joinGroup(isa,ni);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

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
                                ServerData serv = new ServerData(datagramPacket.getAddress(), data.getPort());
                                synchronized (servers) {
                                    servers.add(serv); // Add server to list of active servers
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ObjectOutputStream oos = new ObjectOutputStream(baos);

                                oos.writeObject(serv.getIdentifier());

                                datagramPacket.setData(baos.toByteArray());
                                datagramPacket.setLength(baos.size());
                                datagramSocket.send(datagramPacket);

                                System.out.println("New Server Registered! --> " + datagramPacket.getAddress().getHostAddress() + ":" + data.getPort());
                            }
                            case PING -> {
                                System.out.println("Ping server "+data.getId()+"! --> " + datagramPacket.getAddress().getHostAddress());
                                for (ServerData serv : servers) {
                                    if (serv.getIdentifier() == data.getId())
                                        serv.pinged();
                                }
                            }
                            case BD_UPDATE -> {
                                if (systemReq instanceof Serv2GrdsDBup) {
                                    Serv2GrdsDBup db_update_data = (Serv2GrdsDBup) data;

                                    System.out.println("Difusion " + db_update_data.getType());
                                    if (ms != null) {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                                        oos.writeObject(db_update_data);
                                        oos.flush();

                                        byte [] send = baos.toByteArray();

                                        DatagramPacket dp = new DatagramPacket(send,send.length,mulIP, Multicast.MULTICAST_GRDS_PORT_DIFFUSION);
                                        ms.send(dp);
                                    }
                                }
                                else break;
                            }
                            case REMOVE_CLIENT -> {
                                for (ServerData serv : servers) {
                                    if (serv.getIdentifier() == ((Serv2Grds) systemReq).getId()) {
                                        serv.removeClient();
                                        System.out.println("["+serv.getIdentifier()+"] One client has been left...");
                                    }
                                }
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
                            ServerData serv;
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
        for (ServerData serv : servers) { //2 Servidores -> 1º -> 1 cli | 2ª -> 0 cli
            if (serv.getNumCli() < minCli && serv.getNumTimeouts() < 1) { //O nClientes no server for menor que o do server atribuido && nao tiver timeouts
                minCli = serv.getNumCli();
                servMin = serv;
            }
        }
        return servMin;
    }
}
