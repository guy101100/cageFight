package co.nz.splashYay.cagefight;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;

import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Creep;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.entities.Tower;

public class SlowRepeatingTask extends Thread {
	private int bugCounterTileIsNull = 0;
	private TMXTiledMap mTMXTiledMap;	
	private GameData gameData;
	
	
	public SlowRepeatingTask(TMXTiledMap mTMXTiledMap, GameData gameData) {
		this.mTMXTiledMap = mTMXTiledMap;
		this.gameData = gameData;
	}
	
	@Override
	public void run() {
		
		
		while (true) {
			HashMap temp = (HashMap) gameData.getEntities().clone();

			Iterator it = temp.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				
				
				
				
				
				if (pairs.getValue() instanceof Player) {
					Player player = (Player) pairs.getValue();
					checkTileEffect(player);
					player.healEntity(player.getRegenAmount());
					

				} else if (pairs.getValue() instanceof Creep) {
					Creep creep = (Creep) pairs.getValue();
					checkTileEffect(creep);
					creep.healEntity(creep.getRegenAmount());

				} else if (pairs.getValue() instanceof Tower) {
					Tower tower = (Tower) pairs.getValue();
					tower.healEntity(tower.getRegenAmount());
					
				} else if (pairs.getValue() instanceof Base) {
					Base base = (Base) pairs.getValue();
					base.healEntity(base.getRegenAmount());
					
				}
				
			}
			
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}
	
	private void checkTileEffect(Entity entity) {
		final TMXTile tmxTile = mTMXTiledMap.getTMXLayers().get(12).getTMXTileAt(entity.getCenterXpos(), entity.getCenterYpos());
		
		if (tmxTile != null && tmxTile.getGlobalTileID() != 0) {
			try {
				if (tmxTile.getTMXTileProperties(mTMXTiledMap).containsTMXProperty("badHeal", "true")) {
					if (entity.getTeam() == ALL_TEAMS.EVIL) {
						entity.healEntity(30f);
					} else {
						entity.setSpeed(2);
						//damage the entity
					}
					
					
				} else if (tmxTile.getTMXTileProperties(mTMXTiledMap).containsTMXProperty("goodHeal", "true")) {
					if (entity.getTeam() == ALL_TEAMS.GOOD) {
						entity.healEntity(30f);
					} else {
						entity.setSpeed(2);
						//damage the entity
					}
				} 
			} catch (NullPointerException np) {
				
				bugCounterTileIsNull++;
				System.err.println("Null pointer on the tile again : " + bugCounterTileIsNull + " Times");
				
			}
			
			
		} else { // is not on an effecting tile, reverse any effects on the player
			entity.setSpeed(entity.getMaxSpeed());
		}
	}
}
