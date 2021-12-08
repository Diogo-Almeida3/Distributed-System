package data.serv2cli;

public class Serv2Cli {

    private Request request =null;

    public Serv2Cli(Request request){
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
    public enum Request {
        NOTIFICATION_MESSAGE,NOTIFICATION_FILE
    }
}
