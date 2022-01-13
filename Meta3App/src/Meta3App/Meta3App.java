package Meta3App;

import InterfacesRMI.GetRemoteGrdsInterface;
import InterfacesRMI.GetRemoteMeta3AppInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Meta3App extends UnicastRemoteObject implements GetRemoteMeta3AppInterface {

    private static final Scanner sc = new Scanner(System.in);
    private boolean exit = false;

    protected Meta3App(String[] args) throws RemoteException, NotBoundException {
        if(args.length == 1){
            Registry registry = LocateRegistry.getRegistry(args[0],Registry.REGISTRY_PORT);
            GetRemoteGrdsInterface grdsInterface = (GetRemoteGrdsInterface) registry.lookup("GRDS_Service");

            registry.rebind("META3APP_SERVICE",this);

            textInterface(grdsInterface);
        }
        else
            throw new IllegalArgumentException("Invalid arguments! You must use one of the following formatting: <IP_MACHINE>");
    }

    @Override
    public void notify(String description) throws RemoteException {
        System.out.println("\nNotification: " + description);
    }

    @Override
    public void sendServersInfo(ArrayList<String> servers) throws RemoteException {
        System.out.println("\n\nAll servers info:");
        for (String server: servers) {
            System.out.println(server);
        }
    }

    public static void main(String[] args) {
        try{
            new Meta3App(args);
        } catch(IllegalArgumentException exception){
            System.err.println(exception.getMessage());
        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void textInterface(GetRemoteGrdsInterface grdsInterface ){
        boolean isReceivingNotification = false;
        try{
            while(!exit){
                System.out.println("Menu:");

                switch (askOption("Get all servers info", isReceivingNotification ? "Silence Notifications" : "Receive Notifications", "Exit")) {
                    case 0 -> exit = true;
                    case 1 -> grdsInterface.getAllServersInfo(this);
                    case 2 -> {
                        if (isReceivingNotification)
                            grdsInterface.removeObserver(this);
                        else
                            grdsInterface.addObserver(this);
                        isReceivingNotification = !isReceivingNotification;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int askOption(String... options) {
        int option;
        do {
            for (int i = 0; i < options.length-1; i++)
                System.out.printf("%3d - %s\n",i+1,options[i]);
            System.out.printf("\n%3d - %s\n",0,options[options.length-1]);
            option = askInt("\n> ");
        } while (option<0 || option>=options.length);
        return option;
    }

    public static int askInt(String question) {
        System.out.print(question);
        while (!sc.hasNextInt())
            sc.next();
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }
}
