package eu.su.mas.dedaleEtu.mas.behaviours.common;


import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import jade.core.behaviours.SimpleBehaviour;


//classe dont herite les autres, gere le compteur d'actoin et la temporisation
public class DedaleSimpleBehaviour extends SimpleBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;

	
	private DedaleAgent myDedaleAgent;


	public DedaleSimpleBehaviour(final DedaleAgent myagent) {
		super(myagent);
		myDedaleAgent = myagent;
	}


	public void action() {
		myDedaleAgent.incActionsCpt();
		//temporisation generale
		myDedaleAgent.doWait(200);
	}

	public boolean done() {

		return false;
	}

}

