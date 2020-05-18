package serverside;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Random;

public class Server {
    private static final Random random = new Random();
    private ServerSocket serverSocket;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket();
    }

    public static void main(String[] args) {
        int port = random.nextInt();
        try {
            Server server = new Server();
            server.serverSocket.bind(new InetSocketAddress(port));
            System.out.println("Listening on port " + port);
            new ClientHandler(server.serverSocket.accept()).start();
            while (true) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
