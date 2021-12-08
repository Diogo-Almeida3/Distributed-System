package server.threads;

import data.cli2serv.*;
import server.utils.DB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class ThreadClient extends Thread {

    private boolean exit = false;

    private Socket sCli;
    private DB db;

    private String cliUsername = null;

    private long lastTimeOn = Calendar.getInstance().getTimeInMillis();

    public String getCliUsername() {
        return cliUsername;
    }

    public boolean isOffline() {return Calendar.getInstance().getTimeInMillis() - lastTimeOn > 30 * 1000;}

    public ThreadClient(Socket sCli, DB db) {
        this.sCli = sCli;
        this.db = db;
    }

    public void refreshDB() {

    }

    public void setExit(boolean exit) {
        this.exit = exit;
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
                    }
                    case LIST_CONTACT -> {
                        Cli2ServListContacts listContacts = (Cli2ServListContacts) cliMessage;
                        db.listContacts(listContacts.getUsername());
                        //Todo Resolver o que pode ser feito
                    }
                    case DELETE_CONTACT -> {
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
                    case CONTACT_REQUEST -> {
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
                }
                lastTimeOn = Calendar.getInstance().getTimeInMillis();
                if (cliMessage.getRequestType() != Cli2Serv.RequestType.EXIT)
                    db.updateState(cliUsername,true);

                /* Enviar via udp ao grds a dizer que houve alterações */

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


