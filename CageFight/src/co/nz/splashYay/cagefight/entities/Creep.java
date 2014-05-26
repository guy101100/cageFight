package co.nz.splashYay.cagefight.entities;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class Creep extends AIUnit {
	
	
	private static final long serialVersionUID = 1L;
	
	
	
	
	public Creep(int id, int maxhealth, int currenthealth, int xpos, int ypos, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		setMaxSpeed(5);
		setSpeed(5);
		
	}
	
	
	public void updateFromServer(Creep ai){
		
		this.currenthealth = ai.getCurrenthealth();
		this.direction = ai.getDirection();
		this.maxhealth = ai.getMaxhealth();
		this.xpos = ai.getXPos();
		this.ypos  = ai.getYPos();
		this.speed = ai.getSpeed();
		
		this.target = ai.getTarget();
	}
	
	public void killCreep(){
		this.setAlive(false);	
		if  (lastEntityThatAttackedMe instanceof Player)
		{
			((Player)lastEntityThatAttackedMe).addExperience(150);
			((Player)lastEntityThatAttackedMe).checkForLevel();
		}
		// TO ADD : death annimation
		this.getBody().setActive(false);
	}
	
	@Override
	public void checkState(GameData gameData){
		EntityState oldState = getState();
		
		if (currenthealth <= 0) {
			state = EntityState.DEAD;
			
		} else if (hasTarget() && getTarget().isAlive()) {
			
			if (getDistanceToTarget(getTarget()) < getAttackRange()) {
				state = EntityState.ATTACKING;
			} else {
				state = EntityState.MOVING;
			}
					
			
		} else {
			state = EntityState.IDLE;
			
		}
		
		if (oldState != getState()) {
			stateChanged = true;
		}
	}


	
	
	
	
	
}
