package co.nz.splashYay.cagefight;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import co.nz.splashYay.cagefight.scenes.ClientGameScene;
import co.nz.splashYay.cagefight.scenes.MenuScene;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;
import co.nz.splashYay.cagefight.scenes.SplashScene;


public class SceneManager {

	private AllScenes currentScene;
	
	private SplashScene splashScene;
	private ClientGameScene clientGameScene;
	private MenuScene menuScene;
	private ServerGameScene serverGameScene;

	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private String ipAddress;
	private Player player;

	public SceneManager(BaseGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
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
		menuScene = new MenuScene(activity, this);
		menuScene.loadRes();
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

	public Scene createMenuScene() {
		menuScene.createScene();
		return menuScene;		
	}

	public Scene createClientGameScene() {
		clientGameScene.createScene();
		return clientGameScene;
	}
	public Scene createServerGameScene() {
		serverGameScene.createScene();
		return serverGameScene;
	}
	
	
	//setters	
	public void createPlayerAndStartGame(int id) {		
		this.player = new Player((id + ""), id, 1, 1, 0, 0);		
		loadClientGameRes();
		createClientGameScene();
		setCurrentScene(AllScenes.GAME_CLIENT);
	}
	
	public void setIpaddress(String ipAddress){
		this.ipAddress = ipAddress;
		
	}
	
	
	//getters
	public Player getPlayer(){
		return player;
	}
	
	
	
	
	
	
	
	

	

}
