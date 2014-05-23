package co.nz.splashYay.cagefight.entities;

import java.util.ArrayList;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public abstract class Building extends AIUnit{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Building(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS team) {
		super(xpos, ypos, maxhealth, currenthealth, id, team);
		// TODO Auto-generated constructor stub
		this.attackRange = 150;
		this.attackCoolDown = 5000;
		this.damage = 10;
	}
	
	public void attackTargetsInRange(ArrayList<Entity> entities){
		ArrayList<Entity> toDamage = new ArrayList<Entity>();
		
		
		
		for (Entity ent : entities) {
			double distance = getDistanceToTarget(ent);						
			if (distance < attackRange) {
				toDamage.add(ent);
			}			
		}
		if (toDamage.size() > 0) {
			int damage = this.getDamage()/toDamage.size();
			for (Entity ent : toDamage) {
				ent.damageEntity(damage);
			}
		}
		
		
		
	}
	
	@Override
	public void checkState(){
		if (getCurrenthealth() <= 0) {
			state = EntityState.DEAD;
		} else if (System.currentTimeMillis() >= getLastAttackTime() + getAttackCoolDown()) {
			state = EntityState.ATTACKING;
		} else {
			state = EntityState.IDLE;
		}
	}
	
	

}
