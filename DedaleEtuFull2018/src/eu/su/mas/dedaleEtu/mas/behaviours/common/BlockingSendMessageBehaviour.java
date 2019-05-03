package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.io.IOException;
import java.util.Date;

import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Block;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

//envoi d'un message de blocage
public class BlockingSendMessageBehaviour extends DedaleSimpleBehaviour{
	private static final long serialVersionUID = 9088209402507795289L; 
	private boolean finished=false;
	private DedaleAgent myDedaleAgent;
	private String[] agentslist;
	private String objective;
	private String dest;
	private String recieverName;
	private int priority;
	/**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param agentslist is the list of all the agents
		 */
	 
	 
	 
	 public BlockingSendMessageBehaviour (DedaleAgent myagent,String recieverName, int priority, String dest, String objective){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.agentslist = myDedaleAgent.getIdList();
		this.recieverName = recieverName;
		this.objective=objective;
		this.dest = dest;
		this.priority=priority;
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
				  msg.setProtocol("BLOCK");
				  Block b = new Block(myDedaleAgent.getLocalName(), priority, dest, objective, myDedaleAgent.getPosition());
				  msg.setContentObject(b);
				} catch (IOException e) {
					e.printStackTrace();
				} 
			 
		

			 this.myDedaleAgent.sendMessage(msg);
			 this.finished=true;
			 myDedaleAgent.setLockingStart(new Date());
			 System.out.println(this.myDedaleAgent.getLocalName()+" ----> Block on "+myDedaleAgent.getPosition()+" to "+myDedaleAgent.getTargetNode() +" during "+myDedaleAgent.getMainBehaviour().getBehaviourName());//*peut etre ajout� le receveur*/);
		 }
	 }
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
