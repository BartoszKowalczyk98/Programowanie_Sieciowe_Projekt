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

public class Client implements Runnable {// to be usuniete jak przestane testowac z palucha
	private static final Scanner scanner = new Scanner(System.in);
	private final String multicastIP = "230.0.0.0";
	private final int listeningPort = 7;
	private UDPDiscoverClient udpDiscoverClient;


	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}

	private static int getPort() throws InputMismatchException {
		System.out.println("Podaj nr portu do połączenia: ");
		int port = scanner.nextInt();
		scanner.nextLine();
		return port;
	}

	public void run() {
		try (Socket socket = new Socket()) {

			udpDiscoverClient = new UDPDiscoverClient(InetAddress.getByName(multicastIP), listeningPort);
			udpDiscoverClient.sendDiscoverSignal();
			String[] serverlist = udpDiscoverClient.receiveResponseAfterDiscovery();
			findLastConnectedServer(serverlist);
			// TODO: 18.05.2020 wybieranie servera z listy w gui(?)
			int port = getPort();
			InetAddress inetAddress = InetAddress.getByName("localhost");
			SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
			socket.connect(socketAddress);

			ClientThread clientThread = new ClientThread(socket);
			System.out.println(socket.getInetAddress());
			System.out.println(socket.getLocalAddress());
			clientThread.start();
			clientThread.join();
			saveServerAsDefault(socket);
		} catch (IOException | IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (InputMismatchException e) {
			System.out.println("Wpisałeś błędne dane!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void findLastConnectedServer(String[] serverlist) {
		String lastConnectedServer = readFile();
		if (lastConnectedServer == null) {
			System.out.println("no servers are remembererd");
			return;
		}
		for (String server : serverlist) {
			if (server.contains(lastConnectedServer)) {
				System.out.println("default server would be:");
				System.out.println(server);
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

	private void saveServerAsDefault(Socket socket) throws IOException {
		createFile();
		writeToFile(socket);
	}

	private void writeToFile(Socket socket) throws IOException {
		FileWriter myWriter = new FileWriter("lastRecordedServer.txt");
		myWriter.write(socket.getLocalAddress().toString());
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
}
