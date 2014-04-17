package co.nz.splashYay.cagefight;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import co.nz.splashYay.cagefight.scenes.ClientGameScene;
import co.nz.splashYay.cagefight.scenes.MenuScreenScene;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;
import co.nz.splashYay.cagefight.scenes.SplashScene;


public class SceneManager {

	private AllScenes currentScene;
	
	private SplashScene splashScene;
	private ClientGameScene clientGameScene;
	private MenuScreenScene menuScene;
	private ServerGameScene serverGameScene;	;

	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private String ipAddress;
	
	private boolean gameStarted;
	private boolean server;

	public SceneManager(BaseGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		gameStarted = false;
	}
	
	//scene management
	public enum AllScenes {
		SPLASH, MENU, GAME_CLIENT, GAME_SERVER
	}
	
	public void setCurrentScene(AllScenes currentScene) {
		this.currentScene = currentScene;
		switch (currentScene) {
		case SPLASH:
			engine.setScene(splashScene);
			break;
		case MENU:
			engine.setScene(menuScene);
			break;
		case GAME_CLIENT:
			engine.setScene(clientGameScene);
			break;
		case GAME_SERVER:
			engine.setScene(serverGameScene);
			break;		
		default:
			break;
		}

	}
	
	

	//load Resources methods for each scene

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
	public void startGame() {					
		loadClientGameRes();
		createClientGameScene();
		setCurrentScene(AllScenes.GAME_CLIENT);
	}
	
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
