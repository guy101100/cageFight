package co.nz.splashYay.cagefight.scenes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.content.Context;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.os.Environment;
import android.widget.TextView;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;

public abstract class GameScene extends Scene {
	protected BaseGameActivity activity;
	protected Engine engine;
	protected Camera camera;
	
	protected BitmapTextureAtlas playerTexture;
	protected TiledTextureRegion playerTextureRegion;
	protected FixedStepPhysicsWorld phyWorld;
	protected BuildableBitmapTextureAtlas mBuildBitmapTextureAtlas;
	protected TMXTiledMap mTMXTiledMap;
	protected BitmapTextureAtlas mBitmapTextureAtlas;
	
	protected GameData gameData;
	
	protected PlayerControlCommands playerCommands = new PlayerControlCommands();
	
	protected BitmapTextureAtlas mOnScreenControlTexture;
	protected ITextureRegion mOnScreenControlBaseTextureRegion;
	protected ITextureRegion mOnScreenControlKnobTextureRegion;
	
	protected BitmapTextureAtlas baseTexture;
	protected TiledTextureRegion baseTextureRegion;
	
	protected BitmapTextureAtlas towerTexture;
	protected TiledTextureRegion towerTextureRegion;
	
	protected BitmapTextureAtlas AITexture;
	protected TiledTextureRegion AITextureRegion;
	
	protected Sprite sPlayer;
	protected Player player;
	

	//HUD
	private HUD hud = new HUD();
	private ButtonSprite attack;
	private AnalogOnScreenControl joyStick;
	private ValueBar targetInfo;
	private ValueBar playerInfo;
	private ValueBar playerExpBar;
	
	
	protected Rectangle targetRec;

	private IFont mFont;
	private Text playerGoldInfo;	

	
	
	
	
	
	
	public void loadRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBuildBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 512, 256, TextureOptions.NEAREST);
		
		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "sprite.png", 4, 1);
		
		//ai
		//this.AITexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 64, 64);
		this.AITextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "sprite.png", 4, 1);
		//AITexture.load();
		
		//base
	//	this.baseTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 256); // width and height must be factor of two eg:2,4,8,16 etc
		this.baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "sprite.png", 4, 1);
	//	baseTexture.load();
		
		//tower 
		//this.towerTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 256); // width and height must be factor of two eg:2,4,8,16 etc
		this.towerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "sprite.png", 4, 1);
		//towerTexture.load();

		try {
			this.mBuildBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			this.mBuildBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 16, 16, TextureOptions.DEFAULT);
		this.mBitmapTextureAtlas.load();
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
		
		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24);
		this.mFont.load();
		
		
		
		
		
		

	}
	
	protected void setUpMap() {
		// Load the TMX level
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.activity.getAssets(), this.activity.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.activity.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(
						TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer,
						TMXTile pTMXTile,
						TMXProperties<TMXTileProperty> pTMXTileProperties) {
					// TODO Auto-generated method stub
					
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/map.tmx");

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		for (TMXLayer layer : this.mTMXTiledMap.getTMXLayers()) {
			layer.setScale(2f);
			layer.setScaleCenter(0f,0f);
			this.attachChild(layer);
		}

		createUnwalkableObjects(mTMXTiledMap);

	}
	
	private void createUnwalkableObjects(TMXTiledMap map){
        // Loop through the object groups
         for(final TMXObjectGroup group: this.mTMXTiledMap.getTMXObjectGroups()) {
                 if(group.getTMXObjectGroupProperties().containsTMXProperty("wall", "true")){
                         // This is our "wall" layer. Create the boxes from it
                         for(final TMXObject object : group.getTMXObjects()) {
                                final Rectangle rect = new Rectangle(object.getX()*2, object.getY()*2, object.getWidth()*2, object.getHeight()*2, this.engine.getVertexBufferObjectManager());
                                final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
                                PhysicsFactory.createBoxBody(phyWorld, rect, BodyType.StaticBody, boxFixtureDef);
                                rect.setVisible(false);
                                //rect.setColor(Color.GREEN);
                                this.attachChild(rect);
                         }
                 }
         }
}

	
	public void attachHUD(){
		this.camera.setHUD(hud);
	}
	
	protected void setUpHUD()
	{
		//Setup HUD components	

		// sets what the joystick does
		joyStick = new AnalogOnScreenControl(20, camera.getHeight() - this.mOnScreenControlBaseTextureRegion.getHeight() - 20, this.camera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, this.activity.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

				//System.out.println(pValueX + " " + pValueY);
				
				playerCommands.setMovementX(pValueX);
				playerCommands.setMovementY(pValueY);				
				if (sPlayer != null) {
					playerCommands.setClientPosX(sPlayer.getX());
					playerCommands.setClientPosY(sPlayer.getY());
				}
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				
				
			    
					        
		        
		        
			} 
		});

		joyStick.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		joyStick.getControlBase().setAlpha(0.5f);
		joyStick.getControlBase().setScaleCenter(0, 128);
		joyStick.getControlBase().setScale(1.25f);
		joyStick.getControlKnob().setScale(1.25f);
		joyStick.refreshControlKnobPosition();

		setChildScene(joyStick);// attach control joystick
		
		//Create target info
		targetInfo = new ValueBar(camera.getWidth() / 2 + 80, 5, 160, 30, activity.getVertexBufferObjectManager());
		targetInfo.setVisible(false);		
		hud.attachChild(targetInfo);
		
		
		//Create player info
		playerInfo = new ValueBar(camera.getWidth() / 2 -180, 5, 160, 30, activity.getVertexBufferObjectManager());
		//targetInfo.setVisible(false);
		
		/*
		playerExpBar = new ValueBar(camera.getWidth() / 2 -180, 5, 160, 30, activity.getVertexBufferObjectManager());

		playerGoldInfo = new Text(camera.getWidth() / 2 -400, 5, font, "" + player.getGold(), activity.getVertexBufferObjectManager());
		
		
		hud.attachChild(playerExpBar);
		hud.attachChild(playerGoldInfo);
		*/
		hud.attachChild(playerInfo);
		
		
		
		//Set attack button properties
		attack = new ButtonSprite(camera.getWidth() - 100, camera.getHeight() - 120, mOnScreenControlKnobTextureRegion, this.activity.getVertexBufferObjectManager())
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {	        	
	        	if (touchEvent.isActionDown())
	            {
	                attack.setColor(Color.RED);
	                
	                Entity target = player.getNearestEnemyEntity(gameData);
	                
	                if(target != null)
	                {
		                if(!player.hasTarget())
		                {
		                	playerCommands.setTargetID(target.getId());
		                }
		                else if(!player.getTarget().isAlive())
	                	{
	                		playerCommands.setTargetID(target.getId());
	                	}
	                }
	                
	                playerCommands.setAttackCommand(true);
	                
	            }
	        	else if (touchEvent.isActionUp())
	            {
	                attack.setColor(Color.WHITE);
	                playerCommands.setAttackCommand(false);
	            }
	            return true;
	        };
	    };
	    
	    attack.setColor(Color.WHITE);
	    attack.setScale(2.0f);
	    
	    this.hud.attachChild(attack);
	    this.hud.registerTouchArea(attack);
		
		
	    
	    
		
		//setup targeting square
		targetRec = new Rectangle(0, 0, 64, 64, engine.getVertexBufferObjectManager());
		targetRec.setColor(Color.RED);
		this.attachChild(targetRec);
	}
	/*
	public void updateExpBar()
	{
		playerExpBar.setProgressPercentage((float) player.getExperience() / (float) player.getLevelExp());
	}
	*/
	public void updateValueBars()
	{
		playerInfo.setProgressPercentage(( (float)player.getCurrenthealth() / (float)player.getMaxhealth() ));
		
		if(player.hasTarget())
		{
			targetInfo.setProgressPercentage(( (float)player.getTarget().getCurrenthealth() / (float)player.getTarget().getMaxhealth() )); 
			targetInfo.setVisible(true);
		}
		else
			targetInfo.setVisible(false);

	}
	
	protected void updateTargetMarker() {
		if (playerCommands.getTargetID() != 0) {
			Sprite targetSprite = gameData.getEntityWithId(playerCommands.getTargetID()).getSprite();
			targetRec.setSize(targetSprite.getWidth() + 6, targetSprite.getHeight() + 6);
			targetRec.setPosition(targetSprite.getX() -3, targetSprite.getY()-3);
		}
	}
	
	/**
	 * Called whenever an entity sprite is touched
	 * @param sprite the sprite that was touched
	 */
	protected void setTargetFromSpriteTouch(Sprite sprite){
		Entity touched = gameData.getEntityFromSprite(sprite);
		if (touched.isAlive() && touched.getTeam() != player.getTeam()) {
			playerCommands.setTargetID(touched.getId());
		}
			
	}
	
	public void unloadRes(){		
		playerTexture.unload();
		baseTexture.unload();
		mBitmapTextureAtlas.unload();
		mOnScreenControlTexture.unload();
		hud.detachChildren();
		this.detachChildren();
	}

	public PlayerControlCommands getPlayerCommands() {
		return playerCommands;
	}

	
	
	
	
	

	
	
}
