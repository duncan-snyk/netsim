package uk.co.ukmaker.netsim.netlist;

import static uk.co.ukmaker.netsim.netlist.Parser.State.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/**
	 * Nasty variable set when the Parser is first invoked by a command which loads a file
	 * Sets the base working directory for includes
	 */
	private File baseDir = null;
	
	private State state = IDLE;
	private int lineNumber = 0;
	
	private String[]  tokens;
	private int idx;
	
	private static Pattern portNamePattern = Pattern.compile("([a-zA-Z0-9_]+)(\\[([0-9]+)\\])?");
	
	Circuit circuit = null;
	String tok;
	String name;
	String clazz;
	String componentName;
	String portName;
	
	Map<String, Terminal> terminals;
	Map<String, Wire> wires;
	Map<String, Component> components;
	
	// Map of user-defined entities referenced with the #include directive
	// the key is the entity name
	Map<String, File> includes = new HashMap<String, File>();
	
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
	
	/**
	 * Default no-args constructor
	 */
	public Parser() {
		
	}
	
	/**
	 * Constructor invoked by include
	 */
	public Parser(Parser p) {
		includes = p.includes;
	}
	
	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}
	
	public File getBaseDir() {
		return baseDir;
	}
	
	public Circuit getEntity() {
		return circuit;
	}
	
	public void parse(InputStream input) throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
		String line;
		
		while((line = br.readLine()) != null) {
			lineNumber++;
			parseLine(line);
		}
		
		br.close();
	}
	
	public void parseLine(String line) throws Exception {
		
		tokenize(line);
		
		while(more() && state != DONE) {
			
			tok = getToken();
			
			if(tok.startsWith("#") || "".equals(tok)) {
				// discard the rest of the line
				return;
			}
			
			switch(state) {
			case IDLE: // expect an entity
				
				if("include".equals(tok)) {
					state = INCLUDE;
				} else if("entity".equals(tok)) {
					state = ENTITY;
				} else{
					throw new ParsingException(lineNumber, tok);
				}
				
				break;
				
			case INCLUDE:
				// expect a filename
				File includeFile;
				if(baseDir == null) {
					includeFile = new File(tok);
				} else {
					includeFile = new File(baseDir.getAbsolutePath() + File.separator + tok);
				}
				
				if(!includes.containsKey(includeFile.getAbsolutePath())) {
				
					if(!includeFile.exists()) {
						throw new ParsingException(lineNumber, "Cannot find file "+includeFile.getAbsolutePath()+" to include");
					}
					
					if(!includeFile.canRead()) {
						throw new ParsingException(lineNumber, "Cannot read include file "+includeFile.getAbsolutePath());
					}
					
					if(!includeFile.isFile()) {
						throw new ParsingException(lineNumber, "Cannot include "+includeFile.getAbsolutePath()+" - it is not a file");
					}
										
					FileInputStream is;
					
					try {
						is = new FileInputStream(includeFile);
					} catch (FileNotFoundException e1) {
						throw new ParsingException(lineNumber, "Error handling include file "+includeFile.getAbsolutePath()+" - "+e1.getMessage());
					}
					
					Parser p = new Parser(this);
					p.setBaseDir(baseDir);
					p.parse(is);
					
					includes.put(p.getEntity().getName(), includeFile);
				}
				
				includeFile = null;
				
				state = IDLE;
				break;
				
			case ENTITY: // expect a valid name
			//	if(includes.containsKey(tok)) {
			//		throw new ParsingException(lineNumber, "An entity named "+tok+" has already been defined in file "+
			//				includes.get(tok).getAbsolutePath());
			//	}
				circuit = new Circuit(tok);
				terminals = new HashMap<String, Terminal>();
				wires = new HashMap<String, Wire>();
				components = new HashMap<String, Component>();

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
				attachTerminals(generatePortNames(tok), Terminal.Type.INPUT);
				break;
				
			case OUTPUT:
				state = PORT_OR_COMPONENT;
				attachTerminals(generatePortNames(tok), Terminal.Type.OUTPUT);
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
					
					if(!includes.containsKey(clazz)) {
						throw new ParsingException(lineNumber, "No such user-defined entity: "+clazz);
					}
					
					// OK. Cloning a previously loaded definition is tricky
					// Essentially cloning it's internal network is the same as what the Compiler
					// does flattening a netlist. So let's be lazy here and just reparse the file so we get a new one
					
					c = include(clazz);
					c.setName(tok);
					
				} else {
					Model model;
					try {
						
						clazz = "uk.co.ukmaker.netsim.models."+clazz;
						model = (Model)Class.forName(clazz).newInstance();
						
						// Build a Device to represent it
						c = new Device(tok, model);
						
					} catch (ClassNotFoundException e) {
						throw new ParsingException(lineNumber, "Device not found: "+clazz);
					}
				}
				components.put(tok,  c);
				try {
					circuit.addComponent(c);
				} catch (Exception e) {
					throw new ParsingException(lineNumber, e.getMessage());
				}
				break;
				
			case NET:
				
				if(wires.containsKey(tok)) {
					throw new ParsingException(lineNumber, "Duplicate net name: "+tok);
				}
				
				Wire w = new Wire();
				Terminal t;
				
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
						
						t = components.get(componentName).getTerminal(portName);
						t.setExternalWire(w);
					} else {
						t = terminals.get(name);
						
						if(t == null) {
							throw new ParsingException(lineNumber, "No such terminal: "+name);
						}
						t.setInternalWire(w);
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
	
	public static String[] generatePortNames(String baseName) throws Exception {
		Matcher m = portNamePattern.matcher(baseName);
		
		if(!m.matches()) {
			throw new Exception(baseName + " is not a valid port name");
		}
		
		String portName = m.group(1);
		String[] names;
		
		if(m.group(3) != null) {
			int max = Integer.parseInt(m.group(3));
			names = new String[max];
			
			for(int i=0; i<max; i++) {
				names[i] = portName+i;
			}
		} else {
			names = new String[1];
			names[0] = portName;
		}
		
		return names;
	}
	
	public void attachTerminals(String[] names, Terminal.Type type) throws ParsingException {
		for(String name : names) {
			if(terminals.containsKey(name)) {
				throw new ParsingException(lineNumber, "Duplicate terminal name: "+name);
			}
			Terminal terminal = new Terminal(circuit, name, type);
			terminals.put(name, terminal);
			circuit.addTerminal(terminal);
		}
	}
	
	public Component include(String name) throws Exception {
		FileInputStream is;
		
		try {
			is = new FileInputStream(includes.get(name));
		} catch (FileNotFoundException e1) {
			throw new ParsingException(lineNumber, "Error handling include file "+includes.get(name).getAbsolutePath()+" - "+e1.getMessage());
		}
		
		Parser p = new Parser(this);
		p.setBaseDir(baseDir);
		p.parse(is);
		
		return p.getEntity();
	}
}
