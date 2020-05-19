package serverside;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Random;

public class Server implements Runnable {//to be usuniete jak przestane testowac z palucha
	boolean serverIsRunning;

	private static final Random random = new Random();
	private ServerSocket serverSocket;


	public Server() throws IOException {
		serverIsRunning = true;
		this.serverSocket = new ServerSocket();
		// TODO: 19.05.2020 listing ther iterfaces avaliable for use and enclosing them in container
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
			serverSocket.bind(new InetSocketAddress(randomizePortNumber()));
			System.out.println("Listening on address " + serverSocket.getInetAddress() +
					" on port " + serverSocket.getLocalPort());
			while (serverIsRunning) {
				new ServerClientHandler(serverSocket.accept()).start();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int randomizePortNumber() {
		int result;
		do {
			result = random.nextInt(19999);
		} while (result == 7);
		return result;
	}
}
