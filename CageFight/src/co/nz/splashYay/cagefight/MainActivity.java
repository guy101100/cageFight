package co.nz.splashYay.cagefight;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;

import co.nz.splashYay.cagefight.SceneManager.AllScenes;

public class MainActivity extends BaseGameActivity {

	protected static final int CAM_WIDTH = 720;
	protected static final int CAM_HEIGHT = 480;

	private SceneManager sceneManager;
	private Camera mCamera;
	
	@Override
	public Engine onCreateEngine(EngineOptions engOpt) {
		
		return new LimitedFPSEngine(engOpt, 30);
		
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		mCamera = new Camera(0, 0, CAM_WIDTH, CAM_HEIGHT);		
		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAM_WIDTH, CAM_HEIGHT), mCamera);
		options.getTouchOptions().setNeedsMultiTouch(true);
		return options;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		// TODO Auto-generated method stub
		sceneManager = new SceneManager(this, mEngine, mCamera);
		Intent intent = getIntent();
		boolean server = intent.getExtras().getBoolean("server");
		String ip = "";
		if(!server){
			ip = intent.getExtras().getString("ip");
			sceneManager.setIpaddress(ip);
			sceneManager.setServer(false);
		} else {
			sceneManager.setServer(true);
		}
		
		sceneManager.loadSplashRes();

		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());

	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		mEngine.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// TODO Auto-generated method stub
				mEngine.unregisterUpdateHandler(pTimerHandler);				
				
				if (sceneManager.isServer()) {
					sceneManager.loadServerGameRes();
					sceneManager.createServerGameScene();
					sceneManager.setCurrentScene(AllScenes.GAME_SERVER);
				} else {
					sceneManager.loadClientGameRes();
					sceneManager.createClientGameScene();
					sceneManager.setCurrentScene(AllScenes.GAME_CLIENT);
				}
				
				
			}
		}));

		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}

}
