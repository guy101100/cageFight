package co.nz.splashYay.cagefight.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.math.MathUtils;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.ItemManager.AllItems;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.UpgradeItem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends Entity implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private static final int MAXLEVEL = 20;
	private String name;
	
	private float movementX = 0;
	private float movementY = 0;
	private boolean attackCommand = false;
	private int attackState = 0;
	
	private long lastSpecialAttackTime = 0;
	private long specialAttackCooldown = 6000;
	
	//Player Stats
	private int experience;
	private int level;	
	
	private int gold;	
	private int killCount = 0;
	private int deathCount = 0;
	
	private int expToLevel = 120;
	
	private boolean atShop;
	private AllItems wantsToPurchase;
	
	//points a player has to level up abilities

	private int abilityPoints;
	
	

	public Player(String name, int id, int maxhealth, int currenthealth, int xpos, int ypos, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		this.name = name;
		this.level = 1;
		setMaxSpeed(10);
		setSpeed(10);	
		
		atShop = false;
		wantsToPurchase = null;
		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////
	//                         Client only methods
	///////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Used by the ClientInNetCom to update the players data
	 * @param player to get data from
	 */
	public void updateFromServer(Player player){
		this.xpos = player.getXPos();
		this.ypos = player.getYPos();
		this.atShop = player.isAtShop();
		
		
		this.direction = player.getDirection();
		
		this.currenthealth = player.getCurrenthealth();
		this.maxhealth = player.getMaxhealth();
		
		this.speed = player.getSpeed();		
					
		this.target = player.getTarget();
		
		this.experience = player.getExperience();
		this.expToLevel = player.getLevelExp();
		this.level = player.getLevel();
		this.movementX = player.getMovementX();
		this.movementY = player.getMovementY();
		this.team = player.getTeam();
		this.gold = player.getGold();
		this.killCount = player.getKillCount();
		this.deathCount = player.getDeathCount();
		
		if (this.state != player.getState()) {
			this.state = player.getState();
			this.stateChanged = true;
		}
		
		
	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////
	//                         sever only methods
	///////////////////////////////////////////////////////////////////////
	
	public void specialAttack(ArrayList<Entity> entities) {
		int specialAttackRange = 200;
		int damage = 75;
		System.out.println("SPECIAL");
		for (Entity ent : entities) {
			if (ent.getState() != EntityState.DEAD && getDistanceToTarget(ent) < specialAttackRange) {

				ent.damageEntity(damage);

			}
		}
		

	}
	
	
	/**
	 * removes gold, changes the sprite image, set body inactive, sets respawn time
	 */
	public void killPlayer(){
		this.setAlive(false);
		// TO ADD : change players sprite to "dead Image".
		
		this.getBody().setActive(false);
		this.setRespawnTime();
		System.out.println("Player : " + this.getId() + " has Died. Respawn in " + getRespawnTime() + " [" + System.currentTimeMillis() + "]");
		
		//Add 1 to the killer's kill count
		if(this.getLastEntityThatAttackedMe() instanceof Player)
		{
			Player killer = (Player) this.getLastEntityThatAttackedMe();
			killer.setKillCount(killer.getKillCount() + 1);
			killer.addGold(this.getLevel() * 75 );
		}
		
		//Add 1 to this players death count
		this.setDeathCount(getDeathCount() + 1);
		
		//Remove gold on death
		if (!this.spendGold(this.getLevel() * 10)) {
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
	    this.setXPos(x);
	    this.setYPos(y);
	    final Vector2 v2 = Vector2Pool.obtain(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
	    
	    getBody().setTransform(v2, angle);
	    Vector2Pool.recycle(v2);
	    
	    this.setPlayerState(EntityState.IDLE);
		setAlive(true);
	    System.out.println("Player : " + this.getId() + " has respawned [" + System.currentTimeMillis() + "]");
	    
		
	}
	
	public void levelUp() {
		if (this.getLevel() < MAXLEVEL) {
			this.level++;
			System.out.println("Player : " + this.getId() + " has leveled up to level " + this.getLevel() + "!");
			
			int extraExp = this.getExperience() - this.getLevelExp();
			this.setNextLevelExp(calcNextLevelExp());			
			this.resetExperience();
			this.addExperience(extraExp);
			
			System.out.println("Current " + getExperience());
			this.setAbilityPoints(this.getAbilityPoints() + 1);
			
			setMaxDamage(getMaxDamage() + (level * 5));
			setMaxhealth((int) (getMaxhealth() + (level * 10)));
			setRegenAmount(getRegenAmount()+2);
			
		}
	}
	
	
	
	private int calcNextLevelExp() {
		return (int) (this.getLevelExp()*1.5);
	}



	public void addExperience(int experience) {
		if (experience >= 0) {
			this.experience += experience;
		}
		checkForLevel();
	}
	
	public void resetExperience() {
		this.experience = 0;
	}


	public void checkForLevel()
	{
		if (this.getExperience() >= this.expToLevel)
		{
			this.levelUp();
		}
	}
	
	/**
	 * Checks and update the players state
	 */
	@Override
	public void checkState(GameData gameData){
		EntityState oldState = getState();
		if (this.getCurrenthealth() <= 0) {
			setPlayerState(EntityState.DEAD);			
			
		} else  if (attackCommand)
		{
			if(attackState == 0 && System.currentTimeMillis() >= (getLastAttackTime() + getAttackCoolDown()) && getDistanceToTarget(getTarget()) < getAttackRange())
			{
				setPlayerState(EntityState.ATTACKING);	
			}
			else if(attackState == 1 && System.currentTimeMillis() >= (getLastSpecialAttackTime() + getSpecialAttackCooldown()) )
			{
				setPlayerState(EntityState.SPECIALATTACKING);	
			} else {
				setPlayerState(EntityState.IDLE);
			}
			
		} else if (getMovementX() != 0 && getMovementY() != 0) {
			setPlayerState(EntityState.MOVING);
			
		} else {
			setPlayerState(EntityState.IDLE);
		}	
		
		if (oldState != getState()) {
			stateChanged = true;
		}
	}
	
	public boolean purchaseItem(UpgradeItem item){
		if (spendGold(item.getCost())) {
			wantsToPurchase = null;	
			return true;
		} else {
			return false;
		}
	}
	
	private boolean spendGold(int amount){
		if (getGold() - amount >= 0) {
			setGold(getGold() - amount);
			return true;
		} else {
			return false;
		}
		
		
	}
	
	public void addGold(int amount){
		this.gold += amount;
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
		return expToLevel;
	}
	
	public void setNextLevelExp(int LevelExp)
	{
		this.expToLevel = LevelExp;
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
	 * Sets respawn time to current system time + respawn time
	 * @Param respawnTime how long it will take to respawn the player
	 */
	@Override
	public void setRespawnTime() {
		long respawnLength = 10000;
		this.respawnTime = (System.currentTimeMillis() + respawnLength);
	}

	public String getStatsString()
	{
		return "Player ID: "+ this.getId() + "; Level: " + level + "; Gold: " + gold + "; K/D: " + this.getKillCount() + "/" + this.getDeathCount();
	}



	public int getKillCount() {
		return killCount;
	}



	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}



	public int getDeathCount() {
		return deathCount;
	}



	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}



	public AllItems getWantsToPurchase() {
		return wantsToPurchase;
	}



	public void setWantsToPurchase(AllItems wantsToPurchase) {
		this.wantsToPurchase = wantsToPurchase;
	}



	public boolean isAtShop() {
		return atShop;
	}



	public void setAtShop(boolean isAtShop) {
		this.atShop = isAtShop;
	}



	public long getSpecialAttackCooldown() {
		return specialAttackCooldown;
	}



	public void setSpecialAttackCooldown(long specialAttackCooldown) {
		this.specialAttackCooldown = specialAttackCooldown;
	}



	public long getLastSpecialAttackTime() {
		return lastSpecialAttackTime;
	}



	public void setLastSpecialAttackTime(long lastSpecialAttackTime) {
		this.lastSpecialAttackTime = lastSpecialAttackTime;
	}



	public int getAttackState() {
		return attackState;
	}



	public void setAttackState(int attackState) {
		this.attackState = attackState;
	}
	
	
	
	
	



	

	
	
	

	
	
	
	
	
}
