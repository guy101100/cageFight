package co.nz.splashYay.cagefight.entities;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public abstract class AIUnit extends Entity{
	
	private int agroDistance = 500;
	private int loseTargetDistance = 500;
	
	public AIUnit(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS team) {
		super(xpos, ypos, maxhealth, currenthealth, id, team);
		
		
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
	
	public float getXdirectionToTarget(){
		return getTarget().getCenterXpos() - this.getCenterXpos();
	}
	
	public float getYdirectionToTarget(){
		return getTarget().getCenterYpos() - this.getCenterYpos();
	}
	
	//
	
	public void moveTowardsObjective(){
		if (hasTarget()) {
			final Body creepBody = getBody();			
			float x = getXdirectionToTarget();
			float y = getYdirectionToTarget();			
			final Vector2 velocity = Vector2Pool.obtain(distanceToMoveWithAngle(getAngleOfLineToTarget(), getSpeed()));
			creepBody.setLinearVelocity(velocity);
			Vector2Pool.recycle(velocity);
		}		
	}
	
	
	
    
    public Vector2 distanceToMoveWithAngle(double angle, float distance) {
        int xDir = 0;
        int yDir = 0;
        angle = Math.toRadians(angle);
        xDir = (int) (Math.sin(angle) * distance);
        yDir = (int) (Math.cos(angle) * distance);
        Vector2 direction = Vector2Pool.obtain(xDir, -yDir);        
        return direction;
    }
    
        
    //
	
	
	/**
	 * checks if there are enemy units inside the AI units agro radius
	 * @return true if there are enemy units close by, else returns false
	 */
	private boolean checkAgroRadius(GameData gd){
		for (Entity e : gd.getEntities().values()) {
			if (e.isAlive()) {
				if (e.getId() != this.id && e.getTeam() != this.team) {
					float x = getCenterXpos() - e.getCenterXpos();
					x *= x;
					float y = getCenterYpos() - e.getCenterYpos();
					y *= y;
					double distanceFromUnit = Math.sqrt(x + y);					
							
					if (Math.abs(distanceFromUnit) < agroDistance) {
						
						return true;
					}
				}
			}
		}
		
		return false;
		
		
	}
	
	private void msgOut(String out) {
		if (getTeam() == ALL_TEAMS.EVIL) {
			System.out.println(out);
		}
	}
	
	/**
	 * checks and updates the AI units objective
	 * @param gd
	 */
	public void checkAndUpdateObjective(GameData gd) {
		if (hasTarget() && getTarget().isAlive()) {
			
			msgOut("Has target : " + getTarget().getClass().toString());
			
			if (getTarget() instanceof Base || getTarget() instanceof Tower) {
				if (checkAgroRadius(gd)) { //check if there is a enemy to target
					setTarget(getNearestEnemyEntity(gd));
				}	
				
			} else {
				if (getDistanceToTarget() > loseTargetDistance) {
					msgOut("Target is outside of agrorange");
					if (checkAgroRadius(gd)) { //check if there is a enemy to target						
						setTarget(getNearestEnemyEntity(gd));
						msgOut("there is another target in range : " + getTarget().getClass().toString());
						
					} else {
						//set to a default objective
						msgOut("no target in range, reset to default target" );
						setTarget(null);
					}

				}
			}

		} else {
			msgOut("Does not have target : ");
			if (checkAgroRadius(gd)) { //check if there is a enemy to target
				setTarget(getNearestEnemyEntity(gd));
				msgOut("there is another target in range : " + getTarget().getClass().toString());
			} else {
				msgOut("no target in range, reset to default target" );
				setTarget(null);

			}
		}

	}

}
