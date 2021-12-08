package data.cli2serv;

import java.io.Serializable;

public abstract class Cli2Serv implements Serializable {
    private static final long serialVersionUID = 1L;

    private RequestType requestType;

    public Cli2Serv(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public enum RequestType{
        REGISTER, LOGIN, SEARCH_USER, EDIT_USER,
        ADD_CONTACT, LIST_CONTACT, DELETE_CONTACT,
        CREATE_GROUP, JOIN_GROUP, LIST_GROUPS,EDIT_GROUP,LEAVE_GROUP,
        CONTACT_REQUEST,SEND_MESSAGE,EXIT
    }
}
