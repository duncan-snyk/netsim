package uk.co.ukmaker.netsim.amqp.messages;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsMapContaining.*;

import org.junit.Test;

import uk.co.ukmaker.netsim.amqp.messages.node.InstallModelMessage;

public class ModelMessageTest {
	
	@Test
	public void shouldRoundtrip() {
		String name = "TEST";
		int unitId = 77;
		String className = "CLASSNAME";
		Map<String, String> pinToNetMap = new HashMap<String, String>();
		pinToNetMap.put("A", "B");
		pinToNetMap.put("C", "D");
		pinToNetMap.put("E", "F");
		pinToNetMap.put("G", "H");
		
		InstallModelMessage message = new InstallModelMessage(name, unitId, className, pinToNetMap);
		
		byte[] bytes = message.getBytes();
		
		InstallModelMessage rt = InstallModelMessage.read(null, bytes);
		
		assertThat(rt.getClassName(), is("CLASSNAME"));
		assertThat(rt.getName(), is("TEST"));
		assertThat(rt.getUnitId(), is(77));
		assertThat(rt.getPinToNetMap(), hasEntry(is("A"), is("B")));
		assertThat(rt.getPinToNetMap(), hasEntry(is("C"), is("D")));
		assertThat(rt.getPinToNetMap(), hasEntry(is("E"), is("F")));
		assertThat(rt.getPinToNetMap(), hasEntry(is("G"), is("H")));
	}

}
