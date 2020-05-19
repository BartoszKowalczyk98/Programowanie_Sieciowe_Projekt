import clientside.Client;
import serverside.Server;

import java.io.IOException;


public class TesterIgues {
	public static void main(String[] args) {
		try {
			new Thread(new Server()).start();
			new Thread(new Client()).start();

		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
