package data.cli2serv;

public class Cli2ServFile extends Cli2Serv {
    private String sender, receiver, filename;
    private int groupID;
    private boolean send2group;
    private byte[] filePart;
    private int idOfRequest = -1;

    public Cli2ServFile(String sender, String receiver, byte[] filePart, int idOfRequest, String filename) { // Send message to one user
        super(RequestType.SEND_FILE);
        this.sender = sender;
        this.receiver = receiver;
        this.filePart = filePart;
        this.idOfRequest = idOfRequest;
        this.filename = filename;
        send2group = false;
    }

    public Cli2ServFile(String sender, int groupId, byte[] filePart, int idOfRequest, String filename) { // Send message to group
        super(RequestType.SEND_FILE);
        this.sender = sender;
        this.groupID = groupId;
        this.filePart = filePart;
        this.idOfRequest = idOfRequest;
        this.filename = filename;
        send2group = true;
    }

    public String getFilename() {
        return filename;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getGroupID() {
        return groupID;
    }

    public boolean isSend2group() {
        return send2group;
    }

    public byte[] getFilePart() {
        return filePart;
    }

    public int getIdOfRequest() {
        return idOfRequest;
    }
}
