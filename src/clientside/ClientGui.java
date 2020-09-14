package clientside;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGui {
	private JTextArea display;
	private JPanel mainPanel;
	private JLabel upperLabel;
	private JPanel serverListPanel;
	private JFrame jFrame;
	private Client clientClassHandle;

	public ClientGui(String title) {
		jFrame = new JFrame(title);
		jFrame.setContentPane(mainPanel);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.pack();
		jFrame.setSize(800, 600);
		upperLabel.setText("Client is not connected to any server");

		try {
			clientClassHandle = new Client(this);
		} catch (IOException exception) {
			exception.printStackTrace();
			return;
		}

		jFrame.setVisible(true);
		clientClassHandle.run();
	}

	public void updateDisplay(String toBeDisplayed) {
		display.append(toBeDisplayed);
	}

	public void updateUpperLabel(String toBeDisplayed) {
		upperLabel.setText(toBeDisplayed);
	}

	public void getNewServerList(String[] serverList, String lastConnectedServer) {
		serverListPanel.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.gridx = 0;
		c.weighty = 1;

		for (String server : serverList) {

			Container container = new Container();
			GridLayout gridLayout = new GridLayout(1, 2);
			container.setLayout(gridLayout);
			JLabel serverAddress = new JLabel(server);
			JButton serverConnectButton = new JButton("Connect");
			serverConnectButton.addActionListener(e -> {
				updateUpperLabel("Connected to server and exchanging information.");
				String[] serverinfo = server.split(" ");
				String address = serverinfo[1].substring(1);
				int port = Integer.valueOf(serverinfo[3]);
				clientClassHandle.connectToServerWithGivenPort(port, address);
				serverConnectButton.setEnabled(false);
				for (Component component : serverListPanel.getComponents()) {
					if (component instanceof JButton) {
						component.setEnabled(false);
					}
				}
				serverListPanel.revalidate();
				serverListPanel.repaint();
			});

			if (server.equals(lastConnectedServer)) {
				serverAddress.setText(server + " <-Previously connected");
			}
			container.add(serverAddress);
			container.add(serverConnectButton);

			serverListPanel.add(container, c);
		}
		serverListPanel.revalidate();
		serverListPanel.repaint();
	}
}
