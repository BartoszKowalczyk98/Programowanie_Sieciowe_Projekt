package serverside;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.sleep;

public class Server implements Runnable {
	private final int listeningPort = 7;
	private final String multicastIP = "230.0.0.0";
	boolean serverIsRunning;

	private List<Integer> listOfUsedPorts;
	private static final Random random = new Random();
	private List<ServerSocket> serverSocketList;
	private List<ServerSocket> usedServerSocketList;
	private static ServerGUI gui;
	private CopyOnWriteArrayList<ServerClientHandler> serverClientHandlersList;
	List<NetworkInterface> nets;

	public Server() throws IOException {
		serverIsRunning = true;

		serverSocketList = new ArrayList<>();
		usedServerSocketList = new ArrayList<>();
		listOfUsedPorts = new CopyOnWriteArrayList<>();
		serverClientHandlersList = new CopyOnWriteArrayList<>();
		listOfUsedPorts.add(7);//port na broadcast
		nets = Collections.list(NetworkInterface.getNetworkInterfaces());
	}

	public static void main(String[] args) {
		gui = new ServerGUI("Server");
	}

	public void run() {
		try {
			getAndDisplayServerSocketList();
			//starting a new thread to handle the discovering of servers and their ports
			UDPDiscorverHandler udpDiscorverHandler =
					new UDPDiscorverHandler(InetAddress.getByName(multicastIP), listeningPort, serverSocketList);
			new Thread(udpDiscorverHandler).start();

			while (serverIsRunning) {

				startListeningForClients();
				checkConnectedClients();
				//noinspection BusyWait
				sleep(1000);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method checks if list of available interfaces is empty and if not starts listening on them for connection from
	 * clients
	 */
	private void startListeningForClients() {
		if (!serverSocketList.isEmpty()) {
			for (ServerSocket serverSocket : serverSocketList) {
				new Thread(() -> {
					try {
						ServerClientHandler handlerToBeSoonAddedToList =
								new ServerClientHandler(serverSocket.accept(), gui);
						handlerToBeSoonAddedToList.start();
						serverSocketList.remove(serverSocket);//remove from pool of available serversockets
						usedServerSocketList.add(serverSocket);//add to busy socket list
						serverClientHandlersList.add(handlerToBeSoonAddedToList);
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}).start();
			}
		}
	}

	/**
	 * Method checks list of client handler threads and if any of them finished cleans it up, updates display, and
	 * returns interface to list to be available for reuse in future
	 */
	private void checkConnectedClients() {
		for (ServerClientHandler serverClientHandler : serverClientHandlersList) {
			if (!serverClientHandler.isRunning) {
				serverClientHandlersList.remove(serverClientHandler);
				for (ServerSocket serverSocket : usedServerSocketList) {
					if (serverSocket.getInetAddress().equals(serverClientHandler.connectionSocket.getInetAddress())) {
						serverSocketList.add(serverSocket);
						usedServerSocketList.remove(serverSocket);
						break;
					}
				}
			}
		}
	}

	/**
	 * lists all available interfaces and displays them on display in GUI
	 *
	 * @throws IOException              IO error when opening the socket
	 * @throws IllegalArgumentException if port is outside range
	 */
	private void getAndDisplayServerSocketList() throws IOException {
		String toBeDisplayed = "";
		for (NetworkInterface net : nets) {
			if (net.isUp()) {
				int portNo = randomizePortNumber();
				ServerSocket serverSocket = new ServerSocket();
				List<InetAddress> inetAddresses = Collections.list(net.getInetAddresses());
				serverSocket.bind(new InetSocketAddress(inetAddresses.get(0), portNo));
				listOfUsedPorts.add(portNo);
				toBeDisplayed += "Listening on address " + serverSocket.getInetAddress() +
						" on port " + serverSocket.getLocalPort() + "\n";
				serverSocketList.add(serverSocket);
			}
		}

		gui.updateDisplay(toBeDisplayed);
	}

	/**
	 * method to get random number that is not already in use
	 *
	 * @return free port number as int
	 */
	private int randomizePortNumber() {
		int result;
		do {
			result = random.nextInt(19999);
		} while (listOfUsedPorts.contains(result));
		listOfUsedPorts.add(result);
		return result;
	}
}
