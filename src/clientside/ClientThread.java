package clientside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ClientThread extends Thread {
	boolean isRunning;
	long t1, t2, tCli, tServ;
	double delta;
	long frequency;

	SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	DecimalFormat df = new DecimalFormat("###.###");
	Socket socket;
	DataOutputStream dataOutputStream;
	DataInputStream dataInputStream;
	ClientGui guiHandle;


	public ClientThread(Socket socket, ClientGui guiHandle) {
		isRunning = true;
		this.socket = socket;
		try {
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.guiHandle = guiHandle;
	}

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

				delta = (double) tServ + (t2 - t1) / 2.0 - (double) tCli;
				tCli += delta;
//				System.out.printf("Client time + delta: " + ISO8601DATEFORMAT.format(tCli) + " Delta = %.3f\n", delta);

				guiHandle.updateDisplay("Client time + delta: " +
						ISO8601DATEFORMAT.format(tCli) +
						" Delta = " + df.format(delta) + "\n");
				//noinspection BusyWait
				sleep(frequency);

			} catch (InterruptedException | IOException e) {
				isRunning = false;
				e.printStackTrace();
			}
		}
	}
}
