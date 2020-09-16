package clientside;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client {
	private final String multicastIP = "230.0.0.0";
	private final int listeningPort = 7;
	private UDPDiscoverClient udpDiscoverClient;
	private static ClientGui gui;

	private ClientThread clientThread;
	private Socket socket;

	public Client(ClientGui guiHandle) throws IOException {
		udpDiscoverClient = new UDPDiscoverClient(InetAddress.getByName(multicastIP), listeningPort);
		socket = new Socket();
		gui = guiHandle;
	}


	public static void main(String[] args) {
		new ClientGui("Client");
	}

	public void run() {
		try {
			while (!isClientConnected()) {
				gui.updateUpperLabel("Sending Discover signal!");
				udpDiscoverClient.sendDiscoverSignal();
				String[] serverlist = udpDiscoverClient.receiveResponseAfterDiscovery();
				if (serverlist == null) {
					gui.updateUpperLabel("No servers available!");
					sleep(1000);
					continue;
				}
				String lastConnectedServer = findLastConnectedServer(serverlist);

				gui.updateUpperLabel("listing all avaliable servers");
				gui.getNewServerList(serverlist, lastConnectedServer);
				sleep(1000);
				gui.updateUpperLabel("waiting for next refresh");
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void disconnectFromServer() {
		clientThread.isRunning.set(false);
	}

	/**
	 * as the name implies method used to connect to sever with known port
	 * and start a thread that exchanges information with server
	 *
	 * @param port    port number to server
	 * @param address ip address of server to connect to
	 */
	public void connectToServerWithGivenPort(int port, String address) {
		try {
			SocketAddress socketAddress = new InetSocketAddress(address, port);
			socket.connect(socketAddress);

			clientThread = new ClientThread(socket, gui);
			clientThread.start();
			saveServerAsDefault(address);
			gui.updateUpperLabel("Connected to server and exchanging information!");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public boolean isClientConnected() {
		if (clientThread != null)
			return clientThread.isRunning.get();
		else
			return false;
	}

	/**
	 * method that looks for any previously saved server
	 * and compares it with actual list given by server
	 *
	 * @param serverlist list of currently available servers
	 * @return "None" if no default server was found, ip address if one was found
	 */
	private String findLastConnectedServer(String[] serverlist) {
		String lastConnectedServer = readFile();
		if (lastConnectedServer == null) {
			gui.updateUpperLabel("No servers were recognised");
			return "None";
		}
		for (String server : serverlist) {
			if (server.contains(lastConnectedServer)) {
				return server;
			}
		}
		return "None";
	}

	private String readFile() {
		File myObj = new File("lastRecordedServer.txt");
		Scanner myReader;
		try {
			myReader = new Scanner(myObj);
		} catch (FileNotFoundException e) {
			return null;
		}
		String data = null;
		while (myReader.hasNextLine()) {
			data = myReader.nextLine();
		}
		myReader.close();
		return data;
	}

	/**
	 * whole method that saves lastly connected server that includes
	 * creating file and writing a single line inside
	 *
	 * @param defaultServerAddress ip address as string
	 * @throws IOException
	 */
	private void saveServerAsDefault(String defaultServerAddress) throws IOException {
		createFile();
		writeToFile(defaultServerAddress);
	}

	private void writeToFile(String defaultServerAddress) throws IOException {
		FileWriter myWriter = new FileWriter("lastRecordedServer.txt");
		myWriter.write(defaultServerAddress);
		myWriter.close();
	}

	private void createFile() throws IOException {
		File fileToRecognisePreviousServer = new File("lastRecordedServer.txt");
		if (fileToRecognisePreviousServer.createNewFile()) {
			gui.updateUpperLabel("File created " + fileToRecognisePreviousServer.getName());
		} else {
			gui.updateUpperLabel("Default Server saved");
		}
	}

}
