package co.nz.splashYay.cagefight.scenes;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;
import co.nz.splashYay.cagefight.Entity;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.network.InFromClientListener;
import co.nz.splashYay.cagefight.network.OutToClientListener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class ServerGameScene extends GameScene {
	

	// networking
	private SceneManager sceneManager;	
	private InFromClientListener iFCL;
	private OutToClientListener oTCL;
	private BitmapTextureAtlas mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;

	//HUD
	private HUD hud = new HUD();
	private ButtonSprite attack;
	private AnalogOnScreenControl joyStick;
	private ValueBar targetInfo;

	//server Player
	Player player;
	
	
	public ServerGameScene(BaseGameActivity act, Engine eng, Camera cam, SceneManager sceneManager) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;		
		this.sceneManager = sceneManager;
	}

	public void loadRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.playerTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 64, 64);

		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(playerTexture, this.activity, "player.png", 0, 0);

		playerTexture.load();

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);

		this.mBitmapTextureAtlas.load();
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
		
		gameData = new GameData();

	}

	public void createScene() {

		this.engine.registerUpdateHandler(new FPSLogger());
		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false);
		this.registerUpdateHandler(phyWorld);
		
		setUpMap();
		setUpHUD();

		iFCL = new InFromClientListener(gameData, this);
		oTCL = new OutToClientListener(gameData, this);
		iFCL.start();
		oTCL.start();
		
		player = new Player("", gameData.getUnusedID(), 1, 1, 50, 50);
		addEntityToGameDataObj(player);
		
		//game loop
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {

				processPlayerActions();
				oTCL.updateClients();

			}

			@Override
			public void reset() {
			}
		});
		//end game loop
		
		
	}
	
	private void processPlayerActions(){
		Iterator it = gameData.getEntities().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Player player = (Player) pairs.getValue();
			
			player.checkState();// checks and updates the players state
			
			player.setXPos(player.getSprite().getX());// set player position(in data) to the sprites position.
			player.setYPos(player.getSprite().getY());
			
			if (player.getPlayerState() == EntityState.MOVING) {
				player.getBody().setActive(true);
				final Body playerBody = player.getBody();
				final Vector2 velocity = Vector2Pool.obtain(player.getMovementX() * player.getSpeed(), player.getMovementY() * player.getSpeed());
				playerBody.setLinearVelocity(velocity);
				Vector2Pool.recycle(velocity);
				

				if (player.getMovementX() != 0 && player.getMovementY() != 0) {
					float direction = MathUtils.radToDeg((float) Math.atan2(player.getMovementX(), -player.getMovementY()));
					player.getSprite().setRotation(direction);
					player.setDirection(direction);
				}
			}  else {
				//stops players from moving
				final Body playerBody = player.getBody();
				final Vector2 velocity = Vector2Pool.obtain(0, 0);
				playerBody.setLinearVelocity(velocity);
				Vector2Pool.recycle(velocity);
				
				if (player.getPlayerState() == EntityState.ATTACKING) {
					if (player.getTarget() != null && System.currentTimeMillis() >= (player.getLastAttackTime() + player.getAttackCoolDown())  ) {
						player.attackTarget();
						player.setLastAttackTime(System.currentTimeMillis());
					}
					
					
					
				} else if (player.getPlayerState() == EntityState.IDLE) {
					//do nothing
					
					
				} else if (player.getPlayerState() == EntityState.DEAD) {
					//kill the player and check if when to respawn
					if (player.isAlive()) {
						player.killPlayer();
					}	else {
						if (player.getRespawnTime() <= System.currentTimeMillis()) {
							player.respawn();
						}
					}
				}			
				
				
			}		
		}
	}
	
	
	
	
	@Override
	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			if (newEntity instanceof Player) {
				Player newPlayer = (Player) newEntity;
				gameData.addPlayer(newPlayer);
				Sprite tempS = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2, playerTextureRegion, this.engine.getVertexBufferObjectManager());

				//final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added
				//tempS.registerUpdateHandler(tempPhyHandler); // added
				
				final FixtureDef playerFixDef = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
				newPlayer.setBody(PhysicsFactory.createCircleBody(phyWorld, tempS, BodyType.DynamicBody, playerFixDef));
				
				phyWorld.registerPhysicsConnector(new PhysicsConnector(tempS, newPlayer.getBody(), true, false));			
				
				this.attachChild(tempS);			
				
				newPlayer.setSprite(tempS);
				//newPlayer.setPhyHandler(tempPhyHandler);
				
				tempS.setPosition(newPlayer.getXPos(), newPlayer.getYPos());
				
				if (newPlayer.getId() == player.getId()) {
					camera.setChaseEntity(tempS);
				}
			}
			

		}
	}
	
	
	private void setUpHUD(){
		joyStick = new AnalogOnScreenControl(20, camera.getHeight() - this.mOnScreenControlBaseTextureRegion.getHeight() - 20, this.camera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, this.activity.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				player.setMovementX(pValueX);
				player.setMovementY(pValueY);
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				//do nothing
			}
		});

		joyStick.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		joyStick.getControlBase().setAlpha(0.5f);
		joyStick.getControlBase().setScaleCenter(0, 128);
		joyStick.getControlBase().setScale(1.25f);
		joyStick.getControlKnob().setScale(1.25f);
		joyStick.refreshControlKnobPosition();

		setChildScene(joyStick);
		
		//Create TargetInfo
		targetInfo = new ValueBar(camera.getWidth() / 2 - 80, 5, 160, 30, activity.getVertexBufferObjectManager());
		targetInfo.setAlpha(0.8f);
		
		hud.attachChild(targetInfo);

		//Set attack button properties
		attack = new ButtonSprite(camera.getWidth() - 100, camera.getHeight() - 120, mOnScreenControlKnobTextureRegion, this.activity.getVertexBufferObjectManager())
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {
	        	if (touchEvent.isActionDown())
	            {
	                attack.setColor(Color.RED);
	                player.setAttackCommand(true);
	                player.setTarget(player.targetNearestPlayer(gameData));
	                
	            }
	        	else if (touchEvent.isActionUp())
	            {	            	
	                attack.setColor(Color.WHITE);
	                player.setAttackCommand(false);
	            }
	            return true;
	        };
	    };
	    
	    attack.setColor(Color.WHITE);
	    attack.setScale(2.0f);
	    
	    this.hud.attachChild(attack);
	    this.hud.registerTouchArea(attack);
		
		this.camera.setHUD(hud);
		
	}
	
	

	
}
