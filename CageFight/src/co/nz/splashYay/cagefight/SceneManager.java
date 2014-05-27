package co.nz.splashYay.cagefight;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import co.nz.splashYay.cagefight.SoundManager.MUSIC_TYPE;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.scenes.ClientGameScene;
import co.nz.splashYay.cagefight.scenes.LoadingScene;
import co.nz.splashYay.cagefight.scenes.MenuScreenScene;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;
import co.nz.splashYay.cagefight.scenes.SplashScene;


public class SceneManager {

	private AllScenes currentScene;
	
	private SplashScene splashScene;
	private ClientGameScene clientGameScene;
	private MenuScreenScene menuScene;
	private ServerGameScene serverGameScene;
	private LoadingScene loadingScene;

	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private String ipAddress;
	
	private boolean gameStarted;
	private boolean server;
	
	private SoundManager soundManager;

	public SceneManager(BaseGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		gameStarted = false;
		soundManager = new SoundManager(activity, engine);
	}
	
	//scene management
	public enum AllScenes {
		SPLASH, MENU, GAME_CLIENT, GAME_SERVER, LOAD_SCENE
	}
	
	public void setCurrentScene(AllScenes currentScene) {
		/*
		if (this.currentScene != null) {
			switch (this.currentScene) {
			case SPLASH:
				splashScene.unloadRes();
				break;
			case MENU:
				menuScene.unloadRes();
				break;
			case GAME_CLIENT:
				clientGameScene.unloadRes();
				break;
			case GAME_SERVER:
				serverGameScene.unloadRes();
				break;
			case LOAD_SCENE:				
				break;
			default:
				break;
			}
		}
		*/
		
		
		
		this.currentScene = currentScene;
		switch (currentScene) {
		case SPLASH:
			engine.setScene(splashScene);
			break;
		case MENU:
			engine.setScene(menuScene);
			soundManager.setMusic(MUSIC_TYPE.MENU);
			break;
		case GAME_CLIENT:			
			engine.setScene(clientGameScene);
			clientGameScene.attachHUD();
			soundManager.setMusic(MUSIC_TYPE.GAME1);
			break;
		case GAME_SERVER:			
			engine.setScene(serverGameScene);
			serverGameScene.attachHUD();
			soundManager.setMusic(MUSIC_TYPE.GAME1);
			break;	
		case LOAD_SCENE:
			engine.setScene(loadingScene);
			break;
		default:
			break;
		}

	}
	
	

	//load Resources methods for each scene
	public void loadLoadingRes(){
		loadingScene = new LoadingScene(activity, engine, camera);
		loadingScene.loadRes();
	}
	public void loadSplashRes() {
		splashScene = new SplashScene(activity, engine, camera);
		splashScene.loadRes();
	}

	public void loadMenuRes() {
		menuScene = new MenuScreenScene(activity, engine, camera, this);		
		menuScene.loadMenuRes();
		
		
	}

	public void loadClientGameRes() {
		clientGameScene = new ClientGameScene(activity, engine, camera, ipAddress, this);
		clientGameScene.loadRes();

	}
	public void loadServerGameRes() {
		serverGameScene = new ServerGameScene(activity, engine, camera, this);
		serverGameScene.loadRes();
	}
	
	
	//Create Scenes

	public Scene createSplashScene() {
		splashScene.createScene();
		return splashScene;
	}
	
	public Scene createLoadingScene() {
		loadingScene.createScene();
		return loadingScene;
	}
	

	
	public Scene createClientGameScene() {
		clientGameScene.createScene();
		return clientGameScene;
	}
	public Scene createServerGameScene() {
		serverGameScene.createScene();
		return serverGameScene;
	}
	public Scene createMenuScene() {
		menuScene.createMenuScene();
		return menuScene;
	}
	
	
	//setters	
	
	
	public void setIpaddress(String ipAddress){
		this.ipAddress = ipAddress;
		
	}	
	
	public void setPlayer(Player player) {
		clientGameScene.setPlayer(player);
	}



	//getters
	
	
	



	public void setServer(boolean b) {
		this.server = b;
		
	}



	public SoundManager getSoundManager() {
		return soundManager;
	}



	public boolean isServer() {
		return server;
	}



	public boolean isGameStarted() {
		return gameStarted;
	}



	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}




	
	
	

	
	
	
	
	
	
	
	
	

	

}
