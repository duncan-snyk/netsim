package uk.co.ukmaker.netsim;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduledValueQueue {
	
	private Schedule head;
	private Schedule tail;
	
	private ReentrantLock lock = new ReentrantLock();
	private Condition awaitingDrivers = lock.newCondition();
	
	private boolean awaiting = false;
	private long awaitedMoment = 0;
	private int awaitedDrivers = 0;
	
	public static class Schedule {
		
		public int drivers = 1;
		public ScheduledValue value;
		public Schedule next;
		public Schedule prev;
		
		public Schedule(ScheduledValue value) {
			this.value = value;
		}
	}
	
	public void schedule(ScheduledValue value) {
		// Since we expect values to be scheduled in order, start at the tail and work back
		
		lock.lock();
		
		try {
		
		Schedule s = new Schedule(value);
		
		if(head == null) {
			head = s;
			tail = s;
			signal(s);
			return;
		}
		
		Schedule l = tail;
		
		while(l != null) {
			
			// Deal with multiple drivers at the same time
			if(l.value.getMoment() == value.getMoment()) {
				l.drivers++;
				l.value = new ScheduledValue(value.getMoment(), SignalValue.X);
				signal(l);
				return;
			}
			
			if(l.value.getMoment() < value.getMoment()) {
				if(l.next == null) { 
					// i.e. l is tail
					l.next = s;
					s.prev = l;
					tail = s;
				} else {
					// insert our value after l
					s.next = l.next;
					l.next = s;
					s.prev = l;
					s.next.prev = s;
				}
				signal(s);
				return;
			}
			
			l=l.prev;
		}
		
		/// got to the beginning, so make it head
		s.next = head;
		head.prev = s;
		head = s;
		
		signal(s);
		
		} finally {
			lock.unlock();
		}
	}
	
	protected void signal(Schedule s) {
		if(awaiting && s.value.getMoment() == awaitedMoment && awaitedDrivers == s.drivers) {
			awaitingDrivers.signal();
		}		
	}
	
	public ScheduledValue head() {
		if(head == null) {
			return null;
		}
		
		return head.value;
	}
	
	public ScheduledValue useHead() {
		if(head == null) {
			return null;
		}
		
		ScheduledValue v = head.value;
		head = head.next;
		return v;
	}
	
	public ScheduledValue tail() {
		if(tail == null) {
			return null;
		}
		
		return tail.value;
	}
	
	public ScheduledValue getScheduledValue(long moment) {
		Schedule s = head;
		while(s != null) {
			
			if(s.value.getMoment() == moment) {
				return s.value;
			}
			
			s = s.next;
		}
		
		return null;
	}
	
	public ScheduledValue useScheduledValue(long moment) {
		Schedule s = head;
		Schedule p = null;
		while(s != null) {
			
			if(s.value.getMoment() == moment) {
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
				
				return s.value;
			}
			
			s = s.next;
		}
		
		return null;
	}
	
	public int getScheduledDrivers(long moment) {
		Schedule s = head;
		while(s != null) {
			
			if(s.value.getMoment() > moment) {
				return 0;
			}
			
			if(s.value.getMoment() == moment) {
				return s.drivers;
			}
			
			s = s.next;
		}
		
		return 0;
	}
	
	public void await(long moment, int drivers) throws InterruptedException {
		
		lock.lock();
		
		try {
		awaitedMoment = moment;
		awaitedDrivers = drivers;
		awaiting = true;
		
		while(getScheduledDrivers(moment) != drivers) {
			awaitingDrivers.await();
		}
		
		} finally {
			lock.unlock();
		}
		
	}
	
	public String toString() {
		return toString(true);
	}
	
	public String toString(boolean limitTo10Values) {
		int i=0;
		Schedule s = head;
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		while(s != null) {
			i++;
			if(limitTo10Values && i >= 10) {
				break;
			}
			if(!first) {
				sb.append(",");
			} else {
				first = false;
			}
			sb.append(s.value.toString());
			
			s = s.next;
		}
		
		return sb.toString();
	}
	
	public static ScheduledValueQueue fromString(String s) throws Exception {
		ScheduledValueQueue q = new ScheduledValueQueue();
		String[] bits = s.split(",");
		for(String bit : bits) {
			if(!bit.isEmpty()) {
				q.schedule(ScheduledValue.fromString(bit));
			}
		}
		return q;
	}
}
