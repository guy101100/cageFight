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
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import co.nz.splashYay.cagefight.CustomSprite;
import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.GameState;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.Team;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Building;
import co.nz.splashYay.cagefight.entities.Creep;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.entities.Tower;
import co.nz.splashYay.cagefight.network.ClientOutNetCom;
import co.nz.splashYay.cagefight.network.UDPReciver;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ClientGameScene extends GameScene {

	// networking
	private ClientOutNetCom oNC;
	//private ClientInNetCom iNC;
	private String ipAddress;
	UDPReciver udpr;
	private boolean statsUp;
	

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

		oNC = new ClientOutNetCom(ipAddress, sceneManager, this);
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
					
					checkGameState();
					processEntities();
					updateTargetMarker();	
					updateHUD();
					
				}
			}

			@Override
			public void reset() {				
			}	
		});
		
		
		
		
		// end game loop

		return this;

	}
	
	private void checkGameState() {
		if (gameData.getGameState() == GameState.FINISHED && !statsUp) {
			StatsScene stats = new StatsScene(activity, engine, camera, gameData);
			stats.loadResources();
			stats.createScene();
			this.setChildScene(stats);
			this.hud.setVisible(false);
			statsUp = true;

		}
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
				
			} else if (pairs.getValue() instanceof Creep) {
				Creep creep = (Creep) pairs.getValue();
				processCreep(creep);
				
			} else if (pairs.getValue() instanceof Base) {
				Base base = (Base) pairs.getValue();
				processBuilding(base);
				
			} else if (pairs.getValue() instanceof Tower) {
				Tower tower = (Tower) pairs.getValue();
				processBuilding(tower);
			}
			
			
			

		}
	}
	
	private void processBuilding(Building building) {
		updateHealthBar(building);
		
				
		switch (building.getState()) {
		case IDLE:

			break;
		case ATTACKING:
			if (building.hasStateChanged()) {
				building.setStateChanged(false);
				boolean attacked = false;
				for (Entity ent : gameData.getEntitiesOnTeam(building.getEnemyTeam())) {
					if (building.getDistanceToTarget(ent) <= building.getAttackRange()) {
						towerAttackExplosion(ent);
						attacked = true;
					}
				}			
				if (attacked) {
					sceneManager.getSoundManager().playTowerAttackSound(player, building);
				}
			}
			
			
			break;
		case DEAD:
			if (building.hasStateChanged() && building.getState() == EntityState.DEAD) {
				building.setStateChanged(false);
				sceneManager.getSoundManager().playRandomDeathSound(this.player, building);
				building.setAlive(false);
			}

			break;
		}
	}

	
	private void processPlayer(Player player) {
			
		updateHealthBar(player);
		
		player.getSprite().setRotation(player.getDirection());
		player.getParentSprite().getHealthBar().setProgressPercentage(player.getCurrenthealth() / player.getMaxhealth());
		
		switch (player.getState()) {
		case MOVING:			
			player.setAnnimation(0, 5, 100, false);
			break;

		case IDLE:
			
			break;
		case ATTACKING:
			if (player.hasStateChanged()) {
				sceneManager.getSoundManager().playRandomAttackSound(this.player, player);		
			}

			break;
		case SPECIALATTACKING:
			if (player.hasStateChanged()) {
				sceneManager.getSoundManager().playTowerAttackSound(this.player, player);	
				makeXCycleAnnimation(player.getXPos(), player.getYPos(), specialAttackRegion, 24, 30, 3f, 2); 
			}
			break;
		case DEAD:
			if (player.hasStateChanged()) {				
				sceneManager.getSoundManager().playRandomDeathSound(this.player, player);
				player.setAlive(false);
			}
			break;
		}

		float x = player.getXPos() - player.getParentSprite().getX();
		float y = player.getYPos() - player.getParentSprite().getY();

		player.getPhyHandler().setVelocity(x * player.getSpeed(), y * player.getSpeed());
			
		
		
		
	}
	
	
	
	
	private void processCreep(Creep creep) {		
		updateHealthBar(creep);		
		
		creep.getSprite().setRotation(creep.getDirection());		
		switch (creep.getState()) {
		case MOVING:
			
			creep.setAnnimation(4, 11, 100, false);
			
			break;

		case IDLE:
			creep.setAnnimation(0, 3, 150, false);
			
			
			break;
			
		case ATTACKING:
			
			if (creep.hasStateChanged()) {	
				creep.setAnnimation(11, 21, 100, true);
				sceneManager.getSoundManager().playRandomAttackSound(this.player, creep);				
			}
			
			break;
		case DEAD:
			
			if (creep.hasStateChanged()) {
				creep.setAnnimation(26, 27, 1500, true);
				sceneManager.getSoundManager().playRandomDeathSound(this.player, creep);
				creep.setAlive(false);
			}
			
			break;
		}
		
		float x = creep.getXPos() - creep.getParentSprite().getX();
		float y = creep.getYPos() - creep.getParentSprite().getY();

		creep.getPhyHandler().setVelocity(x * creep.getSpeed(), y * creep.getSpeed());
	}
	
	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			TiledTextureRegion temp  = null;			
			
			if (newEntity instanceof Player) {
				temp = playerTextureRegion;
			} else if (newEntity instanceof Creep) {
				temp = AITextureRegion;
			} else if (newEntity instanceof Tower) {
				temp = towerTextureRegion;				
			} else if (newEntity instanceof Base) {
				temp = baseTextureRegion;				
			}
			
			
			gameData.addEntity(newEntity);
			AnimatedSprite tempS = new AnimatedSprite(0, 0, temp, this.engine.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					setTargetFromSpriteTouch(this);
					return true;
				}
			};
			
			if (newEntity.getTeam() == ALL_TEAMS.GOOD) {
				tempS.setColor(Color.YELLOW);
			} else {
				tempS.setColor(Color.CYAN);
			}
			
			registerTouchArea(tempS);
			setTouchAreaBindingOnActionDownEnabled(true);
			
			CustomSprite cust = new CustomSprite(newEntity.getXPos(), newEntity.getYPos(), tempS.getWidth(), tempS.getHeight(), blankTextureRegion, this.engine.getVertexBufferObjectManager());

			newEntity.setSprite(cust, tempS);
			
			ValueBar hp = new ValueBar(25, 0, (float)(cust.getWidth()- 50), 10, this.engine.getVertexBufferObjectManager());
			cust.setHealthBar(hp);			
			
			this.attachChild(cust);
			
			final PhysicsHandler tempPhyHandler = new PhysicsHandler(cust); // added
			cust.registerUpdateHandler(tempPhyHandler); // added			
			newEntity.setPhyHandler(tempPhyHandler);
			
			
			
			if (player != null && newEntity.getId() == player.getId()) {
				camera.setChaseEntity(cust);
				sPlayer = tempS;
			}		
			
		}
		
	}
	
	

	
	

	public void setPlayer(Player player) {
		this.player = player;
		this.addEntityToGameDataObj(player);
		
	}

	

}
