package client.UI.Text;

import Constants.Notification;
import client.Logic.Client;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class UIText implements UIClient {
    Client logic;
    private boolean exit = false;

    public UIText(Client logic) {
        this.logic = logic;
    }

    public UIText() {
    }

    public void setLogic(Client logic) {
        this.logic = logic;
    }

    public void run() {
        menuLoginReg();
        while (!exit) {
            System.out.println("Menu:");
            switch (Utils.askOption("Contacts", "Groups", "Edit Profile", "Exit")) {
                case 0 -> {
                    logic.exitServer();
                    exit = true;
                }
                case 1 -> contactsCommands();
                case 2 -> groupsCommands();
                case 3 -> profileCommands();
            }
        }
    }

    private void contactsCommands() {
        System.out.println("Menu Contacts:");
        switch (Utils.askOption("Send Messages","See my Messages", "Search User", "Pending Contact Requests", "Contact List", "Add Contact","Refuse Contact","Delete Contact", "Go Back")) {
            case 1 -> sendMessages();
            case 2 -> seeMessages();
            case 3 -> searchUser();
            case 4 -> pendingContactRequest();
            case 5 -> contactList();
            case 6 -> addContact();
            case 7 -> refuseContact();
            case 8 -> deleteContact();
        }
    }

    private void seeMessages() {
        int op = Utils.askOption("See users messages","See groups messages","Go Back");
        ArrayList<String> names;
        switch (op) {
            case 1 -> {
                System.out.println("Choose the contact that you want to view the messages: ");
                names = logic.getContactsWithMessages();
                names.add("Go Back");
                op = Utils.askOption(names.toArray(new String[names.size()]));
                if (op == 0) return;
                String name = names.get(op-1);
                System.out.println("Messages from " +name +":\n");
                for (String msg : logic.getMessagesFrom(name))
                    System.out.println(msg);
                System.out.println("\n");
            }
            case 2 -> {
                System.out.println("Choose the group that you want to view the messages: ");
                names = logic.getGroupsWithMessages();
                names.add("Go Back");
                op = Utils.askOption(names.toArray(new String[names.size()]));
                if (op == 0) return;
                int group = Integer.parseInt(names.get(op-1).substring(6));
                System.out.println("Messages from group " +group +":\n");
                for (String msg : logic.getMessagesFromGroup(group))
                    System.out.println(msg);
                System.out.println("\n");
            }
            default -> {
                return;
            }
        }


    }

    private void groupsCommands() {
        System.out.println("Menu Groups:");
        switch (Utils.askOption("List Groups", "Join Group", "Create Group", "Manage Group", "Leave Group", "Go Back")) {
            case 1 -> listGroups();
            case 2 -> joinGroup();
            case 3 -> createGroup();
            case 4 -> adminGroup();
            case 5 -> leaveGroup();
        }
    }

    private void profileCommands() {
        System.out.println("Menu Profile:");
        switch (Utils.askOption("Edit Name", "Edit Username", "Edit password", "Go Back")) {
            case 1 -> editProfileName();
            case 2 -> editProfileUsername();
            case 3 -> editProfilePass();
        }
    }

    public void editProfileName() {
        if (logic.editProfileName(Utils.askString("Enter a new name: ")))
            System.out.println("Success editing profile name");
        else
            System.out.println("Failed editing profile name");
    }

    public void editProfileUsername() {
        if (logic.editProfileUsername(Utils.askString("Enter a new Username: "), Utils.askString("Enter your password: ")))
            System.out.println("Success editing profile username");
        else
            System.out.println("Failed editing profile username");
    }

    public void editProfilePass() {
        if (logic.editProfilePass(Utils.askString("Enter a old password: "), Utils.askString("Enter a new password: ")))
            System.out.println("Success editing profile username");
        else
            System.out.println("Failed editing profile username");
    }


    private void menuLoginReg() {
        boolean success = false;
        System.out.println("Menu:");
        switch (Utils.askOption("Login", "Register", "Exit")) {
            case 0 -> exit = true;
            case 1 -> {
                int attempts = 0;
                do {
                    success = logic.login(Utils.askString("Username: "), Utils.askString("Password: "));

                    if (success)
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
                if (success)
                    System.out.println("Register with success!");
                else {
                    menuLoginReg();
                    System.out.println("It was not possible to register this user!");
                }
            }
        }
    }


    private void sendMessages() {
        boolean success = false;
        int op = Utils.askOption("Send to user","Send to group","Go Back");
        switch (op) {
            case 1 -> success = logic.sendMessageTo(Utils.askString("Message to: "), Utils.askString("Message:\n\t"));
            case 2 -> success = logic.sendMessageTo(Utils.askInt("Group ID: "), Utils.askString("Message:\n\t"));
        }

        if (success)
            System.out.println("Your message was sent successfully!");
        else
            System.out.println("An error occurred while sending the message!");
    }

    private void contactList() {
        String list = logic.contactList();
        if (list != null)
            System.out.println(list);
        else
            System.out.println("Your Contact list is empty.Add contacts to your list.");
    }

    private void searchUser() {
        String userInfo = logic.searchUser(Utils.askString("Enter a name or username: "));
        if (userInfo != null)
            System.out.println(userInfo);
        else
            System.out.println("User not Found.");
    }

    private void pendingContactRequest() {
        String pendingList = logic.pendingContactList();
        if (pendingList != null)
            System.out.println(pendingList);
        else
            System.out.println("Your list of pending contacts request is empty");

    }

    private void addContact() {
        String username = Utils.askString("Enter a username: ");
        if (logic.addContact(username))
            System.out.println("Contact invitation sent to " + username);
        else
            System.out.println("User with name"+username+"not Found.");
    }

    private void refuseContact(){
        String username = Utils.askString("Enter a username:");
        if(logic.refuseContact(username))
            System.out.println("Contact refused by:"+username);
        else
            System.out.println("Error refused contact with name"+ username);
    }

    private void deleteContact() {
        String deletContact = Utils.askString("Enter a contact to delete from the list: ");
        if (logic.deleteContact(deletContact))
            System.out.println("Contact with name "+deletContact+" deleted.");
        else
            System.out.println("Error deleting contact with name"+ deletContact);

    }

    private void createGroup() {
        String name = Utils.askString("Enter a name of a group: ");
        if (logic.createGroup(name))
            System.out.println("Group created with name "+name+" success");
        else
            System.out.println("Error creating group with name "+name);
    }

    private void joinGroup() {
        int id = Utils.askInt("Id of the group: ");
        if (logic.joinGroup(id))
            System.out.println("Successful sending request to join the group " + id);
        else
            System.out.println("Failure to submit request to join the group " + id);
    }

    private void listGroups() {
        String list = logic.listGroups();
        if (list != null)
            System.out.println(list);
        else
            System.out.println("There are no groups registered.");
    }

    private void leaveGroup() {
        int id = Utils.askInt("Id for leave the group: ");
        if (logic.leaveGroup(id))
            System.out.println("Successfully exited the group " + id);
        else
            System.out.println("Failed to leave the group " + id);

    }

    private void adminGroup() {
        System.out.println("Admin Group Menu");
        switch (Utils.askOption("Rename group", "Accept group member","Refuse group member", "Delete group member", "Delete group", "List waiting members", "Exit")) {
            case 0 -> {
            }
            case 1 -> rename();
            case 2 -> acceptanceGroupMember();
            case 3 -> refuseGroupMember();
            case 4 -> deleteGroupMember();
            case 5 -> deleteGroup();
            case 6 -> listWaitingMembers();
        }
    }

    public void rename(){
        if (logic.renameGroup(Utils.askInt("Enter the id group:"), Utils.askString("Enter a new name for group:")))
            System.out.println("Successfully rename group name");
        else
            System.out.println("Failed to rename group name");
    }

    public void acceptanceGroupMember(){
        String name = Utils.askString("Enter the username of the member to add: ");
        if (logic.acceptanceGroupMember(Utils.askInt("Enter the id group: "), name))
            System.out.println("Sucessfully add member with name " + name);
        else
            System.out.println("Failed to add member with name " + name);
    }

    public void refuseGroupMember(){
        String name = Utils.askString("Enter the username of the member to refuse:");
        if (logic.refuseGroupMember(Utils.askInt("Enter the id group: "), name))
            System.out.println("Sucessfully refuse member with name " + name);
        else
            System.out.println("Failed to refuse member with name " + name);
    }

    public void deleteGroupMember(){
        String name = Utils.askString("Enter the username of the member to be deleted: ");
        if (logic.deleteGroupMember(Utils.askInt("Enter the id group: "), name))
            System.out.println("Sucessfully remove member with name " + name);
        else
            System.out.println("Failed to remove member with name " + name);
    }

    public void deleteGroup(){
        int id = Utils.askInt("Enter the id group to delete: ");
        if (logic.deleteGroup(id))
            System.out.println("Sucessfully remove group with id " + id);
        else
            System.out.println("Failed to delete group with id " + id);
    }

    public void listWaitingMembers(){
        String list = logic.listWaitingMembers(Utils.askInt("Enter the id group to list:"));
        if (list != null)
            System.out.println(list);
        else
            System.out.println("There are no members waiting");
    }

    @Override
    public void notification(Notification type) {
        switch (type) {
            case CONTACT_REQUEST -> System.out.println("\nYou have a new contact request!");

            case JOIN_GROUP_REQUEST -> System.out.println("\nYou have a new request to join in your group!");

            case CONTACT_REQ_RESPONSE -> System.out.println("\nYou have a response to a contact request!");

            case JOIN_GROUP_REQ_NEG_RESPONSE -> System.out.println("\nYou were not accepted into a group where you had sent a request to join!");

            case MESSAGE -> System.out.println("\nYou have a new message!");

            case MESSAGE_DELETE -> System.out.println("\nA message was deleted!");

            case FILE -> System.out.println("\nYou have a new file to transfer!");

            case FILE_DELETE -> System.out.println("\nOne file has been deleted!");

            case GROUP_DELETE -> System.out.println("\nA group you were in was deleted!");

            case CONTACT_DELETE -> System.out.println("\nA contact of yours was deleted!");

            case LEAVE_GROUP -> System.out.println("\nOne user has left of your group!");

            case ACCEPT_MEMBER -> System.out.println("\nYou have been accepted into a group!");

            case EDIT_GROUP -> System.out.println("\nThe name of the group you are in has been edited.");

        }
    }
}
