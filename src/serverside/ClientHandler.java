package serverside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

class ClientHandler extends Thread {
	long frequency = new Random().nextInt(1000) + 11;
	boolean isRunning;

	Socket connectionSocket;
	DataOutputStream dataOutputStream;
	DataInputStream dataInputStream;

	public ClientHandler(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
		try {
			this.dataInputStream = new DataInputStream(connectionSocket.getInputStream());
			this.dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				switch (dataInputStream.readUTF()) {
					case "Frequency":
						dataOutputStream.writeLong(frequency);
						break;
					case "ServerTime":
						dataOutputStream.writeLong(System.currentTimeMillis());
						break;
					default:
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
