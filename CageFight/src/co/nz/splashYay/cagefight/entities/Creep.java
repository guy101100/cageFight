package co.nz.splashYay.cagefight.entities;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.badlogic.gdx.math.Vector2;

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
		
		this.xpos = ai.getXPos();
		this.ypos = ai.getYPos();
		this.direction = ai.getDirection();
		
		this.currenthealth = ai.getCurrenthealth();
		this.maxhealth = ai.getMaxhealth();
		
		this.speed = ai.getSpeed();
		
		this.target = ai.getTarget();
		
		if (this.state != ai.getState()) {
			this.state = ai.getState();
			this.stateChanged = true;
		}
		
	}
	
	public void killCreep(){
		this.setAlive(false);
		this.setRespawnTime();
		// TO ADD : death annimation
		this.getBody().setActive(false);
	}
	
	@Override
	public void checkState(GameData gameData){
		EntityState oldState = getState();
		
		if (currenthealth <= 0) {
			state = EntityState.DEAD;
			
		} else if (hasTarget() && getTarget().isAlive()) {
			
			if (System.currentTimeMillis() >= (getLastAttackTime() + getAttackCoolDown()) && getDistanceToTarget(getTarget()) < getAttackRange()) {
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
	
	/**
	 * Reactivates body, teleports body to spawn point, heals player, set state to idle
	 */
	public void respawn(GameData gd){
		
		
		this.currenthealth = this.maxhealth; //heal the player to full health
		
		
		
		//TO ADD : reset sprite to alive sprite
		
		//teleports the body to the respawn position	    
	    final float angle = getBody().getAngle(); // keeps the body angle
	    int x = gd.getTeam(getTeam()).getSpawnXpos();
	    int y = gd.getTeam(getTeam()).getSpawnYpos();
	    final Vector2 v2 = Vector2Pool.obtain(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
	    
	    
	    
	    getBody().setTransform(v2, angle);
	    Vector2Pool.recycle(v2);
	    this.getBody().setActive(true); // reactivates the body
	    
	    this.setState(EntityState.IDLE);
		this.setAlive(true);
		
		
	    System.out.println("Creep : " + this.getId() + " has respawned [" + System.currentTimeMillis() + "]");
	    
		
	}


	
	
	
	
	
}
