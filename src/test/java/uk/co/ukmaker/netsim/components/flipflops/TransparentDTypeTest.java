package uk.co.ukmaker.netsim.components.flipflops;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.components.ComponentTest;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static uk.co.ukmaker.netsim.SignalValue.*;

public class TransparentDTypeTest extends ComponentTest {
	
	private Input d;
	private Input clk;
	private Output q;
	private Output qn;
	
	private TransparentDType ff;
	

	@Override
	public Component getComponent() {
		return ff;
	}

	@Override
	public List<Input> getInputs() {
		return newArrayList(d, clk);
	}

	@Override
	public List<Output> getOutputs() {
		return newArrayList(q, qn);
	}
	
	@Override
	public void setup() {
		ff = new TransparentDType();
		d = (Input)ff.getPorts().get("d");
		clk = (Input)ff.getPorts().get("clk");
		q = (Output)ff.getPorts().get("q");
		qn = (Output)ff.getPorts().get("qn");
	}
	
	@Test
	public void shouldIgnoreDWhenClkHigh() {
		
		expect(X, X, X, X);

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
		
		expect(X, X, X, X);

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
