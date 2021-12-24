package client.Logic;

import data.cli2serv.Cli2ServFile;

import java.io.*;
import java.util.Arrays;

public class ThreadSendFile extends Thread {
    private ObjectOutputStream out2serv;
    private ObjectInputStream inServ;
    private String sender,receiver;
    private File file;
    private int requestId = -1;
    private int idGroup;
    private boolean sendToGroup=false;

    public ThreadSendFile(ObjectOutputStream out2serv, ObjectInputStream inServ, String sender, String receiver, File file) {
        this.out2serv = out2serv;
        this.receiver = receiver;
        this.sender = sender;
        this.file = file;
        this.inServ = inServ;
        sendToGroup=false;
    }

   public ThreadSendFile(ObjectOutputStream out2serv,ObjectInputStream inserv,String sender,int idGroup, File file) {
       this.out2serv = out2serv;
       this.inServ = inserv;
       this.sender = sender;
       this.idGroup = idGroup;
       this.file = file;
       sendToGroup = true;
   }
    @Override
    public void run() {
        try {
            // Send file int parts
            FileInputStream fis = new FileInputStream(file);
            while (fis.available() > 0) {
                byte[] fileChunk = new byte[512];
                int bytesRead = fis.read(fileChunk);
                if(!sendToGroup)
                    out2serv.writeObject(new Cli2ServFile(sender,receiver, Arrays.copyOf(fileChunk,bytesRead),requestId,file.getName()));
                else
                    out2serv.writeObject(new Cli2ServFile(sender,idGroup,Arrays.copyOf(fileChunk,bytesRead),requestId,file.getName()));
                if (requestId==-1)
                    requestId = (int) inServ.readObject();
            }
            if(!sendToGroup)
                out2serv.writeObject(new Cli2ServFile(sender,receiver, new byte[0],requestId,file.getName())); // Send zero bytes to inform the server that the file was all sent
            else
                out2serv.writeObject(new Cli2ServFile(sender,idGroup,new byte[0],requestId,file.getName()));
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }
    }
}
