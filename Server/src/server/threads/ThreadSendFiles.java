package server.threads;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadSendFiles extends Thread {
    private ServerSocket server;
    private int port;
    private String serverDirectory;

    public ThreadSendFiles(String serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

    public String getIp() {
        return null;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        //TCP faz coisas tatataat

        try {
            /* Create automatic TCP */
            server = new ServerSocket(0);
            port = server.getLocalPort();
            while (true) {
                Socket send = server.accept();
                InputStream inputStream = send.getInputStream();
                OutputStream out = send.getOutputStream();
                ObjectInputStream in = new ObjectInputStream(inputStream);

                /* Wait for the servers to request a file transfer */
                String filename = (String) in.readObject();

                FileInputStream fileInputStream = new FileInputStream(serverDirectory + filename); //Filename must contain the sender of the file too

                /* Send the file to the other server */
                while (fileInputStream.available() != 0) {
                    byte[] buffer = new byte[512];
                    int tam = fileInputStream.read(buffer);
                    out.write(buffer, 0, tam);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
