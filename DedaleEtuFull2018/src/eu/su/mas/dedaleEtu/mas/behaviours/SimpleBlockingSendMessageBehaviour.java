package eu.su.mas.dedaleEtu.mas.behaviours;

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
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/* Behaviour permettant d'indiquer ca pr�sence aux voisins environnants
 
  */

public class SimpleBlockingSendMessageBehaviour extends SimpleBehaviour{
	 private static final long serialVersionUID = 9088209402507795289L;
	 
	 private boolean finished=false;
	 private DedaleAgent myDedaleAgent;
	 private String[] agentslist;
	 
	 
	 
	 /**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param agentslist is the list of all the agents
		 */
	 
	 
	 
	 public SimpleBlockingSendMessageBehaviour (DedaleAgent myagent,String[] agentslist){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.agentslist = agentslist;
	 }
	 
	 
	 public void action() {
		 //creation du message
		 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		 msg.setSender(this.myDedaleAgent.getAID());
		 
		 
		 for(int i =0; i<agentslist.length; i++) {
		 msg.addReceiver(new AID(agentslist[i], AID.ISLOCALNAME));
		 }
		 
		
		//2° compute the random value
		 try {
			  msg.setProtocol("BLOCKSIMPLE");
			  msg.setContentObject(myDedaleAgent.getName());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		 this.myDedaleAgent.send(msg);
		 this.finished=true;
		 
		 System.out.println(this.myDedaleAgent.getLocalName()+" ----> BlockSimple sent "/*peut etre ajout� le receveur*/);
	 }
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
