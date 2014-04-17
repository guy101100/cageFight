package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.SceneManager.AllScenes;

public class ClientConnectScene extends Scene implements IOnMenuItemClickListener {

	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private SceneManager sceneManager;
	
	private MenuScene menuChildScene;	
	private final int MENU_JOIN = 0;
	private final int MENU_BACK = 1;
	
	public ITextureRegion menu_background_region;
	protected BitmapTextureAtlas joinTexture;
	public ITextureRegion join;
	public ITextureRegion back;

	
	
	
	
	public ClientConnectScene(BaseGameActivity act, Engine eng, Camera cam, SceneManager sceneManager){
		this.activity = act;
		this.engine = eng;
		this.camera = cam;		
		this.sceneManager = sceneManager;
	}
	
	
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0, 0);
	    
	    final IMenuItem joinMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_JOIN, join, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
	    final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, back, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    
	    
	    menuChildScene.addMenuItem(joinMenuItem);
	    menuChildScene.addMenuItem(backMenuItem);
	    
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    float cX = (camera.getWidth()/2) - (joinMenuItem.getWidth()/2);
	    
	    joinMenuItem.setPosition(cX, (camera.getHeight()/2));
	    backMenuItem.setPosition(cX, joinMenuItem.getY() + joinMenuItem.getHeight() );
	    
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}
	
	
	
	public void createMenuScene() {
		createMenuChildScene();	
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_JOIN:
			sceneManager.loadServerGameRes();
			sceneManager.createServerGameScene();
			sceneManager.setCurrentScene(AllScenes.GAME_SERVER);
			return true;		
		case MENU_BACK:
			activity.finish();
			return true;
		default:
			return false;
		}
	}

}
