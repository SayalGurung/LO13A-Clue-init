package clueGame;

//this is thrown when the board layour config file is badly formatted

public class BadConfigFormatException extends Exception {
	
	public BadConfigFormatException() {
		super ("bad configuration file format");
	}
	
	public BadConfigFormatException(String message) {
		super(message);
	}
}
