package serverside;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Random;

public class Server implements Runnable {
	private static final Random random = new Random();
	private ServerSocket serverSocket;

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
		int port = random.nextInt(19999);
		try {
			Server server = new Server();
			server.serverSocket.bind(new InetSocketAddress(port));
			System.out.println("Listening on port " + port);
			ServerClientHandler serverClientHandler = new ServerClientHandler(server.serverSocket.accept());
			serverClientHandler.start();
			serverClientHandler.join();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
