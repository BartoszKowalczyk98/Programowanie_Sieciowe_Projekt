package serverside;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {//to be usuniete jak przestane testowac z palucha
	boolean serverIsRunning;

	private List<Integer> listOfUsedPorts;
	private static final Random random = new Random();
	private final ServerSocket serverSocket;
	private static ServerGUI gui;


	public Server() throws IOException {
		serverIsRunning = true;
		this.serverSocket = new ServerSocket();
		listOfUsedPorts = new CopyOnWriteArrayList<>();
		listOfUsedPorts.add(7);//port na broadcast
		// TODO: 19.05.2020 listing ther iterfaces avaliable for use and enclosing them in container
		// TODO: 19.05.2020 when poping from listing use getlocalport from serversocket
	}

	public static void main(String[] args) {
		gui = new ServerGUI("Server");
	}

	public void run() {
		try {
			serverSocket.bind(new InetSocketAddress(randomizePortNumber()));
			String toBeDisplayed = "Listening on address " + serverSocket.getInetAddress() +
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
		} while (listOfUsedPorts.contains(result));
		listOfUsedPorts.add(result);
		return result;
	}
}
