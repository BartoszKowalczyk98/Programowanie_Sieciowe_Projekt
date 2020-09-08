package clientside;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client implements Runnable {// to be usuniete jak przestane testowac z palucha
	private static final Scanner scanner = new Scanner(System.in);
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
		new ClientGui("tytulek");
	}

	public void run() {
		try {
			while (!isClientConnected()) {
				udpDiscoverClient.sendDiscoverSignal();
				String[] serverlist = udpDiscoverClient.receiveResponseAfterDiscovery();
				if (serverlist == null) {
					System.out.println("no servers available");
					continue;
				}
				findLastConnectedServer(serverlist);// TODO: 10.08.2020 make it not only print
				gui.updateUpperLabel("listing all avaliable servers");
				gui.getNewServerList(serverlist);
				gui.updateUpperLabel("waiting for next refresh");
				sleep(1000);
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void connectToServerWithGivenPort(int port, String address) {
		try {
			SocketAddress socketAddress = new InetSocketAddress(address, port);
			socket.connect(socketAddress);

			clientThread = new ClientThread(socket, gui);
			clientThread.start();
//		clientThread.join();

			saveServerAsDefault(address);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public boolean isClientConnected() {
		if (clientThread != null)
			return clientThread.isRunning;
		else
			return false;
	}

	private void findLastConnectedServer(String[] serverlist) {
		String lastConnectedServer = readFile();
		if (lastConnectedServer == null) {
			System.out.println("no servers are remembererd");
			return;
		}
		for (String server : serverlist) {
			if (server.contains(lastConnectedServer)) {
				return;
			}
		}
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
			System.out.println("File created: " + fileToRecognisePreviousServer.getName());
		} else {
			System.out.println("File already exists.");
		}
	}

	private static int getPort() throws InputMismatchException {
		System.out.println("Podaj nr portu do połączenia: ");
		int port = scanner.nextInt();
		scanner.nextLine();
		return port;
	}
}
