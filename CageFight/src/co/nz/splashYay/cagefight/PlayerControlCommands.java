package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.net.InetAddress;

import co.nz.splashYay.cagefight.ItemManager.AllItems;

public class PlayerControlCommands implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private float movementX;
	private float movementY;
	
	private boolean attackCommand;
	private int attackState;
	private int targetID;
	
		
	private float clientPosX;
	private float clientPosY;
	
	private AllItems purchaseItem;
	
	
	
	
	public PlayerControlCommands(){
		movementX =0;
		movementY = 0;
		clientPosX = 0;
		clientPosY = 0;
		attackCommand = false;
		attackState = 0;
		purchaseItem = null;
		
	}
	
	
	


	public AllItems getPurchaseItem() {
		return purchaseItem;
	}





	public void setPurchaseItem(AllItems purchaseItem) {
		this.purchaseItem = purchaseItem;
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





	public int getAttackState() {
		return attackState;
	}





	public void setAttackState(int attackState) {
		this.attackState = attackState;
	}
	
	
	
	
	
	
	
	
	
	
}
