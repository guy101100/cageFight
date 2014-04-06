package co.nz.splashYay.cagefight;

import java.io.Serializable;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;

public class Entity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int xpos;
	private int ypos;
	private float direction;
	private int currenthealth;
	private int maxhealth;
	private int speed;
	
	
	
	private transient Sprite sprite;
	private transient PhysicsHandler phyHandler;
	private transient MoveModifier moveModifier;
	
	public Entity(int xpos, int ypos, int maxhealth, int currenthealth)
	{
		this.xpos = xpos;
		this.ypos = ypos;
		this.maxhealth = maxhealth;
		this.currenthealth = currenthealth;
		this.speed = 300;
		this.direction = 0;
	}


	public int getXPos() {
		return xpos;
	}
	
	public void setXPos(int xpos) {
		this.xpos = xpos;
	}
	
	public int getYpos() {
		return ypos;
	}
	
	public void setYpos(int ypos) {
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
	
	
	
	
	
	
	
	

	
}
