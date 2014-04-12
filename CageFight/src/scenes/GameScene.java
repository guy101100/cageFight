package scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;

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
	
	
	
	
	
	protected void setUpMap() {
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

		}
	}

	
	
}
