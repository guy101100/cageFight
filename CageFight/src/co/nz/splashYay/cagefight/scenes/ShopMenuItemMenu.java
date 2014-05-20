package co.nz.splashYay.cagefight.scenes;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;

import co.nz.splashYay.cagefight.UpgradeItem;

public class ShopMenuItemMenu extends MenuScene implements IOnMenuItemClickListener{
	
	private UpgradeItem upgradeItem;
	
	private final int BACK = 0;
	private final int BUY = 1;
	
	public ShopMenuItemMenu() {
		
	}
	
	public void loadRes(){
		
	}
	
	public void createScene(){
		
		
	}
	
	public void setUpgradeItem(UpgradeItem item){
		this.upgradeItem = item;
		this.update();
	}
	
	private void update(){
		
	}
	
	
	
	

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case BACK:
			this.back();			
			return true;
		case BUY:
			
			return true;
			
		default:
			return false;
			
		}
		
	}

}
