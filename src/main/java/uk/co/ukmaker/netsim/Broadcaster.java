package uk.co.ukmaker.netsim;

public interface Broadcaster {

	void askForQuiet();

	void resetAll();

	void broadcast(Component c);

	void broadcastAndNoteFutures(Component c);

	void waitOnFutures();

	void discardReplies();

	void broadcastAndNoteFutures(EventType eventType);

	void sendUpdatesAndNoteFutures();

}
