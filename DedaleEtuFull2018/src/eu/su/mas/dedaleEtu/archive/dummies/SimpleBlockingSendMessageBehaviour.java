package eu.su.mas.dedaleEtu.archive.dummies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/* Behaviour permettant d'indiquer ca pr�sence aux voisins environnants
 
  */

public class SimpleBlockingSendMessageBehaviour extends DedaleSimpleBehaviour{
	 private static final long serialVersionUID = 9088209402507795289L;
	 
	 private boolean finished=false;
	 private DedaleAgent myDedaleAgent;
	 private String[] agentslist;

	private String recieverName;
	private static int nbsent=0;
	 
	 
	 
	 /**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param agentslist is the list of all the agents
		 */
	 
	 
	 
	 public SimpleBlockingSendMessageBehaviour (DedaleAgent myagent,String recieverName){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.agentslist = myDedaleAgent.getIdList();
		this.recieverName = recieverName;
	 }
	 
	 
	 public void action() {
		 
		 if(myDedaleAgent.getLockingStart() !=null && new Date().getTime() -myDedaleAgent.getLockingStart().getTime()<500) {
			 if(!myDedaleAgent.isCheckingBehaviourRunning()) {
				 myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
			 }
			 this.finished=true;
			
		 }
		 else {
			 //creation du message
			 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			 msg.setSender(this.myDedaleAgent.getAID());
			 
			 
			 if(this.recieverName=="-1") {
					for(int i =0; i<agentslist.length; i++) {
						if(!agentslist[i].equals(myDedaleAgent.getLocalName())) {
							msg.addReceiver(new AID(agentslist[i], AID.ISLOCALNAME));
						}
					}
	
				}
			 
			
			//2° compute the random value
			 try {
				  msg.setProtocol("BLOCKSIMPLE");
				  msg.setContentObject(new Couple<String, Integer>(myDedaleAgent.getLocalName(), new Integer(myDedaleAgent.getPriority())));
				} catch (IOException e) {
					e.printStackTrace();
				} 
			 
		

			 this.myDedaleAgent.sendMessage(msg);
			 this.finished=true;
			 myDedaleAgent.setLockingStart(new Date());
			 myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
			 System.out.println(this.myDedaleAgent.getLocalName()+" ----> BlockSimple on "+myDedaleAgent.getPosition()+" to "+myDedaleAgent.getTargetNode()/*peut etre ajout� le receveur*/);
		 }
	 }
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
