package uk.co.ukmaker.netsim;

import java.util.List;

import uk.co.ukmaker.netsim.models.Model;

/**
 * An instance of this class handles running the simulation
 * 
 * @author duncan
 *
 */
public class Dispatcher {
	
	private Broadcaster broadcaster;
	
	private List<Model> models;
	
	
	/**
	 * Initialise the network by broadcasting the list of components to the cluster
	 */
	public void initialise() {
		
		broadcaster.askForQuiet();
		broadcaster.resetAll();
		
		for(Model c : models) {
			broadcaster.broadcastAndNoteFutures(c);
		}
		
		broadcaster.waitOnFutures();
		broadcaster.discardReplies();
		broadcaster.broadcastAndNoteFutures(EventType.INIT);
		broadcaster.waitOnFutures();
		// Don't discard them - this is the initial state
	}
	
	/**
	 * Run the simulation
	 */
	public void run() {
		while(true) {
			broadcaster.sendUpdatesAndNoteFutures();
			broadcaster.waitOnFutures();
		}
	}

}
