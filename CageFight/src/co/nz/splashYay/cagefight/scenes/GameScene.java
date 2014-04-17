package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.opengl.GLES20;
import co.nz.splashYay.cagefight.Entity;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.ValueBar;

public abstract class GameScene extends Scene {
	protected BaseGameActivity activity;
	protected Engine engine;
	protected Camera camera;
	
	protected BitmapTextureAtlas playerTexture;
	protected ITextureRegion playerTextureRegion;
	protected FixedStepPhysicsWorld phyWorld;
	
	protected TMXTiledMap mTMXTiledMap;
	protected BitmapTextureAtlas mBitmapTextureAtlas;
	
	protected GameData gameData;
	
	protected PlayerControlCommands playerCommands = new PlayerControlCommands();
	
	protected BitmapTextureAtlas mOnScreenControlTexture;
	protected ITextureRegion mOnScreenControlBaseTextureRegion;
	protected ITextureRegion mOnScreenControlKnobTextureRegion;
	
	protected BitmapTextureAtlas baseTexture;
	protected TextureRegion baseTextureRegion;
	
	protected Sprite sPlayer;
	protected Player player;
	

	//HUD
	private HUD hud = new HUD();
	private ButtonSprite attack;
	private AnalogOnScreenControl joyStick;
	private ValueBar targetInfo;
	private ValueBar playerInfo;
	
	
	protected Rectangle targetRec;
	
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
                                final Rectangle rect = new Rectangle(object.getX(), object.getY(),object.getWidth(), object.getHeight(), this.engine.getVertexBufferObjectManager());
                                final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
                                PhysicsFactory.createBoxBody(phyWorld, rect, BodyType.StaticBody, boxFixtureDef);
                                rect.setVisible(false);
                                this.attachChild(rect);
                         }
                 }
         }
}

	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			Player newPlayer = (Player) newEntity;
			gameData.addPlayer(newPlayer);
			Sprite tempS = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2, playerTextureRegion, this.engine.getVertexBufferObjectManager());

			final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added
			tempS.registerUpdateHandler(tempPhyHandler); // added
			this.attachChild(tempS);
			newPlayer.setSprite(tempS);
			newPlayer.setPhyHandler(tempPhyHandler);
			tempS.setPosition(newPlayer.getXPos(), newPlayer.getYPos());

		}
	}
	
	protected void setUpHUD()
	{
		//Setup HUD components	

		// sets what the joystick does
		joyStick = new AnalogOnScreenControl(20, camera.getHeight() - this.mOnScreenControlBaseTextureRegion.getHeight() - 20, this.camera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, this.activity.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

				
				
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
		
		playerInfo = new ValueBar(camera.getWidth() / 2 -180, 5, 160, 30, activity.getVertexBufferObjectManager());
		//targetInfo.setVisible(false);
		
		hud.attachChild(playerInfo);
		
		
		
		//Set attack button properties
		attack = new ButtonSprite(camera.getWidth() - 100, camera.getHeight() - 120, mOnScreenControlKnobTextureRegion, this.activity.getVertexBufferObjectManager())
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {	        	
	        	if (touchEvent.isActionDown())
	            {
	                attack.setColor(Color.RED);
	                
	                Entity target = player.targetNearestEntity(gameData);
	                
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
		
		this.camera.setHUD(hud);
		
		//setup targeting square
		targetRec = new Rectangle(0, 0, 64, 64, engine.getVertexBufferObjectManager());
		targetRec.setColor(Color.RED);
		this.attachChild(targetRec);
	}
	
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
	protected void setTarget(Sprite sprite){
		Entity touched = gameData.getEntityFromSprite(sprite);
		if (touched.getTeamID() != player.getTeamID()) {
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
	

	
	
}
