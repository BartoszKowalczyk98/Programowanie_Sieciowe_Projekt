package clientside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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

	public void sendDiscoverSignal() throws IOException {
		buf = "DISCOVERY".getBytes();

		DatagramPacket packet
				= new DatagramPacket(buf, buf.length, group, port);
		datagramSocket.send(packet);
	}

	public String[] receiveResponseAfterDiscovery() throws IOException {
		while (true) {
			buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			multicastSocket.receive(packet);
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
				for (String line : lines) {
					if (!line.contains("127.0.0.1")) {
						System.out.println(line);
					}
				}
				return lines;
			}
		}
		return null;
	}
}
