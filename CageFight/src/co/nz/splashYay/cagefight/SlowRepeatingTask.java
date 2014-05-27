package co.nz.splashYay.cagefight;

import java.util.ArrayList;
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
import co.nz.splashYay.cagefight.scenes.ServerGameScene;

public class SlowRepeatingTask extends Thread {
	private int bugCounterTileIsNull = 0;
	private TMXTiledMap mTMXTiledMap;	
	private GameData gameData;
	private ServerGameScene sGS;
	
	
	public SlowRepeatingTask(TMXTiledMap mTMXTiledMap, GameData gameData, ServerGameScene sGS) {
		this.mTMXTiledMap = mTMXTiledMap;
		this.gameData = gameData;
		this.sGS = sGS;
	}
	
	@Override
	public void run() {
		
		
		while (true) {
			ArrayList<Integer> ids = (ArrayList<Integer>) gameData.getIDs().clone();			
			
			for(int id : ids) {
				Entity ent = gameData.getEntityWithId(id);
				
				if (ent != null && ent.isAlive()) {
					if (ent instanceof Player) {
						Player player = (Player) ent;						
						checkTileEffect(player);
						player.healEntity(player.getRegenAmount());

					} else if (ent instanceof Creep) {
						Creep creep = (Creep) ent;
						checkTileEffect(creep);
						creep.healEntity(creep.getRegenAmount());

					} else if (ent instanceof Tower) {
						Tower tower = (Tower) ent;
						tower.healEntity(tower.getRegenAmount());

					} else if (ent instanceof Base) {
						Base base = (Base) ent;
						base.healEntity(base.getRegenAmount());

					}
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
		final TMXTile tmxTile = mTMXTiledMap.getTMXLayers().get(0).getTMXTileAt(entity.getCenterXpos(), entity.getCenterYpos());
		
		if (tmxTile != null && tmxTile.getGlobalTileID() != 0) {
			try {
				if (tmxTile.getTMXTileProperties(mTMXTiledMap).containsTMXProperty("badHeal", "true")) {
					if (entity.getTeam() == ALL_TEAMS.BAD) {
						entity.healEntity(30f);
					} else {
						entity.setSpeed(entity.getMaxSpeed()/2);
						sGS.towerAttackExplosion(entity);
						entity.damageEntity(30);
					}
					
					
				} else if (tmxTile.getTMXTileProperties(mTMXTiledMap).containsTMXProperty("goodHeal", "true")) {
					if (entity.getTeam() == ALL_TEAMS.GOOD) {
						entity.healEntity(30f);

					} else {
						entity.setSpeed(entity.getMaxSpeed()/2);
						sGS.towerAttackExplosion(entity);
						entity.damageEntity(30);
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
