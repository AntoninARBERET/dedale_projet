package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.tools.AlphaNumCompare;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/* Behaviour permettant d'indiquer ca pr�sence aux voisins environnants
 
  */

public class PingReceptionBehaviour extends DedaleSimpleBehaviour{
	 private static final long serialVersionUID = 9088209402507795289L;
	 
	 private boolean finished=false;
	 private DedaleAgent myDedaleAgent;
	 private String recievedName;
	 private int moveNum;
	 private int moveMax=2;

	 private int recievedPrio;
	
	 
	 
	 
	 /**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param agentslist is the list of all the agents
		 */
	 
	 
	 
	 public PingReceptionBehaviour (DedaleAgent myagent, String recievedName){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.recievedName = recievedName;
		this.moveNum=0;
		this.recievedPrio=recievedPrio;
	 }
	 
	 
	 public void action() {
		 int actionsCpt =myDedaleAgent.getActionsCpt();
		 if(myDedaleAgent.getLastPing(recievedName)<actionsCpt-DedaleAgent.pingGap) {
			 myDedaleAgent.setLastPing(recievedName, actionsCpt);
			 myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, recievedName));
		 }
	 }
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
