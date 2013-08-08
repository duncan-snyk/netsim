package uk.co.ukmaker.netsim.ports;

import uk.co.ukmaker.netsim.Component;

abstract public class AbstractPort  implements Port {
	
	/**
	 * The name assigned to this port by the owning component
	 */
	private final String name;
	/**
	 * The Component to which this port belongs
	 */
	private final Component component;

	public AbstractPort(final Component component, final String name) {
		this.component = component;
		this.name = name;
	}
	
	@Override
	public Component getComponent() {
		return component;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
