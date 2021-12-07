package server.threads;

import data.cli2serv.*;
import server.utils.DB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

public class ThreadClient extends Thread {

    private boolean exit = false;

    private Socket sCli;
    private DB db;

    private String cliUsername = null;

    public ThreadClient(Socket sCli, DB db) {
        this.sCli = sCli;
        this.db = db;
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
                    }
                    case LIST_USERS -> {
                    }
                    case ADD_CONTACT -> {
                    }
                    case LIST_CONTACT -> {
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
//                        try {
                            exit = true;
//                            db.updateState(cli2ServExit.getUsername(), false); //TODO: Verificar se é aqui que ele mudar o username
                            System.out.println("User: " + cli2ServExit.getUsername() + " left...");
//                            oos.writeObject(true);
//                            break;
//                        } catch (SQLException e) {
//                            System.err.println("Error while login...");
//                        }
                        // TODO: Informar GRDS que este servidor tem menos um cliente
                    }
                }
            }
            catch (SocketException e) {
                if (!exit) {
                    try {
                        db.updateState(cliUsername, false);
                        System.out.println("User: " + cliUsername + " asdas ddebug teste  7-12has lost connection...");
                    } catch (SQLException exception) {
                        System.err.println("Error while login...");
                    }
                    setExit(true);
                    break;
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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


