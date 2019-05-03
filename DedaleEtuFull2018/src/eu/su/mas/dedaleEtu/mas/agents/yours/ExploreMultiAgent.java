package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.common.PingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.explorer.ExploExplorerBehaviour;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;



public class ExploreMultiAgent extends DedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	
	protected void setup(){
		
		super.setup();
		
		type="explorer";

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
		
		p=new Property();
		p.setName("lockPicking");
		p.setValue(new Integer(myLockPicking));
		sd.addProperties(p);
		
		
		//Register the service
		try {
			DFService. register ( this , dfd ) ;
		} catch (FIPAException fe) {
			fe . printStackTrace () ; 
		}
				
		//Ajout de la premiere behaviour et des deux permanente 
		lb.add(new ExploExplorerBehaviour(this));
		lb.add(new PingBehaviour(this));
		lb.add(new ReceiveMessageBehaviour(this));
		
		
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	public List<String> getOpenable(){
		List<String> openable = new ArrayList<String>();
		
		for(String t : getClosedTresor()) {
			if((int)(getMap().getNode(t).getAttribute("lockPicking"))<=myLockPicking&&(int)(getMap().getNode(t).getAttribute("force"))<=myStrengh){
				openable.add(t);
				
			}
		}
		return openable;
	}
	
}
