package client.UI.Text;

import client.Logic.Client;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class UIText {
    Client logic;
    private boolean exit = false;

    public UIText(Client logic) {

        this.logic = logic;
        try {
            logic.inicialComsSend();
            logic.inicialComsReceived();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        menuLoginReg();
        while(!exit){
            switch ( Utils.askOption("Edit Profile", "Send Messages", "Contact List",
                    "Pending contact requests","Add Contact",
                    "Delete Contact","Create Group","Exit")){
                case 0 -> {
                    logic.exitServer();
                    exit = true;
                }
                case 1 ->
                        userProfile();
                case 2 ->
                        sendMessages();
                case 3 ->
                        contactList();
                case 4 ->
                        pendingContactRequest();
                case 5 ->
                        addContact();
                case 6 ->
                        deleteContact();
                case 7 ->
                        createGroup();
            }
        }

    }

    private void menuLoginReg() {
        switch (Utils.askOption("Login","Register","Exit")) {
            case 0 -> exit = true;
            case 1 -> {
                boolean success = false;
                int attempts = 0;

                do {
                    success = logic.login(Utils.askString("Username: "), Utils.askString("Password: "));

                    if(success)
                        System.out.println("Login with success!");
                    else {
                        System.out.println("Username or password entered is invalid!");
                        if (++attempts >= 3) {
                            exit = true;
                            break;
                        }
                    }
                } while (!success);
            }
            case 2 -> logic.register(Utils.askString("Username: "), Utils.askString("Name: "), Utils.askString("Password: "));
        }
    }

    private void userProfile(){
        switch (Utils.askOption("Edit Name","Edit Username","Edit password","Go Back")){
            case 0->{

            }

            case 1->{
                    if(logic.editProfileName(Utils.askString("Enter a new name: ")))
                        System.out.println("Success editing profile name");
                    else
                        System.out.println("Failed editing profile name");

            }
            case 2->{
                    if(logic.editProfileUsername(Utils.askString("Enter a new Username: "),Utils.askString("Enter your password: ")))
                        System.out.println("Success editing profile username");
                    else
                        System.out.println("Failed editing profile username");

            }
            case 3->{
                if(logic.editProfilePass(Utils.askString("Enter a old password: "),Utils.askString("Enter a new password: ")))
                    System.out.println("Success editing profile password");
                else
                    System.out.println("Failed editing profile password");
            }

        }
    }

    private void sendMessages(){

    }

    private void contactList(){

    }

    private void pendingContactRequest(){

    }
    private void addContact(){

    }

    private void deleteContact(){

    }

    private void createGroup(){

    }

}
