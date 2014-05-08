package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.net.InetAddress;

public class PlayerControlCommands implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private float movementX;
	private float movementY;
	
	private boolean attackCommand;	
	private int targetID;
	
		
	private float clientPosX;
	private float clientPosY;
	
	private InetAddress inetAddr;
	private int port;
	
	
	public PlayerControlCommands(){
		movementX =0;
		movementY = 0;
		clientPosX = 0;
		clientPosY = 0;
		attackCommand = false;
		
	}
	
	
	


	public InetAddress getInetAddr() {
		return inetAddr;
	}





	public void setInetAddr(InetAddress inetAddr) {
		this.inetAddr = inetAddr;
	}





	public int getPort() {
		return port;
	}





	public void setPort(int port) {
		this.port = port;
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


	public boolean isAttackCommand() {
		return attackCommand;
	}


	public void setAttackCommand(boolean attackCommand) {
		this.attackCommand = attackCommand;
	}


	public int getTargetID() {
		return targetID;
	}


	public void setTargetID(int target) {
		this.targetID = target;
	}
	
	
	
	
	
	
	
	
}
