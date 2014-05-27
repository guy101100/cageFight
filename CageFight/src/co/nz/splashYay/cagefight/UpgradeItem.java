package co.nz.splashYay.cagefight;

import java.util.ArrayList;

import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Creep;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.entities.Tower;

public class UpgradeItem {
	
	private String name;
	private String description;
	private int cost;
	
	private int damage;
	private int health;
	private int speed;
	private int cooldown;
	
	private int creepDamage;
	private int creepHealth;
	private int creepSpeed;
	private int creepCooldown;
	private int amountOfCreeps;
	
	private int baseDamage;
	private int baseHealth;	
	private int baseCooldown;
	
	private int towerDamage;
	private int towerHealth;	
	private int towerCooldown;
	private String imageString;
	
	protected BitmapTextureAtlas texture;
	protected ITextureRegion textureRegion;
	private int ID;
	
	
	public UpgradeItem(BaseGameActivity activity, int ID, String name, String description, int cost,int damage, int health, int speed, int cooldown, int creepDamage, int creepHealth, int creepSpeed, int creepCooldown, int amountOfCreeps, int baseDamage, int baseHealth, int baseCooldown, int towerDamage, int towerHealth, int towerCooldown, String imageString) {
		super();
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.imageString = imageString;
		
		
		this.damage = damage;
		this.health = health;
		this.speed = speed;
		this.cooldown = cooldown;
		
		this.creepDamage = creepDamage;
		this.creepHealth = creepHealth;
		this.creepSpeed = creepSpeed;
		this.creepCooldown = creepCooldown;		
		this.amountOfCreeps = amountOfCreeps;
		
		this.baseDamage = baseDamage;
		this.baseHealth = baseHealth;
		this.baseCooldown = baseCooldown;
		
		this.towerDamage = towerDamage;
		this.towerHealth = towerHealth;
		this.towerCooldown = towerCooldown;

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/items/");

		this.texture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512);
		this.textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, activity, imageString, 0, 0);
		texture.load();
	}
	
	
	public boolean effectsPlayer(){
		if (damage > 0 || speed > 0 || health > 0 || cooldown > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean effectsCreep(){
		if (creepDamage > 0 || creepSpeed > 0 || creepHealth > 0 || creepCooldown > 0) {
			return true;
		} else {
			return false;
		}
	}
	public boolean effectsTower(){
		if (towerDamage > 0 || towerHealth > 0 || towerCooldown > 0) {
			return true;
		} else {
			return false;
		}
	}
	public boolean effectsBase(){
		if (baseDamage > 0 || baseHealth > 0 || baseCooldown > 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<String> getStatBonusesString(){
		ArrayList<String> toReturn = new ArrayList<String>();
		
		if (damage != 0) {
			toReturn.add("Damage + " + damage);
		}
		if (health != 0) {
			toReturn.add("Health + " + health);
		}
		if (speed != 0) {
			toReturn.add("Speed + " + speed);
		}
		if (cooldown != 0) {
			toReturn.add("Attack Speed + " + cooldown);
		}
		
		
		if (creepDamage != 0) {
			toReturn.add("Creep damage + " + creepDamage);
		}
		if (creepHealth != 0) {
			toReturn.add("Creep health + " + creepHealth);
		}
		if (creepSpeed != 0) {
			toReturn.add("Creep speed + " + creepSpeed);
		}
		if (creepCooldown != 0) {
			toReturn.add("Creep attack speed + " + creepCooldown);
		}
		if (amountOfCreeps != 0) {
			toReturn.add("Creeps + " + amountOfCreeps);
		}
		
		
		if (towerDamage != 0) {
			toReturn.add("Tower Damage + " + towerDamage );
		}
		if (towerHealth != 0) {
			toReturn.add("Tower Health + " + towerHealth);
		}		
		if (towerCooldown != 0) {
			toReturn.add("Tower attack speed + " + towerCooldown);
		}
		
		if (baseDamage != 0) {
			toReturn.add("Base Damage + " + baseDamage);
		}
		if (baseHealth != 0) {
			toReturn.add("Base Health + " + baseHealth);
		}		
		if (baseCooldown != 0) {
			toReturn.add("Base attack speed + " + baseCooldown);
		}
		
		
		
		
		return toReturn;
	}
	
	

	public int getID() {
		return ID;
	}




	public BitmapTextureAtlas getTexture() {
		return texture;
	}



	public ITextureRegion getTextureRegion() {
		return textureRegion;
	}



	public String getName() {
		return name;
	}


	public String getDescription() {
		return description;
	}


	public int getCost() {
		return cost;
	}


	public int getDamage() {
		return damage;
	}


	public int getHealth() {
		return health;
	}


	public int getSpeed() {
		return speed;
	}


	public int getCooldown() {
		return cooldown;
	}


	public int getCreepDamage() {
		return creepDamage;
	}


	public int getCreepHealth() {
		return creepHealth;
	}


	public int getCreepSpeed() {
		return creepSpeed;
	}


	public int getCreepCooldown() {
		return creepCooldown;
	}


	public int getAmountOfCreeps() {
		return amountOfCreeps;
	}


	public int getBaseDamage() {
		return baseDamage;
	}


	public int getBaseHealth() {
		return baseHealth;
	}


	public int getBaseCooldown() {
		return baseCooldown;
	}


	public int getTowerDamage() {
		return towerDamage;
	}


	public int getTowerHealth() {
		return towerHealth;
	}


	public int getTowerCooldown() {
		return towerCooldown;
	}


	public String getImageString() {
		return imageString;
	}


	public void upgradeEntity(Entity entity) {
		if (entity instanceof Player) {
			entity.setMaxDamage(entity.getMaxDamage() + damage);
			entity.setDamage(entity.getDamage() + damage);
			entity.setMaxhealth((int) (entity.getMaxhealth() + health));
			entity.setMaxSpeed((int) (entity.getMaxSpeed() + speed));
			entity.setAttackCoolDown(entity.getAttackCoolDown() - cooldown);
			
		} else if (entity instanceof Creep) {
			entity.setMaxDamage(entity.getMaxDamage() + creepDamage);
			entity.setDamage(entity.getDamage() + creepDamage);
			entity.setMaxhealth((int) (entity.getMaxhealth() + creepHealth));
			entity.setMaxSpeed((int) (entity.getMaxSpeed() + creepSpeed));
			entity.setAttackCoolDown(entity.getAttackCoolDown() - creepCooldown);
			
		} else if (entity instanceof Tower) {
			entity.setMaxDamage(entity.getMaxDamage() + towerDamage);
			entity.setDamage(entity.getDamage() + towerDamage);
			entity.setMaxhealth((int) (entity.getMaxhealth() + towerHealth));	
			entity.setAttackCoolDown(entity.getAttackCoolDown() - towerCooldown);
			
		} else if (entity instanceof Base) {
			entity.setMaxDamage(entity.getMaxDamage() + baseDamage);
			entity.setDamage(entity.getDamage() + baseDamage);
			entity.setMaxhealth((int) (entity.getMaxhealth() + baseHealth));			
			entity.setAttackCoolDown(entity.getAttackCoolDown() - baseCooldown);
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
