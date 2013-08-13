package uk.co.ukmaker.netsim.netlist;

import static uk.co.ukmaker.netsim.netlist.Parser.State.COMPONENT;
import static uk.co.ukmaker.netsim.netlist.Parser.State.DONE;
import static uk.co.ukmaker.netsim.netlist.Parser.State.ENTITY;
import static uk.co.ukmaker.netsim.netlist.Parser.State.IDLE;
import static uk.co.ukmaker.netsim.netlist.Parser.State.INPUT;
import static uk.co.ukmaker.netsim.netlist.Parser.State.NET;
import static uk.co.ukmaker.netsim.netlist.Parser.State.NET_OR_END;
import static uk.co.ukmaker.netsim.netlist.Parser.State.OUTPUT;
import static uk.co.ukmaker.netsim.netlist.Parser.State.PORT_OR_COMPONENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * Really simple and dim parser Spits out Circuits ready for Compilation
 * 
 * @author duncan
 *
 */
public class Parser {
	
	private State state = IDLE;
	private int lineNumber = 0;
	
	private String[]  tokens;
	private int idx;
	
	Circuit circuit = null;
	String tok;
	String name;
	String clazz;
	String componentName;
	String portName;
	
	Map<String, Terminal> terminals = new HashMap<String, Terminal>();
	Map<String, Wire> wires = new HashMap<String, Wire>();
	Map<String, Component> components = new HashMap<String, Component>();
	
	// Map of user-defined entities referenced with the #include directive
	Map<String, Circuit> entities = new HashMap<String, Circuit>();
	
	public enum State {
		IDLE,
		INCLUDE,
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
		return circuit;
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
	
	public void parseLine(String line) throws ParsingException, InstantiationException, IllegalAccessException {
		
		tokenize(line);
		
		Wire w;
		
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
				if(entities.containsKey(tok)) {
					throw new ParsingException(lineNumber, "An entity named "+tok+" has already been defined");
				}
				circuit = new Circuit(tok);
				entities.put(tok,  circuit);
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
				if(terminals.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate input name: "+tok);
				}
				Terminal input = new Terminal(tok, Terminal.Type.INPUT);
				terminals.put(tok, input);
				circuit.addTerminal(input);
				
				break;
				
			case OUTPUT:
				state = PORT_OR_COMPONENT;
				if(terminals.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate output name: "+tok);
				}
				
				Terminal output = new Terminal(tok, Terminal.Type.OUTPUT);
				terminals.put(tok, output);
				circuit.addTerminal(output);
				break;
				
			case COMPONENT:
				
				Component c;
				
				if(components.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate component name: "+tok);
				}
				// A component class name either starts with a $ for a user-defined entity or is taken as the class name of a model
				clazz = getToken();
				state = PORT_OR_COMPONENT;
				if(clazz.startsWith("$")) {
					
					clazz = clazz.substring(1);
					
					if(!entities.containsKey(clazz)) {
						throw new ParsingException(lineNumber, "No such user-defined entity: "+clazz);
					}
					
					c = entities.get(clazz);
					
				} else {
					Model model;
					try {
						model = (Model)Class.forName("uk.co.ukmaker.netsim.components."+clazz).newInstance();
						// Build a Device to represent it
						c = new Device(tok, clazz);
						for(Pin p : model.getPins().values()) {
							if(p instanceof InputPin) {
								((Device)c).addTerminal(new Terminal(p.getName(), Terminal.Type.INPUT));
							} else {
								((Device)c).addTerminal(new Terminal(p.getName(), Terminal.Type.OUTPUT));
							}
						}
						
					} catch (ClassNotFoundException e) {
						throw new ParsingException(lineNumber, "Device not found: "+clazz);
					}
				}
				components.put(tok,  c);
				circuit.addComponent(c);
				break;
				
			case NET:
				
				if(wires.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate net name: "+tok);
				}
				
				w = new Wire();
				
				wires.put(tok,  w);
				
				ungetToken();
				
				while(more()) {
					name = getToken();
					// if there is a dot then it is the name of the port on a component
					// otherwise it is a port declared here
					if(name.contains(".")) {
						String[] bits = name.split("\\.");
						componentName = bits[0];
						portName = bits[1];
						
						if(!components.containsKey(componentName)) {
							throw new ParsingException(lineNumber, "Unknown component: "+componentName);
						}
						
						w.addTerminal(components.get(componentName).getTerminal(portName));
						
					} else {
						w.addTerminal(terminals.get(name));
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
		tokens = line.trim().split("\\s\\s*");
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
