package server;


import java.io.*;
import java.net.*;
import java.sql.SQLException;

import static server.utils.Constants.*;

public class Server{

    MulticastSocket ms;
    private int grdsPort;
    private String grdsIp;
    private String sgbdIP;


    public Server(String[] args) throws SQLException {
        if(args.length != 1){
            this.grdsIp = args[0];
            this.grdsPort = Integer.parseInt(args[1]);
            this.sgbdIP = args[2];
        } else{
            //todo Transmite um pedido multicast para o endereço 230.30.30.30 e Porto UDP
            // Senão transmitir resposta ao fim de três tentativas consecutivas, termina |
            // se obtiver resposta grava o endereço ip e o porto do GRDS
            try{
                ms = new MulticastSocket(MULTICAST_PORT);
                InetAddress ip = InetAddress.getByName(MULTICAST_IP);
                InetSocketAddress isa = new InetSocketAddress(ip,MULTICAST_PORT);
                NetworkInterface ni = NetworkInterface.getByName("wlan0");
                ms.joinGroup(isa,ni);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                System.out.println("Requesting GRDS Coordinates...");


                /* Enviamos uma mensagem a fazer um pedido e aguardamos por resposta */
                for(int i = 0; i < 3; i++ ){
                    String mensagem = "REQUEST";
                    oos.writeObject(mensagem);
                    oos.flush();
                    byte[] msgBytes = baos.toByteArray();
                    DatagramPacket dp = new DatagramPacket(msgBytes, msgBytes.length,ip,MULTICAST_PORT);
                    ms.send(dp);
                }
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
        }

        // Ligação à base de dados - Lança exceção para fora caso não consiga se conectar
        DB db = new DB();
    }
}