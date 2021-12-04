package client.Logic;

import data.Cli2Grds;
import data.cli2serv.Cli2ServChgProf;
import data.cli2serv.Cli2ServExit;
import data.cli2serv.Cli2ServLog;
import data.cli2serv.Cli2ServReg;

import java.io.*;
import java.net.*;

public class Client {

    private int grdsPort;
    private String grdsIp;
    private DatagramSocket ds;
    private Socket sCli = null;
    private ObjectOutputStream out2serv = null;
    private ObjectInputStream inServ = null;
    private String username = null;

    private boolean isLogged = false;


    public String getUsername() {
        return username;
    }

    public Client(String args[]) throws IOException {
        this.grdsIp = args[0];
        this.grdsPort = Integer.parseInt(args[1]);
        ds = new DatagramSocket();
        inicialComsSend();
        inicialComsReceived();
    }


    public void inicialComsSend() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);

        Cli2Grds send2grds = new Cli2Grds();

        out.writeObject(send2grds); // send serialized object
        out.flush();

        byte[] req = baos.toByteArray();

        DatagramPacket dpSend = new DatagramPacket(req, req.length, InetAddress.getByName(grdsIp), grdsPort);
        ds.send(dpSend);


    }

    public void inicialComsReceived() throws IOException {
        DatagramPacket dpReceived = new DatagramPacket(new byte[5000], 5000); // TODO: Diminuir tamanho do array
        ds.receive(dpReceived);

        if (dpReceived.getLength() == 0) {
            System.err.println("No servers available...");
            ds.close();
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(dpReceived.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);

        Cli2Grds infoServ = null;
        try {
            infoServ = (Cli2Grds) ois.readObject();
            sCli = new Socket("localhost", infoServ.getPortIp());
            out2serv = new ObjectOutputStream(sCli.getOutputStream());
            inServ = new ObjectInputStream(sCli.getInputStream());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ds.close();
        }
    }

    public boolean login(String username, String password) {
        Cli2ServLog log = new Cli2ServLog(username, password);
        try {
            out2serv.writeObject(log);
            isLogged = (boolean) inServ.readObject();
            if (isLogged) this.username = username;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Login Error in communication with server!");
        }
        return isLogged;
    }

    public boolean register(String username, String name, String password) {
        Cli2ServReg reg = new Cli2ServReg(username, name, password);
        try {
            out2serv.writeObject(reg);
            isLogged = (boolean) inServ.readObject();
            if (isLogged) this.username = username;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error Register in communication with server!");
        }
        return isLogged;
    }

    public boolean editProfileName(String newName){
        if (!isLogged) return false;

        Cli2ServChgProf prof = new Cli2ServChgProf(username,newName, Cli2ServChgProf.typeEdit.EDIT_NAME);
        boolean success = false;
        try{
            out2serv.writeObject(prof);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.err.println("Error editing name in profile in communication with server!");
        }
        return success;
    }
    public boolean editProfileUsername(String newUsername){
        if (!isLogged) return false;

        Cli2ServChgProf prof = new Cli2ServChgProf(username,newUsername,Cli2ServChgProf.typeEdit.EDIT_USERNAME);
        boolean success = false;
        try{
            out2serv.writeObject(prof);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error editing username in profile in communication with server");
        }
        return success;
    }

    public boolean editProfilePass(String password,String newPassword){
        if (!isLogged) return false;

        Cli2ServChgProf prof = new Cli2ServChgProf(username,password,newPassword,Cli2ServChgProf.typeEdit.EDIT_USERNAME);
        boolean success = false;
        try{
            out2serv.writeObject(prof);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error editing password in profile in communication with server");
        }
        return success;
    }

    public void exitServer() {
        try {
            out2serv.writeObject(new Cli2ServExit(username));
            inServ.readObject(); // Wait for response of server to close client
        } catch (IOException | ClassNotFoundException e) {
           e.getMessage();
        }
    }
}
