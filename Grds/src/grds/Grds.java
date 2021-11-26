package grds;

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



//    private class ServerData

}
