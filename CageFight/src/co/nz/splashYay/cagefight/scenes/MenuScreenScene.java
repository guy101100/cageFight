package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.SceneManager.AllScenes;
import co.nz.splashYay.cagefight.network.ClientCheckCom;

public class MenuScreenScene extends Scene implements IOnMenuItemClickListener {
	
	
	
	
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private SceneManager sceneManager;
		
	public ITextureRegion menu_background_region;
	public ITextureRegion server;
	public ITextureRegion client;
	private TextureRegion quit;
	private TextureRegion join;
	private TextureRegion back;
	private TextureRegion ip;
	private TextureRegion title;
	
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	private MenuScene startMenu;
	private MenuScene joinMenu;
	private MenuScene optionsMenu;
	private int currentMenueScene;
	
	
	private final int MENU_SERVER = 0;
	private final int MENU_CLIENT = 1;
	private final int MENU_QUIT = 2;
	private final int MENU_JOIN = 3;
	private final int MENU_IP = 4;
	private final int MENU_BACK = 5;
	private final int MENU_TITLE = 6;
	private TextureRegion title2;
	
	private String ipAddress = "";
	private Font mFont;
	private Text ipText;
	
	
	


	
	public MenuScreenScene(BaseGameActivity act, Engine eng, Camera cam, SceneManager sceneManager){
		this.activity = act;
		this.engine = eng;
		this.camera = cam;		
		this.sceneManager = sceneManager;
	}
	
	
	private void createOptionsMenuScene(){
		optionsMenu = new MenuScene(camera);
		optionsMenu.setPosition(0, 0);
		
	}
	
	private void createJoinMenuScene(){
		joinMenu = new MenuScene(camera);
	    joinMenu.setPosition(0, 0);
	    
	    final IMenuItem joinMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_JOIN, join, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
	    final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, back, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem serverIpMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_IP, ip, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem titleItem2 = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_TITLE, title2, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    
	    joinMenu.addMenuItem(titleItem2);
	    joinMenu.addMenuItem(joinMenuItem);
	    joinMenu.addMenuItem(serverIpMenuItem);
	    joinMenu.addMenuItem(backMenuItem);
	    ipText = new Text(100, 160, this.mFont, "Server IP : ", "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
	    joinMenu.attachChild(ipText);
	    
	    joinMenu.buildAnimations();
	    joinMenu.setBackgroundEnabled(false);
	    
	    float cX = (camera.getWidth()/3) - (joinMenuItem.getWidth()/2);
	    titleItem2.setPosition(0, 25);
	    ipText.setPosition(cX, joinMenuItem.getY() - ipText.getHeight());
	    joinMenuItem.setPosition(cX, (camera.getHeight()/2) - (joinMenuItem.getHeight()/3));
	    serverIpMenuItem.setPosition(cX, joinMenuItem.getY() + joinMenuItem.getHeight() + 5 );
	    backMenuItem.setPosition(cX, serverIpMenuItem.getY() + serverIpMenuItem.getHeight() + 5 );
	    
	    
	    
	    
	    
	    
	    joinMenu.setOnMenuItemClickListener(this);
	    	    
	}

	private void createMenuChildScene()
	{
	    startMenu = new MenuScene(camera);
	    startMenu.setPosition(0, 0);
	    
	    final IMenuItem serverMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SERVER, server, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem clientMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_CLIENT, client, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, quit, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    final IMenuItem titleItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_TITLE, title, engine.getVertexBufferObjectManager()), 1.2f, 1);
	    createBackground();
	    
	    startMenu.addMenuItem(serverMenuItem);
	    startMenu.addMenuItem(clientMenuItem);
	    startMenu.addMenuItem(quitMenuItem);
	    startMenu.addMenuItem(titleItem);
	    
	    startMenu.buildAnimations();
	    startMenu.setBackgroundEnabled(false);
	    
	    float cX = (camera.getWidth()/3) - (serverMenuItem.getWidth()/2);
	    titleItem.setPosition(0, 25);
	    serverMenuItem.setPosition(cX, (camera.getHeight()/2) - (serverMenuItem.getHeight()/3));
	    clientMenuItem.setPosition(cX, serverMenuItem.getY() + serverMenuItem.getHeight() + 5 );
	    quitMenuItem.setPosition(cX, clientMenuItem.getY() + clientMenuItem.getHeight() + 5);
	    
	    startMenu.setOnMenuItemClickListener(this);	    
	    
	    
	}
	
	public void loadMenuRes() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "BackgroundClient.png");
		
		title = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "title.png");
		title2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "title2.png");
		
		server = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "server.png");
		client = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "client.png");
		quit = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "quit.png");
		join = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "join.png");
		back = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "back.png");
		ip = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "ip.png");

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();


	}
	
	public void createMenuScene() {
		createMenuChildScene();
		createJoinMenuScene();
		setChildScene(startMenu);
		currentMenueScene = 0;
		
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, menu_background_region, engine.getVertexBufferObjectManager())
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}

	
	public void getServerIp(final Context context) {
		Handler mHandler = new Handler(Looper.getMainLooper());

		mHandler.post(new Runnable() {
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);

				alert.setView(input);
				alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//sceneManager.setIpaddress(input.getText().toString().trim());
						//sceneManager.startGame();
						ipAddress = input.getText().toString().trim();
						String text = "Server IP : " + ipAddress;
						if (text.length() < 40) {
							ipText.setText(text);
						} else {
							makeAToast("Error entering IP Address");
						}
						
					}
				});

				alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
				alert.show();
			}
		});

	}
	
	private void makeAToast(final String toToast){
		Handler mHandler = new Handler(Looper.getMainLooper());

		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(activity, toToast, Toast.LENGTH_SHORT).show();				
			}
			
		});
		
	}
	
	
	
	

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_SERVER:
			sceneManager.setCurrentScene(AllScenes.LOAD_SCENE);
			engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
				
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					// TODO Auto-generated method stub
					engine.unregisterUpdateHandler(pTimerHandler);
					sceneManager.createServerGameScene();
					sceneManager.setCurrentScene(AllScenes.GAME_SERVER);				
					
				}
			}));	
			
			sceneManager.loadServerGameRes();
			return true;
			
		case MENU_CLIENT:
			setChildScene(joinMenu);
			currentMenueScene = 1;
			return true;
			
		case MENU_QUIT:
			activity.finish();
			return true;
			
		case MENU_JOIN:
			if (ipAddress.equalsIgnoreCase("")) {
				makeAToast("Please enter a server IP first.");
			} else {
				ClientCheckCom checkServer = new ClientCheckCom(ipAddress);
				checkServer.start();				
				if (checkServer.checkForServer()) {
					sceneManager.setCurrentScene(AllScenes.LOAD_SCENE);					
					sceneManager.setIpaddress(ipAddress);
					
					
					engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
						
						@Override
						public void onTimePassed(TimerHandler pTimerHandler) {
							// TODO Auto-generated method stub
							engine.unregisterUpdateHandler(pTimerHandler);
							sceneManager.createClientGameScene();
							sceneManager.setCurrentScene(AllScenes.GAME_CLIENT);				

						}
					}));	
					sceneManager.loadClientGameRes();
					
				} else {
					makeAToast("No respose from server.");
				}
				
			}
			
			
			return true;
			
		case MENU_BACK:
			setChildScene(startMenu);
			currentMenueScene = 0;
			return true;
			
		case MENU_IP:
			getServerIp(activity);
			return true;
			
		case MENU_TITLE:
			//easter egg
			return true;
			
		default:
			return false;
		}
	}

	public int getCurrentMenueScene() {
		return currentMenueScene;
	}
	
	public void setMenuScene(int x){
		if (x == 0) {
			setChildScene(startMenu);
			currentMenueScene = 0;
		} else if (x == 1) {
			setChildScene(joinMenu);
			currentMenueScene = 1;
		}
	}

	public void unloadRes() {
		mFont.unload();
		menuTextureAtlas.unload();
	}
	
	



	
	
	
	
	
	
	
	
	
	
	
	
	
}
