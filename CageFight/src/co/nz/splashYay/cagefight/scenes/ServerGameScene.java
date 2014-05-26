package co.nz.splashYay.cagefight.scenes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import android.graphics.Typeface;
import co.nz.splashYay.cagefight.CustomSprite;
import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.GameState;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.SlowRepeatingTask;
import co.nz.splashYay.cagefight.ValueBar;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Creep;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.entities.Tower;
import co.nz.splashYay.cagefight.network.Client;
import co.nz.splashYay.cagefight.network.InFromClientListener;
import co.nz.splashYay.cagefight.network.ServerCheckListener;
import co.nz.splashYay.cagefight.network.UDPServer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class ServerGameScene extends GameScene {
	// networking
	private InFromClientListener iFCL;
	
	private ServerCheckListener sCL;
	private UDPServer udp;

	private SlowRepeatingTask slowLoop;
	

	
	
	
	public ServerGameScene(BaseGameActivity act, Engine eng, Camera cam, SceneManager sceneManager) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;		
		this.sceneManager = sceneManager;
	}

	

	public void createScene() {
		gameData = new GameData();
		this.engine.registerUpdateHandler(new FPSLogger());
		this.setBackground(new Background(0, 125, 58));
		this.phyWorld = new FixedStepPhysicsWorld(30, 30, new Vector2(0, 0), false);		
		this.registerUpdateHandler(phyWorld);
		
		
		
		setUpMap();
		setUpHUD();
		setUpBasesTowersAndAIunits();

		iFCL = new InFromClientListener(gameData, this);
		//oTCL = new OutToClientListener(gameData, this);
		sCL = new ServerCheckListener();
		udp = new UDPServer(gameData);
		iFCL.start();
		//oTCL.start();
		sCL.start();
		udp.start();
		
		slowLoop = new SlowRepeatingTask(mTMXTiledMap, gameData);
		slowLoop.start();
		
		player = new Player("", gameData.getUnusedID(), 500, 250, gameData.getTeam(ALL_TEAMS.EVIL).getSpawnXpos(), gameData.getTeam(ALL_TEAMS.EVIL).getSpawnYpos(), ALL_TEAMS.EVIL);
		addEntityToGameDataObj(player);
		
		//game loop
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				checkVictory();
				processServerPlayerControls();
				processEntityActions();
				updateTargetMarker();
				updateHUD();
				
				
			}

			@Override
			public void reset() {
			}
		});
		//end game loop
		
		
	}
	
	
	private void processServerPlayerControls() {
		Player tempPlayer = (Player)gameData.getEntityWithId(player.getId());
		tempPlayer.setMovementX(playerCommands.getMovementX());
		tempPlayer.setMovementY(playerCommands.getMovementY());
		tempPlayer.setAttackCommand(playerCommands.isAttackCommand());
		tempPlayer.setTarget(gameData.getEntityWithId(playerCommands.getTargetID()));
	}
	
	private void processEntityActions(){
		
		
		HashMap temp = (HashMap) gameData.getEntities().clone();
		
		Iterator it = temp.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			
			if (pairs.getValue() instanceof Player) {
				Player player = (Player) pairs.getValue();
				proccessPlayer(player);
				
			} else if (pairs.getValue() instanceof Base) {
				Base base = (Base) pairs.getValue();
				proccessBase(base);
			} else if (pairs.getValue() instanceof Tower) {
				Tower tower = (Tower) pairs.getValue();
				proccessTower(tower);
			} else if (pairs.getValue() instanceof Creep) {
				Creep ai = (Creep) pairs.getValue();
				proccessCreep(ai);
			}
			
		}
		
	}

	private void proccessCreep(Creep creep) {
		creep.checkState(gameData);

		creep.checkAndUpdateObjective(gameData);

		creep.setXPos(creep.getSprite().getParent().getX());
		creep.setYPos(creep.getSprite().getParent().getY());
		updateHealthBar(creep);
		

		switch (creep.getState()) {
		case MOVING:
			
			creep.setAnnimation(6, 11, 100);
			
			creep.getSprite().setRotation(creep.getDirectionToTarget());
			creep.setDirection(creep.getDirectionToTarget());			
			
			
			creep.moveTowardsObjective();
			
					
			break;
		case IDLE:
			creep.stopEntity();
			creep.setAnnimation(0, 3, 150);
			break;
		case ATTACKING:
			creep.stopEntity();
			if (creep.getTarget() != null && System.currentTimeMillis() >= (creep.getLastAttackTime() + creep.getAttackCoolDown())) {
				creep.setAnnimation(11, 16, 100);			
				creep.attackTarget();
				creep.setLastAttackTime(System.currentTimeMillis());
				sceneManager.getSoundManager().playRandomAttackSound(player, creep);
			} else {
				if (System.currentTimeMillis() >= (creep.getLastAttackTime()) + 600 ) {
					creep.setAnnimation(0, 3, 150);
				}
			}

			break;
		case DEAD:
			creep.stopEntity();
			if (creep.isAlive()) {
				creep.killCreep();
				sceneManager.getSoundManager().playRandomDeathSound(player, creep);
				creep.setAnnimation(26, 27, 1000);
			}	
			
			break;

		default:
			break;
		}
	}
	
	private void proccessBase(Base base) {
		base.checkState(gameData);
		updateHealthBar(base);
		
		switch (base.getState()) {
		case ATTACKING:
			if (base.getTeam() == ALL_TEAMS.EVIL) {
				for (Entity ent : base.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.GOOD))) {
					towerAttackExplosion(ent);
				}
			} else {
				for (Entity ent : base.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.EVIL))) {
					towerAttackExplosion(ent);
				}
			}
			
			base.setLastAttackTime(System.currentTimeMillis());
			sceneManager.getSoundManager().playTowerAttackSound(player, base);
			
			break;
		case IDLE:
			//do nothing
			break;
			
		case DEAD:
			if (base.isAlive()) {
				base.destroyBase();
				this.makeSingleCycleAnnimation(base.getCenterXpos(), base.getCenterYpos(), explosionTextureRegion, 9, 100 );
				//play base destroy sound, change music				
			}
			break;

		default:
			break;
		}
		
	}

	private void proccessTower(Tower tower) {
		tower.checkState(gameData);
		updateHealthBar(tower);
		switch (tower.getState()) {
		case ATTACKING:
			
			if (tower.getTeam() == ALL_TEAMS.EVIL) {
				for (Entity ent : tower.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.GOOD))) {
					towerAttackExplosion(ent);
				}
			} else {
				for (Entity ent : tower.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.EVIL))) {
					towerAttackExplosion(ent);
				}
			}
			
			tower.setLastAttackTime(System.currentTimeMillis());
			sceneManager.getSoundManager().playTowerAttackSound(player, tower);
			
			break;
		case IDLE:
			//do nothing
			break;

		case DEAD:
			if (tower.isAlive()) {
				tower.destroyTower();
				this.makeSingleCycleAnnimation(tower.getXPos(), tower.getYPos(), explosionTextureRegion, 9, 100);
				//play base destroy sound, change music
			}
			break;

		default:
			break;
		}
	}

	private void proccessPlayer(Player player) {		
		
		player.checkState(gameData);// checks and updates the players state
		
		player.setXPos(player.getSprite().getParent().getX());// set player position(in data) to the sprites position.
		player.setYPos(player.getSprite().getParent().getY());
		updateHealthBar(player);		
		
		
		if (player.getPlayerState() == EntityState.MOVING) {
			player.getBody().setActive(true);
			final Body playerBody = player.getBody();
			final Vector2 velocity = Vector2Pool.obtain(player.getMovementX() * player.getSpeed(), player.getMovementY() * player.getSpeed());
			playerBody.setLinearVelocity(velocity);
			Vector2Pool.recycle(velocity);
			

			if (player.getMovementX() != 0 && player.getMovementY() != 0) {
				float direction = MathUtils.radToDeg((float) Math.atan2(player.getMovementX(), -player.getMovementY()));
				player.getSprite().setRotation(direction);
				player.setDirection(direction);
			}
		}  else {
			//stops players from moving
			player.stopEntity();
			
			if (player.getPlayerState() == EntityState.ATTACKING) {
				if (player.getTarget() != null && System.currentTimeMillis() >= (player.getLastAttackTime() + player.getAttackCoolDown())  ) {
										
					if(player.getDistanceToTarget(player.getTarget()) < player.getAttackRange())
					{
						player.attackTarget();
						player.setLastAttackTime(System.currentTimeMillis());
						sceneManager.getSoundManager().playRandomAttackSound(this.player, player);
					}
				}
				
				
				
			} else if (player.getPlayerState() == EntityState.IDLE) {
				//do nothing
				
				
				
			} else if (player.getPlayerState() == EntityState.DEAD) {
				//kill the player and check if when to respawn
				if (player.isAlive()) {
					player.killPlayer();
					sceneManager.getSoundManager().playRandomDeathSound(this.player, player);
				}	else {
					if (player.getRespawnTime() <= System.currentTimeMillis()) {
						player.respawn(gameData);
					}
				}
			}			
			
			
		}		
	}
	
	
	
	private void setUpBasesTowersAndAIunits(){
		Base team1Base = new Base(2750, 770, 1000, 1000, gameData.getUnusedID(), ALL_TEAMS.GOOD);
		addEntityToGameDataObj(team1Base);
		gameData.setGoodBase(team1Base);
		
		Base team2Base = new Base(898, 770, 1000, 1000, gameData.getUnusedID(), ALL_TEAMS.EVIL);
		addEntityToGameDataObj(team2Base);
		gameData.setEvilBase(team2Base);
		
		Tower tower1 = new Tower(2180, 838, 750, 750, gameData.getUnusedID(), ALL_TEAMS.GOOD);
		addEntityToGameDataObj(tower1);	
		gameData.setGoodTower(tower1);
		
		Tower tower2 = new Tower(1474, 838, 750, 750, gameData.getUnusedID(), ALL_TEAMS.EVIL);
		addEntityToGameDataObj(tower2);
		gameData.setEvilTower(tower2);
		
		Rectangle rec = new Rectangle(tower2.getCenterXpos(), tower2.getCenterYpos(), 5, 5, this.engine.getVertexBufferObjectManager());
		rec.setColor(Color.WHITE);
		this.attachChild(rec);
		Rectangle rec2 = new Rectangle(team2Base.getCenterXpos(), team2Base.getCenterYpos(), 5, 5, this.engine.getVertexBufferObjectManager());
		rec2.setColor(Color.WHITE);
		this.attachChild(rec2);
		
		for (int i = 0; i < 3; i++) {
			Creep unit1 = new Creep(gameData.getUnusedID(), 100, 100, gameData.getTeam(ALL_TEAMS.GOOD).getSpawnXpos(), gameData.getTeam(ALL_TEAMS.GOOD).getSpawnYpos(), ALL_TEAMS.GOOD);
			addEntityToGameDataObj(unit1);	
			Creep unit2 = new Creep(gameData.getUnusedID(), 100, 100, gameData.getTeam(ALL_TEAMS.EVIL).getSpawnXpos(), gameData.getTeam(ALL_TEAMS.EVIL).getSpawnYpos(), ALL_TEAMS.EVIL);
			addEntityToGameDataObj(unit2);
		}
		
	}
	
	
	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			TiledTextureRegion temp  = null;
			BodyType body = BodyType.DynamicBody;
			
			if (newEntity instanceof Player) {
				temp = playerTextureRegion;
			} else if (newEntity instanceof Creep) {
				temp = AITextureRegion;
			} else if (newEntity instanceof Tower) {
				temp = towerTextureRegion;
				body = BodyType.StaticBody;
			} else if (newEntity instanceof Base) {
				temp = baseTextureRegion;
				body = BodyType.StaticBody;
			}
			
			
			gameData.addEntity(newEntity);
			AnimatedSprite tempS = new AnimatedSprite(newEntity.getXPos(), newEntity.getYPos(), temp, this.engine.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					setTargetFromSpriteTouch(this);
					return true;
				}
			};
			registerTouchArea(tempS);
			setTouchAreaBindingOnActionDownEnabled(true);
			
			CustomSprite cust = new CustomSprite(newEntity.getXPos(), newEntity.getYPos(), tempS.getWidth(), tempS.getHeight(), blankTextureRegion, this.engine.getVertexBufferObjectManager());

			newEntity.setSprite(cust, tempS);
			final FixtureDef AIFixDef = PhysicsFactory.createFixtureDef(1, 0f, 1f);
			newEntity.setBody(PhysicsFactory.createCircleBody(phyWorld,  newEntity.getCenterXpos(), newEntity.getCenterYpos(), AITextureRegion.getWidth()/4, body, AIFixDef));
			
			ValueBar hp = new ValueBar(25, 0, (float)(cust.getWidth()*0.75), 10, this.engine.getVertexBufferObjectManager());
			cust.setHealthBar(hp);
			
			phyWorld.registerPhysicsConnector(new PhysicsConnector(cust, newEntity.getBody(), true, false));
			this.attachChild(cust);
			
			if (player != null && newEntity.getId() == player.getId()) {
				camera.setChaseEntity(cust);
			}		
			
		}
		
	}
	
	public void updateHealthBar(Entity entity){
		if (entity.isAlive()) {
			entity.getParentSprite().showHealthBar();
			entity.getParentSprite().getHealthBar().setProgressPercentage((float)entity.getCurrenthealth() / (float)entity.getMaxhealth());

		} else {
			entity.getParentSprite().hideHealthBar();
		}
	}
	
	
	
	


	public void addClient(Client client) {
		udp.addClient(client);		
	}

		
		
	
	
	

	
}
