package client.Logic;

import data.Cli2Grds;
import data.cli2serv.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {

    private int grdsPort;
    private String grdsIp;
    private DatagramSocket ds;
    private Socket sCli = null;
    private ObjectOutputStream out2serv = null;
    private ObjectInputStream inServ = null;
    private String username = null;

    private ThreadServerTCP threadServerTCP;

    private boolean noServer = false;
    private boolean isLogged = false;


    public String getUsername() {
        return username;
    }

    public Client(String args[]) throws IOException {
        this.grdsIp = args[0];
        this.grdsPort = Integer.parseInt(args[1]);
        ds = new DatagramSocket();
        connect2serv();
    }

    public boolean getNoServer() {
        return noServer;
    }

    public void connect2serv() {
        try {
            inicialComsSend();
            inicialComsReceived();
        } catch (IOException e) {
            System.err.println("Error to connect to server");
            return;
        }
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
            System.err.println("No servers available. Try again later...");
            ds.close();
            noServer = true;
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(dpReceived.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);

        Cli2Grds infoServ = null;
        try {
            infoServ = (Cli2Grds) ois.readObject();
            sCli = new Socket(InetAddress.getLocalHost().getHostAddress(), infoServ.getPortIp());
            out2serv = new ObjectOutputStream(sCli.getOutputStream());
            inServ = new ObjectInputStream(sCli.getInputStream());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ds.close();
        }

        threadServerTCP = new ThreadServerTCP(this);

        Cli2ServTCPport TCPPort = new Cli2ServTCPport(threadServerTCP.getpPort());
        out2serv.writeObject(TCPPort);

        threadServerTCP.start();
    }

    public boolean login(String username, String password) {
        Cli2ServLog log = new Cli2ServLog(username, password);
        try {
            out2serv.writeObject(log);
            isLogged = (boolean) inServ.readObject();
            if (isLogged) this.username = username;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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

    public boolean editProfileName(String newName) {
        if (!isLogged) return false;

        Cli2ServChgProf prof = new Cli2ServChgProf(newName, username, Cli2ServChgProf.typeEdit.EDIT_NAME);
        boolean success = false;
        try {
            out2serv.writeObject(prof);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error editing name in profile in communication with server!");
        }
        return success;
    }

    public boolean editProfileUsername(String newUsername, String password) {
        if (!isLogged) return false;

        Cli2ServChgProf prof = new Cli2ServChgProf(username, newUsername, password, Cli2ServChgProf.typeEdit.EDIT_USERNAME);
        boolean success = false;
        try {
            out2serv.writeObject(prof);
            success = (boolean) inServ.readObject();
            if (success)
                username = newUsername;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error editing username in profile in communication with server");
        }
        return success;
    }

    public boolean editProfilePass(String password, String newPassword) {
        if (!isLogged) return false;

        Cli2ServChgProf prof = new Cli2ServChgProf(username, newPassword, password, Cli2ServChgProf.typeEdit.EDIT_PASSWORD);
        boolean success = false;
        try {
            out2serv.writeObject(prof);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error editing password in profile in communication with server");
        }
        return success;
    }

    public String searchUser(String username) {
        if (!isLogged) return null;

        Cli2ServSearch search = new Cli2ServSearch(username);
        ArrayList<String> success = null;
        String infoUsers = null;
        try {
            String aux = "";
            out2serv.writeObject(search);
            success = (ArrayList<String>) inServ.readObject();
            for (String info : success) {
                aux += info + "\n";
            }
            if (!aux.equals(""))
                infoUsers = aux;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error search username in communication with server");
        }
        return infoUsers;
    }

    public String contactList(String username) {
        if (!isLogged) return null;

        Cli2ServListContacts listContacts = new Cli2ServListContacts(username);
        ArrayList<String> success = null;
        String infoUsers = null;
        try {
            String aux = "";
            out2serv.writeObject(listContacts);
            success = (ArrayList<String>) inServ.readObject();
            for (String info : success) {
                aux += info + "\n";
            }
            if (!aux.equals(""))
                infoUsers = aux;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error listing contacts in communication with server");
        }
        return infoUsers;
    }


    public void exitServer() {
        try {
            threadServerTCP.setExit(true);
            out2serv.writeObject(new Cli2ServExit(username));
            inServ.readObject(); // Wait for response of server to close client
            threadServerTCP.getOisServ().close();
        } catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
    }

    public boolean addContact(String addUsername) {
        if (!isLogged) return false;

        Cli2ServAdd addContact = new Cli2ServAdd(username, addUsername);
        boolean success = false;
        try {
            out2serv.writeObject(addContact);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error adding an user in communication with server");
        }
        return success;
    }

    public boolean deleteContact(String usernameDel) {

        if (!isLogged) return false;

        Cli2ServDel deleContact = new Cli2ServDel(username,usernameDel);
        boolean success = false;
        try {
            out2serv.writeObject(deleContact);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error deleting contact in communication with server");
        }
        return success;
    }

    public String pendingContactList(String username) {
        if (!isLogged) return null;

        Cli2ServPendContact listContacts = new Cli2ServPendContact(username);
        ArrayList<String> success = null;
        String list = null;
        try {
            String aux = "";
            out2serv.writeObject(listContacts);
            success = (ArrayList<String>) inServ.readObject();
            for (String info : success) {
                aux += info + "\n";
            }
            if (!aux.equals(""))
                list = aux;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error listing pending contacts in communication with server");
        }
        return list;
    }
}
