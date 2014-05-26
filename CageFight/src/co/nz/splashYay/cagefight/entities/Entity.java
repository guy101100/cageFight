package co.nz.splashYay.cagefight.entities;

import java.io.Serializable;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.util.math.MathUtils;

import co.nz.splashYay.cagefight.CustomSprite;
import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Entity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int id;	
	protected float xpos;
	protected float ypos;
	protected float direction;
	protected float currenthealth;
	protected float maxhealth;
	
	protected long respawnTime = 0;
	
	protected float maxSpeed;
	protected float speed;	
	
	protected int maxDamage;
	protected int damage;
	
	protected long lastAttackTime;	
	protected long attackCoolDown;
	
	protected int attackRange;
	
	protected float regenAmount;
	
	protected boolean alive;
	
	protected EntityState state;
	
	private transient CustomSprite customSprite;
	private transient PhysicsHandler phyHandler;
	private transient MoveModifier moveModifier;
	private transient Body body;
	protected ALL_TEAMS team;
	
	protected Entity lastEntityThatAttackedMe;
	
	protected Entity target;
	protected transient boolean stateChanged;


	public Entity(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS team)
	{
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = 0;
		
		this.maxhealth = maxhealth;
		this.currenthealth = currenthealth;
		
		this.maxSpeed = 5;
		this.speed = maxSpeed;
		
		this.maxDamage = 50;
		this.damage = maxDamage;
		
		this.attackRange = 75;
		
		this.lastAttackTime = 0;
		this.attackCoolDown = 2000;
		
		this.alive = true;
		this.id = id;
		this.team = team;
		
		this.lastEntityThatAttackedMe = null;
		this.target = null;
		this.stateChanged = true;
		
		this.state = EntityState.IDLE;
		
		this.regenAmount = 2;
		
	}
	
	
	/**
	 * 
	 * @param gd
	 * @return s the closest non base or tower entity
	 */
	public Entity getNearestEnemyEntity(GameData gd) {
		Entity currentClose = null;

		for (Entity e : gd.getEntities().values()) {
			
			if (e.isAlive() && !(e instanceof Tower) && !(e instanceof Base)) {
				if (e.getId() != this.id && e.getTeam() != this.team) {
					if (currentClose == null)
						currentClose = e;
					else {
						
						double distanceToPlayerCurrent = Math.pow((currentClose.getCenterXpos() - this.getCenterXpos()), 2) + Math.pow((currentClose.getCenterYpos() - this.getCenterYpos()), 2);
						double distanceToPlayerNext = Math.pow((e.getCenterXpos() - this.getCenterXpos()), 2) + Math.pow((e.getCenterYpos() - this.getCenterYpos()), 2);

						if (distanceToPlayerNext < distanceToPlayerCurrent)
							currentClose = e;
					}
				}
			}
		}
		
		
		return currentClose;
	}
	
	public void setAnnimation(int startFrame, int endFrame, long duration){
		if (stateChanged) {			
			stateChanged = false;
			long[] durations = new long[endFrame-startFrame +1];
			for (int i = 0; i < durations.length; i++) {
				durations[i] = duration;
			}			
			getSprite().animate(durations, startFrame, endFrame, true);			
		}
		
		
	}
	
	/**
	 * 
	 */
	public void attackTarget() {
		//cycle annimation
		
		getSprite().setRotation(MathUtils.radToDeg((float) Math.atan2( ( getTarget().getXPos() - getXPos() ), -( getTarget().getYPos()- getYPos() ))));		
		Entity target = getTarget();
		target.damageEntity(this.getDamage());
		target.setLastEntityThatAttackedMe(this);
		if (target instanceof Creep) {
			target.setTarget(this);
		}
		
		
	}
	
	public void stopEntity(){		
		final Vector2 velocity = Vector2Pool.obtain(0, 0);
		getBody().setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}
	
	
	
	public void damageEntity(int amount){
		if (amount >= 0) {
			currenthealth -= amount;
		}
	}
	
	
	public void healEntity(float amount) {
		if (amount >= 0) {
			if (currenthealth + amount > maxhealth) {
				currenthealth = maxhealth;
			} else {
				currenthealth += amount;
			}
		}
	}
	
	
	public float getCenterXpos(){

		return (xpos + (getSprite().getWidth() / 2));

	}
	
	public float getCenterYpos(){
		
		
		if (getSprite() == null) {
			return ypos;
		} else {
			return (ypos + (getSprite().getHeight()/2));
		}
	}

	/**
	 * gets the x position
	 * @return x position
	 */
	public float getXPos() {
		return xpos;
		
	}
	
	
	
	/**
	 * sets the x position
	 * @param xpos
	 */
	public void setXPos(float xpos) {
		this.xpos = xpos;
	}
	
	/**
	 * gets the y position
	 * @return
	 */
	public float getYPos() {
		return ypos;
	}
	
	/**
	 * gets the y position
	 * @param ypos
	 */
	public void setYPos(float ypos) {
		this.ypos = ypos;
	}
	
	
	public double getAngleOfLineToTarget() {
		float xDiff = getTarget().getCenterXpos() - getCenterXpos();
		float yDiff = getTarget().getCenterYpos() - getCenterYpos();
		return Math.toDegrees(Math.atan2(xDiff, -yDiff));
	}

	public double getDistanceToTarget(Entity ent) {
		if (ent != null) {
			float s1 = ent.getCenterXpos() - getCenterXpos();
			float s2 = ent.getCenterYpos() - getCenterYpos();
			return Math.sqrt(s1 * s1 + s2 * s2);
		} else {
			return 9999;
		}
		
	}

	public float getDirectionToTarget() {
		if (hasTarget()) {
			float x = getTarget().getCenterXpos() - this.getCenterXpos();
			float y = getTarget().getCenterYpos() - this.getCenterYpos();
			return MathUtils.radToDeg((float) Math.atan2(x, -y));
		} else {
			return 0;
		}

	}
	
	/**
	 * gets the entitys current health
	 * @return current health of entity
	 */
	public float getCurrenthealth() {
		return currenthealth;
	}
	
	/**
	 * set the current health of entity
	 * @param currenthealth
	 */
	public void setCurrenthealth(int currenthealth) {
		if (currenthealth > this.maxhealth) {
			this.currenthealth = maxhealth;
		} else {
			this.currenthealth = currenthealth;
		}		
	}
	/**
	 * gets the entitys max health
	 * @return max health
	 */
	public float getMaxhealth() {
		return maxhealth;
	}
	
	/**
	 * sets entitys max health
	 * @param maxhealth
	 */
	public void setMaxhealth(int maxhealth) {
		this.maxhealth = maxhealth;
	}
	
	/**
	 * Sets the sprite of the entity
	 * @param sprite
	 */
	public void setSprite(CustomSprite cS, AnimatedSprite sprite){
		this.customSprite = cS;
		this.customSprite.setSprite(sprite);
	}
	/**
	 * gets the entity sprite
	 * @return
	 */
	public AnimatedSprite getSprite() {
		return customSprite.getSprite();
	}
	
	public CustomSprite getParentSprite(){
		return customSprite;
	}
	
	
	
	/**
	 * gets the physics handler that moves the sprite
	 * @return physics handler
	 */
	public PhysicsHandler getPhyHandler() {
		return phyHandler;
	}
	/**
	 * sets the physics handler that moves the sprite
	 * @param phyHandler
	 */
	public void setPhyHandler(PhysicsHandler phyHandler){
		this.phyHandler = phyHandler;
	}
	
	/**
	 * gets the movemodifier that moves the sprite
	 * @return MoveModifier
	 */
	public MoveModifier getMoveModifier() {
		return moveModifier;
	}

	/**
	 * sets the movemodifier that moves the sprite
	 * @param moveModifier
	 */
	public void setMoveModifier(MoveModifier moveModifier) {
		this.moveModifier = moveModifier;
	}

	/**
	 * gets the entitys speed
	 * @return speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * sets the speed of the entity
	 * @param f
	 */
	public void setSpeed(float f) {
		if (f > maxSpeed) {
			speed = maxSpeed;
		} else if (f < 0) {
			speed = 0;
		} else {
			this.speed = f;
		}
	}

	/**
	 * gets the direction the of the entity
	 * @return direction of entity
	 */
	public float getDirection() {
		return direction;
	}

	/**
	 * sets the direction the entity is facing
	 * @param direction
	 */
	public void setDirection(float direction) {
		this.direction = direction;
	}
	
	/**
	 * set the body of the entity
	 * @param body
	 */
	public void setBody(Body body){
		this.body = body;
	}
	/**
	 * gets the body of the entity
	 * @return body of the entity
	 */
	public Body getBody(){
		return body;
	}

	/**
	 * checks if the entity is alive
	 * @return alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * sets if the entity is alive or not
	 * @param alive
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * gets the time of when the entity last attacked attacked
	 * @return time of last attack
	 */
	public long getLastAttackTime() {
		return lastAttackTime;
	}

	/**
	 * sets the time of when the entity last attacked
	 * @param lastAttackTime time of last attack
	 */
	public void setLastAttackTime(long lastAttackTime) {
		this.lastAttackTime = lastAttackTime;
	}

	/**
	 * gets the length of attack cool down in ms
	 * @return ms length of attack cool down
	 */
	public long getAttackCoolDown() {
		return attackCoolDown;
	}

	/**
	 * sets the length of attack cool down
	 * @param attackCoolDown length of cool down
	 */
	public void setAttackCoolDown(long attackCoolDown) {
		this.attackCoolDown = attackCoolDown;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ALL_TEAMS getTeam() {
		return team;
	}

	public void setTeam(ALL_TEAMS team) {
		this.team = team;
	}

	public EntityState getState() {
		return state;
	}
	
	



	public void setState(EntityState state) {
		this.state = state;
	}


	public Entity getLastEntityThatAttackedMe() {
		return lastEntityThatAttackedMe;
	}



	public void setLastEntityThatAttackedMe(Entity lastEntityThatAttackedMe) {
		this.lastEntityThatAttackedMe = lastEntityThatAttackedMe;
	}
	
	
	
	
	
	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public boolean hasTarget()
	{
		return target != null;
	}
	
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
		
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public int getAttackRange() {
		if (getTarget() instanceof Base || getTarget() instanceof Tower) {
			return attackRange+64;
		} else {
			return attackRange;
		}
		
		
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}


	public float getRegenAmount() {
		return regenAmount;
	}
	
	
	public abstract void checkState(GameData gameData);
	
	
	
	/**
	 * The System time the entity will respawn
	 * @return the time the entity will respawn
	 */
	public long getRespawnTime() {
		return respawnTime;
	}
	
	/**
	 * Sets respawn time to current system time + respawn time
	 * @Param respawnTime how long it will take to respawn the player
	 */
	public void setRespawnTime() {
		long respawnLength = 10000;
		this.respawnTime = (System.currentTimeMillis() + respawnLength);
	}
	
	


	
	
	
	
	
	
	
	
	
	
	

	
}
