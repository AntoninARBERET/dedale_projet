package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.common.PingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.tanker.TankerBehaviour;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;



public class TankerAgent extends DedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	private String mySpot;
	private boolean finalSpot;
	
	
	

	protected void setup(){
		
		super.setup();
		type = "tanker";
		List<Behaviour> lb=new ArrayList<Behaviour>();
		this.priority=0;
		mySpot=null;
		finalSpot =false;
		
		//Register to df
		DFAgentDescription dfd = new DFAgentDescription();
		dfd .setName(getAID()); // The agent AID
		
		
		ServiceDescription sd = new ServiceDescription () ;
		sd .setType( type ); // You have to give aname to each service your agent offers
		sd .setName(getLocalName());//(local)name ofthe agent
		dfd . addServices(sd) ;
		
		
		//Register the service
		try {
			DFService. register ( this , dfd ) ;
		} catch (FIPAException fe) {
			fe . printStackTrace () ; 
		}
		
		//Ajout de la premiere behaviour et des deux permanente
		lb.add(new TankerBehaviour(this));
		lb.add(new PingBehaviour(this));
		lb.add(new ReceiveMessageBehaviour(this));
		
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}




	public String getMySpot() {
		return mySpot;
	}




	public void setMySpot(String mySpot) {
		this.mySpot = mySpot;
	}




	public boolean isFinalSpot() {
		return finalSpot;
	}




	public void setFinalSpot(boolean finalSpot) {
		this.finalSpot = finalSpot;
	}
	
	
	
}
