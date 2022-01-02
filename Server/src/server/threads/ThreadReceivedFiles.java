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
            DB db = new DB();
            String filename = db.getFileDirectory(idOfFile);

            /* Create file path filename -> /sender/file*/
            File f = new File( serverDirectory + filename);

            if (f.isFile()) // If this server already has the file then it will not download it
                return;

            /* Open TCP with the server that own the file */
            Socket socketReceiveFile = new Socket(serverIp,serverPort);

            InputStream in = socketReceiveFile.getInputStream();
            OutputStream out = socketReceiveFile.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            /* Inform the server of which file he wants to transfer */
            oos.writeObject(filename);

            f.getParentFile().mkdirs();
            f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            /* Transfer the file*/
            byte [] buf = new byte[512];
            int tam;
            while((tam = in.read(buf)) > 0) {
                fos.write(buf,0,tam);
            }
            fos.flush();
            fos.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
