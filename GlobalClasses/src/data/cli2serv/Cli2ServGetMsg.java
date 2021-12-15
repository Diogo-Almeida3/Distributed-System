package data.cli2serv;

public class Cli2ServGetMsg extends Cli2Serv {
    private String receiver;
    private String sender;
    private boolean isGetContacts;

    public Cli2ServGetMsg(String sender, String receiver) {
        super(RequestType.GET_MESSAGES);
        this.receiver = receiver;
        this.sender = sender;
        this.isGetContacts = false;
    }

    public Cli2ServGetMsg(String receiver) {
        super(RequestType.GET_MESSAGES);
        this.receiver = receiver;
        this.isGetContacts = true;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public boolean isGetContacts() {
        return isGetContacts;
    }
}
