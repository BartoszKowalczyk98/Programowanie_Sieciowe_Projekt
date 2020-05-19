import clientside.Client;
import serverside.Server;

public class TesterIgues {
	public static void main(String[] args) {

		new Thread(new Client()).start();
		Server.main(null);
	}
}
