package co.nz.splashYay.cagefight;

import java.io.Serializable;

public class Player extends Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int experience;
	private float movementX = 0;
	private float movementY = 0;
	
	
	public Player(String name, int id, int maxhealth, int currenthealth, int xpos, int ypos) {
		super(xpos, ypos, maxhealth, currenthealth);
		this.id = id;
		this.name = name;
		
	}

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
	
	/*
	 * Used by the ClientInNetCom to update this players data
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
