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
	protected float xpos;
	protected float ypos;
	protected float direction;
	protected int currenthealth;
	protected int maxhealth;
	protected int speed;	
	protected State state;
	
	
	private transient Sprite sprite;
	private transient PhysicsHandler phyHandler;
	private transient MoveModifier moveModifier;
	private transient Body body;
	
	public Entity(int xpos, int ypos, int maxhealth, int currenthealth)
	{
		this.xpos = xpos;
		this.ypos = ypos;
		this.maxhealth = maxhealth;
		this.currenthealth = currenthealth;
		this.speed = 5;
		this.direction = 0;
		this.state = State.IDLE;
	}


	public float getXPos() {
		return xpos;
	}
	
	public void setXPos(float xpos) {
		this.xpos = xpos;
	}
	
	public float getYpos() {
		return ypos;
	}
	
	public void setYpos(float ypos) {
		this.ypos = ypos;
	}

	public int getCurrenthealth() {
		return currenthealth;
	}

	public void setCurrenthealth(int currenthealth) {
		this.currenthealth = currenthealth;
	}

	public int getMaxhealth() {
		return maxhealth;
	}

	public void setMaxhealth(int maxhealth) {
		this.maxhealth = maxhealth;
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
	public void setPhyHandler(PhysicsHandler phyHandler){
		this.phyHandler = phyHandler;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public PhysicsHandler getPhyHandler() {
		return phyHandler;
	}


	public MoveModifier getMoveModifier() {
		return moveModifier;
	}


	public void setMoveModifier(MoveModifier moveModifier) {
		this.moveModifier = moveModifier;
	}


	public int getSpeed() {
		return speed;
	}


	public void setSpeed(int speed) {
		this.speed = speed;
	}


	public float getDirection() {
		return direction;
	}


	public void setDirection(float direction) {
		this.direction = direction;
	}
	
	public void setBody(Body body){
		this.body = body;
	}
	public Body getBody(){
		return body;
	}


	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}
	
	
	
	
	
	
	
	
	
	

	
}
