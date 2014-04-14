package co.nz.splashYay.cagefight;

public class Base extends Entity{
	private static final long serialVersionUID = 1L;
	
	
	
	public Base(int xpos, int ypos, int maxhealth, int currenthealth) {
		super(xpos, ypos, maxhealth, currenthealth);
		this.speed = 0;
		
	}
	
	
	public void checkState(){
		boolean atackablePlayers = false;
		
		if (currenthealth <= 0) {
			state = EntityState.DEAD;
			
		} else if (atackablePlayers) { 
			state = EntityState.ATTACKING;
		} else {
			state = EntityState.IDLE;			
		}
	}
	
	
	

}
