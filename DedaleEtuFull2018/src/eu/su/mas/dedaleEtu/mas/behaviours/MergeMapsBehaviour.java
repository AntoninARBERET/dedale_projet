package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
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
	private DedaleAgent myDedaleAgent;
	
	MapRepresentation myMap;
	
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param nbValues the number of messages that should be sent to the receiver
	 * @param receiverName The local name of the receiver agent
	 */
	public MergeMapsBehaviour(final DedaleAgent myagent,MapRepresentation myMap, Object recMap) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.myMap = myDedaleAgent.getMap();
		this.recMap=recMap;
		
	}


	public void action() {
		
		//if(recMap instanceof Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>){
			Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>  newMap = (Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>) recMap;
			
			//Ajout des noeuds ouverts
			for(String noeud : newMap.getLeft().getLeft()) {
				if(myMap.getNode(noeud)==null) {
					myMap.addNode(noeud, MapAttribute.open);
				}
			}
			
			//Ajout des noeuds fermes
			for(String noeud : newMap.getLeft().getRight()) {
				if(myMap.getNode(noeud)==null) {
					myMap.addNode(noeud);
				}
			}
			
			//Ajout des arcs
			for(Couple<String,String> arc : newMap.getRight()) {
				myMap.addEdge(arc.getLeft(), arc.getRight());
			}
			
			//myDedaleAgent.setMap(myMap);
			System.out.println(this.myDedaleAgent.getLocalName()+" merged maps ");
		//}else {
			//System.out.println(this.myDedaleAgent.getLocalName()+" echec de merge, mauvais type");
		//}
		
		
		this.finished=true; // After the execution of the action() method, this behaviour will be erased from the agent's list of triggerable behaviours.

		


	}

	public boolean done() {
		return finished;
	}

}
