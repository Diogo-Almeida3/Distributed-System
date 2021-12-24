package data.cli2serv;

public class Cli2ServGetMsg extends Cli2Serv {
    private String receiver;
    private String sender;
    private int groupId;
    private typeRequest request;

    public Cli2ServGetMsg(String sender, String receiver) { // To get the private messages with a specific user
        super(RequestType.GET_MESSAGES);
        this.receiver = receiver;
        this.sender = sender;
        request = typeRequest.GET_MSG_FROM_USER;
    }

    public Cli2ServGetMsg(String receiver, typeRequest request) { // For users to get the groups or contacts they have messages with
        super(RequestType.GET_MESSAGES);
        this.receiver = receiver;
        if (request != typeRequest.GET_CONTACTS && request != typeRequest.GET_GROUPS)
            throw new IllegalArgumentException();
        this.request = request;
    }

    public Cli2ServGetMsg(String sender, int groupId, String receiver) { // To get messages sent in the group
        super(RequestType.GET_MESSAGES);
        this.receiver = receiver;
        this.sender = sender;
        this.groupId = groupId;
        request = typeRequest.GET_MSG_FROM_GROUP;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public typeRequest getRequestInfo() {
        return request;
    }

    public int getGroupId() {
        return groupId;
    }

    public enum typeRequest {
        GET_CONTACTS,GET_GROUPS,GET_MSG_FROM_USER,GET_MSG_FROM_GROUP
    }
}
