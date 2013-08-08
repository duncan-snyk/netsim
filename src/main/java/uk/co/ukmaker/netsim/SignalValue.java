package uk.co.ukmaker.netsim;

public enum SignalValue {
	
	Z,
	ZERO,
	ONE,
	X
	;
	
	
	public boolean isX() {
		return this.equals(X);
	}
	
	public boolean isZ() {
		return this.equals(Z);
	}
	
	public boolean isOne() {
		return this.equals(ONE);
	}
	
	public boolean isZero() {
		return this.equals(ZERO);
	}
	
	public boolean isUnknown() {
		return isZ() || isX();
	}
	
	public boolean isNot(SignalValue other) {
		return (isOne() && other.isZero()) || (isZero() && other.isOne());
	}
	
	public SignalValue not() {
		if(isOne()) {
			return ZERO;
		}
		
		if(isZero()) {
			return ONE;
		}
		
		return X;
	}
}
