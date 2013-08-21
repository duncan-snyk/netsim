package uk.co.ukmaker.netsim.simulation;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.netlist.Net;

public class NetEventQueue {
	
	private Schedule head;
	private Schedule tail;
	
	public static class NetEvent {
		
		public long moment;
		public Net net;
		public int drivers;
		
		public NetEvent(long moment, Net net, int drivers) {
			this.moment = moment;
			this.net = net;
			this.drivers = drivers;
		}
	}
	
	public static class Schedule {
		
		public NetEvent event;
		public Schedule next;
		
		public Schedule(NetEvent event) {
			this.event = event;
		}
	}
	
	public void schedule(NetEvent event) {
		
		Schedule s = new Schedule(event);
		
		if(head == null) {
			head = s;
			tail = s;
			return;
		}
		
		Schedule n = head;
		Schedule p = null;
		
		while(n != null) {
			
			if(n.event.moment > event.moment) {
				if(p == null) {
					s.next = head;
					head = s;
				} else {
					p.next = s;
					s.next = n;
				}
				return;
			}
			
			n = n.next;
		}
		
		/// got to the end, so append it
		tail.next = s;
		tail = s;
	}
	
	public NetEvent head() {
		if(head == null) {
			return null;
		}
		
		return head.event;
	}
	
	public NetEvent tail() {
		if(tail == null) {
			return null;
		}
		
		return tail.event;
	}
	
	public NetEvent getScheduledEvent(long moment) {
		Schedule s = head;
		while(s != null) {
			
			if(s.event.moment == moment) {
				return s.event;
			}
			
			s = s.next;
		}
		
		return null;
	}
	
	public NetEvent useScheduledEvent(long moment) {
		Schedule s = head;
		Schedule p = null;
		while(s != null) {
			
			if(s.event.moment == moment) {
				// unlink it from it's context
				if(p == null && s.next == null) {
					// s is head and tail
					head = null;
					tail = null;
				} else if(p == null) {
					// s is head and has something after it
					head = s.next;
				} else {
					// s is in the middle
					p.next = s.next;
				}
				
				return s.event;
			}
			
			s = s.next;
		}
		
		return null;
	}
	
	public List<NetEvent> useScheduledEvents(long moment) {
		
		List<NetEvent> events = new ArrayList<NetEvent>();
		
		// inefficient for now. So what.
		NetEvent event = null;
		while((event = useScheduledEvent(moment)) != null) {
			events.add(event);
		}
		
		return events;
		
	}
}
