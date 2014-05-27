package co.nz.splashYay.cagefight.entities;

import java.util.ArrayList;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class Base extends Building{
	private static final long serialVersionUID = 1L;
	
	
	
	public Base(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		this.speed = 0;
		
		
	}
	
	
	public void destroyBase(){
		setAlive(false);
		getBody().setActive(false);
		
		
		
	}

	
	
	
	
	
	

}
