package clientside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {
    boolean isRunning;

    Socket socket;

    String messageToSend;
    String messageReceived;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;


    public ClientThread(Socket socket) {
        isRunning = true;
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("client thread runnin");
        while (isRunning) {
        }
    }
}
