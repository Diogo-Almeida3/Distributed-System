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
    }

    public void run(){
        menuLoginReg();
        while(!exit){
            switch ( Utils.askOption("Edit Profile", "Send Messages", "Contact List","Search user",
                    "Pending contact requests","Add Contact",
                    "Delete Contact","Create Group","Join group","List Groups","Leave Group","Manage Group","Exit")){
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
                case 4->
                        searchUser();
                case 5 ->
                        pendingContactRequest();
                case 6 ->
                        addContact();
                case 7 ->
                        deleteContact();
                case 8 ->
                        createGroup();
                case 9->
                        joinGroup();
                case 10 ->
                        listGroups();
                case 11 ->
                        leaveGroup();
                case 12 ->
                        adminGroup();
            }
        }

    }



    private void menuLoginReg() {
        boolean success = false;
        switch (Utils.askOption("Login","Register","Exit")) {
            case 0 -> exit = true;
            case 1 -> {
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
            case 2 -> {
                success = logic.register(Utils.askString("Username: "), Utils.askString("Name: "), Utils.askString("Password: "));
                if(success)
                    System.out.println("Register with success!");
                else {
                    menuLoginReg();
                    System.out.println("It was not possible to register this user!");
                }
            }
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
        String list = logic.contactList(logic.getUsername());
        if(list != null)
            System.out.println(list);
        else
            System.out.println("Your Contact list is empty.Add contacts to your list.");
    }

    private void searchUser(){
        String userInfo = logic.searchUser(Utils.askString("Enter a name or username: "));
        if(userInfo != null)
            System.out.println(userInfo);
        else
            System.out.println("User not Found.");
    }

    private void pendingContactRequest(){
        String pendingList = logic.pendingContactList(logic.getUsername());
        if(pendingList != null)
            System.out.println(pendingList);
        else
            System.out.println("Your list of pending contacts request is empty");


    }
    private void addContact(){
        if(logic.addContact(Utils.askString("Enter a username: ")))
            System.out.println("Contact invitation sent.");
        else
            System.out.println("User not Found.");
    }

    private void deleteContact(){
        if(logic.deleteContact(Utils.askString("Enter a contact to delete from the list: ")))
            System.out.println("Contact deleted.");
        else
            System.out.println("Error deleting contact");

    }

    private void createGroup(){

        if(logic.createGroup(Utils.askString("Enter a name of a group:")))
            System.out.println("Group created with success");
        else
            System.out.println("Error creating group...");

    }
    private void joinGroup() {
        if (logic.joinGroup(Utils.askInt("Id of the group: ")))
            System.out.println("Successful sending request to join the group");
        else
            System.out.println("Failure to submit request to join the group");
    }

    private void listGroups() {
        String list = logic.listGroups();
        if(list != null)
            System.out.println(list);
        else
            System.out.println("There are no groups registered.");
    }

    private void leaveGroup() {
        if(logic.leaveGroup(Utils.askInt("Id for leave the group: ")))
            System.out.println("Successfully exited the group");
        else
            System.out.println("Failed to leave the group");

    }

    private void adminGroup() {
        switch(Utils.askOption("Rename the group name","Delete group member","Extinguish the group","Exit")){
            case 0->{}

            case 1->{
                if(logic.renameGroup(Utils.askInt("Enter the id group:"),Utils.askString("Enter a new name for group:")))
                    System.out.println("Successfully rename group name");
                else
                    System.out.println("Failed to rename group name");
            }
            case 2->{

            }
            case 3->{

            }
            }
        }
    }
