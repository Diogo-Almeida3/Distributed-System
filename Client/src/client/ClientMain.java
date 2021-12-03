package client;

import client.Logic.Client;
import client.UI.Text.UIText;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        try{
            if(args.length == 2) {
                Client logic = new Client(args);
                UIText ui = new UIText(logic);
                ui.run();
            }
            else
                throw new IllegalArgumentException("Invalid arguments! You must use one of the following formatting: <IP_GRDS> <Port_GRDS>");
        } catch (IllegalArgumentException | IOException e){
            System.err.println(e.getMessage());
        }
    }
}
