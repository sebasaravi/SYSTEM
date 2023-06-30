package Server;

import javax.swing.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;

public class ServerClient {
    static Socket sfd = null;
    public static void main(String[] args) throws IOException {
        sfd = new Socket("25.49.127.138",8000);
        String PATH = "C:\\Users\\Sebastian\\Desktop\\Files\\Server\\";
        String pathFile ="";
        DataInputStream inputStream = new DataInputStream(
                new BufferedInputStream(sfd.getInputStream()));
        DataOutputStream outputStream = new DataOutputStream(
                new BufferedOutputStream(sfd.getOutputStream()));
        int count = 1;
        while (true){

            byte[]buffer = new byte[1024];
            int bytesRead;
            pathFile = PATH+"file.txt";

            FileOutputStream fileOut = new FileOutputStream(
                    pathFile);
            while((bytesRead = inputStream.read(buffer)) != -1){
                fileOut.write(buffer, 0 , bytesRead);
            }
            fileOut.close();
        }




    }
}