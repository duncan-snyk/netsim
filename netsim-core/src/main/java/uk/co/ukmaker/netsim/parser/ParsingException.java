package uk.co.ukmaker.netsim.parser;

public class ParsingException extends Exception {
	
	private int line;
	private String token;
	
	public ParsingException(int line, String token) {
		super();
		this.line = line;
		this.token = token;
	}
	
	public String toString() {
		return String.format("%d - %s", line, token);
	}

}
