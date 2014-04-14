package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.util.HashMap;


public class GameData implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();

	public GameData(){
				
		
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
	
	public HashMap<Integer, Entity> getPlayers(){
		return entities;
		
	}
	
	
	
	
}
