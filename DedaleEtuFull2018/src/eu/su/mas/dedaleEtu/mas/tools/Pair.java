package eu.su.mas.dedaleEtu.mas.tools;

import jade.util.leap.Serializable;

public class Pair<T1, T2> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T1 v1;
	private T2 v2;
	
	public Pair(T1 v1, T2 v2) {
		this.v1=v1;
		this.v2=v2;
	}
	
	public T1 getFirst() {
		return v1;
	}
	
	public T2 getSecond() {
		return v2;
	}
	
	public String toString() {
		return("<"+v1.toString()+", "+v2.toString()+">");
	}
}
