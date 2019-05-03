package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.collector.ExploCollectorBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.PingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;



public class CollectMultiAgent extends DedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	private List<String> toRetry;
	private int totalSpace;

	

	protected void setup(){
		
		super.setup();
		type="collector";
		toRetry = new ArrayList<String>();
		totalSpace =  this.getBackPackFreeSpace();
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		//Registering to DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd .setName(getAID()); // The agent AID
		
		
		ServiceDescription sd = new ServiceDescription () ;
		sd .setType( type ); // You have to give aname to each service your agent offers
		sd .setName(getLocalName());//(local)name ofthe agent
		dfd . addServices(sd) ;
		
		Property p=new Property();
		p.setName("strengh");
		p.setValue(new Integer(myStrengh));
		sd.addProperties(p);
		
		
		//Register the service
		try {
			DFService. register ( this , dfd ) ;
		} catch (FIPAException fe) {
			fe . printStackTrace () ; 
		}
		
		
		//Ajout de la premiere behaviour et des deux permanente
		lb.add(new ExploCollectorBehaviour(this));
		lb.add(new PingBehaviour(this));
		lb.add(new ReceiveMessageBehaviour(this));


		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	//Lootable open and closed
	public List<String> getLootable(){
		List<String> lootable = new ArrayList<String>();
		
		for(String t : getOpenTresor()) {
			if((int)(getMap().getNode(t).getAttribute("force"))<=myStrengh&&(int)(getMap().getNode(t).getAttribute("gold"))>=0){
				lootable.add(t);
				
			}
		}
		for(String t : getClosedTresor()) {
			if((int)(getMap().getNode(t).getAttribute("force"))<=myStrengh&&(int)(getMap().getNode(t).getAttribute("gold"))>=0){
				lootable.add(t);
				
			}
		}
		
		return lootable;
	}
	
	//open lootable
	public List<String> getOpenLootable(){
		List<String> lootable = new ArrayList<String>();
		
		for(String t : getOpenTresor()) {
			if((int)(getMap().getNode(t).getAttribute("force"))<=myStrengh&&(int)(getMap().getNode(t).getAttribute("gold"))>=0){
				lootable.add(t);
				
			}
		}

		
		return lootable;
	}
	
	//open or close lootable except "without
	public List<String> getLootableWhithout(List<String> without){
		List<String> lootable = new ArrayList<String>();
		
		for(String t : getLootable()) {
			if(!without.contains(t)){
				lootable.add(t);
				
			}
		}
		
		
		return lootable;
	}

	public List<String> getToRetry() {
		return toRetry;
	}
	
	public void resetToRetry() {
		toRetry = new ArrayList<String>();
	}

	public int getTotalSpace() {
		return totalSpace;
	}
	
}
