package uk.co.ukmaker.netsim;

import uk.co.ukmaker.netsim.models.Model;

public interface Broadcaster {

	void askForQuiet();

	void resetAll();

	void broadcast(Model c);

	void broadcastAndNoteFutures(Model c);

	void waitOnFutures();

	void discardReplies();

	void broadcastAndNoteFutures(EventType eventType);

	void sendUpdatesAndNoteFutures();

}
