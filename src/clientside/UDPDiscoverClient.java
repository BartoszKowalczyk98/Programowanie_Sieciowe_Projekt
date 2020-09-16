package clientside;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//https://www.baeldung.com/java-broadcast-multicast
public class UDPDiscoverClient {
	private DatagramSocket datagramSocket;
	private InetAddress group;
	private byte[] buf;
	private int port;
	private MulticastSocket multicastSocket;

	public UDPDiscoverClient(InetAddress group, int port) throws IOException {
		this.group = group;
		this.port = port;
		datagramSocket = new DatagramSocket();
		multicastSocket = new MulticastSocket(port);
		multicastSocket.joinGroup(group);
		buf = new byte[1024];
	}

	/**
	 * Method that sends out a datagram packet to multicast group and asks for available servers
	 *
	 * @throws IOException if I/O error occurs during sending packet
	 */
	public void sendDiscoverSignal() throws IOException {
		buf = "DISCOVERY".getBytes();

		DatagramPacket packet
				= new DatagramPacket(buf, buf.length, group, port);
		datagramSocket.send(packet);
	}

	/**
	 * Method that waits for respose from server and returns serverlist as string array
	 *
	 * @return Server list as String array, or null if response timed out
	 * @throws IOException if I/O error occurs
	 */
	public String[] receiveResponseAfterDiscovery() throws IOException {
		//timeout to actually proceed with program and not get stuck
		multicastSocket.setSoTimeout(3000);
		while (true) {
			buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				multicastSocket.receive(packet);
			} catch (SocketTimeoutException e) {
				return null;
			}
			String received = new String(
					packet.getData(), 0, packet.getLength());
			if ("END".equals(received)) {
				break;
			}
			if (!received.equals("DISCOVERY")) {
				if (received.length() == 0) {
					return null;
				}
				String[] lines = received.split("\\r?\\n");

				List<String> list = new ArrayList<>(Arrays.asList(lines));
				for (String line : list) {
					if (line.contains("127.0.0.1")) {
//						if needed to filter localhost
						list.remove(line);
//						System.out.println("localhost would be filtered out here!");
					}
				}
				lines = list.toArray(new String[0]);
				return lines;
			}
		}
		return null;
	}
}
