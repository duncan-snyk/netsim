package uk.co.ukmaker.netsim.models.flipflops;

import static com.google.common.collect.Lists.newArrayList;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.models.ModelTest;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class TransparentDTypeTest extends ModelTest {
	
	private InputPin d;
	private InputPin clk;
	private OutputPin q;
	private OutputPin qn;
	
	private TransparentDType ff;
	

	@Override
	public Model getComponent() {
		return ff;
	}

	@Override
	public List<InputPin> getInputs() {
		return newArrayList(d, clk);
	}

	@Override
	public List<OutputPin> getOutputs() {
		return newArrayList(q, qn);
	}
	
	@Override
	public void setup() {
		ff = new TransparentDType();
		d = (InputPin)ff.getPins().get("d");
		clk = (InputPin)ff.getPins().get("clk");
		q = (OutputPin)ff.getPins().get("q");
		qn = (OutputPin)ff.getPins().get("qn");
	}
	
	@Test
	public void shouldIgnoreDWhenClkHigh() {
		
		expect(X, null, X, null);

		// Set to known values to start
		apply(ZERO, ZERO);
		expect(X, ZERO, X, ONE);

		apply(ZERO, ONE);
		expect(ZERO, null, ONE, null);

		// Changing d to any value should have no effect
		apply(ONE, ONE);
		expect(ZERO, null, ONE, null);

		apply(Z, ONE);
		expect(ZERO, null, ONE, null);

		apply(X, ONE);
		expect(ZERO, null, ONE, null);
	}	
	
	@Test
	public void shouldFollowDWhenClkLow() {
		
		expect(X, null, X, null);

		// Set to known values to start
		apply(ZERO, ZERO);
		expect(X, ZERO, X, ONE);

		apply(ZERO, ZERO);
		expect(ZERO, null, ONE, null);

		apply(ONE, ZERO);
		expect(ZERO, ONE, ONE, ZERO);

		apply(Z, ZERO);
		expect(ONE, X, ZERO, X);

		apply(X, ONE);
		expect(X, null, X, null);
	}
}
