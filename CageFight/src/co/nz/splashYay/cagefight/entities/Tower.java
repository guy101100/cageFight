package co.nz.splashYay.cagefight.entities;

import java.security.acl.LastOwnerException;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class Tower extends Building{
	private static final long serialVersionUID = 1L;
	
	
	
	public Tower(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		this.speed = 0;
		
	}
	
	public void destroyTower(){
		setAlive(false);
		if (getLastEntityThatAttackedMe() instanceof Player) {
			if (lastEntityThatAttackedMe instanceof Player) {
				((Player) lastEntityThatAttackedMe).addExperience(150);	
				((Player) lastEntityThatAttackedMe).addGold(50);
			}		
		}
		getBody().setActive(false);
		
		
	}
	
	
	
		
		
	
	
	
	

}