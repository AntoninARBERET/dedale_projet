package eu.su.mas.dedaleEtu.mas.behaviours.common;


import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Block;
import eu.su.mas.dedaleEtu.mas.knowledge.Help;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.AlphaNumCompare;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

//reception des messages
public class ReceiveMessageBehaviour extends DedaleSimpleBehaviour{

	private static final long serialVersionUID = 9088209402507795289L;

	
	private DedaleAgent myDedaleAgent;

	public ReceiveMessageBehaviour(final DedaleAgent myagent) {
		super(myagent);
		myDedaleAgent = myagent;
		this.temporised=false;
	}


	public void action() {
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);	
		myDedaleAgent.setCheckingBehaviourRunning(true);

		final ACLMessage msg = this.myDedaleAgent.receive(msgTemplate);
		if (msg != null /*&&new Date().getTime()- msg.getPostTimeStamp()<1000*/) {
			try {
				

				//switch case selon le type de message dans le protocole
				switch(msg.getProtocol()) {
				
				//reception ping
				case "PING":
					 int actionsCpt =myDedaleAgent.getActionsCpt();
					 String recievedName = msg.getSender().getLocalName();
					 if(myDedaleAgent.getLastPing(recievedName)<actionsCpt-DedaleAgent.pingGap) {
						 myDedaleAgent.setLastPing(recievedName, actionsCpt);
						 myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, recievedName));
					 }
					break;
				
				//reception map
				case "MAP":
					MapRepresentation.MergeWithSendableMap(myDedaleAgent, msg.getContentObject());
					break;
					
				//reception du message de blocage	
				case "BLOCK":
					Block b = (Block) msg.getContentObject();
					System.out.println(myDedaleAgent.getLocalName()+" -----> sur "+myDedaleAgent.getPosition()+" recu : "+b.toString());
					//si je suis sur la position de conflit
					if(myDedaleAgent.getPosition().equals(b.getDest())){//TODO NPE
						boolean greaterPrio=false;
						//check priorite
						if(myDedaleAgent.getPriority()>b.getPriority()) {
							//System.out.println(myDedaleAgent.getLocalName()+" -----> ignore block car prio : "+myDedaleAgent.getPriority()+"plus haute" +b.getSender());
							greaterPrio=true;
						}
						else if(myDedaleAgent.getPriority()==b.getPriority() && AlphaNumCompare.isFirst(myDedaleAgent.getLocalName(), b.getSender())){
							//System.out.println(myDedaleAgent.getLocalName()+" -----> ignore block car id avant " +b.getSender());

							greaterPrio=true;
						}
						if(!greaterPrio) {
							DedaleSimpleBehaviour mainBehav = myDedaleAgent.getMainBehaviour();
							myDedaleAgent.addBehaviour(new BlockHandlingBehaviour(myDedaleAgent, b, mainBehav));
						}
						
					}
					break;
					
				case "HELP":
					Help h = (Help) msg.getContentObject();
					if(h.getObjective().equals(myDedaleAgent.getTargetNode())){
						myDedaleAgent.addBehaviour(new HelpingBehaviour(myDedaleAgent, h, myDedaleAgent.getMainBehaviour()));
					}
					break;
				
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			block();
		}
	}

	public boolean done() {

		return false; 
	}

}

