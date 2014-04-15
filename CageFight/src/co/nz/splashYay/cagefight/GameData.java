package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class GameData implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	private ArrayList<Integer> IDs = new ArrayList<Integer>();

	public GameData(){
				
		
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
	
	
	
	
	
	
}
