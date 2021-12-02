package data;

import java.io.Serializable;
import java.net.InetAddress;


public class Serv2Grds implements Serializable {
    private static final long serialVersionUID = 1L;

    private Request request = null;
    private int port = 0;

    public Serv2Grds(int port) {
        this.port = port;
    }

    public Serv2Grds(Request request, int port) {
        this.request = request;
        this.port = port;
    }

    public Serv2Grds(System client, Request register) {
        this.request = request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public int getPort() {
        return port;
    }

    public enum Request {
        REGISTER,PING
    }
}