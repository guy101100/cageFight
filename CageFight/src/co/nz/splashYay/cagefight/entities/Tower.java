package co.nz.splashYay.cagefight.entities;

import java.security.acl.LastOwnerException;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class Tower extends AIUnit{
	private static final long serialVersionUID = 1L;
	
	
	
	public Tower(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS teamId, GameData gd) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId, gd);
		this.speed = 0;
		
	}
		
		
	/**
	 * Used by the ClientInNetCom to update the towers data
	 * @param tower to get data from
	 */
	public void updateFromServer(Tower tower){
		this.currenthealth = tower.getCurrenthealth();
		this.maxhealth = tower.getMaxhealth();
		this.lastAttackTime = tower.getLastAttackTime();
		this.attackCoolDown = tower.getAttackCoolDown();
		this.state = tower.getState();
		this.alive = tower.isAlive();		
		
	}
	
	
	

}