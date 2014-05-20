package co.nz.splashYay.cagefight.entities;

import java.io.Serializable;
import java.util.Timer;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.math.MathUtils;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends Entity implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private static final int MAXLEVEL = 20;
	private String name;
	
	private float movementX = 0;
	private float movementY = 0;
	private boolean attackCommand = false;
	
	//Player Stats
	private int experience;
	private int level;
	private long respawnTime = 0;
	private int LevelExp;
	private int gold;
	private Timer goldTimer;
	
	//points a player has to level up abilities

	private int abilityPoints;
	
	

	public Player(String name, int id, int maxhealth, int currenthealth, int xpos, int ypos, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		this.name = name;
		this.level = 1;
		setMaxSpeed(10);
		setSpeed(10);	
		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////
	//                         Client only methods
	///////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Used by the ClientInNetCom to update the players data
	 * @param player to get data from
	 */
	public void updateFromServer(Player player){
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
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////
	//                         sever only methods
	///////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 * removes gold, changes the sprite image, set body inactive, sets respawn time
	 */
	public void killPlayer(){
		this.setAlive(false);
		// TO ADD : change players sprite to "dead Image".
		
		this.getBody().setActive(false);
		this.setRespawnTime();
		System.out.println("Player : " + this.getId() + " has Died. Respawn in " + getRespawnTime() + " [" + System.currentTimeMillis() + "]");
		
		this.setGold(this.getGold() - this.getLevel() * 10);
		if (this.getGold() < 0)
		{
			this.setGold(0);
		}
	}
	
	/**
	 * Reactivates body, teleports body to spawn point, heals player, set state to idle
	 */
	public void respawn(GameData gd){
		this.setPlayerState(EntityState.IDLE);
		setAlive(true);
		
		currenthealth = maxhealth; //heal the player to full health
		
		this.getBody().setActive(true); // reactivates the body
		
		//TO ADD : reset sprite to alive sprite
		
		//teleports the body to the respawn position	    
	    final float angle = getBody().getAngle(); // keeps the body angle
	    int x = gd.getTeam(getTeam()).getSpawnXpos();
	    int y = gd.getTeam(getTeam()).getSpawnYpos();
	    final Vector2 v2 = Vector2Pool.obtain(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
	    
	    getBody().setTransform(v2, angle);
	    Vector2Pool.recycle(v2);
	    System.out.println("Player : " + this.getId() + " has respawned [" + System.currentTimeMillis() + "]");
	    
	    
	    
	    
	    
	    

		
	}
	
	public void levelUp()
	{
		if (this.getLevel() < MAXLEVEL)
		{
			this.level++;
			System.out.println("Player : " + this.getId() + " has leveled up to level " + this.getLevel() + "!");
			
			int extraExp = this.getExperience() - this.getLevelExp();
			
			this.setExperience(0 + extraExp);
			
			this.setAbilityPoints(this.getAbilityPoints() + 1);
		}
	}
	
	/**
	 * Checks and update the players state
	 */
	public void checkState(){
		if (this.getCurrenthealth() <= 0) {
			setPlayerState(EntityState.DEAD);			
			
		} else  if (attackCommand) {
			setPlayerState(EntityState.ATTACKING);	
			
		} else if (getMovementX() != 0 && getMovementY() != 0) {
			setPlayerState(EntityState.MOVING);
			
		} else {
			setPlayerState(EntityState.IDLE);
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
	
	public int getLevelExp()
	{
		return LevelExp;
	}
	
	public void setNextLevelExp(int LevelExp)
	{
		this.LevelExp = LevelExp;
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
	
	

	public EntityState getPlayerState() {
		return state;
	}


	public void setPlayerState(EntityState state) {
		this.state = state;
	}
	
	public int getAbilityPoints() {
		return abilityPoints;
	}



	public void setAbilityPoints(int abilityPoints) {
		this.abilityPoints = abilityPoints;
	}
	
	public int getGold() {
		return gold;
	}



	public void setGold(int gold) {
		this.gold = gold;
	}


	public void initialiseGoldTimer()
	{
		try {
			goldTimer.wait(1000);
			this.setGold(this.getGold() + 1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public long getRespawnTime() {
		return respawnTime;
	}
	
	/**
	 * Sets respawn time to current system time + respawn time
	 * @Param respawnTime how long it will take to respawn the player
	 */
	private void setRespawnTime() {
		long respawnLength = this.level * 10000;
		this.respawnTime = (System.currentTimeMillis() + respawnLength);
	}

	public String getStatsString()
	{
		return "Player ID: "+ this.getId() + "; Level: " + level + ";Gold: " + gold;
	}
	
	



	

	
	
	

	
	
	
	
	
}
