package uk.co.ukmaker.netsim.netlist;

import uk.co.ukmaker.netsim.models.Model;

/**
 * An abstract class providing easy support for connecting to a circuit's terminals
 * @author duncan
 *
 */
public class TestClip<T extends Model> extends Device<T> {

	public TestClip(Component parentComponent, String name, T model) {
		super(parentComponent, name, model);
	}
	
	public TestClip(String name, T model) {
		super(name, model);
	}

	public TestClip(T model) {
		super(model);
	}

	public void attach(Terminal deviceTerminal) throws Exception {
		Terminal clipTerminal = getTerminal("pin");
		
		// if both terminals already have a wire attached, we'll need to bridge them
		if(deviceTerminal.getExternalWire() != null & clipTerminal.getExternalWire() != null) {
			Terminal bridge = new Terminal(this, this.getName()+"_"+deviceTerminal.getName()+"_bridge", Terminal.Type.INPUT);
			bridge.setExternalWire(deviceTerminal.getExternalWire());
			bridge.setInternalWire(clipTerminal.getExternalWire());
		} else if(clipTerminal.getExternalWire() != null) {
			deviceTerminal.setExternalWire(clipTerminal.getExternalWire());
		} else if(deviceTerminal.getExternalWire() != null) {
			clipTerminal.setExternalWire(deviceTerminal.getExternalWire());
		} else {
			Wire w = new Wire();
			clipTerminal.setExternalWire(w);
			deviceTerminal.setExternalWire(w);
		}
	}

}
