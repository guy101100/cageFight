package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class LoadingScene extends Scene {
	private BitmapTextureAtlas splashTA;
	private ITextureRegion splashTR;
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	public LoadingScene(BaseGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		
	}
	
	
	
	public void loadRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 512, 512);
		splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTA, this.activity, "cage.png", 0, 0);
		splashTA.load();
	}
	
	public Scene createScene() {
		
		this.setBackground(new Background(1, 50, 100));
		Sprite splashImage = new Sprite(0, 0, splashTR, engine.getVertexBufferObjectManager());
		splashImage.setPosition((camera.getWidth() - splashImage.getWidth()) / 2, (camera.getHeight() - splashImage.getHeight()) / 2);
		this.attachChild(splashImage);
		
		splashImage.registerEntityModifier(new LoopEntityModifier(new RotationModifier(1f,0f,360f)));
		
		
		
		return this;
	}



	public void unloadRes() {
		// TODO Auto-generated method stub
		splashTA.unload();
		
	}
}
