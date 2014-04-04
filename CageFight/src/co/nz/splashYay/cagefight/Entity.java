package co.nz.splashYay.cagefight;

public class Entity {
	
	private int xpos;
	private int ypos;
	private int currenthealth;
	private int maxhealth;
	
	public Entity(int xpos, int ypos, int maxhealth, int currenthealth)
	{
		this.xpos = xpos;
		this.ypos = ypos;
		this.maxhealth = maxhealth;
		this.currenthealth = currenthealth;
	}


	public int getXPos() {
		return xpos;
	}
	
	public void setXPos(int xpos) {
		this.xpos = xpos;
	}
	
	public int getYpos() {
		return ypos;
	}
	
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getCurrenthealth() {
		return currenthealth;
	}

	public void setCurrenthealth(int currenthealth) {
		this.currenthealth = currenthealth;
	}

	public int getMaxhealth() {
		return maxhealth;
	}

	public void setMaxhealth(int maxhealth) {
		this.maxhealth = maxhealth;
	}

	
}
