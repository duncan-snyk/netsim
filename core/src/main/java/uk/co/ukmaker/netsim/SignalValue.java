package uk.co.ukmaker.netsim;

public enum SignalValue {
	
	Z("Z"),
	ZERO("0"),
	ONE("1"),
	X("X"),
	N("N") // no-change
	;
	
	private String v;
	
	private SignalValue(String v) {
		this.v = v;
	}
	
	
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
	
	public boolean isNoChange() {
		return this.equals(N);
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
	
	public static SignalValue fromInt(int i) throws Exception {
		if(i == 0) return ZERO;
		if(i == 1) return ONE;
		throw new Exception("Illegal value for fromInt - "+i);
	}
	
	public static SignalValue fromChar(char c) throws Exception {
		if(c == '0') return ZERO;
		if(c == '1') return ONE;
		if(c == 'Z' || c == 'z') return Z;
		if(c == 'X' || c == 'x') return X;
		if(c == 'N' || c == 'n') return N;
		throw new Exception("Illegal value for fromChar - "+c);
	}
	
	public String toString() {
		return v;
	}
}
