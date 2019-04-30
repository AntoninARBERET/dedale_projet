package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.archive.dummies.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.archive.dummies.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.archive.dummies.SimpleTankerBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.PingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.tanker.TankerBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * ExploreSolo agent. 
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited
 *  
 *  
 * @author hc
 *
 */

public class TankerAgent extends DedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	private String mySpot;
	private boolean finalSpot;
	//private String[] idList;
	
	
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){
		
		super.setup();
		final Object[] args = getArguments();
		//idList = (String[])args[2];
		//myMap=new MapRepresentation();
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
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		lb.add(new TankerBehaviour(this));
		lb.add(new PingBehaviour(this));
		lb.add(new ReceiveMessageBehaviour(this));
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
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
