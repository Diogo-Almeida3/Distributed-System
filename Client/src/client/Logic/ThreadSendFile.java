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

    public ThreadSendFile(ObjectOutputStream out2serv, ObjectInputStream inServ, String sender, String receiver, File file) {
        this.out2serv = out2serv;
        this.receiver = receiver;
        this.sender = sender;
        this.file = file;
        this.inServ = inServ;
    }

    @Override
    public void run() {
        try {
            // Send file int parts
            FileInputStream fis = new FileInputStream(file);
            while (fis.available() > 0) {
                byte[] fileChunk = new byte[512];
                int bytesRead = fis.read(fileChunk);
                out2serv.writeObject(new Cli2ServFile(sender,receiver, Arrays.copyOf(fileChunk,bytesRead),requestId,file.getName()));
                if (requestId==-1)
                    requestId = (int) inServ.readObject();
            }
            out2serv.writeObject(new Cli2ServFile(sender,receiver, new byte[0],requestId,file.getName())); // Send zero bytes to inform the server that the file was all sent
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }
    }
}
