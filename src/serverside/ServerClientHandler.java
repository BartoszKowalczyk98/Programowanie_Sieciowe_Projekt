package serverside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

class ServerClientHandler extends Thread {
	long frequency = new Random().nextInt(1000) + 11;
	boolean isRunning;

	Socket connectionSocket;
	DataOutputStream dataOutputStream;
	DataInputStream dataInputStream;

	public ServerClientHandler(Socket connectionSocket) {
		isRunning = true;
		this.connectionSocket = connectionSocket;
		try {
			this.dataInputStream = new DataInputStream(connectionSocket.getInputStream());
			this.dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Socket was not connected!");
		}
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				//noinspection EnhancedSwitchMigration
				switch (dataInputStream.readUTF()) {
					case "Frequency":
						dataOutputStream.writeLong(frequency);
						break;
					case "ServerTime":
						dataOutputStream.writeLong(System.currentTimeMillis());
						break;
					default:
						System.out.println("Unknown Command!");
						isRunning = false;
						break;
				}
			} catch (IOException e) {
				isRunning = false;
				System.out.println("Client closed connection!");
			}
		}
	}
}
