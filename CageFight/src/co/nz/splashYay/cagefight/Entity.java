package co.nz.splashYay.cagefight;

import java.io.Serializable;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.physics.box2d.Body;

public class Entity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int id;
	protected int teamID;
	protected float xpos;
	protected float ypos;
	protected float direction;
	protected int currenthealth;
	protected int maxhealth;
	protected int speed;	
	private long lastAttackTime;	
	private long attackCoolDown;
	
	protected boolean alive;
	
	protected EntityState state;
	
	private transient Sprite sprite;
	private transient PhysicsHandler phyHandler;
	private transient MoveModifier moveModifier;
	private transient Body body;
	
	
	
	public Entity(int xpos, int ypos, int maxhealth, int currenthealth, int id, int teamId)
	{
		this.xpos = xpos;
		this.ypos = ypos;
		this.maxhealth = maxhealth;
		this.currenthealth = currenthealth;
		this.speed = 5;
		this.direction = 0;
		lastAttackTime = 0;
		attackCoolDown = 2000;
		alive = true;
		this.id = id;
		this.teamID = teamId;
		
		this.state = EntityState.IDLE;
		
	}

	/**
	 * gets the x position
	 * @return x position
	 */
	public float getXPos() {
		if (sprite == null) {
			return xpos;
		} else {
			return xpos + (sprite.getWidth()/2);
		}
		
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
		if (sprite == null) {
			return ypos;
		} else {
			return ypos + (sprite.getHeight()/2);
		}
	}
	
	/**
	 * gets the y position
	 * @param ypos
	 */
	public void setYPos(float ypos) {
		this.ypos = ypos;
	}
	
	/**
	 * gets the entitys current health
	 * @return current health of entity
	 */
	public int getCurrenthealth() {
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
	public int getMaxhealth() {
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
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
	/**
	 * gets the entitys sprite
	 * @return
	 */
	public Sprite getSprite() {
		return sprite;
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
	public int getSpeed() {
		return speed;
	}

	/**
	 * sets the speed of the entity
	 * @param speed
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
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

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	
	
	
	


	
	
	
	
	
	
	
	
	
	
	

	
}
