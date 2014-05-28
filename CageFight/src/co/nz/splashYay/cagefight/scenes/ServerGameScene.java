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
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
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
import co.nz.splashYay.cagefight.ItemManager;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.SlowRepeatingTask;
import co.nz.splashYay.cagefight.UpgradeItem;
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
		
		slowLoop = new SlowRepeatingTask(mTMXTiledMap, gameData, this);
		slowLoop.start();
		
		player = new Player("", gameData.getUnusedID(), 500, 250,0, 0, ALL_TEAMS.BAD);
		player.setGold(100);
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
		
		tempPlayer.setAttackState(playerCommands.getAttackState());
		
		tempPlayer.setWantsToPurchase(playerCommands.getPurchaseItem());
		playerCommands.setPurchaseItem(null);
		
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
			
			creep.setAnnimation(4, 11, 100, false);
			
			creep.getSprite().setRotation(creep.getDirectionToTarget());
			creep.setDirection(creep.getDirectionToTarget());			
			
			
			creep.moveTowardsObjective();
			
					
			break;
		case IDLE:
			creep.stopEntity();
			creep.setAnnimation(0, 3, 150, false);
			break;
		case ATTACKING:
			creep.stopEntity();
			if (creep.getTarget() != null) {
				creep.setAnnimation(11, 21, 100, false);			
				creep.attackTarget();
				creep.setLastAttackTime(System.currentTimeMillis());
				sceneManager.getSoundManager().playRandomAttackSound(player, creep);
			} else {
				if (System.currentTimeMillis() >= (creep.getLastAttackTime()) + 600 ) {
					creep.setAnnimation(0, 3, 150, false);
				}
			}

			break;
		case DEAD:
			creep.stopEntity();
			if (creep.isAlive()) {
				creep.killCreep();
				sceneManager.getSoundManager().playRandomDeathSound(player, creep);
				creep.setAnnimation(26, 27, 1000, false);
			} else {
				if (creep.getRespawnTime() <= System.currentTimeMillis()) {
					creep.respawn(gameData);
				}
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
			if (base.getTeam() == ALL_TEAMS.BAD) {
				for (Entity ent : base.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.GOOD))) {
					towerAttackExplosion(ent);
				}
			} else {
				for (Entity ent : base.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.BAD))) {
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
				this.makeXCycleAnnimation(base.getCenterXpos(), base.getCenterYpos(), explosionTextureRegion, 9, 100, 1f, 1);
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
			
			if (tower.getTeam() == ALL_TEAMS.BAD) {
				for (Entity ent : tower.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.GOOD))) {
					towerAttackExplosion(ent);
				}
			} else {
				for (Entity ent : tower.attackTargetsInRange(gameData.getEntitiesOnTeam(ALL_TEAMS.BAD))) {
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
				this.makeXCycleAnnimation(tower.getXPos(), tower.getYPos(), explosionTextureRegion, 9, 100, 1f, 1);
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
		checkItemPurchases(player);
		
		if (player.getPlayerState() == EntityState.MOVING) {
			
			player.setAnnimation(0, 5, 100, false);
			
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
				if (player.getTarget() != null   ) {	
						player.attackTarget();
						player.setLastAttackTime(System.currentTimeMillis());
						sceneManager.getSoundManager().playRandomAttackSound(this.player, player);
				}
				
				
				
			}
			else if(player.getPlayerState() == EntityState.SPECIALATTACKING)
			{
				//Perform Special Attack
				player.setLastSpecialAttackTime(System.currentTimeMillis());
				player.specialAttack(gameData.getEntitiesOnTeam(player.getEnemyTeam()));				
				makeXCycleAnnimation(player.getXPos(), player.getYPos(), specialAttackRegion, 24, 30, 3f, 2); 
				sceneManager.getSoundManager().playTowerAttackSound(this.player, this.player);
				
			}
			else if (player.getPlayerState() == EntityState.IDLE) {
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
	
	private void checkItemPurchases(Player player){
		if (player.getWantsToPurchase() != null) {
			
			UpgradeItem item = itemManager.getItem(player.getWantsToPurchase());
			player.setWantsToPurchase(null);
			if (player.purchaseItem(item)) {
				System.out.println("Player : " + player.getId() + " purchased " + item.getName());
				if (item.effectsPlayer()) {
					item.upgradeEntity(player);
				}
				
				if (item.effectsCreep()) {
					for (Entity ent : gameData.getEntitiesOnTeam(player.getEnemyTeam())) {
						if (ent instanceof Creep) {
							item.upgradeEntity(ent);
						}
					}
				}
				
				if (item.effectsTower()) {
					if (player.getTeam() == ALL_TEAMS.GOOD) {
						item.upgradeEntity(gameData.getGoodTower());
					} else {
						item.upgradeEntity(gameData.getEvilTower());
					}
				}
				if (item.effectsBase()) {
					if (player.getTeam() == ALL_TEAMS.GOOD) {
						item.upgradeEntity(gameData.getGoodTower());
					} else {
						item.upgradeEntity(gameData.getEvilTower());
					}
				}
				
			}
		}
	}
	
	
	private void setUpBasesTowersAndAIunits(){
		Base team1Base = new Base(2750, 770, 1000, 1000, gameData.getUnusedID(), ALL_TEAMS.GOOD);
		addEntityToGameDataObj(team1Base);
		gameData.setGoodBase(team1Base);
		
		Base team2Base = new Base(898, 770, 1000, 1000, gameData.getUnusedID(), ALL_TEAMS.BAD);
		addEntityToGameDataObj(team2Base);
		gameData.setEvilBase(team2Base);
		
		Tower tower1 = new Tower(2180, 838, 750, 750, gameData.getUnusedID(), ALL_TEAMS.GOOD);
		addEntityToGameDataObj(tower1);	
		gameData.setGoodTower(tower1);
		
		Tower tower2 = new Tower(1474, 838, 750, 750, gameData.getUnusedID(), ALL_TEAMS.BAD);
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
			Creep unit2 = new Creep(gameData.getUnusedID(), 100, 100, gameData.getTeam(ALL_TEAMS.BAD).getSpawnXpos(), gameData.getTeam(ALL_TEAMS.BAD).getSpawnYpos(), ALL_TEAMS.BAD);
			addEntityToGameDataObj(unit2);
		}
		
	}
	
	
	public void addEntityToGameDataObj(Entity newEntity) {
		if (newEntity != null) {
			TiledTextureRegion temp  = null;
			BodyType body = BodyType.DynamicBody;
			
			if (newEntity instanceof Player) {
				temp = playerTextureRegion;
				if (gameData.getLastPlayerAddedTo() == null || gameData.getLastPlayerAddedTo() == ALL_TEAMS.GOOD) {
					newEntity.setTeam(ALL_TEAMS.BAD);
					gameData.setLastPlayerAddedTo(ALL_TEAMS.BAD);
					newEntity.setXPos(gameData.getTeam(ALL_TEAMS.BAD).getSpawnXpos()); 
					newEntity.setYPos(gameData.getTeam(ALL_TEAMS.BAD).getSpawnYpos());
				} else {
					newEntity.setTeam(ALL_TEAMS.GOOD);
					gameData.setLastPlayerAddedTo(ALL_TEAMS.GOOD);
					newEntity.setXPos(gameData.getTeam(ALL_TEAMS.GOOD).getSpawnXpos()); 
					newEntity.setYPos(gameData.getTeam(ALL_TEAMS.GOOD).getSpawnYpos());
				}
			} else if (newEntity instanceof Creep) {
				temp = AITextureRegion;
			} else if (newEntity instanceof Tower) {
				temp = towerTextureRegion;
				body = BodyType.StaticBody;
			} else if (newEntity instanceof Base) {
				temp = baseTextureRegion;
				body = BodyType.StaticBody;
			}
			
			
			
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
			final FixtureDef AIFixDef = PhysicsFactory.createFixtureDef(1, 0f, 1f);
			
			if (newEntity instanceof Base || newEntity instanceof Tower) {
				newEntity.setBody(PhysicsFactory.createBoxBody(phyWorld, cust, body, AIFixDef));

			} else {
				newEntity.setBody(PhysicsFactory.createCircleBody(phyWorld,  newEntity.getCenterXpos(), newEntity.getCenterYpos(), AITextureRegion.getWidth()/4, body, AIFixDef));
			}
			
			ValueBar hp = new ValueBar(25, 0, (float)(cust.getWidth() - 50), 10, this.engine.getVertexBufferObjectManager());
			cust.setHealthBar(hp);
			
			phyWorld.registerPhysicsConnector(new PhysicsConnector(cust, newEntity.getBody(), true, false));
			this.attachChild(cust);
			
			if (player != null && newEntity.getId() == player.getId()) {
				camera.setChaseEntity(cust);
			}		
			
			gameData.addEntity(newEntity);
			
		}
		
	}
	
	
	
	
	
	
	


	public void addClient(Client client) {
		udp.addClient(client);		
	}

		
		
	
	
	

	
}
