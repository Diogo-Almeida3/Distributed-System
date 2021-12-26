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
        ADD_CONTACT, LIST_CONTACT, DELETE_CONTACT,CK_CONTACT,
        CREATE_GROUP, JOIN_GROUP, LIST_GROUPS,LEAVE_GROUP,GET_FILE,
        LIST_REQUESTS,SEND_MESSAGE,DELETE_MESSAGE,EXIT,TCP_PORT,ADMIN_GROUP,GET_MESSAGES,REFUSE_CONTACT,SEND_FILE,DELETE_FILE;
    }
}
