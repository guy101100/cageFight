package co.nz.splashYay.cagefight;

public class Player {
	private int health;
	private int id;
	private String name;
	private int experience;
	
	public Player(String name, int id, int health){
		this.id = id;
		this.name = name;
		
	}
	
	public String getName()
	{
		return name;
	}
	
}
