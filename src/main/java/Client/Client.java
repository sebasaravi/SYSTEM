package Client;

import javax.swing.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;

public class Client {
    static Socket sfd = null;
    public static void main(String[] args) throws IOException {
        sfd = new Socket("192.168.0.6",8000);
        DataInputStream inputStream = new DataInputStream(
                new BufferedInputStream(sfd.getInputStream()));
        DataOutputStream outputStream = new DataOutputStream(
                new BufferedOutputStream(sfd.getOutputStream()));
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showDialog(fileChooser,
                JFileChooser.APPROVE_SELECTION);
        outputStream.writeUTF("1");
        outputStream.flush();
        //while(true){
        File file = fileChooser.getSelectedFile();
        FileInputStream fileOut = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileOut.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        //}
        sfd.close();

    }
}
