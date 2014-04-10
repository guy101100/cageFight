package co.nz.splashYay.cagefight;

import java.io.Serializable;

import com.badlogic.gdx.physics.box2d.Body;

public class Player extends Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int experience;
	private int level;
	private float movementX = 0;
	private float movementY = 0;
	private float respawnTime = 0;
	
	
	
	public Player(String name, int id, int maxhealth, int currenthealth, int xpos, int ypos) {
		super(xpos, ypos, maxhealth, currenthealth);
		this.id = id;
		this.name = name;
		this.level = 0;
		
	}
	
	private void killPlayer(){
		//remove some gold.
		//change players sprite to "dead Image".
		this.setRespawnTime(this.calculateRespawnLength());
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Getters and setters

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getMovementX() {
		return movementX;
	}

	public void setMovementX(float movementX) {
		this.movementX = movementX;
	}

	public float getMovementY() {
		return movementY;
	}

	public void setMovementY(float movementY) {
		this.movementY = movementY;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/*
	 * The System time the player will respawn
	 */
	public float getRespawnTime() {
		return respawnTime;
	}
	
	/*
	 * Sets respawn time to current system time + respawn time
	 * @Param respawnTime how long it will take to respawn the player
	 */
	private void setRespawnTime(float respawnTime) {
		this.respawnTime = System.currentTimeMillis() + respawnTime;
	}
	
	/*
	 * Returns the length of the players respawn time
	 */
	private long calculateRespawnLength(){
		return this.level * 10000;
	}
	
	

	/*
	 * Used by the ClientInNetCom to update the players data
	 */
	public void updatePlayerInfoFromOtherPlayerData(Player player){
		this.experience = player.getExperience();
		this.movementX = player.getMovementX();
		this.movementY = player.getMovementY();
		this.currenthealth = player.getCurrenthealth();
		this.direction = player.getDirection();
		this.maxhealth = player.getMaxhealth();
		this.xpos = player.getXPos();
		this.ypos  = player.getYpos();
		this.speed = player.getSpeed();
		
		
		
	}
	
	
	
	
}
