package data.cli2serv;

public class Cli2ServMsg extends Cli2Serv {
    private String sender, receiver, message;

    public Cli2ServMsg(String sender, String receiver, String message) {
        super(RequestType.SEND_MESSAGE);
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
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
}
