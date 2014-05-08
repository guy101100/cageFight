package co.nz.splashYay.cagefight.scenes;

import java.util.Iterator;
import java.util.Map;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Creep;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.entities.Tower;
import co.nz.splashYay.cagefight.network.ClientOutNetCom;
import co.nz.splashYay.cagefight.network.UDPReciver;

import com.badlogic.gdx.math.Vector2;

public class ClientGameScene extends GameScene {

	// networking
	private SceneManager sceneManager;
	private ClientOutNetCom oNC;
	//private ClientInNetCom iNC;
	private String ipAddress;
	UDPReciver udpr;
	
	
	
	

	public ClientGameScene(BaseGameActivity act, Engine eng, Camera cam, String ipAddress, SceneManager sceneManager) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
		gameData = new GameData();	
		
		
	}

	

	public Scene createScene() {
		this.engine.registerUpdateHandler(new FPSLogger());

		oNC = new ClientOutNetCom(ipAddress, sceneManager);
		//iNC = new ClientInNetCom(ipAddress, gameData, this);
		udpr = new UDPReciver(this, ipAddress, gameData);

		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false); // Gravity! //sensorManager.Gavity_earth
		this.registerUpdateHandler(phyWorld);

		setUpMap();
		setUpHUD();
		

		// start networking threads
		oNC.start();
		//iNC.start();
		udpr.start();

		// game loop
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (sceneManager.isGameStarted() &&	gameData.getEntityWithId(player.getId()) != null) {					
					processEntities();
					updateTargetMarker();
					oNC.sendToServer(playerCommands);
					
					updateValueBars();
					
				}
			}

			@Override
			public void reset() {				
			}	
		});
		
		
		
		
		// end game loop

		return this;

	}
	
	
	
	/**
	 * Moves players by setting a moveModifier : move from here to there in X seconds.
	 */
	private void processEntities() {
		Iterator it = gameData.getEntities().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (pairs.getValue() instanceof Player) {
				Player player = (Player) pairs.getValue();
				processPlayer(player);
			}
			
			
			

		}
	}
	
	private void processPlayer(Player player) {
		// move player			
		if (player.getMovementX() != 0 && player.getMovementY() != 0) {
			player.getSprite().setRotation(player.getDirection());
		}

		// if player is not moving and is out of sync, move to actual position.
		float moveTime = 0.125f; // time in seconds it takes to move to actual position

		if (player.getSprite().getEntityModifierCount() == 0) { // if there is no move modifier add one
			MoveModifier moveModifier = new MoveModifier(moveTime, player.getSprite().getX(), player.getXPos(), player.getSprite().getY(), player.getYPos());
			player.setMoveModifier(moveModifier);
			player.getSprite().registerEntityModifier(moveModifier);
		} else {
			player.getMoveModifier().reset(moveTime, player.getSprite().getX(), player.getXPos(), player.getSprite().getY(), player.getYPos()); // move player to where actual coords are
		}
	}
	
	

	
	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			if (newEntity instanceof Player) {
				Player newPlayer = (Player) newEntity;
				gameData.addPlayer(newPlayer);
				Sprite tempS = new Sprite(newPlayer.getXPos(), newPlayer.getYPos(), playerTextureRegion, this.engine.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						setTarget(this);		
						
						return true;
					}
				};

				final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added
				tempS.registerUpdateHandler(tempPhyHandler); // added
				this.attachChild(tempS);
				newPlayer.setSprite(tempS);
				newPlayer.setPhyHandler(tempPhyHandler);				
				if (newPlayer.getId() == player.getId()) {
					sPlayer = tempS;
					camera.setChaseEntity(sPlayer);
				}
			} else if (newEntity instanceof Base) {
				
				Base newBase = (Base) newEntity;
				gameData.addEntity(newBase);				
				Sprite baseS = new Sprite(newBase.getXPos(), newBase.getYPos(), baseTextureRegion, this.engine.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						setTarget(this);		
						
						return true;
					}
				};
				newBase.setSprite(baseS);				
				this.attachChild(baseS);
				
				
				
			} else if (newEntity instanceof Tower) {
				Tower newTower = (Tower) newEntity;
				gameData.addEntity(newTower);
				Sprite towerS = new Sprite(newTower.getXPos(), newTower.getYPos(), towerTextureRegion, this.engine.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						setTarget(this);

						return true;
					}
				};
				newTower.setSprite(towerS);
				this.attachChild(towerS);
				
			} else if (newEntity instanceof Creep) {
				Creep newAIUnit = (Creep) newEntity;
				gameData.addEntity(newAIUnit);
				Sprite tempS = new Sprite(newAIUnit.getXPos(), newAIUnit.getYPos(), AITextureRegion, this.engine.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						setTarget(this);		
						
						return true;
					}
				};

				final PhysicsHandler tempPhyHandler = new PhysicsHandler(tempS); // added
				tempS.registerUpdateHandler(tempPhyHandler); // added
				this.attachChild(tempS);
				newAIUnit.setSprite(tempS);
				newAIUnit.setPhyHandler(tempPhyHandler);				
				
			}
			

		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		
	}

	

}
