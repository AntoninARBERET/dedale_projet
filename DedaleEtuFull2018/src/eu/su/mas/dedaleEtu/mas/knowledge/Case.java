package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.Date;

import dataStructures.tuple.Couple;
//classe representant un noeud pour les envois de maps
public class Case implements Serializable{

	private static final long serialVersionUID = 1429822429237280539L;
	private String id;
	private boolean node_open;//mis a true car si on cree la case c'est que le noeud est ouvert
	private Date date;
	private int gold;
	private int force;
	private boolean tresor_ouvert;
	private boolean wumpus;
	private int lockPicking;
	private Couple<String,String> agent;
	
	
	
	public Case(String id,boolean node_open,int gold,boolean tresor_ouvert,int lockPicking,int force, boolean wumpus, Couple<String,String> agent, Date date) {
		this.id=id;
		this.node_open=node_open;
		this.date=date;
		this.gold=gold;
		this.tresor_ouvert=tresor_ouvert;
		this.wumpus=wumpus;
		this.force=force;
		this.lockPicking=lockPicking;
		this.agent=agent;
	}
	
	public int getLockPicking() {
		return lockPicking;
	}

	public Couple<String, String> getAgent() {
		return agent;
	}

	public int getForce() {
		return force;
	}
	
	public boolean isWumpus() {
		return wumpus;
	}

	public void setWumpus(boolean wumpus) {
		this.wumpus = wumpus;
	}

	//recupere l'id de la case
	public String getId() {
		return this.id;
	}
	
	//change l'id de la case
	public void setId(String id) {
		this.id=id;
	}
	
	public boolean is_Open() {
		return this.node_open;
	}
	
	public Date getDate() {
		return date;
	}
/*	
	public boolean is_Agent() {
		return this.agent;
	}
*/	
	public int getGold() {
		return this.gold;
	}
	
	public void SetGold(int gold) {
		this.gold=gold;
	}
	
	public boolean is_TresorOuvert() {
		return this.tresor_ouvert;
	}
	
	public void SetTresorOuvert(boolean tresor_ouvert) {
		this.tresor_ouvert=tresor_ouvert;
	}
}