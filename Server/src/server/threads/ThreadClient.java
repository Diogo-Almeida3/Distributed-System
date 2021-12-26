package server.threads;

import Constants.Notification;
import data.serv2grds.Serv2Grds;
import data.cli2serv.*;
import data.serv2cli.Serv2Cli;
import data.serv2grds.Serv2GrdsDBup;
import server.utils.DB;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ThreadClient extends Thread {


    private static final String serverDirectory = "./Files/"+ ManagementFactory.getRuntimeMXBean().getName();

    private boolean exit = false;

    private ThreadPing threadPing;

    private Socket sCli;

    private Socket socketSend2Cli;
    private OutputStream os2Cli;
    private ObjectOutputStream oos2Cli;

    private String grdsIp;
    private int grdsPort;

    private DB db;

    private String cliUsername = null;

    private long lastTimeOn = Calendar.getInstance().getTimeInMillis();

    private ServerSocket socket2grds;

    public String getCliUsername() {
        return cliUsername;
    }

    public boolean isOffline() {
        return Calendar.getInstance().getTimeInMillis() - lastTimeOn > 30 * 1000;
    }

    private int idFilesRequest = 0;
    HashMap<Integer, FileOutputStream> downloadFiles = new HashMap<>();

    ThreadSendFiles threadSendFiles;

    public ThreadClient(Socket sCli, DB db, String grdsIp, int grdsPort, ThreadPing threadPing, ThreadSendFiles threadSendFiles) throws IOException {
        this.sCli = sCli;
        this.db = db;
        this.grdsIp = grdsIp;
        this.grdsPort = grdsPort;
        this.threadPing = threadPing;
        this.socket2grds = new ServerSocket(0);
        this.threadSendFiles = threadSendFiles;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void notification(Notification request) throws IOException {
        Serv2Cli send = new Serv2Cli(request);
        oos2Cli.writeObject(send);
    }

    private int servID() {
        return threadPing.getIdServ();
    }

    private void send2GRDS(Serv2Grds info2send) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(info2send);
            oos.flush();

            DatagramPacket dpResp = new DatagramPacket(baos.toByteArray(), baos.size(),
                    InetAddress.getByName(grdsIp), grdsPort);
            ds.send(dpResp);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Cli2Serv cliMessage = null;

        try {
            oos = new ObjectOutputStream(sCli.getOutputStream());
            ois = new ObjectInputStream(sCli.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!exit) {
            try {
                cliMessage = (Cli2Serv) ois.readObject();
                switch (cliMessage.getRequestType()) {
                    case REGISTER -> {
                        Cli2ServReg regData = (Cli2ServReg) cliMessage;
                        cliUsername = regData.getUsername();
                        try {
                            if (db.registUser(regData.getUsername(), regData.getName(), regData.getPassword())) {
                                db.updateState(cliUsername, true);
                                oos.writeObject(true);
                                System.out.println("User: " + cliUsername + "registed successfully");
                                send2GRDS(new Serv2GrdsDBup(Notification.USER_LOGIN));
                            } else {
                                oos.writeObject(false);
                                System.out.println("User failed registed...");
                            }
                        } catch (SQLException e) {
                            System.err.println("Error while registering a new user...");
                        } catch (SocketException e) {
                            setExit(true);
                        }
                    }
                    case LOGIN -> {
                        Cli2ServLog logData = (Cli2ServLog) cliMessage;
                        cliUsername = logData.getUsername();
                        try {
                            if (db.loginUser(cliUsername, logData.getPassword())) {
                                db.updateState(cliUsername, true);
                                oos.writeObject(true);
                                System.out.println("User: " + cliUsername + " logged successfully");
                                send2GRDS(new Serv2GrdsDBup(Notification.USER_LOGIN));
                            } else {
                                oos.writeObject(false);
                                System.out.println("User failed login...");
                            }
                        } catch (SQLException e) {
                            System.err.println("Error while login...");
                        } catch (SocketException e) {
                            setExit(true);
                        }
                    }
                    case EDIT_USER -> {
                        Cli2ServChgProf profData = (Cli2ServChgProf) cliMessage;
                        boolean checkNull = false;
                        try {
                            switch (profData.getEditReq()) {
                                case EDIT_NAME -> {
                                    checkNull = db.editName(profData.getNewName(), profData.getOldUsername());
                                }
                                case EDIT_PASSWORD -> {
                                    checkNull = db.editPassword(profData.getNewPassword(), profData.getOldPassword(), profData.getOldUsername());
                                }
                                case EDIT_USERNAME -> {
                                    checkNull = db.editUsername(profData.getNewUsername(), profData.getOldUsername(), profData.getOldPassword());
                                }
                            }
                            if (checkNull) {
                                oos.writeObject(true);
                                System.out.println("Edit successfully...");
                                send2GRDS(new Serv2GrdsDBup(Notification.EDIT_USER));
                            } else {
                                oos.writeObject(false);
                                System.out.println("Edit failed...");
                            }

                        } catch (SQLException e) {
                            System.err.println("Error while editing...");
                        } catch (SocketException e) {
                            setExit(true);
                        }
                    }
                    case CK_CONTACT -> {
                        Cli2ServCkContact names = (Cli2ServCkContact) cliMessage;
                        if (names.isCkGroup())
                            oos.writeObject(db.isContact(names.getName1(),names.getGroupId()));
                        else
                            oos.writeObject(db.isContact(names.getName1(),names.getName2()));
                    }
                    case SEARCH_USER -> {
                        Cli2ServSearch search = (Cli2ServSearch) cliMessage;
                        ArrayList<String> info = db.searchUser(search.getUsername());
                        oos.writeObject(info);

                    }
                    case ADD_CONTACT -> {
                        Cli2ServAdd contact = (Cli2ServAdd) cliMessage;

                        if (db.addContact(contact.getUsername(), contact.getAddUsername())) {
                            oos.writeObject(true);
                            send2GRDS(new Serv2GrdsDBup(Notification.CONTACT_REQUEST,contact.getAddUsername()));
                            System.out.println("Invite from " + contact.getUsername() + " to " + contact.getAddUsername());
                        } else {
                            oos.writeObject(false);
                            System.out.println("User failed add contact...");
                        }
                    }
                    case REFUSE_CONTACT ->{
                        Cli2ServRefuse refuseContact =(Cli2ServRefuse) cliMessage;
                        if(db.refuseContact(refuseContact.getUsername(),refuseContact.getRefuseUsername())){
                            oos.writeObject(true);
                            // todo:send notification to contact
                            System.out.println("Refuse from " + refuseContact.getUsername() + " to " + refuseContact.getRefuseUsername());
                        }
                        else{
                            oos.writeObject(false);
                            System.out.println("User failed refuse contact...");
                        }
                    }
                    case LIST_CONTACT -> {
                        Cli2ServListContacts listContacts = (Cli2ServListContacts) cliMessage;
                        ArrayList<String> info = db.listContacts(listContacts.getUsername());
                        oos.writeObject(info);
                    }
                    case DELETE_CONTACT -> {
                        Cli2ServDel deleContact = (Cli2ServDel) cliMessage;

                        if (db.deleteContact(deleContact.getUsername(), deleContact.getUsernameDel())) {
                            oos.writeObject(true);
                            send2GRDS(new Serv2GrdsDBup(Notification.CONTACT_DELETE, deleContact.getUsernameDel()));
                            System.out.println("Invite from " + deleContact.getUsername() + " to " + deleContact.getUsernameDel());
                        } else {
                            oos.writeObject(false);
                            System.out.println("User failed delete contact...");
                        }
                    }
                    case CREATE_GROUP -> {
                        Cli2ServCreatGroup creatGroup = (Cli2ServCreatGroup) cliMessage;
                        oos.writeObject(db.createGroup(creatGroup.getUsername(), creatGroup.getGroupName()));
                    }
                    case JOIN_GROUP -> {
                        Cli2ServInvGroup cli2ServInvGroup = (Cli2ServInvGroup) cliMessage;
                        if (db.joinGroup(cliUsername, cli2ServInvGroup.getGroupID())) {
                            oos.writeObject(true);
                            String adminName = db.getGroupAdminUsername(cli2ServInvGroup.getGroupID());
                            send2GRDS(new Serv2GrdsDBup(Notification.JOIN_GROUP_REQUEST, adminName));
                        } else oos.writeObject(false);
                    }
                    case LIST_GROUPS -> {
                        Cli2ServListGroup cli2ServListGroup = (Cli2ServListGroup) cliMessage;
                        ArrayList<String> info = db.listGroups();
                        oos.writeObject(info);
                    }
                    case LEAVE_GROUP -> {
                        Cli2ServLeavGroup cli2ServLeavGroup = (Cli2ServLeavGroup) cliMessage;

                        if (db.leaveGroup(cliUsername, cli2ServLeavGroup.getIdGroup())) {
                            oos.writeObject(true);
                            String adminName = db.getGroupAdminUsername(cli2ServLeavGroup.getIdGroup());
                            send2GRDS(new Serv2GrdsDBup(Notification.LEAVE_GROUP,adminName));
                        } else oos.writeObject(false);
                    }
                    case LIST_REQUESTS -> {
                        Cli2ServPendContact pendContact = (Cli2ServPendContact) cliMessage;
                        ArrayList<String> info = db.listPendingContacts(pendContact.getUsername());
                        oos.writeObject(info);
                    }
                    case SEND_MESSAGE -> {
                        Cli2ServMsg message = (Cli2ServMsg) cliMessage;
                        boolean success;
                        if (message.isSend2group()) // Send messages to one group
                            success = db.sendMessage2Group(message.getSender(),message.getGroupID(),message.getMessage());
                        else  // Send messages to one user
                            success = db.sendMessage(message.getSender(),message.getReceiver(),message.getMessage());

                        if(success) {
                            oos.writeObject(true);
                            if (message.isSend2group())
                                send2GRDS(new Serv2GrdsDBup(Notification.MESSAGE, db.getGroupUsers(((Cli2ServMsg) cliMessage).getGroupID())));
                            else
                                send2GRDS(new Serv2GrdsDBup(Notification.MESSAGE, message.getReceiver()));
                        } else oos.writeObject(false);
                    }
                    case DELETE_MESSAGE -> {
                        Cli2ServDelMsg delMsg =(Cli2ServDelMsg) cliMessage;

                        String name = db.getReceiverDeleteMessage(delMsg.getIdMessage()); // Gets the receiver's name or the id of the group to whom the message was sent

                        if(db.deleteMessage(delMsg.getUsername(),delMsg.getIdMessage())) { // Delete message
                            if (name.startsWith("Group:")) { // Is group
                                int groupId = Integer.parseInt(name.substring(6));
                                send2GRDS(new Serv2GrdsDBup(Notification.MESSAGE_DELETE, db.getGroupUsers(groupId)));
                            }
                            else // Is user
                                send2GRDS(new Serv2GrdsDBup(Notification.MESSAGE_DELETE,name));
                            oos.writeObject(true);
                        }
                        else {
                            oos.writeObject(false);
                        }
                    }
                    case GET_MESSAGES -> {
                        Cli2ServGetMsg request = (Cli2ServGetMsg) cliMessage;

                        switch (request.getRequestInfo()) {
                            case GET_CONTACTS -> oos.writeObject(db.listContactsWithMessages(request.getReceiver()));

                            case GET_GROUPS -> oos.writeObject(db.listGroupsWithMessages(request.getReceiver()));

                            case GET_MSG_FROM_USER ->  oos.writeObject(db.getMessages(request.getSender(), request.getReceiver(),30));

                            case GET_MSG_FROM_GROUP -> oos.writeObject(db.getMessagesFromGroup(request.getSender(),request.getGroupId(),30));
                        }
                    }
                    case ADMIN_GROUP -> {
                        Cli2ServAdminGroup cli2ServAdminGroup = (Cli2ServAdminGroup) cliMessage;

                        /* If the user requesting is the group admin advance else return unsucessful */
                        if (db.getGroupAdminBool(cli2ServAdminGroup.getIdGroup(), cli2ServAdminGroup.getUsername())) {
                            switch (cli2ServAdminGroup.getTypeEdit()) {
                                case EDIT_NAME-> {
                                    if(db.editGroupName(cli2ServAdminGroup.getIdGroup(),cli2ServAdminGroup.getNameNewGroup())) {
                                        send2GRDS(new Serv2GrdsDBup(Notification.EDIT_GROUP, db.getGroupUsers(cli2ServAdminGroup.getIdGroup())));
                                        oos.writeObject(true);
                                    }
                                    else
                                        oos.writeObject(false);

                                }
                                case DELETE_MEMBER -> {
                                    if(db.kickGroupMember(cli2ServAdminGroup.getIdGroup(),cli2ServAdminGroup.getUserKick())) {
                                        send2GRDS(new Serv2GrdsDBup(Notification.LEAVE_GROUP, cli2ServAdminGroup.getUserKick()));
                                        oos.writeObject(true);
                                    }
                                    else
                                        oos.writeObject(false);
                                }
                                case ACCEPT_MEMBER -> {
                                    if(db.acceptMember(cli2ServAdminGroup.getIdGroup(),cli2ServAdminGroup.getAcceptUser())) {
                                        send2GRDS(new Serv2GrdsDBup(Notification.ACCEPT_MEMBER,cli2ServAdminGroup.getAcceptUser()));
                                        oos.writeObject(true);
                                    }
                                    else
                                        oos.writeObject(false);
                                }
                                case REFUSE_MEMBER -> {
                                    if(db.refuseMember(cli2ServAdminGroup.getIdGroup(),cli2ServAdminGroup.getRefuseUser())) {
                                        send2GRDS(new Serv2GrdsDBup(Notification.JOIN_GROUP_REQ_POS_RESPONSE,cli2ServAdminGroup.getAcceptUser()));
                                        oos.writeObject(true);
                                    }
                                    else
                                        oos.writeObject(false);
                                }
                                case DELETE_GROUP -> {
                                    ArrayList<String> tempUsers2Notify = db.getGroupUsers(cli2ServAdminGroup.getIdGroup());
                                    if(db.deleteGroup(cli2ServAdminGroup.getIdGroup())) {
                                        send2GRDS(new Serv2GrdsDBup(Notification.GROUP_DELETE, tempUsers2Notify));
                                        oos.writeObject(true);
                                    }
                                    else
                                        oos.writeObject(false);
                                }
                                case WAITING_MEMBERS -> {
                                    ArrayList<String> info = db.listGroupWaitingList(cli2ServAdminGroup.getIdGroup());
                                    oos.writeObject(info);

                                }
                            }
                        }
                        else
                            oos.writeObject(false);
                    }
                    case EXIT -> {
                        Cli2ServExit cli2ServExit = (Cli2ServExit) cliMessage;
                        try {
                            exit = true;
                            db.updateState(cli2ServExit.getUsername(), false);
                            System.out.println("User: " + cli2ServExit.getUsername() + " left...");
                            oos.writeObject(true);
                            break;
                        } catch (SQLException e) {
                            System.err.println("Error while login...");
                        }
                    }
                    case TCP_PORT -> {
                        Cli2ServTCPport cli2ServTCPport = (Cli2ServTCPport) cliMessage;
                        socketSend2Cli = new Socket(InetAddress.getLocalHost().getHostAddress(), cli2ServTCPport.getPort());
                        os2Cli = socketSend2Cli.getOutputStream();
                        oos2Cli = new ObjectOutputStream(os2Cli);
                    }
                    case SEND_FILE -> {
                        Cli2ServFile cli2ServFile = (Cli2ServFile) cliMessage;
                        int id = cli2ServFile.getIdOfRequest();

                        if (downloadFiles.containsKey(id)) // A directory for this file already exists and is already being constructed
                        {
                            byte[] filechunk = cli2ServFile.getFilePart();
                            if (filechunk.length == 0) { // File fully received
                                FileOutputStream fos = downloadFiles.get(id);
                                fos.flush();
                                fos.close();
                                downloadFiles.remove(id);

                                if (cli2ServFile.isSend2group()) {
                                    ArrayList<String> users2notify = db.getGroupUsers(cli2ServFile.getGroupID());
                                    String[] aux = users2notify.toArray(new String[users2notify.size()]);

                                    send2GRDS(new Serv2GrdsDBup(Notification.NEW_FILE_AVAILABLE, threadSendFiles.getIp(), threadSendFiles.getPort(),id,aux));
                                }
                                else
                                    send2GRDS(new Serv2GrdsDBup(Notification.NEW_FILE_AVAILABLE, threadSendFiles.getIp(), threadSendFiles.getPort(),id,cli2ServFile.getReceiver()));
                            }
                            else  // File under construction
                                downloadFiles.get(id).write(filechunk);
                        } else if (id == -1){ // Create directory for the file and add to hashmap
                            String sender = cli2ServFile.getSender();
                            String fileName = cli2ServFile.getFilename();

                            int idOfFile;
                            if (cli2ServFile.isSend2group())
                                idOfFile = db.sendFile(sender,cli2ServFile.getGroupID(),cli2ServFile.getFilename());
                            else
                                idOfFile = db.sendFile(sender,cli2ServFile.getReceiver(),cli2ServFile.getFilename());

                            oos.writeObject(idOfFile);

                            File file;

                            if (cli2ServFile.isSend2group()) {
                                file = new File(serverDirectory +"/Group-"+ cli2ServFile.getGroupID() + "/"+idOfFile+"-"+fileName);
                            } else {
                                file = new File(serverDirectory +'/'+sender + "/"+idOfFile+"-"+fileName);
                            }

                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            downloadFiles.put(idOfFile, new FileOutputStream(file));
                            FileOutputStream fos = downloadFiles.get(idOfFile);
                            fos.write(cli2ServFile.getFilePart());
                        }
                    }
                    case GET_FILE -> {
                        Cli2ServGetFile cli2ServGetFile = (Cli2ServGetFile) cliMessage;

                        String [] aux = cli2ServGetFile.getFilename().split("-");
                        int messageId = Integer.parseInt(aux[0]);

                        cli2ServGetFile = new Cli2ServGetFile(threadSendFiles.getIp(),threadSendFiles.getPort(),db.getFileDirectory(messageId));

                        oos.writeObject(cli2ServGetFile);
                    }
                    case DELETE_FILE ->  {
                        Cli2ServDelFile delFile =(Cli2ServDelFile) cliMessage;

                        String name = db.getReceiverDeleteFile(delFile.getIdFile()); // Gets the receiver's name or the id of the group to whom the file was sent
                        String dirOfFile = db.getFileDirectory(delFile.getIdFile());
                        if(db.deleteFile(delFile.getUsername(),delFile.getIdFile())) { // Delete file
                            if (name.startsWith("Group:")) { // Is group
                                int groupId = Integer.parseInt(name.substring(6));
                                Serv2GrdsDBup serv2GrdsDBup = new Serv2GrdsDBup(Notification.FILE_DELETE, db.getGroupUsers(groupId));
                                serv2GrdsDBup.putExtra(dirOfFile);
                                send2GRDS(serv2GrdsDBup);
                            }
                            else { // Is user
                                Serv2GrdsDBup serv2GrdsDBup = new Serv2GrdsDBup(Notification.FILE_DELETE,name);
                                serv2GrdsDBup.putExtra(dirOfFile);
                                send2GRDS(serv2GrdsDBup);
                            }
                            oos.writeObject(true);
                        }
                        else {
                            oos.writeObject(false);
                        }
                    }
                }
                lastTimeOn = Calendar.getInstance().getTimeInMillis();
                if (cliMessage.getRequestType() != Cli2Serv.RequestType.EXIT)
                    db.updateState(cliUsername, true);

                /* Send via udp to the group saying that there were changes */

            } catch (SocketException e) {
                if (!exit) {
                    try {
                        db.updateState(cliUsername, false);
                        System.out.println("User: " + cliUsername + " has lost connection...");
                    } catch (SQLException exception) {
                        System.err.println("Error while login...");
                    }
                    setExit(true);
                    break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        // Enviar  via UDP ao grds que o cliente saiu
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            Serv2Grds serv2Grds = new Serv2Grds(Serv2Grds.Request.REMOVE_CLIENT, servID());
            out.writeObject(serv2Grds);
            out.flush();

            byte[] req = baos.toByteArray();

            DatagramPacket reqSend = new DatagramPacket(req, req.length, InetAddress.getByName(grdsIp), grdsPort);
            ds.send(reqSend);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (oos != null)
                oos.close();
            if (ois != null)
                ois.close();
            if (sCli != null)
                sCli.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


