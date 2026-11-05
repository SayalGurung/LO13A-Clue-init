package clueGame;

/**
 * Thrown when a setup or layout config file is badly formatted.
 */
public class BadConfigFormatException extends Exception {
	public BadConfigFormatException() {
		super("Bad configuration file format");
	}

	public BadConfigFormatException(String message) {
		super(message);
	}
}
