package Exceptions;

public class UnknownCommandException extends Exception {
	public UnknownCommandException() {
	}

	public UnknownCommandException(String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		return "Server Encountered unknown command during handling of client!";
	}
}
