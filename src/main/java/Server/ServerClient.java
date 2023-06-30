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
            String receive = inputStream.readUTF();
            System.out.println(receive);
            switch (receive){
                case "1":
                    byte[]buffer = new byte[1024];
                    int bytesRead;
                    pathFile = PATH+"file1.txt";

                    FileOutputStream fileOut = new FileOutputStream(
                            pathFile);
                    while((bytesRead = inputStream.read(buffer)) != -1){
                        fileOut.write(buffer, 0 , bytesRead);
                    }
                    fileOut.close();

                case "2":
                    System.out.println(receive);
                    String filePath = PATH + "file1.txt";
                    File file = new File(filePath);
                    if (file.exists()) {
                        FileInputStream fileIn = new FileInputStream(file);
                        byte[] buffer2 = new byte[1024];
                        int bytesread;
                        while ((bytesread = fileIn.read(buffer2)) != -1) {
                            outputStream.write(buffer2, 0, bytesread);
                        }
                        fileIn.close();
                        outputStream.flush();
                    } else {
                        System.out.println("El archivo no existe.");
                    }
                    break;
            }

        }




    }
}
