package client;

import client.Logic.Client;
import client.UI.Text.UIText;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

public class ClientMain {
    public static void main(String[] args) {
        try{
            if(args.length == 2) {
                UIText ui = new UIText();
                Client logic = new Client(args,ui);
                if(logic.getNoServer())
                    return;
                ui.setLogic(logic);
                ui.run();
            }
            else
                throw new IllegalArgumentException("Invalid arguments! You must use one of the following formatting: <IP_GRDS> <Port_GRDS>");
        } catch (ConnectException e) {
            System.err.println("It is not possible to run the server since not all initial conditions are met!");
        }
        catch (IllegalArgumentException | IOException e){
            System.err.println(e.getMessage());
        }
    }
}
