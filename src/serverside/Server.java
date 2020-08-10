package serverside;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {
	private final int listeningPort = 7;
	private final String multicastIP = "230.0.0.0";
	boolean serverIsRunning;

	private List<Integer> listOfUsedPorts;
	private static final Random random = new Random();
	private List<ServerSocket> serverSocketList;
	private static ServerGUI gui;
	List<NetworkInterface> nets;

	public Server() throws IOException {
		serverIsRunning = true;

		serverSocketList = new CopyOnWriteArrayList<>();
		listOfUsedPorts = new CopyOnWriteArrayList<>();
		listOfUsedPorts.add(7);//port na broadcast
		nets = Collections.list(NetworkInterface.getNetworkInterfaces());

		// TODO: 19.05.2020 when poping from listing use getlocalport from serversocket
	}

	public static void main(String[] args) {
//		gui = new ServerGUI("Server");
		try {
			new Thread(new Server()).start();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void run() {
		try {
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
			System.out.println(toBeDisplayed);//temp do szybszego testowania

//			gui.updateDisplay(toBeDisplayed);

			//starting a new thread to handle the discovering of servers and their ports
			UDPDiscorverHandler udpDiscorverHandler =
					new UDPDiscorverHandler(InetAddress.getByName(multicastIP), listeningPort, serverSocketList);
			new Thread(udpDiscorverHandler).start();

			while (serverIsRunning) {
				if (serverSocketList.size() > 0) {
					ServerSocket serverSocket = serverSocketList.remove(0);
					new ServerClientHandler(serverSocket.accept()).start();
				}
//				else {
//					gui.updateDisplay("All interfaces are occupied\n");
//				}
			}
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
