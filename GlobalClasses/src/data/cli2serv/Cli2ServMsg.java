package data.cli2serv;

public class Cli2ServMsg extends Cli2Serv {
    private String sender, receiver, message;
    private int groupID;
    private boolean send2group;

    public Cli2ServMsg(String sender, String receiver, String message) { // Send message to one user
        super(RequestType.SEND_MESSAGE);
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        send2group = false;
    }

    public Cli2ServMsg(String sender, int groupId, String message) { // Send message to group
        super(RequestType.SEND_MESSAGE);
        this.sender = sender;
        this.message = message;
        this.groupID = groupId;
        send2group = true;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public int getGroupID() {
        return groupID;
    }

    public boolean isSend2group() {
        return send2group;
    }
}
