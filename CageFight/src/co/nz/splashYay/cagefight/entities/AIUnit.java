package co.nz.splashYay.cagefight.entities;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public abstract class AIUnit extends Entity{
	private transient GameData gd;
	private int agroDistance = 500;
	private int loseTargetDistance = 500;
	
	public AIUnit(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS team, GameData gd) {
		super(xpos, ypos, maxhealth, currenthealth, id, team);
		this.gd = gd;
		
	}
	
	public void checkState(){
		boolean atackablePlayers = false;
		
		if (currenthealth <= 0) {
			state = EntityState.DEAD;
			
		} else if (atackablePlayers) { 
			state = EntityState.ATTACKING;
		} else {
			state = EntityState.IDLE;			
		}
	}
	
	/**
	 * checks if there are enemy units inside the AI units agro radius
	 * @return true if there are enemy units close by, else returns false
	 */
	private boolean checkAgroRadius(){
		for (Entity e : gd.getEntities().values()) {
			if (e.isAlive()) {
				if (e.getId() != this.id && e.getTeam() != this.team) {
					double distanceFromUnit = Math.pow((e.getCenterXpos() - this.getCenterXpos()), 2) + Math.pow((e.getCenterYpos() - this.getCenterYpos()), 2);
					if (distanceFromUnit < agroDistance) {
						return true;
					}
				}
			}
		}
		
		return false;
		
		
	}
	
	private void checkObjective(){
		if (hasTarget() && getTarget().isAlive()) {
			double distanceFromTarget = Math.pow((target.getCenterXpos() - this.getCenterXpos()), 2) + Math.pow((target.getCenterYpos() - this.getCenterYpos()), 2);
			if (distanceFromTarget < loseTargetDistance) {
				
			} else {
				if (checkAgroRadius()) {
					targetNearestEnemyEntity(gd);
				} else {
					//set to a default objective
				}
			}
		}
		
		
		
	}

	

}
