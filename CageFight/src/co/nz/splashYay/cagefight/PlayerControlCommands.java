package co.nz.splashYay.cagefight;

import java.io.Serializable;

public class PlayerControlCommands implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private float movementX;
	private float movementY;
	private float direction;
	
	private float clientPosX;
	private float clientPosY;
	
	
	public PlayerControlCommands(){
		movementX =0;
		movementY = 0;
		direction = 0;
		clientPosX = 0;
		clientPosY = 0;
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


	public float getDirection() {
		return direction;
	}


	public void setDirection(float direction) {
		this.direction = direction;
	}


	public float getClientPosX() {
		return clientPosX;
	}


	public void setClientPosX(float clientPosX) {
		this.clientPosX = clientPosX;
	}


	public float getClientPosY() {
		return clientPosY;
	}


	public void setClientPosY(float clientPosY) {
		this.clientPosY = clientPosY;
	}
	
	
	
	
}
