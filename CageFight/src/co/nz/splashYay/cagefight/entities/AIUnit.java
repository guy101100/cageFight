package co.nz.splashYay.cagefight.entities;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;

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
	public double GetAngleOfLineBetweenTwoPoints() {
        float xDiff = getTarget().getCenterXpos() - getCenterXpos();
        float yDiff = getTarget().getCenterYpos() - getCenterYpos();
        return Math.toDegrees(Math.atan2(xDiff, -yDiff));
    }
    public double LengthBetweenTwoPoints() {
        float s1 = getTarget().getCenterXpos() - getCenterXpos();
        float s2 = getTarget().getCenterYpos() - getCenterYpos();
        return Math.sqrt(s1 * s1 + s2 * s2);
    }
	
    
    public Vector2 distanceToMoveWithAngle(double angle, int distance) {
        int xDir = 0;
        int yDir = 0;
        angle = Math.toRadians(angle);
        xDir = (int) (Math.sin(angle) * distance);
        yDir = (int) (Math.cos(angle) * distance);
        Vector2 direction = Vector2Pool.obtain(xDir, -yDir);
        System.out.println(xDir + " " + -yDir);
        return direction;
    }
    
    /*
    public static void main(String[] args) {
        Vector enemy = new Vector(0, 0);
        Vector player = new Vector(100, 100);
        int minAttackingDistance = 50;
        int aggroDistance = 2000;
        int enemySpeed = 100;
        
        
        double lengthFromEnemyToPlayer = LengthBetweenTwoPoints(enemy, player);
        if (lengthFromEnemyToPlayer > minAttackingDistance && lengthFromEnemyToPlayer < aggroDistance) {
            Vector distanceToMove = distanceToMoveWithAngle(enemy, GetAngleOfLineBetweenTwoPoints(enemy, player), enemySpeed);
            System.out.println(distanceToMove.getX() + " " + distanceToMove.getY());
        }
    }
    */
    
    
    
    //
	public float getDirectionToTarget(){
		if (hasTarget()) {
			float x = getTarget().getCenterXpos() - this.getCenterXpos();
			float y = getTarget().getCenterYpos() - this.getCenterYpos();
			return MathUtils.radToDeg((float) Math.atan2(x, -y));			
		} else {
			return 0;			
		}
				
		
	}
	
	/**
	 * checks if there are enemy units inside the AI units agro radius
	 * @return true if there are enemy units close by, else returns false
	 */
	private boolean checkAgroRadius(GameData gd){
		for (Entity e : gd.getEntities().values()) {
			if (e.isAlive() && e instanceof Player) {
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
	
	/**
	 * checks and updates the AI units objective
	 * @param gd
	 */
	public void checkAndUpdateObjective(GameData gd) {
		if (hasTarget() && getTarget().isAlive()) {
			
			if (getTarget() instanceof Base || getTarget() instanceof Tower) {
				
				if (checkAgroRadius(gd)) { //check if there is a enemy to target
					getNearestEnemyEntity(gd);
				} 
				
			} else {
				
				double distanceFromTarget = Math.pow((getTarget().getCenterXpos() - this.getCenterXpos()), 2) + Math.pow((getTarget().getCenterYpos() - this.getCenterYpos()), 2);
				
				if (distanceFromTarget < loseTargetDistance) {
					//keep current target
					
				} else {
					
					if (checkAgroRadius(gd)) { //check if there is a enemy to target
						getNearestEnemyEntity(gd);
						
					} else {
						//set to a default objective
						
					}
				}
				
			}
			
			
			
		} else {
			
			
			if (checkAgroRadius(gd)) { //check if there is a enemy to target
				setTarget(getNearestEnemyEntity(gd));
				
			} else {
				//set to a default objective
				
			}
			
			
		}
		
		
		
	}

	

}
