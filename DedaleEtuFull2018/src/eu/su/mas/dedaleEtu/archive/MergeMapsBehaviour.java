package eu.su.mas.dedaleEtu.archive;

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

	private Object recMap;
	private DedaleAgent myDedaleAgent;
	
	
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param nbValues the number of messages that should be sent to the receiver
	 * @param receiverName The local name of the receiver agent
	 */
	public MergeMapsBehaviour(final DedaleAgent myagent, Object recMap) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.recMap=recMap;
		
	}


	public void action() {
		
		//if(recMap instanceof Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>){
			Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>  newMap = (Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>) recMap;
			if(myDedaleAgent.getMap()==null) {
				System.out.println(myDedaleAgent.getLocalName()+" ----> MAP NULL");
				try {
					this.myDedaleAgent.doWait(250);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			int o=0, f=0, a=0;
			//Ajout des noeuds ouverts
			for(String noeud : newMap.getLeft().getLeft()) {
				if(myDedaleAgent.getMap().getNode(noeud)==null) {
					myDedaleAgent.getMap().addNode(noeud, MapAttribute.open);
					myDedaleAgent.getOpenNodes().add(noeud);
					o++;
				}
			}
			
			//Ajout des noeuds fermes
			for(String noeud : newMap.getLeft().getRight()) {
				myDedaleAgent.getMap().addNode(noeud);
				myDedaleAgent.getClosedNodes().add(noeud);
				myDedaleAgent.getOpenNodes().remove(noeud);
				f++;
			}
			
			//Ajout des arcs
			for(Couple<String,String> arc : newMap.getRight()) {
				myDedaleAgent.getMap().addEdge(arc.getLeft(), arc.getRight());
				a++;
			}
			
			//myDedaleAgent.setMap(myDedaleAgent.getMap());
			System.out.println(this.myDedaleAgent.getLocalName()+" merged maps - o = " +o+" f = "+f+" a = "+a );
		//}else {
			//System.out.println(this.myDedaleAgent.getLocalName()+" echec de merge, mauvais type");
		//}
		
		
		this.finished=true; // After the execution of the action() method, this behaviour will be erased from the agent's list of triggerable behaviours.

		


	}

	public boolean done() {
		return finished;
	}

}
