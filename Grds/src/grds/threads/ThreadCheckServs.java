package grds.threads;

import InterfacesRMI.GetRemoteMeta3AppInterface;
import grds.data.ServerData;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThreadCheckServs extends Thread {      // check timeout servers
    private ArrayList<ServerData> servers;
    private boolean exit = false;
    private CopyOnWriteArrayList<GetRemoteMeta3AppInterface> observers;

    public ThreadCheckServs(ArrayList<ServerData> servers, CopyOnWriteArrayList<GetRemoteMeta3AppInterface> observers) {
        this.servers = servers;
        this.observers = observers;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }


    @Override
    public void run() {
        while (!exit) {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {}

            Iterator it = servers.iterator();
            while (it.hasNext()) {
                ServerData serv = (ServerData) it.next();
                if (serv.isTimeout()) {
                    System.out.println("Server "+ serv.getIdentifier() +" - "+ serv.getAddress().getHostAddress() + " has lost connection...");
                    for (GetRemoteMeta3AppInterface obs : observers) {
                        try {
                            obs.notify("Server "+ serv.getIdentifier() +" - "+ serv.getAddress().getHostAddress() + " has lost connection...");
                        } catch (RemoteException e) {
                            System.err.println("An error occurred notifying a Meta3App");
                        }
                    }
                    it.remove();
                }
            }
        }
    }
}
