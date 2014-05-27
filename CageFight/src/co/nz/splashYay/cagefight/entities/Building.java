package co.nz.splashYay.cagefight.entities;

import java.util.ArrayList;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.scenes.GameScene;

public abstract class Building extends AIUnit{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Building(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS team) {
		super(xpos, ypos, maxhealth, currenthealth, id, team);
		// TODO Auto-generated constructor stub
		this.attackRange = 150;
		this.agroDistance = attackRange;
		this.attackCoolDown = 5000;
		this.maxDamage = 75;
		this.damage = 75;
	}
	
	public ArrayList<Entity> attackTargetsInRange(ArrayList<Entity> entities){
		ArrayList<Entity> toDamage = new ArrayList<Entity>();
		
		
		
		for (Entity ent : entities) {							
			if (ent.isAlive() && getDistanceToTarget(ent) < attackRange) {
				if (System.currentTimeMillis() >= ent.getRespawnTime() + 2500 ) {
					toDamage.add(ent);
				}
				
			}			
		}
		if (toDamage.size() > 0) {
			int damage = this.getDamage()/toDamage.size();
			for (Entity ent : toDamage) {
				ent.damageEntity(damage);
				//gS.towerAttackExplosion(ent);
			}
		}
		
		return toDamage;
		
	}
	
	@Override
	public void checkState(GameData gameData){
		EntityState oldState = getState();
		if (getCurrenthealth() <= 0) {
			state = EntityState.DEAD;
		} else if (System.currentTimeMillis() >= getLastAttackTime() + getAttackCoolDown() && checkAgroRadius(gameData)) {
			state = EntityState.ATTACKING;
		} else {
			state = EntityState.IDLE;
		}
		if (oldState != getState()) {
			stateChanged = true;
		}
	}
	
	/**
	 * Used by the ClientInNetCom to update the towers data
	 * @param tower to get data from
	 */
	public void updateFromServer(Building building){
		this.currenthealth = building.getCurrenthealth();
		this.maxhealth = building.getMaxhealth();
		this.lastAttackTime = building.getLastAttackTime();
		this.attackCoolDown = building.getAttackCoolDown();
		
		if (this.state != building.getState()) {
			this.state = building.getState();
			this.stateChanged = true;
		}
		
	}
	
	

}
