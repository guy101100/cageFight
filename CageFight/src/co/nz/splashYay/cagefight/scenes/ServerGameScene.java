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
import org.andengine.entity.primitive.Rectangle;
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
import org.andengine.extension.tmx.TMXTile;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;
import co.nz.splashYay.cagefight.Base;
import co.nz.splashYay.cagefight.Entity;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.network.InFromClientListener;
import co.nz.splashYay.cagefight.network.OutToClientListener;
import co.nz.splashYay.cagefight.network.ServerCheckListener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class ServerGameScene extends GameScene {
	

	// networking
	private SceneManager sceneManager;	
	private InFromClientListener iFCL;
	private OutToClientListener oTCL;
	private ServerCheckListener sCL;
	
	

	
	
	
	public ServerGameScene(BaseGameActivity act, Engine eng, Camera cam, SceneManager sceneManager) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;		
		this.sceneManager = sceneManager;
	}

	

	public void createScene() {
		gameData = new GameData();
		this.engine.registerUpdateHandler(new FPSLogger());
		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false);
		this.registerUpdateHandler(phyWorld);
		
		
		
		setUpMap();
		setUpHUD();
		setUpBases();

		iFCL = new InFromClientListener(gameData, this);
		oTCL = new OutToClientListener(gameData, this);
		sCL = new ServerCheckListener();
		iFCL.start();
		oTCL.start();
		sCL.start();
		
		player = new Player("", gameData.getUnusedID(), 100, 1, 50, 50, 0);
		addEntityToGameDataObj(player);
		
		//game loop
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				processServerPlayerControls();
				processEntityActions();
				updateTargetMarker();
				oTCL.updateClients();
				
				updateValueBars();
				

			}

			@Override
			public void reset() {
			}
		});
		//end game loop
		
		
	}
	
	
	
	private void processServerPlayerControls() {
		Player tempPlayer = (Player)gameData.getEntityWithId(player.getId());
		tempPlayer.setMovementX(playerCommands.getMovementX());
		tempPlayer.setMovementY(playerCommands.getMovementY());
		tempPlayer.setAttackCommand(playerCommands.isAttackCommand());
		tempPlayer.setTarget(gameData.getEntityWithId(playerCommands.getTargetID()));
	}
	
	private void processEntityActions(){
		Iterator it = gameData.getEntities().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			
			if (pairs.getValue() instanceof Player) {
				Player player = (Player) pairs.getValue();
				proccessPlayer(player);
				
			} else if (pairs.getValue() instanceof Base) {
				Base base = (Base) pairs.getValue();
				proccessBase(base);
			}
			
		}
	}
	
	private void proccessBase(Base base) {
		base.checkState();
	}
	
	private void proccessPlayer(Player player) {		
		
		player.checkState();// checks and updates the players state
		
		player.setXPos(player.getSprite().getX());// set player position(in data) to the sprites position.
		player.setYPos(player.getSprite().getY());
		
		checkTileAffect(player);
		
		
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
					
					double distanceSqr = Math.pow((player.getTarget().getXPos() - player.getXPos()), 2) + Math.pow((player.getTarget().getYPos() - player.getYPos()), 2);
					double distance = Math.sqrt(distanceSqr);
					
					if(distance < 75)
					{
						player.attackTarget();
						player.setLastAttackTime(System.currentTimeMillis());
					}
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
	
	private void checkTileAffect(Player player) {
		final TMXTile tmxTile = mTMXTiledMap.getTMXLayers().get(12).getTMXTileAt(player.getXPos(), player.getYPos());
		
		if (tmxTile != null && tmxTile.getGlobalTileID() != 0) {
			
			if (tmxTile.getTMXTileProperties(mTMXTiledMap).containsTMXProperty("badHeal", "true")) {
				if (player.getTeamID() == 0) {
					player.healPlayer(1);
				} else {
					player.setSpeed(2);
					//damage the player
				}
				
				
			} else if (tmxTile.getTMXTileProperties(mTMXTiledMap).containsTMXProperty("goodHeal", "true")) {
				if (player.getTeamID() == 1) {
					player.healPlayer(1);
				} else {
					player.setSpeed(2);
					//damage the player
				}
			} 
			
			
		} else { // is not on an effecting tile, reverse any effects on the player
			player.setSpeed(10);
		}
	}
	
	private void setUpBases(){
		Base base1 = new Base(500, 500, 10, 10, gameData.getUnusedID(), 1);
		addEntityToGameDataObj(base1);
		Base base2 = new Base(750, 500, 10, 10, gameData.getUnusedID(), 2);
		addEntityToGameDataObj(base2);	
		
	}
	
	
	
	@Override
	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			if (newEntity instanceof Player) {
				Player newPlayer = (Player) newEntity;
				gameData.addPlayer(newPlayer);
				
				Sprite tempS = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2, playerTextureRegion, this.engine.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						setTarget(this);		
						
						return true;
					}
				};
				registerTouchArea(tempS);
				setTouchAreaBindingOnActionDownEnabled(true);
				
				
				final FixtureDef playerFixDef = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
				newPlayer.setBody(PhysicsFactory.createCircleBody(phyWorld, tempS, BodyType.DynamicBody, playerFixDef));
				
				phyWorld.registerPhysicsConnector(new PhysicsConnector(tempS, newPlayer.getBody(), true, false));					
								
				this.attachChild(tempS);			
				
				newPlayer.setSprite(tempS);
				
				
				if (newPlayer.getId() == player.getId()) {
					camera.setChaseEntity(tempS);
				}
				
				
			} else if (newEntity instanceof Base) {
				Base newBase = (Base) newEntity;
				gameData.addEntity(newBase);
				FixtureDef baseFix = PhysicsFactory.createFixtureDef(0, 0f, 0f);
				Sprite baseS = new Sprite(newBase.getXPos(), newBase.getYPos(), baseTextureRegion, this.engine.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						setTarget(this);		
						
						return true;
					}
				};
				registerTouchArea(baseS);
				setTouchAreaBindingOnActionDownEnabled(true);
				newBase.setSprite(baseS);
				newBase.setBody(PhysicsFactory.createBoxBody(phyWorld, baseS, BodyType.StaticBody, baseFix));
				this.attachChild(baseS);
				
				
				
			}
			

		}
	}

	
	
	
	

	
}
