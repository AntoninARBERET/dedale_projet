package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;

//envoie de la carte
public class SendMapBehaviour extends DedaleSimpleBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished=false;
	
	private String receiverName;
	private String[] agentsIds;
	private DedaleAgent myDedaleAgent;
	

	public SendMapBehaviour(final DedaleAgent myagent, String receiverNames) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.receiverName=receiverNames;
		this.agentsIds = myDedaleAgent.getIdList();
		
	}


	public void action() {
		try {
			this.myAgent.doWait(300);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(this.myDedaleAgent.getAID());
		
		if(this.receiverName=="-1") {
			for(int i =0; i<agentsIds.length; i++) {
				if(!agentsIds[i].equals(myDedaleAgent.getLocalName())) {
					msg.addReceiver(new AID(agentsIds[i], AID.ISLOCALNAME));
				}
			}

		}
		else {
			msg.addReceiver(new AID(this.receiverName, AID.ISLOCALNAME));  
		}
			
		try {
			msg.setProtocol("MAP");
			//creation de la sendable map
			msg.setContentObject(myDedaleAgent.getMap().getSendableMap(myDedaleAgent));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.myDedaleAgent.sendMessage(msg);
		myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
		this.finished=true; 

		

	}

	public boolean done() {
		return finished;
	}

}
