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
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapRessources;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/* Behaviour permettant d'indiquer ca pr�sence aux voisins environnants
 
  */

public class PingBehaviour extends SimpleBehaviour{
	 private static final long serialVersionUID = 9088209402507795289L;
	 
	 private boolean finished=false;

	 private DedaleAgent myDedaleAgent;

	private String[] agentslist;
	 
	 public PingBehaviour (DedaleAgent myagent,String[] agentslist){
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
		 
		/* for (int i=0; i<agents.length;i++)
				msg.addReceiver( agents[i].getName() ); 
		 */
		 
		//2° compute the random value
		 try {
			  msg.setProtocol("PING");
			  msg.setContentObject(myDedaleAgent.getPosition());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		 this.myDedaleAgent.send(msg);
		 this.finished=true;
		 
		 
	 }
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
