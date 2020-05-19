package serverside;

import javax.swing.*;
import java.io.IOException;

public class ServerGUI {
	private JPanel panel1;
	private JButton startButton;
	private JButton stopButton;
	private JTextArea display;
	private JLabel isServerUp;
	private JScrollPane scrollPane;
	private JPanel lowerPanel;
	private JPanel upperPanel;
	private JFrame jFrame;
	private Server serverClassHandle;

	public ServerGUI(String title) {
		jFrame = new JFrame(title);
		jFrame.setContentPane(panel1);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.pack();
		jFrame.setSize(600,400);
		startButton.setText("Start");
		stopButton.setText("Stop");
		isServerUp.setText("Server is not up");

		//server handle constructing
		try {
			serverClassHandle = new Server();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		stopButton.setEnabled(false);
		startButton.addActionListener(e -> {
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			new Thread(serverClassHandle).start();
			isServerUp.setText("Server is running");
		});

		stopButton.addActionListener(e -> {
			serverClassHandle.serverIsRunning = false;
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			isServerUp.setText("Server is not up");
		});
		display.setText(serverClassHandle.getToBeDisplayed());


		jFrame.setVisible(true);
	}

	public void updateDisplay(String toBeDisplayed){
		display.append(toBeDisplayed);
	}
}
