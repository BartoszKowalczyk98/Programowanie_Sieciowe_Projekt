package serverside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UDPDiscorverHandler implements Runnable {
	private int listeningPort;
	private List<ServerSocket> serverSocketList;
	private InetAddress group;
	private byte[] buf;
	private MulticastSocket multicastSocket;

	public UDPDiscorverHandler(InetAddress group, int listeningPort,
							   List<ServerSocket> serverSocketList) throws IOException {
		this.listeningPort = listeningPort;

		this.group = group;
		multicastSocket = new MulticastSocket(listeningPort);
		this.serverSocketList = new CopyOnWriteArrayList<>(serverSocketList);
		multicastSocket.joinGroup(this.group);
		buf = new byte[1024];
	}

	@Override
	public void run() {
		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				multicastSocket.receive(packet);
				String received = new String(
						packet.getData(), 0, packet.getLength());
				if ("DISCOVERY".equals(received)) {
					String messageToSend = "";
					for (ServerSocket serverSocket : serverSocketList) {
						messageToSend += "INetAddress " + serverSocket.getInetAddress() + " port " + serverSocket.getLocalPort() + "\n";
					}
					buf = messageToSend.getBytes();
					packet = new DatagramPacket(buf, buf.length, group, listeningPort);
					multicastSocket.send(packet);
					packet = new DatagramPacket("END".getBytes(), "END".length(), group, listeningPort);
					multicastSocket.send(packet);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}


}
