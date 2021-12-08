package server.threads;

import server.utils.DB;

import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ThreadActivityClient extends Thread {

    private ArrayList<ThreadClient> clients;
    private boolean exit=false;
    private DB db;

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public ThreadActivityClient(ArrayList<ThreadClient> clients,DB db) {
        this.clients = clients;
        this.db = db;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                Thread.sleep(1000);

                for (ThreadClient client : clients) {
                    String username = client.getCliUsername();
                    if (client.isOffline() && username != null) {
                        db.updateState(username,false);
                    }
                }
            } catch (InterruptedException e) {
                System.err.println();
            }  catch (SQLException e) {
                System.err.println("Error on update user state to offline!");
            }
        }
    }
}
