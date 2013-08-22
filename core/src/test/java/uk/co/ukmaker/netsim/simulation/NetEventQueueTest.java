package uk.co.ukmaker.netsim.simulation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.simulation.NetEventQueue.NetEvent;

public class NetEventQueueTest {
	
	@Test
	public void shouldQueueEventsInOrder() {
		
		NetEventQueue q = new NetEventQueue();
		
		NetEvent e0 = new NetEvent(1, null, 0);
		NetEvent e1 = new NetEvent(2, null, 0);
		NetEvent e2 = new NetEvent(5, null, 0);
		NetEvent e3 = new NetEvent(3, null, 0);
		
		q.schedule(e0);
		q.schedule(e1);
		q.schedule(e2);
		q.schedule(e3);
		
		List<NetEvent> e;
		
		e= q.useScheduledEvents(1);		
		assertEquals(1, e.size());
		
		e= q.useScheduledEvents(2);		
		assertEquals(1, e.size());
		
		e= q.useScheduledEvents(3);		
		assertEquals(1, e.size());
		
		e= q.useScheduledEvents(5);		
		assertEquals(1, e.size());
		
	}

}
