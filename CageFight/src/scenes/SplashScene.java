package scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class SplashScene extends Scene {
	private BitmapTextureAtlas splashTA;
	private ITextureRegion splashTR;
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	public SplashScene(BaseGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		
	}
	
	
	
	public void loadRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 256);
		splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTA, this.activity, "splash.png", 0, 0);
		splashTA.load();
	}
	
	public Scene createScene() {
		
		this.setBackground(new Background(1, 1, 1));
		Sprite splashImage = new Sprite(0, 0, splashTR, engine.getVertexBufferObjectManager());
		splashImage.setPosition((camera.getWidth() - splashImage.getWidth()) / 2, (camera.getHeight() - splashImage.getHeight()) / 2);
		this.attachChild(splashImage);
		return this;
	}
}
