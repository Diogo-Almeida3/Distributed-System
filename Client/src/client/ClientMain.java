package client;

import client.Logic.Client;
import client.UI.Text.UIText;

import java.io.IOException;

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
        } catch (IllegalArgumentException | IOException e){
            System.err.println(e.getMessage());
        }
    }
}
