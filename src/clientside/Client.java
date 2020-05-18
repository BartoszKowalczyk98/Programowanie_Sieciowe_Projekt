package clientside;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        try (Socket socket = new Socket()){
            // TODO: 18.05.2020 multicast o port
            int port = getPort();

            // TODO: 18.05.2020 wybieranie servera z listy w gui(?)
            InetAddress inetAddress = InetAddress.getByName("localhost");
            SocketAddress socketAddress =new InetSocketAddress(inetAddress,port);
            socket.connect(socketAddress);

            ClientThread clientThread = new ClientThread(socket);
            while (clientThread.isRunning);//czekanie może lepiej by było tutaj pomyśleć nad sleepem(?)
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Wpisałeś błędne dane!");
        }
    }

    //pobieranie nr portu
    private static int getPort() throws InputMismatchException {
        System.out.println("Podaj nr portu do połączenia: ");
        int port = scanner.nextInt();
        scanner.nextLine();
        return port;
    }
}
