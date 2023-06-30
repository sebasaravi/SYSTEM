package Server;

import jdk.internal.org.objectweb.asm.Handle;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    public static ArrayList<Socket> clients = new ArrayList<Socket>();
    private ServerSocket network;

    public Server(){
        try{
            network = new ServerSocket(8000);
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run(){
        int count =0;
        while(true){
            try{
                Socket client = network.accept();
                if(count < 1){
                    clients.add(client);
                    count++;
                    System.out.println(client.getInetAddress());
                }

                Handler handler = new Handler(client);
                Thread t = new Thread(handler);
                t.start();
            }catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}
