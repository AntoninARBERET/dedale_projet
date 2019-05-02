package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

public class Block implements Serializable{
	private String sender, dest, objective, origin;
	private int priority;

	public Block(String sender, int priority, String dest, String objective, String origin) {
		super();
		this.sender = sender;
		this.priority=priority;
		this.dest = dest;
		this.objective = objective;
		this.origin=origin;
	}

	public String getSender() {
		return sender;
	}

	public String getDest() {
		return dest;
	}

	public String getObjective() {
		return objective;
	}
	
	public int getPriority() {
		return priority;
	}
	
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String toString() {
		return("Block envoyé par "+sender+" vers le noeud "+dest+" pour acceder a "+objective);
	}
}
