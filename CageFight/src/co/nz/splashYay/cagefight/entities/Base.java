package co.nz.splashYay.cagefight.entities;

import java.util.ArrayList;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class Base extends Building{
	private static final long serialVersionUID = 1L;
	
	
	
	public Base(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		this.speed = 0;
		
		
	}
	
	
	public void destroyBase(){
		setAlive(false);
		getBody().setActive(false);
		
		
		
	}

	
	
	
	
	
	/**
	 * Used by the ClientInNetCom to update the towers data
	 * @param base to get data from
	 */
	public void updateFromServer(Base base){
		this.currenthealth = base.getCurrenthealth();
		this.maxhealth = base.getMaxhealth();
		this.lastAttackTime = base.getLastAttackTime();
		this.attackCoolDown = base.getAttackCoolDown();
		this.state = base.getState();
		this.alive = base.isAlive();		
		
	}

}
