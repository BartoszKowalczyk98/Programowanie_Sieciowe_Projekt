package clientside;

import javax.swing.*;
import java.awt.*;
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
	DecimalFormat df = new DecimalFormat("##0.0000");
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
		frequency = getFrequency();
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

	private Long getFrequency() {
		long f = 0;
		boolean validFrequency = false;
		while (!validFrequency) {
			try {
				f = Long.parseLong(JOptionPane.showInputDialog("Input desired frequency of information exchange (a " +
						"number between 10 and 1000"));
				if (f >= 10 && f <= 1000) {
					validFrequency = true;
				} else {
					JOptionPane.showMessageDialog(null, "The number you input was wrong try again");
				}
			} catch (NumberFormatException | HeadlessException exception) {
				JOptionPane.showMessageDialog(null, "The number you input was wrong try again");
			}
		}
		return f;

	}
}
