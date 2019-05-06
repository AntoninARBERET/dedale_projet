package eu.su.mas.dedaleEtu.mas.behaviours.common;


import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import jade.core.behaviours.SimpleBehaviour;


//classe dont herite les autres, gere le compteur d'actoin et la temporisation
public class DedaleSimpleBehaviour extends SimpleBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;

	
	private DedaleAgent myDedaleAgent;
	protected boolean suspended;
	protected boolean temporised;
	
	public DedaleSimpleBehaviour(final DedaleAgent myagent) {
		super(myagent);
		myDedaleAgent = myagent;
		suspended=false;
		
	}


	public void action() {
		
		
		
	}
	public void onAction() {
		if(suspended) {
			return;
		}
		if(temporised) {
			myDedaleAgent.doWait(300);
		}
		myDedaleAgent.incActionsCpt();
	}

	public boolean done() {

		return false;
	}
	
	public void suspend() {
		suspended=true;
	}
	
	public void resume(){
		suspended=false;
	}

}

