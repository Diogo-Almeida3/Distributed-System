package grds.threads;

import grds.data.ServerData;

import java.util.ArrayList;
import java.util.Iterator;

public class ThreadCheckServs extends Thread {      // check timeout servers
    private ArrayList<ServerData> servers;
    private boolean exit = false;

    public ThreadCheckServs(ArrayList<ServerData> servers) {
        this.servers = servers;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }


    @Override
    public void run() {
        while (!exit) {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {}

            Iterator it = servers.iterator();
            while (it.hasNext()) {
                ServerData serv = (ServerData) it.next();
                if (serv.isTimeout()) {
                    System.out.println("Server "+ serv.getIdentifier() +" - "+ serv.getAddress().getHostAddress() + " has lost connection...");
                    it.remove();
                }
            }
        }
    }
}
