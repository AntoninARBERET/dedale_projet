package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;

//envoie le ping en permanence
public class PingBehaviour extends DedaleSimpleBehaviour{
	 private static final long serialVersionUID = 9088209402507795289L;
	 
	 private DedaleAgent myDedaleAgent;
	 private String[] agentslist;
	 
	 
	 public PingBehaviour (DedaleAgent myagent){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.agentslist = myDedaleAgent.getIdList();
		this.temporised=true;
	 }
	 
	 
	 public void action() {
		onAction();
		 //creation du message
		 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		 msg.setSender(this.myDedaleAgent.getAID());
		 
		 
		 for(int i =0; i<agentslist.length; i++) {
		 msg.addReceiver(new AID(agentslist[i], AID.ISLOCALNAME));
		 }
		 
		
		 try {
			  msg.setProtocol("PING");
			  msg.setContentObject(myDedaleAgent.getPosition());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		 this.myDedaleAgent.send(msg);
		 
	 }
	 
	 
	 
	 public boolean done() {
			return false;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
