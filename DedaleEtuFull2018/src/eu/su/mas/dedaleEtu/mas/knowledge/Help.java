package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

//classe representant un blocage
public class Help implements Serializable{
	
	private static final long serialVersionUID = 31787038228418415L;
	private String sender,  objective;
	private boolean end;
	private String node;



	public Help(String sender, String objective, String node, boolean end) {
		super();
		this.sender = sender;
		this.objective = objective;
		this.node=node;
	}

	public String getSender() {
		return sender;
	}


	public String getObjective() {
		return objective;
	}
	
	public boolean isEnd() {
		return end;
	}

	public String getNode() {
		return node;
	}
	
	public String toString() {
		return("from " + sender+" on "+node+" to get "+objective);
	}

	
}
