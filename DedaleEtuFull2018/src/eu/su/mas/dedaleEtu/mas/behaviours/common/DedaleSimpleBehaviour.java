package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.Date;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * This behaviour is a one Shot.
 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
 * 
 * @author Cédric Herpson
 *
 */
public class DedaleSimpleBehaviour extends SimpleBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished=false;
	
	private DedaleAgent myDedaleAgent;

	/**
	 * 
	 * This behaviour is a one Shot.
	 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
	 * @param myagent
	 */
	public DedaleSimpleBehaviour(final DedaleAgent myagent) {
		super(myagent);
		myDedaleAgent = myagent;
	}


	public void action() {
		myDedaleAgent.incActionsCpt();
		//temporisation generale
		//myDedaleAgent.doWait(200);
	}

	public boolean done() {
		//myDedaleAgent.setCheckingBehaviourRunning(false);

		return false; //change pour check en permanence
	}

}

