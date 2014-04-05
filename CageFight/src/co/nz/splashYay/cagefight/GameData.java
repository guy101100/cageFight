package co.nz.splashYay.cagefight;

import java.io.Serializable;
import java.util.HashMap;


public class GameData implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	//public HashSet<Player> players;
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();

	public GameData(){
				
		
	}
	

	
	
	
	public Player getPlayerWithID(int id){
		return (Player) players.get(id);
	}
	
	public void addPlayer(Player player){
		if (player !=null ) {
			players.put(player.getId(), player);
		} else {
			System.out.println("Null player");
		}
	}
	
	public HashMap<Integer, Player> getPlayers(){
		return players;
		
	}
	
	
	
	
}
