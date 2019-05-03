package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.io.IOException;
import java.util.Date;

import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Block;
import eu.su.mas.dedaleEtu.mas.knowledge.Help;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

//envoi d'un message de demande d'aide
public class SendHelpBehaviour extends DedaleSimpleBehaviour{
	private static final long serialVersionUID = 9088209402507795289L; 
	private boolean finished=false;
	private DedaleAgent myDedaleAgent;
	private String[] agentslist;
	private String objective;
	private String dest;
	private String recieverName;
	private int priority;
	private String type;

	 
	 
	 
	 public SendHelpBehaviour (DedaleAgent myagent, String type, String objective){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.agentslist = myDedaleAgent.getIdList();
		this.type = type;
		this.objective=objective;

	 }
	 
	 
	 public void action() {
		 
		
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
			  msg.setProtocol("HELP");
			  Help h = new Help(myDedaleAgent.getLocalName(), type, objective);
			  msg.setContentObject(h);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		 
	

		 this.myDedaleAgent.sendMessage(msg);
		 this.finished=true;
	 }
 
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
