package co.nz.splashYay.cagefight;

import java.util.HashMap;

import org.andengine.ui.activity.BaseGameActivity;

public class ItemManager {
	
	HashMap<AllItems, UpgradeItem> items;
	
	
	public ItemManager(BaseGameActivity activity) {
		items = new HashMap<AllItems, UpgradeItem>();
		int id = 1;
		
		//activity, id,  name,  description, cost, damage,  health,  speed,  cooldown,  creepDamage,  creepHealth,  creepSpeed,  creepCooldown,  amountOfCreeps,  baseDamage,  baseHealth,  baseCooldown,  towerDamage,  towerHealth,  towerCooldown,  imageString 
		
		items.put(AllItems.AXE, new UpgradeItem(activity, id++ , "Power Axe", "Can I axe you a question?", 300, 500, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"axe.png", AllItems.AXE));
		items.put(AllItems.SHIELD, new UpgradeItem(activity,id++ , "Basic Shield", "A tiny Shield", 50, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"shield.png", AllItems.SHIELD));
		items.put(AllItems.SONICBOOTS, new UpgradeItem(activity,id++ , "Speedy boots", "Makes you run faster", 100, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"missing.png", AllItems.SONICBOOTS));
		items.put(AllItems.SWIFTBLADE, new UpgradeItem(activity, id++ , "Swift Blade", "Increase your attack speed", 500, 0, 0, 0, 500, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"swiftblade.png", AllItems.SWIFTBLADE));
		
		items.put(AllItems.CREEP_SPEED, new UpgradeItem(activity, id++ , "Creepy Boots", "Boots! But for your creeps!", 100, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,"missing.png", AllItems.CREEP_SPEED));
		items.put(AllItems.CREEP_HP, new UpgradeItem(activity, id++ , "Creep DR", "Increases creep health.", 100, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0,"missing.png", AllItems.CREEP_HP));
		items.put(AllItems.CREEP_DAMAGE, new UpgradeItem(activity, id++ , "BattleCreep", "Increases creep attack Damage.", 100, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"missing.png", AllItems.CREEP_DAMAGE));

	}
	
	public UpgradeItem getItem(AllItems item){
		return items.get(item);
	}
	
	
	
	public enum AllItems {
		AXE, SHIELD, SONICBOOTS, SWIFTBLADE, CREEP_SPEED, CREEP_HP, CREEP_DAMAGE
	}
	
	
	
}
