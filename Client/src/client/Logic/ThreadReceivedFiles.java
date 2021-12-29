package client.Logic;

import java.io.*;
import java.net.Socket;


public class ThreadReceivedFiles extends Thread {
    private String serverIp;
    private int serverPort;
    private String filename;
    private String clientDirectory;

    public ThreadReceivedFiles(String serverIp, int serverPort, String filename,String username) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.filename = filename;
        clientDirectory = "./Files/Client/"+ username +"/Downloads/";
        }

    @Override
    public void run() {

        try {
            /* Open TCP with the server that own the file */
            Socket socketReceiveFile = new Socket(serverIp,serverPort);

            InputStream in = socketReceiveFile.getInputStream();
            OutputStream out = socketReceiveFile.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            /* Inform the server of which file he wants to transfer */
            oos.writeObject(filename);

            /* Create file path filename -> /sender/file*/
            String []aux = filename.split("/");
            File f = new File( clientDirectory + aux[aux.length-1]);

            if (f.isFile()) // If this client already has the file then it will not download it
                return;

            f.getParentFile().mkdirs();
            f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);

            /* Transfer the file*/
            byte [] buf = new byte[512];
            int tam;
            while((tam = in.read(buf)) != -1)
                fos.write(buf,0,tam);

            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
