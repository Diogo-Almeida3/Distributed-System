package client.threads;

import client.utils.Utils;
import data.cli2serv.Cli2Serv;
import data.cli2serv.Cli2ServLog;
import data.cli2serv.Cli2ServReg;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ThreadLoginRegist extends Thread {

    private Socket sCli;

    public ThreadLoginRegist(Socket socket) {
        this.sCli = socket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(sCli.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sCli.getInputStream());

            switch ( Utils.askOption("Registo","Login","Sair")){
                case 0:
                    break;
                case 1:
                    registry(oos);
                    break;
                case 2:
                    login(oos);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(ObjectOutputStream oos){
        Cli2ServLog log = new Cli2ServLog(Utils.askString("Username: "),Utils.askString("Password: "));
        try {
            oos.writeObject(log);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registry(ObjectOutputStream oos){

        Cli2ServReg reg = new Cli2ServReg(Utils.askString("Username: "),
                Utils.askString("Name: "),Utils.askString("Password: "));
        try {
            oos.writeObject(reg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}