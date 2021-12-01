package client;

import java.io.IOException;
import java.sql.SQLException;

public class ClientMain {
    public static void main(String[] args) {

        try{
            if(args.length == 2)
                new Client(args);
            else
                throw new IllegalArgumentException("Invalid arguments! You must use one of the following formatting: <IP_GRDS> <Port_GRDS>");
        } catch (IllegalArgumentException | IOException e){
            System.err.println(e.getMessage());
        }
    }
}
