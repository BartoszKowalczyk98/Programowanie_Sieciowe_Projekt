package serverside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler extends Thread {
    Socket connectionSocket;

    String messageToSend;
    String messageReceived;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        try {
            this.dataInputStream = new DataInputStream(connectionSocket.getInputStream());
            this.dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("I entered run method!\nFor socket: " + connectionSocket.getInetAddress() +
                "\nPort: " + connectionSocket.getPort());
    }
}
