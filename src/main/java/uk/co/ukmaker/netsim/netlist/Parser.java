package uk.co.ukmaker.netsim.netlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.Circuit;
import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.Net;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.InputPort;
import uk.co.ukmaker.netsim.ports.Output;
import uk.co.ukmaker.netsim.ports.OutputPort;
import static uk.co.ukmaker.netsim.netlist.Parser.State.*;

/**
 * Really simple and dim parser
 * 
 * @author duncan
 *
 */
public class Parser {
	
	private Map<String, Component> entities = new HashMap<String, Component>();
	private State state = IDLE;
	private int lineNumber = 0;
	
	private String[]  tokens;
	private int idx;
	
	Circuit entity = null;
	String tok;
	String name;
	String clazz;
	String componentName;
	String portName;
	
	Map<String, Input> inputs = new HashMap<String, Input>();
	Map<String, Output> outputs = new HashMap<String, Output>();
	Map<String, Net> nets = new HashMap<String, Net>();
	Map<String, Component> components = new HashMap<String, Component>();
	
	public enum State {
		IDLE,
		ENTITY,
		PORT,
		PORT_OR_COMPONENT,
		INPUT,
		OUTPUT,
		COMPONENT,
		NET_OR_END,
		NET,
		DONE
	}
	
	public Circuit getEntity() {
		return entity;
	}
	
	public void parse(InputStream input) throws IOException, ParsingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
		String line;
		
		while((line = br.readLine()) != null) {
			lineNumber++;
			parseLine(line);
		}
		
		br.close();
	}
	
	public void parseLine(String line) throws ParsingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		tokenize(line);
		
		while(more() && state != DONE) {
			
			tok = getToken();
			
			if(tok.startsWith("#") || "".equals(tok)) {
				// discard the rest of the line
				return;
			}
			
			switch(state) {
			case IDLE: // expect an entity
				if(!"entity".equals(tok)) {
					throw new ParsingException(lineNumber, tok);
				}
				state = ENTITY;
				break;
				
			case ENTITY: // expect a valid name
				entity = new Circuit(tok);
				state = PORT_OR_COMPONENT;
				break;
				
			case PORT_OR_COMPONENT:
				if("input".equals(tok)) {
					state = INPUT;
				} else if("output".equals(tok)) {
					state = OUTPUT;
				} else if("component".equals(tok)) {
					state = COMPONENT;
				} else if("net".equals(tok)) {
					state = NET;
				} else {
					throw new ParsingException(lineNumber, tok);
				}
				break;
			
			case INPUT:
				state = PORT_OR_COMPONENT;
				if(inputs.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate input name: "+tok);
				}
				Input input = new InputPort(entity, tok);
				inputs.put(tok, input);
				entity.getPorts().put(tok, input);
				
				break;
				
			case OUTPUT:
				state = PORT_OR_COMPONENT;
				if(outputs.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate output name: "+tok);
				}
				
				Output output = new OutputPort(entity, tok);
				outputs.put(tok, output);
				entity.getPorts().put(tok,  output);
				break;
				
			case COMPONENT:
				if(components.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate component name: "+tok);
				}
				clazz = getToken();
				state = PORT_OR_COMPONENT;
				Component c = (Component)Class.forName("uk.co.ukmaker.netsim.components."+clazz).newInstance();
				components.put(tok, c);
				entity.addComponent(c);
				break;
				
			case NET:
				
				if(nets.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate net name: "+tok);
				}
				
				Net n = entity.addNet(tok);
				
				nets.put(tok,  n);
				
				ungetToken();
				
				while(more()) {
					name = getToken();
					// if there is a dot then it is the name of the port on a component
					// otherwise it is a port declared here
					if(name.contains(".")) {
						String[] bits = name.split("\\.");
						componentName = bits[0];
						portName = bits[1];
						n.addPort(components.get(componentName).getPorts().get(portName));
					} else {
						if(inputs.containsKey(name)) {
							n.addPort(inputs.get(name));
						} else {
							n.addPort(outputs.get(name));
						}
					}
				}
				
				state = NET_OR_END;
				
				break;
				
			case NET_OR_END:
				if("net".equals(tok)) {
					state = NET;
				} else if(";".equals(tok)) {
					state = DONE;
				} else {
					throw new ParsingException(lineNumber, tok);
				}
			}
		}
	}
	
	public void tokenize(String line) {
		tokens = line.trim().split("\\s");
		idx = 0;
	}
	
	public String getToken() throws ParsingException {
		if(idx >= tokens.length) {
			throw new ParsingException(lineNumber, "Unexpected EOL");
		}
		return tokens[idx++];
	}
	
	public boolean more() {
		return idx < tokens.length;
	}
	
	public void ungetToken() {
		if(idx == 0) {
			throw new RuntimeException("Attempt to unget nothing");
		}
		idx--;
	}
}
