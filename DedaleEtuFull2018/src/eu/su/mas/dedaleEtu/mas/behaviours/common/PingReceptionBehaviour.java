package eu.su.mas.dedaleEtu.mas.behaviours.common;


import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;

//reception du ping
public class PingReceptionBehaviour extends DedaleSimpleBehaviour{
	 private static final long serialVersionUID = 9088209402507795289L;
	 
	 private boolean finished=false;
	 private DedaleAgent myDedaleAgent;
	 private String recievedName;

	 public PingReceptionBehaviour (DedaleAgent myagent, String recievedName){
		super(myagent);
		this.myDedaleAgent = myagent;
		this.recievedName = recievedName;
		this.temporised=false;
	 }
	 
	 
	 public void action() {
		 int actionsCpt =myDedaleAgent.getActionsCpt();
		 //si la derniere reponse a cet agent est assez ancienne
		 if(myDedaleAgent.getLastPing(recievedName)<actionsCpt-DedaleAgent.pingGap) {
			 myDedaleAgent.setLastPing(recievedName, actionsCpt);
			 myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, recievedName));
		 }
	 }
	 
	 
	 
	 public boolean done() {
			return finished;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
