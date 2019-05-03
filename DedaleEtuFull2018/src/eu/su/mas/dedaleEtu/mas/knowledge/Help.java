package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

//classe representant un blocage
public class Help implements Serializable{
	
	private static final long serialVersionUID = 31787038228418415L;
	private String sender,  objective, type;
	private int priority;

	public Help(String sender, String type, String objective) {
		super();
		this.sender = sender;
		this.type = type;
		this.objective = objective;
	}

	public String getSender() {
		return sender;
	}


	public String getObjective() {
		return objective;
	}
	
	public int getPriority() {
		return priority;
	}

	public String getType() {
		return type;
	}
	
	


	
}
