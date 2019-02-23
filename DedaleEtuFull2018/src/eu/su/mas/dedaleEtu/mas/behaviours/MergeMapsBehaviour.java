package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;


/***
 * This behaviour allows the agent who possess it to send nb random int within [0-100[ to another agent whose local name is given in parameters
 * 
 * There is not loop here in order to reduce the duration of the behaviour (an action() method is not preemptive)
 * The loop is made by the behaviour itslef
 * 
 * @author Cédric Herpson
 *
 */

public class MergeMapsBehaviour extends SimpleBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished=false;
	/**
	 * number of values to send
	 */
	
	/**
	 * Name of the agent that should receive the values
	 */
	private String receiverName;
	private String[] agentsIds;
	private Object recMap;
	
	MapRepresentation myMap;
	
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param nbValues the number of messages that should be sent to the receiver
	 * @param receiverName The local name of the receiver agent
	 */
	public MergeMapsBehaviour(final Agent myagent,MapRepresentation myMap, Object recMap) {
		super(myagent);
		this.myMap = myMap;
		this.recMap=recMap;
		
	}


	public void action() {
		
		Pair<ArrayList<String>,ArrayList<Pair<String,String>>> newMap = (Pair<ArrayList<String>,ArrayList<Pair<String,String>>>) recMap;
		
		for(String noeud : newMap.getFirst()) {
			myMap.addNode(noeud);
		}
		
		for(Pair<String,String> arc : newMap.getSecond()) {
			myMap.addEdge(arc.getFirst(), arc.getSecond());
		}
		
		ExploreMultiAgent ema = (ExploreMultiAgent) myAgent;
		ema.setMap(myMap);
		
		this.finished=true; // After the execution of the action() method, this behaviour will be erased from the agent's list of triggerable behaviours.

		
		System.out.println(this.myAgent.getLocalName()+" marged maps ");

	}

	public boolean done() {
		return finished;
	}

}
