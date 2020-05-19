package clientside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {
	boolean isRunning;
	long t1, t2, tCli, tServ;
	double delta;
	long frequency;

	Socket socket;
	DataOutputStream dataOutputStream;
	DataInputStream dataInputStream;


	public ClientThread(Socket socket) {
		isRunning = true;
		this.socket = socket;
		try {
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * T1 - starting time
	 * send gimmie ur time
	 * recieve time Tserv
	 * remember time T2 // Tcli = T2
	 * calculate delta= Tserv + (T2-T1)/2 - Tcli
	 * sout Tcli + delta
	 * sout delta
	 * sleep na f:(10-1000)ms
	 * */

	@Override
	public void run() {
		System.out.println("Client thread runnin");
		try {
			dataOutputStream.writeUTF("Frequency");
			frequency = dataInputStream.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (isRunning) {
			t1 = System.currentTimeMillis();
			try {
				dataOutputStream.writeUTF("ServerTime");
				tServ = dataInputStream.readLong();
				t2 = tCli = System.currentTimeMillis();
				delta = tServ + (t2 - t1) / 2.0 - tCli;
				tCli += delta;
				System.out.println("Client time + delta" + tCli + " Delta = " + delta);
				sleep(frequency);

			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
