package server.threads;

import server.utils.DB;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.sql.SQLException;

//CliServ2fiel
public class ThreadReceivedFiles extends Thread {
    private int idOfFile;
    private String serverIp;
    private int serverPort;

    private static final String serverDirectory = "./Files/"+ ManagementFactory.getRuntimeMXBean().getName();

    public ThreadReceivedFiles(String serverIp, int serverPort, int idOfFile) {
        this.idOfFile = idOfFile;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

        try {
            /* Open TCP with the server that own the file */
            Socket receiveFile = new Socket(serverIp,serverPort);

            InputStream in = receiveFile.getInputStream();
            OutputStream out = receiveFile.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            DB db = new DB();
            String filename = db.getFileDirectory(idOfFile);

            /* Inform the server of which file he wants to transfer */
            oos.writeObject(filename);

            /* Create file path filename -> /sender/file*/
            FileOutputStream file = new FileOutputStream(serverDirectory + filename);

            /* Transfer the file*/
            byte [] buf = new byte[512];
            int tam;
            while((tam = in.read(buf)) != -1)
                file.write(buf,0,tam);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
