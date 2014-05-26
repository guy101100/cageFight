package co.nz.splashYay.cagefight;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class CustomSprite extends Sprite{
	
	private AnimatedSprite sprite;
	private ValueBar healthBar;
	
	
	public CustomSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
	}


	public AnimatedSprite getSprite() {
		return sprite;
	}


	public void setSprite(AnimatedSprite sprite) {
		this.sprite = sprite;
		this.sprite.setPosition(0, 0);
		this.attachChild(sprite);
	}


	public ValueBar getHealthBar() {
		return healthBar;
	}


	public void setHealthBar(ValueBar healthBar) {
		this.healthBar = healthBar;				
		this.attachChild(healthBar);
	}
	
	public void hideHealthBar(){
		this.healthBar.setVisible(false);
	}
	
	public void showHealthBar(){
		this.healthBar.setVisible(true);
	}
	
	
	
	
	

}
