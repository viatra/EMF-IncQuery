package org.eclipse.viatra2.emf.incquery.base.itc.main;

public class MeasurementUnit {
	
	private int cycle;
	private int N;
	private int M;
	private int K;
	private int L;
	private String alg;
	private String mode;
	private String graph;
	
	public MeasurementUnit(int cycle, int n, int m, int k, int l, String alg,
			String mode, String graph) {
		super();
		this.cycle = cycle;
		N = n;
		K = k;
		L = l;
		M = m;
		this.alg = alg;
		this.mode = mode;
		this.graph = graph;
	}

	public int getM() {
		return M;
	}

	public void setM(int m) {
		M = m;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int getL() {
		return L;
	}

	public void setL(int l) {
		L = l;
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}
	
}
