package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.util.HashMap;


public class GameData implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();

	public GameData(){
				
		
	}
	
	
	
	/**
	 * gets the first unused id number
	 * @return an unused ID number
	 */
	public int getUnusedID(){
		int toReturn = 0;
		int checkNum = 1;
		while (toReturn == 0) {
			if (!entities.containsKey(checkNum)) {
				toReturn = checkNum;
			} else {
				checkNum++;
			}
		}
		return toReturn;
		
	}

	
	
	
	public Player getPlayerWithID(int id){
		return (Player) entities.get(id);
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
