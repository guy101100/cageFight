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
import co.nz.splashYay.cagefight.scenes.ClientGameScene;
import co.nz.splashYay.cagefight.scenes.MenuScreenScene;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;

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
		
		sceneManager.loadSplashRes();
		sceneManager.loadLoadingRes();

		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		sceneManager.createLoadingScene();
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());

	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub		
		mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// TODO Auto-generated method stub
				mEngine.unregisterUpdateHandler(pTimerHandler);
				sceneManager.setCurrentScene(AllScenes.MENU);				

			}
		}));
		
		sceneManager.loadMenuRes();		
		sceneManager.createMenuScene();
		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}
	
	@Override
    public void onBackPressed()
    {
        Scene scene = this.mEngine.getScene();
        if(scene instanceof MenuScreenScene){
        	int currenScene = ((MenuScreenScene)scene).getCurrentMenueScene();
            if ( currenScene == 0     ) { // main menu 
            	finish();
            	System.exit(0);
            	
            } else if ( currenScene == 1 ) { // client menu
            	((MenuScreenScene)scene).setMenuScene(0);
            }
        } else if (scene instanceof ServerGameScene || scene instanceof ClientGameScene ) {
        	finish();
        	System.exit(0);
        }
        
        
    }
	
	

}
