package co.nz.splashYay.cagefight;

import java.util.HashMap;

import org.andengine.ui.activity.BaseGameActivity;

public class ItemManager {
	
	HashMap<AllItems, UpgradeItem> items;
	
	
	public ItemManager(BaseGameActivity activity) {
		items = new HashMap<AllItems, UpgradeItem>();
		int id = 1;
		
		//activity, id, String name, String description, int cost,int damage, int health, int speed, int cooldown, int creepDamage, int creepHealth, int creepSpeed, int creepCooldown, int amountOfCreeps, int baseDamage, int baseHealth, int baseCooldown, int towerDamage, int towerHealth, int towerCooldown, String imageString
		
		items.put(AllItems.AXE, new UpgradeItem(activity, id++ , "Power Axe", "Can I axe you a question?", 300, 500, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"axe.png", AllItems.AXE));
		items.put(AllItems.SHIELD, new UpgradeItem(activity,id++ , "Basic Shield", "A tiny Shield", 50, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"shield.png", AllItems.SHIELD));
		items.put(AllItems.SONICBOOTS, new UpgradeItem(activity,id++ , "Speedy boots", "Makes you run faster", 100, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"axe.png", AllItems.SONICBOOTS));
		items.put(AllItems.SWIFTBLADE, new UpgradeItem(activity, id++ , "Swift Blade", "Increase your attack speed", 500, 0, 0, 0, 500, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"swiftblade.png", AllItems.SWIFTBLADE));
		
	}
	
	public UpgradeItem getItem(AllItems item){
		return items.get(item);
	}
	
	
	
	public enum AllItems {
		AXE, SHIELD, SONICBOOTS, SWIFTBLADE
	}
	
	
	
}
