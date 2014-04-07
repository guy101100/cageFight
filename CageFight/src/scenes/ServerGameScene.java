package scenes;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.math.MathUtils;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.network.InFromClientListener;
import co.nz.splashYay.cagefight.network.OutToClientListener;

import com.badlogic.gdx.math.Vector2;

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

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);

		this.mBitmapTextureAtlas.load();
		gameData = new GameData();

	}

	public void createScene() {

		this.engine.registerUpdateHandler(new FPSLogger());
		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false);
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

					player.getPhyHandler().setVelocity(player.getMovementX() * player.getSpeed(), player.getMovementY() * player.getSpeed()); // moves
																																				// player
					player.setXPos(player.getSprite().getX());// set player position(in data) to the sprites position.
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

	
}
