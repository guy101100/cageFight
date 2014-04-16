package co.nz.splashYay.cagefight.scenes;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import android.graphics.Typeface;
import android.opengl.GLES20;
import co.nz.splashYay.cagefight.Entity;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.network.ClientInNetCom;
import co.nz.splashYay.cagefight.network.ClientOutNetCom;

import com.badlogic.gdx.math.Vector2;

public class ClientGameScene extends GameScene {

	// networking
	private SceneManager sceneManager;
	private ClientOutNetCom oNC;
	private ClientInNetCom iNC;
	private String ipAddress;
	
	
	

	public ClientGameScene(BaseGameActivity act, Engine eng, Camera cam, String ipAddress, SceneManager sceneManager) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
		gameData = new GameData();		
	}

	public void loadRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/"); // base folder for gfx
		this.playerTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 64, 64); // width and height must be factor of two eg:2,4,8,16 etc
		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(playerTexture, this.activity, "player.png", 0, 0);
		playerTexture.load(); // loads the player texture
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 32, 32, TextureOptions.DEFAULT);
		this.mBitmapTextureAtlas.load();
		// loads the on screen joystick images
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();

	}

	public Scene createScene() {
		this.engine.registerUpdateHandler(new FPSLogger());

		oNC = new ClientOutNetCom(ipAddress, sceneManager);
		iNC = new ClientInNetCom(ipAddress, gameData, this);

		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false); // Gravity! //sensorManager.Gavity_earth
		this.registerUpdateHandler(phyWorld);

		setUpMap();
		setUpHUD();
		

		// start networking threads
		oNC.start();
		iNC.start();

		// game loop
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (sceneManager.isGameStarted() &&
					gameData.getEntityWithId(player.getId()) != null) {					
					mp();
					
					oNC.sendToServer(playerCommands);
					
					updateTargetInfo();
					
				}
			}

			@Override
			public void reset() {				
			}	
		});
		
		
		
		
		// end game loop

		return this;

	}
	
	
	
	/**
	 * Moves players by setting a moveModifier : move from here to there in X seconds.
	 */
	private void mp() {
		Iterator it = gameData.getEntities().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Player player = (Player) pairs.getValue();

			// move player			
			if (player.getMovementX() != 0 && player.getMovementY() != 0) {
				player.getSprite().setRotation(player.getDirection());
			}

			// if player is not moving and is out of sync, move to actual position.
			float moveTime = 0.125f; // time in seconds it takes to move to actual position

			if (player.getSprite().getEntityModifierCount() == 0) { // if there is no move modifier add one
				MoveModifier moveModifier = new MoveModifier(moveTime, player.getSprite().getX(), player.getXPos(), player.getSprite().getY(), player.getYPos());
				player.setMoveModifier(moveModifier);
				player.getSprite().registerEntityModifier(moveModifier);
			} else {
				player.getMoveModifier().reset(moveTime, player.getSprite().getX(), player.getXPos(), player.getSprite().getY(), player.getYPos()); // move player to where actual coords are
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

				final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added
				tempS.registerUpdateHandler(tempPhyHandler); // added
				this.attachChild(tempS);
				newPlayer.setSprite(tempS);
				newPlayer.setPhyHandler(tempPhyHandler);
				tempS.setPosition(newPlayer.getXPos(), newPlayer.getYPos());
				if (newPlayer.getId() == player.getId()) {
					sPlayer = tempS;
					camera.setChaseEntity(sPlayer);
				}
			}
			

		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		
	}

}
