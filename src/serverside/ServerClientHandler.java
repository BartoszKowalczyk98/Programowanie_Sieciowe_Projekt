package serverside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ServerClientHandler extends Thread {
	boolean isRunning;

	protected Socket connectionSocket;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private ServerGUI guiHandle;

	public ServerClientHandler(Socket connectionSocket, ServerGUI guiHandle) {
		this.guiHandle = guiHandle;
		isRunning = true;
		this.connectionSocket = connectionSocket;
		try {
			this.dataInputStream = new DataInputStream(connectionSocket.getInputStream());
			this.dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
		} catch (IOException e) {
			this.guiHandle.updateDisplay("Error in opening streams with client!\n");
		}
	}

	@Override
	public void run() {
		guiHandle.updateDisplay("Client " + connectionSocket.getInetAddress() + " connected and exchanging information\n");
		while (isRunning) {
			try {
				//noinspection EnhancedSwitchMigration
				switch (dataInputStream.readUTF()) {
					case "ServerTime":
						dataOutputStream.writeLong(System.currentTimeMillis());
						break;
					case "End":
						isRunning = false;
						guiHandle.updateDisplay("Client disconnected!\n");
						break;
					default:
						guiHandle.updateDisplay("Client sent an unknown command\n");
						isRunning = false;
						break;
				}
			} catch (IOException e) {
				isRunning = false;
				guiHandle.updateDisplay("Client disconnected!\n");
			}
		}
	}
}
