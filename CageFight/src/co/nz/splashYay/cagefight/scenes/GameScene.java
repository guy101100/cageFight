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
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
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
import org.andengine.opengl.texture.region.ITiledTextureRegion;
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
import co.nz.splashYay.cagefight.GameState;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.SceneManager.AllScenes;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;

public abstract class GameScene extends Scene {
	protected SceneManager sceneManager;
	protected BaseGameActivity activity;
	protected Engine engine;
	protected Camera camera;
	
	private ShopMenuScene shopMenu;
	private boolean shopUp = false;	
	
	protected FixedStepPhysicsWorld phyWorld;
	
	protected TMXTiledMap mTMXTiledMap;
	protected BitmapTextureAtlas mBitmapTextureAtlas;
	
	protected GameData gameData;
	
	protected PlayerControlCommands playerCommands = new PlayerControlCommands();
	
	protected BitmapTextureAtlas mOnScreenControlTexture;
	protected ITextureRegion mOnScreenControlBaseTextureRegion;
	protected ITextureRegion mOnScreenControlKnobTextureRegion;
	
	protected Sprite sPlayer;
	protected Player player;
	
	//HUD
	private HUD hud = new HUD();
	private ButtonSprite attack;
	private AnalogOnScreenControl joyStick;
	private ValueBar targetHealth;
	private ValueBar playerHealth;
	private ValueBar playerExpBar;
	private Text playerGoldInfo;
	private Text playerLevelInfo;
	private Text playerKDInfo;
	
	protected Rectangle targetRec;

	private IFont mFont;
	private IFont infoFont;
	private ButtonSprite shop;	
	
	private GameScene gS = this;
	
	
	private BuildableBitmapTextureAtlas mBuildBitmapTextureAtlas;
	protected TiledTextureRegion explosionTextureRegion;
	protected TiledTextureRegion towerAttackTextureRegion;
	protected TiledTextureRegion AITextureRegion;
	protected TiledTextureRegion baseTextureRegion;
	protected TiledTextureRegion towerTextureRegion;
	protected TiledTextureRegion playerTextureRegion;

	
	public void loadRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBuildBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 4096, 4096, TextureOptions.NEAREST);

		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "creep.png", 6, 6);

		//ai
		this.AITextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "creep.png", 6, 6);

		//base
		this.baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "creep.png", 6, 6);

		//tower
		this.towerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "creep.png",6, 6);
		
	
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 16, 16, TextureOptions.DEFAULT);
		this.mBitmapTextureAtlas.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();

		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24);
		this.mFont.load();
		
		this.infoFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24, Color.WHITE_ABGR_PACKED_INT);
		this.infoFont.load();
		
		this.shopMenu = new ShopMenuScene(activity, engine, camera, this);	
		shopMenu.loadResources();
		shopMenu.createScene();
		
		
		//explosion
		
		this.explosionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "explosion.png", 3, 4);
		this.towerAttackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBuildBitmapTextureAtlas, this.activity, "explosion2.png", 8, 5);
		
		try {
			this.mBuildBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			this.mBuildBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}

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
	
	public void toggleShopMenu(){
		if (shopUp) {
			shopUp = false;
			this.clearChildScene();
			hud.setVisible(true);
			
		} else {
			shopUp = true;
			this.setChildScene(shopMenu);
			hud.setVisible(false);
			
		}
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

		this.hud.attachChild(joyStick);// attach control joystick
		this.hud.registerTouchArea(joyStick.getControlBase());
		
		//Create target info
		targetHealth = new ValueBar(camera.getWidth() / 2 - 80, 5, 160, 30, activity.getVertexBufferObjectManager());
		targetHealth.setVisible(false);		
		hud.attachChild(targetHealth);
		
		
		//Create player info
		playerHealth = new ValueBar(camera.getWidth() / 2 - 150, camera.getHeight() - 50, 300, 30, activity.getVertexBufferObjectManager());
		
		playerExpBar = new ValueBar(camera.getWidth() / 2 - 150, camera.getHeight() - 20, 300, 10, activity.getVertexBufferObjectManager());
		playerExpBar.setProgressColor(Color.GREEN);
		
		playerLevelInfo = new Text(camera.getWidth() / 2, camera.getHeight() - 80, this.infoFont, "xxxxxxx", new TextOptions(HorizontalAlign.CENTER), activity.getVertexBufferObjectManager());
		playerLevelInfo.setScale(1.3f);
		playerLevelInfo.setColor(Color.GREEN);

		playerGoldInfo = new Text(camera.getWidth() / 2 - 100, camera.getHeight() - 80, this.infoFont, "xxxxxxx", activity.getVertexBufferObjectManager());
		playerGoldInfo.setScale(1.3f);
		playerGoldInfo.setColor(Color.YELLOW);
		
		playerKDInfo = new Text(camera.getWidth() / 2 + 100, camera.getHeight() - 80, this.infoFont, "xxxxxxx", activity.getVertexBufferObjectManager());
		playerKDInfo.setScale(1.3f);
		playerKDInfo.setColor(Color.RED);
		
		hud.attachChild(playerKDInfo);
		hud.attachChild(playerGoldInfo);
		hud.attachChild(playerLevelInfo);
		hud.attachChild(playerExpBar);
		hud.attachChild(playerHealth);

		//Set attack button properties
		attack = new ButtonSprite(camera.getWidth() - 100, camera.getHeight() - 120, mOnScreenControlKnobTextureRegion, this.activity.getVertexBufferObjectManager())
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {	        	
	        	if (touchEvent.isActionDown())
	            {
	                attack.setColor(Color.RED);
	                
	                Entity target = player.getNearestEnemyEntity(gameData);
	                
	                if(target != null && player.isAlive())
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
	    
	    
	    //shop button
	    shop = new ButtonSprite(camera.getWidth() - 100, 120, mOnScreenControlKnobTextureRegion, this.activity.getVertexBufferObjectManager())
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {	        	
	        	shopUp = true;
				setChildScene(shopMenu);
				hud.setVisible(false);
	            return true;
	        };
	    };
	    
	    shop.setColor(Color.GREEN);
	    shop.setScale(2.0f);
	    
	    this.hud.attachChild(shop);
	    this.hud.registerTouchArea(shop);
		
		
	    
	    
		
		//setup targeting square
		targetRec = new Rectangle(0, 0, 64, 64, engine.getVertexBufferObjectManager());
		targetRec.setColor(Color.RED);
		this.attachChild(targetRec);
	}
	
	/**
	 * Updates the components of the HUD to represent the correct values.
	 * Shows the target health if a target is selected
	 */
	public void updateHUD()
	{
		playerHealth.setProgressPercentage((float)player.getCurrenthealth() / (float)player.getMaxhealth());
		playerExpBar.setProgressPercentage((float)player.getExperience() / (float) player.getLevelExp());

		playerLevelInfo.setText("" + player.getLevel());
		playerGoldInfo.setText("" + player.getGold());
		playerKDInfo.setText("0/" + player.getDeathCount());
		
		if(player.hasTarget())
		{
			targetHealth.setProgressPercentage(( (float)player.getTarget().getCurrenthealth() / (float)player.getTarget().getMaxhealth() )); 
			targetHealth.setVisible(true);
		}
		else
			targetHealth.setVisible(false);

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
	
	

	public PlayerControlCommands getPlayerCommands() {
		return playerCommands;
	}
	
	public void checkVictory(){
		if(gameData.getGameState().equals(GameState.RUNNING))
		{
			if(!gameData.getGoodBase().isAlive() || !gameData.getEvilBase().isAlive())
			{
				gameData.setGameState(GameState.FINISHED);
				
				StatsScene stats = new StatsScene(activity, engine, camera, gameData);
				stats.loadResources();
				stats.createScene();
				this.setChildScene(stats);
				
				this.hud.setVisible(false);

			}		
		}
	}
	
	public void towerAttackExplosion(Entity ent){
		makeSingleCycleAnnimation(ent.getXPos(), ent.getYPos(), towerAttackTextureRegion, 38,30);		
	}
	
	
	/**
	 * 
	 * @param x x position of sprite animation
	 * @param y y position of sprite animation
	 * @param region sprite region to animate
	 * @param lastFrame the amount of frames to animate though (GET THIS RIGHT OR RISK INFINITE LOOP)
	 * @param speed speed in ms of animation
	 */
	public void makeSingleCycleAnnimation(float x, float y, TiledTextureRegion region, final int lastFrame, int speed){
	    final AnimatedSprite explosion = new AnimatedSprite(x, y, region, this.engine.getVertexBufferObjectManager()); 
	    explosion.animate(speed);
	    this.attachChild(explosion);
	    explosion.registerUpdateHandler(new IUpdateHandler(){

	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            if(explosion.getCurrentTileIndex() == lastFrame){
	                activity.runOnUpdateThread(new Runnable() {
	                @Override                
	                public void run() {
	                  gS.detachChild(explosion);
	                  
	                }
	               });                
	            }
	        }

	        @Override
	        public void reset() {

	        }});
	}

	
	
	
	
	

	
	
}
