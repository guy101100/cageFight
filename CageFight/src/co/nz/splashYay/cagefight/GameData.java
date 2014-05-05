package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.andengine.entity.sprite.Sprite;

import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;


public class GameData implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	private ArrayList<Integer> IDs = new ArrayList<Integer>();
	
	
	private Team team1;
	private Team team2;
	
	public GameData(){
		team1 = new Team(1);
		team2 = new Team(2);
		
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



	public Team getTeam1() {
		return team1;
	}



	public void setTeam1(Team team1) {
		this.team1 = team1;
	}



	public Team getTeam2() {
		return team2;
	}



	public void setTeam2(Team team2) {
		this.team2 = team2;
	}
	
	
	
	
	
	
	
	
}
