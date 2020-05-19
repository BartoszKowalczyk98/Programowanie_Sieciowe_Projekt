package serverside;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Random;

public class Server implements Runnable{
    private static final Random random = new Random();
    private ServerSocket serverSocket;
    static int port = random.nextInt(19999);
    public Server() throws IOException {
        this.serverSocket = new ServerSocket();
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            Server server = new Server();
            server.serverSocket.bind(new InetSocketAddress(port));
            System.out.println("Listening on port " + port);
            ClientHandler clientHandler = new ClientHandler(server.serverSocket.accept());
            clientHandler.start();
            clientHandler.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
