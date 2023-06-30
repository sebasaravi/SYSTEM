package Server;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Handler extends Thread{
    public static final String PATH = "C:\\Users\\Sebastian\\Desktop\\Files\\";
    private String pathFile ="";
    private Socket client;
    private DataInputStream read;
    private DataOutputStream write;
    public Handler(Socket client){
        try{
            this.client = client;
            read = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            write =  new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }



    @Override
    public void run(){
        while(true){
            try {
                String receive = read.readUTF();
                switch(receive){
                    case "1":
                        byte[]buffer = new byte[1024];
                        int bytesRead;
                        pathFile = PATH+client.getInetAddress().toString()+
                                ".txt";
                        FileOutputStream fileOut = new FileOutputStream(
                                pathFile);
                        while((bytesRead = read.read(buffer)) != -1){
                            fileOut.write(buffer, 0 , bytesRead);
                        }
                        fileOut.close();
                        try {
                            cifrarArchivo();
                            desfragmentarArchivoCifrado();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        String[]namesFilePath = obtenerNombresPartes();
                        sendToClient(namesFilePath);
                        break;
                    case "2":
                            System.out.println(Server.clients.size());
                            sendOption("2");
                            receiveToClient();
                            send();
                    break;
                    case "3":

                        break;

                    default:
                        break;
                }
            } catch (IOException ex) {

            }
        }
    }
    public void receiveToClient() throws IOException {

        pathFile = PATH+"definitivo.txt";
        FileOutputStream fileOut = new FileOutputStream(
                pathFile);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for(int i= 0; i < 3; i++){
            DataInputStream in = new DataInputStream(Server.clients.get(i).getInputStream());
            byte[]buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = in.read(buffer)) != -1){
                byteStream.write(buffer,0,bytesRead);
            }
        }
        byteStream.writeTo(fileOut);
        byteStream.close();
        fileOut.close();
    }
    public void sendOption(String opc) throws IOException{
        for(int i= 0; i < 3; i++) {
            DataOutputStream out = new DataOutputStream(Server.clients.get(i).getOutputStream());
            out.writeUTF(opc);
            out.flush();
        }
    }

    public void send() throws IOException {
        FileInputStream fileOut = new FileInputStream(pathFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileOut.read(buffer)) != -1) {
            write.write(buffer, 0, bytesRead);
        }
        write.flush();
    }

    public void sendToClient(String[] names) throws IOException {
        for(int i= 0; i < 3; i++){
            DataOutputStream out = new DataOutputStream(Server.clients.get(i).getOutputStream());
            FileInputStream fileOut = new FileInputStream(names[i]);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileOut.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }
    private String[] obtenerNombresPartes() {
        String[] nombresPartes = new String[3];
        for (int i = 0; i < 3; i++) {
            nombresPartes[i] = PATH+"servidor_" + (i + 1) + ".txt";
        }
        return nombresPartes;
    }

    private void cifrarArchivo() throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(pathFile));

        // Generar una clave secreta de cifrado
        String clave = "miClaveSecreta123";
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] claveBytes = sha.digest(clave.getBytes("UTF-8"));
        SecretKey secretKey = new SecretKeySpec(claveBytes, "AES");

        // Cifrar el archivo utilizando AES
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] archivoCifrado = cipher.doFinal(fileContent);

        // Guardar el archivo cifrado en disco
        pathFile = PATH+"archivo_cifrado.txt";
        FileOutputStream outputStream = new FileOutputStream(pathFile);
        outputStream.write(archivoCifrado);
        outputStream.close();
    }
    private void desfragmentarArchivoCifrado() throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(pathFile));
        int fileSize = fileContent.length;
        int partSize = fileSize / 3;

        for (int i = 0; i < 3; i++) {
            byte[] fragmento = new byte[partSize];
            System.arraycopy(fileContent, i * partSize, fragmento, 0, partSize);
            guardarFragmento(fragmento, PATH+"servidor_" + (i + 1) + ".txt");
        }
    }
    private void guardarFragmento(byte[] fragmento, String nombreArchivo) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(nombreArchivo);
        outputStream.write(fragmento);
        outputStream.close();
    }


}
