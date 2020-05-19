package serverside;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Random;

public class Server implements Runnable {//to be usuniete jak przestane testowac z palucha
	boolean serverIsRunning;

	private static final Random random = new Random();
	private ServerSocket serverSocket;
	private static ServerGUI gui;

	public String getToBeDisplayed() {
		return toBeDisplayed;
	}

	private String toBeDisplayed;

	public Server() throws IOException {
		serverIsRunning = true;
		this.serverSocket = new ServerSocket();
		// TODO: 19.05.2020 listing ther iterfaces avaliable for use and enclosing them in container
	}

	public static void main(String[] args) {
		gui = new ServerGUI("Server");
	}

	public void run() {
		try {
			serverSocket.bind(new InetSocketAddress(randomizePortNumber()));
			toBeDisplayed = "Listening on address " + serverSocket.getInetAddress() +
					" on port " + serverSocket.getLocalPort();
			gui.updateDisplay(toBeDisplayed);
			while (serverIsRunning) {
				new ServerClientHandler(serverSocket.accept()).start();
			}
			serverSocket.close();
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
