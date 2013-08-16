package uk.co.ukmaker.netsim.parser;

public class Token {
	
	public enum Type {
		INCLUDE,
		ENTITY,
		SIMULATION,
		INPUT,
		OUTPUT,
		COMPONENT,
		NET,
		SOURCE,
		PROBE,
		GENERATE,
		EXPECT,
		VECTOR
	}
	
	private Type type;

	
}
