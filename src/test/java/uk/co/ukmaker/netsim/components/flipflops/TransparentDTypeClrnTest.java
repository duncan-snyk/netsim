package uk.co.ukmaker.netsim.components.flipflops;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.*;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.components.ComponentTest;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;
import static org.junit.Assert.*;
import static uk.co.ukmaker.netsim.SignalValue.*;

public class TransparentDTypeClrnTest extends ComponentTest {
	
	private Input d;
	private Input clk;
	private Input clrn;
	private Output q;
	private Output qn;
	
	private TransparentDTypeClrn ff;
	

	@Override
	public Component getComponent() {
		return ff;
	}

	@Override
	public List<Input> getInputs() {
		return newArrayList(d, clk, clrn);
	}

	@Override
	public List<Output> getOutputs() {
		return newArrayList(q, qn);
	}
	
	@Override
	public void setup() {
		ff = new TransparentDTypeClrn();
		d = (Input)ff.getPorts().get("d");
		clk = (Input)ff.getPorts().get("clk");
		clrn = (Input)ff.getPorts().get("clrn");
		q = (Output)ff.getPorts().get("q");
		qn = (Output)ff.getPorts().get("qn");
	}
	
	@Test
	public void shouldIgnoreDWhenClkHigh() {
		
		expect(X, X, X, X);

		// Set to known values to start
		apply(ZERO, ZERO, ONE);
		expect(X, ZERO, X, ONE);

		apply(ZERO, ONE, ONE);
		expect(ZERO, null, ONE, null);

		// Changing d to any value should have no effect
		apply(ONE, ONE, ONE);
		expect(ZERO, null, ONE, null);

		apply(Z, ONE, ONE);
		expect(ZERO, null, ONE, null);

		apply(X, ONE, ONE);
		expect(ZERO, null, ONE, null);
	}	
	
	@Test
	public void shouldFollowDWhenClkLow() {
		
		expect(X, X, X, X);

		// Set to known values to start
		apply(ZERO, ZERO, ONE);
		expect(X, ZERO, X, ONE);

		apply(ZERO, ZERO, ONE);
		expect(ZERO, null, ONE, null);

		apply(ONE, ZERO, ONE);
		expect(ZERO, ONE, ONE, ZERO);

		apply(Z, ZERO, ONE);
		expect(ONE, X, ZERO, X);

		apply(X, ONE, ONE);
		expect(X, null, X, null);
	}
	
	
	@Test
	public void shouldClearWhenClrnLow() {
		
		expect(X, X, X, X);

		// Set to known values to start
		apply(ONE, ZERO, ONE);
		expect(X, ONE, X, ZERO);

		apply(ONE, ONE, ONE);
		expect(ONE, null, ZERO, null);

		// clrn low should reset d
		apply(ONE, ONE, ZERO);
		expect(ONE, ZERO, ZERO, ONE);

		// clrn Z gives x
		apply(ONE, ONE, Z);
		expect(ZERO, X, ONE, X);
		
		// clrn X gives x
		apply(ONE, ONE, X);
		expect(X, null, X, null);
	}
}
