package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import co.nz.splashYay.cagefight.ItemManager;
import co.nz.splashYay.cagefight.UpgradeItem;
import co.nz.splashYay.cagefight.ItemManager.AllItems;

import android.content.ClipData.Item;
import android.graphics.Typeface;

public class ShopMenuScene extends MenuScene implements IOnMenuItemClickListener{
	
	
	
	
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private Font mFont;
	private TextureRegion buyS;
	private TextureRegion backS;
	
	private final int ITEM1 = 1;
	private final int ITEM2 = 2;
	private final int ITEM3 = 3;
	private final int ITEM4 = 4;
	private final int ITEM5 = 5;
	private final int ITEM6 = 6;
	private final int ITEM7 = 7;
	private final int ITEM8 = 8;
	
	private final int CLOSE = 99;
	
	private ItemManager items;
	
	private GameScene gS;
	
	private ShopMenuItemMenu itemMenu;
	
	private float[] xPositions;
	private float[] yPositions ;
	
	int size = 96;
	private Font smallFont;
	

	public ShopMenuScene(BaseGameActivity act, Engine eng, Camera cam, GameScene gS){
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.gS = gS;
		this.setCamera(cam);
		items = gS.getItemManager();
		
		
		xPositions = new float[AllItems.values().length];
		yPositions = new float[AllItems.values().length];	
		
		xPositions[0] = (int) (camera.getWidth() /5) - (size/2);
		yPositions[0] = 100;
		
		for (int i = 1; i < AllItems.values().length; i++) {		
			if (i < 5) {
				yPositions[i] = 100;
				xPositions[i] = (float) (xPositions[i-1] + (size*1.25));
			} else {
				yPositions[i] = (float) (yPositions[0] + (size*1.25));
				xPositions[i] = (float) (xPositions[i-5]);
			}
		}
		
		
		
		
		
		
	}
	
	public void loadResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		
		buyS = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "shopBuy.png");
		backS = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "shopBack.png");
		

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, android.graphics.Color.WHITE);
		this.mFont.load();
		
		this.smallFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24, android.graphics.Color.WHITE);
		this.smallFont.load();
		
	}
	
	public void createScene(){
		this.setPosition(0, 0);
		setBackgroundEnabled(false);
		
		
		final IMenuItem back = new ScaleMenuItemDecorator(new SpriteMenuItem(CLOSE, backS, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(back);
		back.setPosition(camera.getWidth()/4- (back.getWidth()/2), camera.getHeight() - back.getHeight() - 25);
		
		int count = 1;
		for (AllItems item : AllItems.values()) {
			
			final IMenuItem item1 = new ScaleMenuItemDecorator(new SpriteMenuItem(count, items.getItem(item).getTextureRegion(), engine.getVertexBufferObjectManager()), 1.2f, 1);	    
			item1.setSize(size, size);
			addMenuItem(item1);
			item1.setPosition(xPositions[count-1], yPositions[count-1]);	
			count++;
		}
		
				
		this.setOnMenuItemClickListener(this);
		
		
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case CLOSE:
			gS.toggleShopMenu();
			
			return true;
		case ITEM1:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.AXE));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM2:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.SHIELD));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM3:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.SONICBOOTS));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM4:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.SWIFTBLADE));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM5:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.CREEP_SPEED));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM6:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.CREEP_HP));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM7:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.CREEP_DAMAGE));				
			this.setChildScene(itemMenu);
			return true;
		case ITEM8:
			itemMenu = new ShopMenuItemMenu(activity, engine, camera, this, items.getItem(AllItems.SWIFTBLADE));				
			this.setChildScene(itemMenu);
			return true;

			
		default:
			return false;
			
		}
	}

	public Font getmFont() {
		return mFont;
	}

	public GameScene getgS() {
		return gS;
	}

	public Font getSmallFont() {
		return smallFont;
	}

	public TextureRegion getBuyS() {
		return buyS;
	}

	public TextureRegion getBackS() {
		return backS;
	}
	
	
	
	
	
	
	
}
