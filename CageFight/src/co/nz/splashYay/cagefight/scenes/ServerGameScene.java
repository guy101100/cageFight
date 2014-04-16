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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class ServerGameScene extends GameScene {
	

	// networking
	private SceneManager sceneManager;	
	private InFromClientListener iFCL;
	private OutToClientListener oTCL;
	

	
	
	
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

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 16, 16, TextureOptions.DEFAULT);

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
		setUpBases();

		iFCL = new InFromClientListener(gameData, this);
		oTCL = new OutToClientListener(gameData, this);
		iFCL.start();
		oTCL.start();
		
		player = new Player("", gameData.getUnusedID(), 10, 10, 50, 50);
		addEntityToGameDataObj(player);
		
		//game loop
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				processServerPlayerControls();
				processPlayerActions();
				oTCL.updateClients();
				
				updateTargetInfo();

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
		tempPlayer.setTarget((Player)gameData.getEntityWithId(playerCommands.getTargetID()));
	}
	
	private void processPlayerActions(){
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
					
					if(distance < 150)
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
	
	private void setUpBases(){
		Base base1 = new Base(500, 500, 10, 10, gameData.getUnusedID());
		addEntityToGameDataObj(base1);
		Base base2 = new Base(100, 100, 10, 10, gameData.getUnusedID());
		addEntityToGameDataObj(base2);
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
			} else if (newEntity instanceof Base) {
				Base newBase = (Base) newEntity;
				gameData.addEntity(newBase);
				FixtureDef baseFix = PhysicsFactory.createFixtureDef(0, 0f, 0f);
				Rectangle baseRec = new Rectangle(newBase.getXPos(), newBase.getYPos(), 150, 150, this.engine.getVertexBufferObjectManager());
				baseRec.setColor(Color.BLUE);
				newBase.setBody(PhysicsFactory.createBoxBody(phyWorld, baseRec, BodyType.StaticBody, baseFix));
				this.attachChild(baseRec);
				
				
				
			}
			

		}
	}
	
	
	
	

	
}
