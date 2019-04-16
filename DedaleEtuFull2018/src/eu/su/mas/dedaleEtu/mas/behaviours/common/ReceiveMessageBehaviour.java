package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.Date;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * This behaviour is a one Shot.
 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
 * 
 * @author Cédric Herpson
 *
 */
public class ReceiveMessageBehaviour extends SimpleBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished=false;
	
	private DedaleAgent myDedaleAgent;

	/**
	 * 
	 * This behaviour is a one Shot.
	 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
	 * @param myagent
	 */
	public ReceiveMessageBehaviour(final DedaleAgent myagent) {
		super(myagent);
		myDedaleAgent = myagent;
	}


	public void action() {
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);	
		myDedaleAgent.setCheckingBehaviourRunning(true);

		final ACLMessage msg = this.myDedaleAgent.receive(msgTemplate);
		if (msg != null /*&&new Date().getTime()- msg.getPostTimeStamp()<1000*/) {
			try {
				


				switch(msg.getProtocol()) {
				case "PING":
					System.out.println(myDedaleAgent.getLocalName() + " ----> ping recue");
					
					break;
				case "MAP":
					//MapRepresentation.MergeMaps(myDedaleAgent, msg.getContentObject());
					MapRepresentation.MergeWithSendableMap(myDedaleAgent, msg.getContentObject());
					break;
				case "BLOCKSIMPLE":
					Couple<String, Integer> content =(Couple<String, Integer>)msg.getContentObject();
					myDedaleAgent.addBehaviour(new SimpleBlockingReceptionBehaviour(myDedaleAgent, content.getLeft(), content.getRight().intValue()));
					break;
				
				/*if(msg.getProtocol().equals("MAP")) {
					System.out.println(myDedaleAgent.getLocalName() + " ----> map recue");
					//MapRepresentation.MergeMaps(myDedaleAgent, msg.getContentObject());
					MapRepresentation.MergeFullMaps(myDedaleAgent, msg.getContentObject());
					}*/
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.finished=true;
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}

	public boolean done() {
		//myDedaleAgent.setCheckingBehaviourRunning(false);

		return false; //change pour check en permanence
	}

}

