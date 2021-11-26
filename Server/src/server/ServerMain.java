package server;


import java.sql.SQLException;
import java.util.ArrayList;

public class ServerMain {
    public static void main(String[] args) {
        try{
            if(args.length == 3 || args.length ==1 )
                new Server(args);
            else
                throw new IllegalArgumentException("Invalid arguments! You must use one of the following formatting: <IP_GRDS> <Port_GRDS> <IP_SBGD> OR <IP_SGBD>");
        } catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
        } catch (SQLException throwables) {
            System.err.println("Unable to connect to the database!");
        }
    }
}
