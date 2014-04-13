package co.nz.splashYay.cagefight;

import java.io.Serializable;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private float movementX = 0;
	private float movementY = 0;
	private boolean attackCommand = false;
	
	//Player Stats
	private int experience;
	private int level;
	private int damage;
	
	private float respawnTime = 0;
	
	private PlayerState state;
	private Entity target;
	
	
	public Player(String name, int id, int maxhealth, int currenthealth, int xpos, int ypos) {
		super(xpos, ypos, maxhealth, currenthealth);
		this.id = id;
		this.name = name;
		this.level = 0;
		this.state = PlayerState.IDLE;
		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////
	//                         Client only methods
	///////////////////////////////////////////////////////////////////////
	
	
	/**
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
		this.ypos  = player.getYPos();
		this.speed = player.getSpeed();
		
	}
	
	public void targetNearestPlayer(GameData gd)
	{
		Player currentClose = null;
		
		for(Player p : gd.getPlayers().values())
		{
			if(p.getId() != this.id)
			{
				if(currentClose == null)
					currentClose = p;
				else
				{
					double distanceToPlayerCurrent = Math.pow((currentClose.getXPos() - this.getXPos()), 2) + Math.pow((currentClose.getYPos() - this.getYPos()), 2);
					double distanceToPlayerNext = Math.pow((p.getXPos() - this.getXPos()), 2) + Math.pow((p.getYPos() - this.getYPos()), 2);
					
					if(distanceToPlayerNext < distanceToPlayerCurrent)
						currentClose = p;
				}
			}
		}
		
		this.target = currentClose;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////
	//                         sever only methods
	///////////////////////////////////////////////////////////////////////
	/**
	 * removes gold, changes the sprite image, set body inactive, sets respawn time
	 */
	public void killPlayer(){
		this.setAlive(false);
		// TO ADD : remove some gold.
		// TO ADD : change players sprite to "dead Image".
		this.getBody().setActive(false);
		this.setRespawnTime(this.calculateRespawnLength());
		
		
	}
	
	/**
	 * Reactivates body, teleports body to spawn point, heals player, set state to idle
	 */
	private void respawn(){
		this.setPlayerState(PlayerState.IDLE);
		
		currenthealth = maxhealth; //heal the player to full health
		
		this.getBody().setActive(true); // reactivates the body
		
		//TO ADD : reset sprite to alive sprite
		
		//teleports the body to the respawn position
	    final float widthD2 = getSprite().getWidth() / 2;
	    final float heightD2 = getSprite().getHeight() / 2;
	    final float angle = getBody().getAngle(); // keeps the body angle
	    int x = 10; // x & y need to be replaced with team.getRespawnPosition()
	    int y = 10;
	    final Vector2 v2 = Vector2Pool.obtain((x + widthD2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (y + heightD2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
	    getBody().setTransform(v2, angle);
	    Vector2Pool.recycle(v2);
	    
	    
	    
	    
	    
	    

		
	}
	
	/**
	 * Checks and update the players state
	 */
	public void checkState(){
		if (this.getCurrenthealth() <= 0) {
			if (System.currentTimeMillis() >= getRespawnTime()) {
				this.respawn();
			} else {
				setPlayerState(PlayerState.DEAD);
			}
			
			
		} else  if (attackCommand) {
			setPlayerState(PlayerState.ATTACKING);	
			
		} else if (getMovementX() != 0 && getMovementY() != 0) {
			setPlayerState(PlayerState.MOVING);
			
		} else {
			setPlayerState(PlayerState.IDLE);
		}	
		
	}
	
	///////////////////////////////////////////////////////////////////////
	//                        Getters and setters
	///////////////////////////////////////////////////////////////////////

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
	
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public PlayerState getPlayerState() {
		return state;
	}


	public void setPlayerState(PlayerState state) {
		this.state = state;
	}
	
	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
	
	
	/**
	 * Returns true if the player has sent the attack command
	 * @return boolean attackCommand
	 */
	public boolean isAttackCommand() {
		return attackCommand;
	}

	public void setAttackCommand(boolean attackCommand) {
		this.attackCommand = attackCommand;
	}

	/**
	 * The System time the player will respawn
	 * @return the time the player will respawn
	 */
	public float getRespawnTime() {
		return respawnTime;
	}
	
	/**
	 * Sets respawn time to current system time + respawn time
	 * @Param respawnTime how long it will take to respawn the player
	 */
	private void setRespawnTime(float respawnTime) {
		this.respawnTime = System.currentTimeMillis() + respawnTime;
	}
	
	/**
	 * Returns the length of the players respawn time
	 */
	private long calculateRespawnLength(){
		return this.level * 10000;
	}

	
	
	

	
	
	
	
	
}
