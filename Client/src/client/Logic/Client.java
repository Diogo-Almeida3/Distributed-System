package client.Logic;

import client.UI.Text.UIClient;
import client.UI.Text.Utils;
import data.Cli2Grds;
import data.cli2serv.*;
import data.serv2cli.Serv2Cli;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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

    private UIClient ui;

    public String getUsername() {
        return username;
    }

    public Client(String args[], UIClient ui) throws IOException {
        this.grdsIp = args[0];
        this.grdsPort = Integer.parseInt(args[1]);
        this.ui = ui;
        ds = new DatagramSocket();
        if(!connect2serv())
            throw new ConnectException();
    }

    public boolean getNoServer() {
        return noServer;
    }

    public boolean connect2serv() {
        try {
            inicialComsSend();
            inicialComsReceived();
        } catch (IOException e) {
            System.err.println("Error to connect to server");
            return false;
        }
        return true;
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
        DatagramPacket dpReceived = new DatagramPacket(new byte[5000], 5000);
        ds.setSoTimeout(15 * 1000 );
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

        threadServerTCP = new ThreadServerTCP(this,ui);

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
        ArrayList<String> receivedFromServer = null;
        String infoUsers = null;
        try {
            StringBuilder searchInfo = new StringBuilder();
            out2serv.writeObject(search);
            receivedFromServer = (ArrayList<String>) inServ.readObject();
            for (String info : receivedFromServer) {
                searchInfo.append(info + "\n");
            }
            if (!searchInfo.equals(""))
                infoUsers = searchInfo.toString();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error search username in communication with server");
        }
        return infoUsers;
    }

    public String contactList() {
        if (!isLogged) return null;

        Cli2ServListContacts listContacts = new Cli2ServListContacts(username);
        ArrayList<String> receivedFromServer = null;
        String infoUsers = null;
        try {
            StringBuilder contactsList = new StringBuilder();
            out2serv.writeObject(listContacts);
            receivedFromServer = (ArrayList<String>) inServ.readObject();
            for (String info : receivedFromServer) {
                contactsList.append(info + "\n");
            }
            if (!contactsList.isEmpty())
                infoUsers = contactsList.toString();
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

    public boolean refuseContact(String usernameRef) {
        if (!isLogged) return false;
        Cli2ServRefuse refContact = new Cli2ServRefuse(username,usernameRef);
        boolean success = false;
        try {
            out2serv.writeObject(refContact);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error refuse an user in communication with server");
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

    public String pendingContactList() {
        if (!isLogged) return null;

        Cli2ServPendContact listContacts = new Cli2ServPendContact(username);
        ArrayList<String> recivedFromServ = null;
        try {
            StringBuilder pendingContactListBuilder = new StringBuilder();
            out2serv.writeObject(listContacts);
            recivedFromServ = (ArrayList<String>) inServ.readObject();
            for (String name : recivedFromServ) {
                pendingContactListBuilder.append(name + "\n");
            }
            if (!pendingContactListBuilder.isEmpty())
                return pendingContactListBuilder.toString();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error listing pending contacts in communication with server");
        }
        return null;
    }

    public boolean createGroup(String nameGroup) {
        if(!isLogged) return false;

        Cli2ServCreatGroup creatGroup = new Cli2ServCreatGroup(username, nameGroup);
        boolean success = false;
        try {
            out2serv.writeObject(creatGroup);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error creating group in communication with server");
        }
        return success;
    }

    public boolean joinGroup(int groupId) {
        if(!isLogged) return false;
        Cli2ServInvGroup creatGroup = new Cli2ServInvGroup(groupId);
        boolean success = false;
        try {
            out2serv.writeObject(creatGroup);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error trying to join a group in communication with server");
        }
        return success;
    }
    public boolean leaveGroup(int groupId) {
        if(!isLogged) return false;

        Cli2ServLeavGroup leavegroup = new Cli2ServLeavGroup(groupId);
        boolean success = false;
        try {
            out2serv.writeObject(leavegroup);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error trying to leave group in communication with server");
        }
        return success;
    }


    public String listGroups() {
    if (!isLogged) return null;

        Cli2ServListGroup listContacts = new Cli2ServListGroup();
        ArrayList<String> success = null;
        String infoGroups = null;
        try {
            String aux = "";
            out2serv.writeObject(listContacts);
            success = (ArrayList<String>) inServ.readObject();
            for (String info : success) {
                aux += info + "\n";
            }
            if (!aux.equals(""))
                infoGroups = aux;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error listing groups in communication with server");
        }
        return infoGroups;
    }


    public boolean renameGroup(int idGroup, String nameNewGroup) {
        if (!isLogged) return false;

        Cli2ServAdminGroup adminGroup = new Cli2ServAdminGroup(idGroup,username, Cli2ServAdminGroup.typeEdit.EDIT_NAME,nameNewGroup);
        boolean success = false;
        try {
            out2serv.writeObject(adminGroup);
            success = (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error editing name group in communication with server!");
        }
        return success;

    }

    public boolean deleteGroupMember(int idGroup, String nameMember){
        if(!isLogged) return false;

        Cli2ServAdminGroup adminGroup = new Cli2ServAdminGroup(idGroup,username,Cli2ServAdminGroup.typeEdit.DELETE_MEMBER,nameMember);
        boolean sucess = false;
        try{
            out2serv.writeObject(adminGroup);
            sucess = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error deleting member of group in communication with server!");
        }
        return sucess;
    }

    public boolean deleteGroup(int idGroup) {
        if(!isLogged) return false;

        Cli2ServAdminGroup adminGroup = new Cli2ServAdminGroup(idGroup,username,Cli2ServAdminGroup.typeEdit.DELETE_GROUP);
        boolean sucess = false;
        try{
            out2serv.writeObject(adminGroup);
            sucess = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error deleting group in communication with server!");
        }
        return sucess;
    }

    public boolean sendMessageTo(String receiver, String message) {
        if(!isLogged) return false;

        Cli2ServMsg send = new Cli2ServMsg(username,receiver,message);
        boolean success = false;
        try{
            out2serv.writeObject(send);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error on send message to "+receiver+" in communication with server!");
        }
        return success;
    }

    public boolean sendMessageTo(int groupId, String message) {
        if(!isLogged) return false;

        Cli2ServMsg send = new Cli2ServMsg(username,groupId,message);
        boolean success = false;
        try{
            out2serv.writeObject(send);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error on send message to group "+groupId+" in communication with server!");
        }
        return success;
    }

    public boolean deleteMessageTo(int idMessage){
        if(!isLogged) return false;

        Cli2ServDelMsg del = new Cli2ServDelMsg(username,idMessage);
        boolean success = false;
        try{
            out2serv.writeObject(del);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error on delete message with id "+idMessage+" in communication with server!");
        }
        return success;
    }

    public boolean acceptanceGroupMember(int idGroup, String nameMember) {
        if(!isLogged) return false;

        Cli2ServAdminGroup adminGroup = new Cli2ServAdminGroup(idGroup,username,Cli2ServAdminGroup.typeEdit.ACCEPT_MEMBER,nameMember);
        boolean success = false;
        try{
            out2serv.writeObject(adminGroup);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error acceptance member of group in communication with server!");
        }
        return success;
    }
    public boolean refuseGroupMember(int idGroup, String nameMember) {
        if(!isLogged) return false;

        Cli2ServAdminGroup adminGroup = new Cli2ServAdminGroup(idGroup,username,Cli2ServAdminGroup.typeEdit.REFUSE_MEMBER,nameMember);
        boolean success = false;
        try{
            out2serv.writeObject(adminGroup);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error acceptance member of group in communication with server!");
        }
        return success;
    }



    public String listWaitingMembers(int idGroup) {
        if (!isLogged) return null;

        Cli2ServAdminGroup list = new Cli2ServAdminGroup(idGroup,username,Cli2ServAdminGroup.typeEdit.WAITING_MEMBERS);
        ArrayList<String> members = null;
        String infoGroups = null;
        try {
            String aux = "";
            out2serv.writeObject(list);
            members = (ArrayList<String>) inServ.readObject();
            for (String info : members) {
                aux += info + "\n";
            }
            if (!aux.equals(""))
                infoGroups = aux;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error listing groups in communication with server");
        }
        return infoGroups;
    }

    public ArrayList<String> getContactsWithMessages() {
        if(!isLogged) return null;
        Cli2ServGetMsg send = new Cli2ServGetMsg(username, Cli2ServGetMsg.typeRequest.GET_CONTACTS);
        try {
            out2serv.writeObject(send);
            return (ArrayList<String>) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("An error occurred while trying to get the contacts with messages!");
        }
        return null;
    }

    public ArrayList<String> getGroupsWithMessages() {
        if(!isLogged) return null;
        Cli2ServGetMsg send = new Cli2ServGetMsg(username, Cli2ServGetMsg.typeRequest.GET_GROUPS);
        try {
            out2serv.writeObject(send);
            return (ArrayList<String>) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("An error occurred while trying to get the contacts with messages!");
        }
        return null;
    }

    public ArrayList<String> getMessagesFrom(String sender) {
        if(!isLogged) return null;
        Cli2ServGetMsg send = new Cli2ServGetMsg(sender,username);
        try {
            out2serv.writeObject(send);
            return (ArrayList<String>) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("An error occurred while trying to get the messages from "+sender+"!");
        }
        return null;
    }

    public ArrayList<String> getMessagesFromGroup(int groupId) {
        if(!isLogged) return null;
        Cli2ServGetMsg send = new Cli2ServGetMsg(username,groupId,username);
        try {
            out2serv.writeObject(send);
            return (ArrayList<String>) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("An error occurred while trying to get the messages from group "+groupId+"!");
        }
        return null;
    }

    public boolean sendFileTo(String receiver, String dir) throws IllegalArgumentException, IOException, ClassNotFoundException {
        if(!isLogged) return false;
        File f = new File(dir);

        if (!f.isFile()) // Test if the directory sent by the user is valid
            throw new IllegalArgumentException("This directory is not valid!");

        if (!isContact(username,receiver))
            throw new IllegalArgumentException("The contact name is not valid!");

        ThreadSendFile threadSendFile = new ThreadSendFile(out2serv,inServ,username,receiver,f);
        threadSendFile.start();
        return true;
    }


    public boolean sendFileTo(int idGroup,String dir){
        if(!isLogged) return false;
        File f = new File(dir);

        if(!f.isFile())
            throw new IllegalArgumentException("This directory is not valid!");

        if (!isContact(username,idGroup))
            throw new IllegalArgumentException("The group is not valid!");

        ThreadSendFile threadSendFileGroup = new ThreadSendFile(out2serv,inServ,username,idGroup,f);
        threadSendFileGroup.start();
        return false;
    }


    public boolean downloadFile(int id)throws IllegalArgumentException, IOException, ClassNotFoundException {
        if(!isLogged) return false;
        if (id < 0)
            throw new IllegalArgumentException();

        Cli2ServGetFile getFile = new Cli2ServGetFile(id);

        out2serv.writeObject(getFile);
        getFile = (Cli2ServGetFile) inServ.readObject();

        if (getFile.getDir() == null) // You sent a file name not recognized by the server
            throw new IllegalArgumentException();

        ThreadReceivedFiles threadReceivedFiles = new ThreadReceivedFiles(getFile.getServerIp(),getFile.getServerPort(),getFile.getDir(),username);
        threadReceivedFiles.start();
        return true;
    }

    public boolean downloadLastFileAvailable() {
        Serv2Cli lastNotification = threadServerTCP.getLastFileNotification();
        if (lastNotification == null) return false;

        String dir = lastNotification.getMessage();
        if (dir == null) return false;

        int id = Utils.getNumFromDir(dir);
        if (id < 0) return false;

        try {
            return downloadFile(id);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteFile(int idFile) {
        if(!isLogged) return false;

        Cli2ServDelFile delFile = new Cli2ServDelFile(username,idFile);
        boolean success = false;
        try{
            out2serv.writeObject(delFile);
            success = (boolean) inServ.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error on delete file with id "+idFile+" in communication with server!");
        }
        return success;

    }

    public boolean isContact(String name1, String name2) {
        try {
            out2serv.writeObject(new Cli2ServCkContact(name1,name2));
            return (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    public boolean isContact(String name1, int group) {
        try {
            out2serv.writeObject(new Cli2ServCkContact(name1,group));
            return (boolean) inServ.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

}
