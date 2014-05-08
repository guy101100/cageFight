package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.andengine.entity.sprite.Sprite;

import co.nz.splashYay.cagefight.SceneManager.AllScenes;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;


public class GameData implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	
	private ArrayList<Integer> IDs = new ArrayList<Integer>();
	private long sendTime;
	
	private Team goodTeam;
	private Team evilTeam;
	
	public GameData(){
		goodTeam = new Team(1);
		evilTeam = new Team(2);
		
	}
	
	public GameData(GameData gD) {
		this.goodTeam = gD.getTeam(ALL_TEAMS.GOOD);
		this.goodTeam = gD.getTeam(ALL_TEAMS.EVIL);
		this.entities = (HashMap<Integer, Entity>) gD.getEntities().clone();
		this.IDs = (ArrayList<Integer>) gD.getIDs().clone();
		sendTime = System.currentTimeMillis();
	}
	
	
	
	/**
	 * gets the first unused id number
	 * @return an unused ID number
	 */
	public synchronized int getUnusedID(){
		int toReturn = 0;
		int checkNum = 1;
		while (toReturn == 0) {
			if (!IDs.contains(checkNum)) {
				toReturn = checkNum;
				IDs.add(checkNum);
			} else {
				checkNum++;
			}
		}
		return toReturn;
		
	}
	
	public Entity getEntityFromSprite(Sprite sprite) {
		for (Entity entity : entities.values()) {
			if (entity.getSprite() == sprite) {
				return entity;
			}
		}	
		
		return null;
	}

	public void addEntity(Entity entity){
		if (entity != null && !entities.containsKey(entity.getId())) {
			entities.put(entity.getId(), entity);
		}
	}
	
	
	public Entity getEntityWithId(int id){
		return entities.get(id);
	}
	
	public void addPlayer(Player player){ // causes concurrent modification errors
		if (player !=null ) {
			entities.put(player.getId(), player);
		} else {
			System.out.println("Null player");
		}
	}
	
	public HashMap<Integer, Entity> getEntities(){
		return entities;
		
	}
	
	public Team getTeam(ALL_TEAMS team){
		if (team == ALL_TEAMS.GOOD) {
			return goodTeam;
		} else {
			return evilTeam;
		}
	}


	public void setGoodTeam(Team team1) {
		this.goodTeam = team1;
	}

	public void setEvilTeam(Team team2) {
		this.evilTeam = team2;
	}

	public ArrayList<Integer> getIDs() {
		return IDs;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	
	
	
	
	
	
	
	
	
	
}
