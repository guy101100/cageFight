package scenes;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;


import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.network.ClientInNetCom;
import co.nz.splashYay.cagefight.network.ClientOutNetCom;

import com.badlogic.gdx.math.Vector2;

public class ClientGameScene extends Scene {
	
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	//networking
	private SceneManager sceneManager;
	private ClientOutNetCom oNC;
	private ClientInNetCom iNC;
	private String ipAddress;
	
	GameData gameData;
	
	private BitmapTextureAtlas playerTexture;
	private ITextureRegion playerTextureRegion;
	private FixedStepPhysicsWorld phyWorld;
	private Sprite sPlayer; //the actual Players sprite
	//private Player player; // client player
	//control (joystick)
	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	private BitmapTextureAtlas mBitmapTextureAtlas;

	//map
	private TMXTiledMap mTMXTiledMap;
	
	//control values
	PlayerControlCommands playerCommands = new PlayerControlCommands();
	
	



	public ClientGameScene(BaseGameActivity act, Engine eng, Camera cam, String ipAddress, SceneManager sceneManager){
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
		gameData = new GameData();
	}
	
	public void loadGameRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/"); // base folder for gfx
		this.playerTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 64, 64); // width and height must be factor of two eg:2,4,8,16 etc
		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(playerTexture, this.activity, "player.png", 0, 0);
		playerTexture.load(); //loads the player texture

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.mBitmapTextureAtlas.load();
		//loads the on screen joystick images
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this.activity, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();

	}
	
	
	
	
	public Scene createGameScene() {
		this.engine.registerUpdateHandler(new FPSLogger());
		

		oNC = new ClientOutNetCom(ipAddress, sceneManager);
		iNC = new ClientInNetCom(ipAddress, gameData, this);

		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false); //Gravity! //sensorManager.Gavity_earth
		this.registerUpdateHandler(phyWorld);

		setUpMap();
		
		//sets what the joystick does
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, camera.getHeight() - this.mOnScreenControlBaseTextureRegion.getHeight(), this.camera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, this.activity.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

				playerCommands.setMovementX(pValueX);
				playerCommands.setMovementX(pValueY);
				playerCommands.setDirection(MathUtils.radToDeg((float) Math.atan2(pValueX, -pValueY)));
				if (sPlayer != null) {
					playerCommands.setClientPosX(sPlayer.getX());
					playerCommands.setClientPosY(sPlayer.getY());
				}

			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {

			}
		});
		
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		setChildScene(analogOnScreenControl);// attach control joystick
		
		//start networking threads
		oNC.start();
		iNC.start();
		
		
		//game loop
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				
				if (gameData.getPlayerWithID(sceneManager.getPlayer().getId()) != null) {
					Iterator it = gameData.getPlayers().entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();
				        Player player = (Player) pairs.getValue();
				        player.getPhyHandler().setVelocity(player.getMovementX() * player.getSpeed(), player.getMovementY() * player.getSpeed()); // moves player
						if (player.getMovementX() != 0 && player.getMovementY() != 0) {
							player.getSprite().setRotation(MathUtils.radToDeg((float) Math.atan2(player.getMovementX(), -player.getMovementY())));
						}
				        
				        /*
				        if (player.getMovementX() == 0 && player.getMovementY() == 0) { // if player is not inputing controls
							if ((player.getXPos() - player.getSprite().getX() > 50 || player.getXPos() - player.getSprite().getX() < -50) || //if player is more than 50pixels away from actual coords
									(player.getYpos() - player.getSprite().getY() > 50 || player.getYpos() - player.getSprite().getY() < -50)) { 
								
								if (player.getSprite().getEntityModifierCount() == 0) { // if there is no move modifier add one
									MoveModifier moveModifier = new MoveModifier(0.028f, player.getSprite().getX(), player.getXPos(), player.getSprite().getY(), player.getYpos());
									player.setMoveModifier(moveModifier);
									player.getSprite().registerEntityModifier(moveModifier);
								} else {
									player.getMoveModifier().reset(0.028f, player.getSprite().getX(), player.getXPos(), player.getSprite().getY(), player.getYpos()); // move player to where actual coords are

								}
							}

						} else {
							player.getPhyHandler().setVelocity(player.getMovementX() * player.getSpeed(), player.getMovementY() * player.getSpeed()); // moves player
							if (player.getMovementX() != 0 && player.getMovementY() != 0) {
								player.getSprite().setRotation(MathUtils.radToDeg((float) Math.atan2(player.getMovementX(), -player.getMovementY())));
							}
						}	
						*/
				        
				    }
				    
					
					
					
					oNC.sendToServer(playerCommands);
				}
			}
		};
		timer.scheduleAtFixedRate(task, 2000, 30);
		//end game loop

		return this;

	}
	
	
	private void setUpMap() {
		// Load the TMX level
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.activity.getAssets(), this.activity.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.activity.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		this.attachChild(tmxLayer);

	}
	
	public void addPlayerToGameDataObj(Player newPlayer) {
		if (newPlayer != null) {
			gameData.addPlayer(newPlayer);
			Sprite tempS = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2, playerTextureRegion, this.engine.getVertexBufferObjectManager());			
			final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added			
			tempS.registerUpdateHandler(tempPhyHandler); // added
			this.attachChild(tempS);			
			newPlayer.setSprite(tempS);
			newPlayer.setPhyHandler(tempPhyHandler);
			tempS.setPosition(newPlayer.getXPos(), newPlayer.getYpos());
			if (newPlayer.getId() == sceneManager.getPlayer().getId()) {
				camera.setChaseEntity(tempS);
				sPlayer = tempS;
			}
			
		}
	}
}
