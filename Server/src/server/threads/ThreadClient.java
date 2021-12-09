package server.threads;

import data.Serv2Grds;
import data.cli2serv.*;
import data.serv2cli.Serv2Cli;
import server.utils.DB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class ThreadClient extends Thread {

    private boolean exit = false;

    private Socket sCli;

    private Socket socketSend2Cli;
    private OutputStream os2Cli;
    private ObjectOutputStream oos2Cli;

    private DB db;

    private String cliUsername = null;

    private long lastTimeOn = Calendar.getInstance().getTimeInMillis();

    public String getCliUsername() {
        return cliUsername;
    }

    public boolean isOffline() {return Calendar.getInstance().getTimeInMillis() - lastTimeOn > 30 * 1000;}

    public ThreadClient(Socket sCli, DB db) throws IOException {
        this.sCli = sCli;
        this.db = db;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void notification(Serv2Cli.Request request) throws IOException {
        Serv2Cli send = new Serv2Cli(request);
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
                //todo quando acontece um update na db o sv tem de conseguir avisar todos os outros desse update

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
                            } else {
                                oos.writeObject(false);
                                System.out.println("User failed regist...");
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
                        boolean checkNull=false ;
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
                            if(checkNull){
                                oos.writeObject(true);
                                System.out.println("Edit successfully...");
                            } else{
                                oos.writeObject(false);
                                System.out.println("Edit failed...");
                            }

                        } catch (SQLException e) {
                            System.err.println("Error while editing...");
                        } catch (SocketException e) {
                            setExit(true);
                        }
                    }
                    case SEARCH_USER -> {
                        Cli2ServSearch search = (Cli2ServSearch) cliMessage;
                        ArrayList<String> info = db.searchUser(search.getUsername());
                        oos.writeObject(info);

                    }
                    case ADD_CONTACT -> {
                        Cli2ServAdd contact = (Cli2ServAdd) cliMessage;

                        if (db.addContact(contact.getUsername(), contact.getAddUsername())){
                            oos.writeObject(true);
                            System.out.println("Invite from " + contact.getUsername() + " to " + contact.getAddUsername());
                        } else {
                            oos.writeObject(false);
                            System.out.println("User failed login...");
                        }
                    }
                    case LIST_CONTACT -> {
                        Cli2ServListContacts listContacts = (Cli2ServListContacts) cliMessage;
                        ArrayList<String> info = db.listContacts(listContacts.getUsername());
                        oos.writeObject(info);
                    }
                    case DELETE_CONTACT -> {
                        Cli2ServDel deleContact = (Cli2ServDel) cliMessage;

                        if(db.deleteContact(deleContact.getUsername(),deleContact.getUsernameDel())){
                            oos.writeObject(true);
                            System.out.println("Invite from " + deleContact.getUsername() + " to " + deleContact.getUsernameDel());
                        } else {
                            oos.writeObject(false);
                            System.out.println("User failed login...");
                        }
                    }
                    case CREATE_GROUP -> {
                    }
                    case JOIN_GROUP -> {
                    }
                    case LIST_GROUPS -> {
                    }
                    case EDIT_GROUP -> {
                    }
                    case LEAVE_GROUP -> {
                    }
                    case LIST_REQUESTS -> {
                        Cli2ServPendContact pendContact = (Cli2ServPendContact) cliMessage;
                        ArrayList<String> info = db.listPendingContacts(pendContact.getUsername());
                        oos.writeObject(info);
                    }
                    case SEND_MESSAGE -> {
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
                        // TODO: Informar GRDS que este servidor tem menos um cliente
                    }
                    case TCP_PORT -> {
                        Cli2ServTCPport cli2ServTCPport = (Cli2ServTCPport) cliMessage;
                        socketSend2Cli = new Socket(InetAddress.getLocalHost().getHostAddress(),cli2ServTCPport.getPort());
                        os2Cli = socketSend2Cli.getOutputStream();
                        oos2Cli = new ObjectOutputStream(os2Cli);
                    }
                }
                lastTimeOn = Calendar.getInstance().getTimeInMillis();
                if (cliMessage.getRequestType() != Cli2Serv.RequestType.EXIT)
                    db.updateState(cliUsername,true);

                /* Send via udp to the group saying that there were changes */

            }
            catch (SocketException e) {
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
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
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


