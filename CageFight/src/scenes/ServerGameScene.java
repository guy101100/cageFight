package scenes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.network.InFromClientListener;
import co.nz.splashYay.cagefight.network.OutToClientListener;

public class ServerGameScene extends Scene {
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;

	//networking
	private SceneManager sceneManager;
	private String ipAddress;
	private InFromClientListener iFCL;
	private OutToClientListener oTCL;

	private BitmapTextureAtlas playerTexture;
	private ITextureRegion playerTextureRegion;
	private FixedStepPhysicsWorld phyWorld;
	private Player player;
	private GameData gameData;
	//Map
	private TMXTiledMap mTMXTiledMap;
	private BitmapTextureAtlas mBitmapTextureAtlas;

	//debugPlayer
	private TextureRegion debugTextureRegion;
	private BitmapTextureAtlas debugPlayerTexture;

	public ServerGameScene(BaseGameActivity act, Engine eng, Camera cam, String ipAddress, SceneManager sceneManager) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
	}

	public void loadServerGameRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.playerTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 64, 64);
		this.debugPlayerTexture = new BitmapTextureAtlas(activity.getTextureManager(), 64, 64);

		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(playerTexture, this.activity, "player.png", 0, 0);
		this.debugTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(debugPlayerTexture, this.activity, "debugred.png", 0, 0);
		playerTexture.load();
		debugPlayerTexture.load();
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);

		this.mBitmapTextureAtlas.load();
		gameData = new GameData();

	}

	public void createServerGameScene() {

		this.engine.registerUpdateHandler(new FPSLogger());
		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false); //30, 1, new Vector2(0, 0), false, 8, 4
		this.registerUpdateHandler(phyWorld);

		setUpMap();

		iFCL = new InFromClientListener(gameData, this);
		oTCL = new OutToClientListener(gameData, this);
		iFCL.start();
		oTCL.start();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				// job code here				
				Iterator it = gameData.getPlayers().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					Player player = (Player) pairs.getValue();
					
					player.getPhyHandler().setVelocity(player.getMovementX() * player.getSpeed(), player.getMovementY() * player.getSpeed()); // moves player
					player.setXPos(player.getSprite().getX());//set player position to the sprites position.
					player.setYpos(player.getSprite().getX());
					
					
					if (player.getMovementX() != 0 && player.getMovementY() != 0) {
						player.getSprite().setRotation(MathUtils.radToDeg((float) Math.atan2(player.getMovementX(), -player.getMovementY())));
					}
				}
				
				oTCL.updateClients();
			}

		};
		timer.scheduleAtFixedRate(task, 2000, 30);
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
			//Sprite tempDebug = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2, debugTextureRegion, this.engine.getVertexBufferObjectManager());				
			final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added			
			tempS.registerUpdateHandler(tempPhyHandler); // added
			this.attachChild(tempS);
			newPlayer.setSprite(tempS);
			newPlayer.setPhyHandler(tempPhyHandler);
			tempS.setPosition(newPlayer.getXPos(), newPlayer.getYpos());

		}
	}

}
