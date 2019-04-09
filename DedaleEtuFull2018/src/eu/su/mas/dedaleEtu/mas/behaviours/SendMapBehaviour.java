package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.Random;

import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
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

public class SendMapBehaviour extends SimpleBehaviour{
	
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
	private DedaleAgent myDedaleAgent;
	
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param nbValues the number of messages that should be sent to the receiver
	 * @param receiverName The local name of the receiver agent
	 */
	public SendMapBehaviour(final DedaleAgent myagent, String receiverName, String[] agentsIds) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.receiverName=receiverName;
		this.agentsIds = agentsIds;
		
	}


	public void action() {
		try {
			this.myAgent.doWait(2000);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//1°Create the message
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
			
		//2° compute the random value		
		try {
			msg.setProtocol("MAP");
			//msg.setContentObject(myDedaleAgent.getMap().getStringListRepresentation());
			msg.setContentObject(myDedaleAgent.getMap().getSendableMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.myDedaleAgent.send(msg);
		
		this.finished=true; // After the execution of the action() method, this behaviour will be erased from the agent's list of triggerable behaviours.

		
		System.out.println(this.myDedaleAgent.getLocalName()+" ----> myMap sent to "+this.receiverName);

	}

	public boolean done() {
		return finished;
	}

}
