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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.SceneManager.AllScenes;

public class MenuScreenScene extends Scene implements IOnMenuItemClickListener {
	
	
	
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private SceneManager sceneManager;
		
	public ITextureRegion menu_background_region;
	public ITextureRegion server;
	public ITextureRegion client;
	private BuildableBitmapTextureAtlas menuTextureAtlas;


	
	public MenuScreenScene(BaseGameActivity act, Engine eng, Camera cam, SceneManager sceneManager){
		this.activity = act;
		this.engine = eng;
		this.camera = cam;		
		this.sceneManager = sceneManager;
	}
	
	private MenuScene menuChildScene;
	private final int MENU_SERVER = 0;
	private final int MENU_CLIENT = 1;
	private final int MENU_QUIT = 2;
	private TextureRegion quit;

	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0, 0);
	    
	    final IMenuItem serverMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SERVER, server, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem clientMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_CLIENT, client, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, quit, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    
	    
	    menuChildScene.addMenuItem(serverMenuItem);
	    menuChildScene.addMenuItem(clientMenuItem);
	    menuChildScene.addMenuItem(quitMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    float cX = (camera.getWidth()/2) - (serverMenuItem.getWidth()/2);
	    
	    serverMenuItem.setPosition(cX, (camera.getHeight()/2) - (serverMenuItem.getHeight()/3));
	    clientMenuItem.setPosition(cX, serverMenuItem.getY() + serverMenuItem.getHeight() );
	    quitMenuItem.setPosition(cX, clientMenuItem.getY() + clientMenuItem.getHeight() );
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}
	
	public void loadMenuRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		//menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		server = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "server.png");
		client = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "client.png");
		quit = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "quit.png");

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

	}
	
	public void createMenuScene() {
		createMenuChildScene();
		
		
		
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_SERVER:
			sceneManager.loadServerGameRes();
			sceneManager.createServerGameScene();
			sceneManager.setCurrentScene(AllScenes.GAME_SERVER);
			return true;
		case MENU_CLIENT:
			return true;
		case MENU_QUIT:
			return true;
		default:
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
