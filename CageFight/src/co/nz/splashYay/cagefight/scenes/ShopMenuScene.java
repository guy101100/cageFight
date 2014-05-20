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

import co.nz.splashYay.cagefight.UpgradeItem;

import android.graphics.Typeface;

public class ShopMenuScene extends MenuScene implements IOnMenuItemClickListener{
	
	
	
	
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private Font mFont;
	private TextureRegion upgrade;
	
	private final int ITEM1 = 1;
	private final int ITEM2 = 2;
	private final int ITEM3 = 3;
	private final int ITEM4 = 4;
	private final int ITEM5 = 5;
	private final int ITEM6 = 6;
	private final int ITEM7 = 7;
	private final int ITEM8 = 8;
	private final int ITEM9 = 9;
	private final int ITEM10 = 10;
	
	private final int CLOSE = 99;
	
	private UpgradeItem[] items;
	private GameScene gS;
	
	

	public ShopMenuScene(BaseGameActivity act, Engine eng, Camera cam, GameScene gS){
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.gS = gS;
		items = new UpgradeItem[10];
		
		for (int i = 0; i < 10; i++) {
			items[i] = new UpgradeItem(5, 50, 1, 500, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		}
		
	}
	
	public void loadResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		
		upgrade = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "buySquare.jpg");
		

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
		
	}
	
	public void createScene(){
		this.setPosition(0, 0);
		
		//ipText = new Text(100, 160, this.mFont, "Server IP : ", "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
	    //joinMenu.attachChild(ipText);
		
		final IMenuItem item1 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM1, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item1);
		item1.setPosition(50, camera.getHeight()/4);
		
		final IMenuItem item2 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM2, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item2);
		item2.setPosition((float) (item1.getX() + (item1.getWidth()*1.25)), item1.getY());
		
		final IMenuItem item3 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM3, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item3);
		item3.setPosition((float) (item2.getX() + (item1.getWidth()*1.25)), item1.getY());
		
		final IMenuItem item4 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM4, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item4);
		item4.setPosition((float) (item3.getX() + (item1.getWidth()*1.25)), item1.getY());
		
		final IMenuItem item5 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM5, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item5);
		item5.setPosition((float) (item4.getX() + (item1.getWidth()*1.25)), item1.getY());
		
		final IMenuItem item6 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM6, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item6);
		item6.setPosition(item1.getX(), (float) (item1.getY() + (item1.getHeight()*1.25)));
		
		final IMenuItem item7 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM7, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item7);
		item7.setPosition((float) (item6.getX() + (item1.getWidth()*1.25)), item6.getY());
		
		final IMenuItem item8 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM8, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item8);
		item8.setPosition((float) (item7.getX() + (item1.getWidth()*1.25)), item6.getY());
		
		final IMenuItem item9 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM9, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item9);
		item9.setPosition((float) (item8.getX() + (item1.getWidth()*1.25)), item6.getY());
		
		final IMenuItem item10 = new ScaleMenuItemDecorator(new SpriteMenuItem(ITEM10, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(item10);
		item10.setPosition((float) (item9.getX() + (item1.getWidth()*1.25)), item6.getY());
		
		final IMenuItem back = new ScaleMenuItemDecorator(new SpriteMenuItem(CLOSE, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(back);
		back.setPosition(camera.getWidth() - back.getWidth() - 25, + 25);
		
		
		
		this.setOnMenuItemClickListener(this);
		
		
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case CLOSE:
			gS.toggleShopMenu();
			
			return true;
		case 2:
			
			return true;
			
		default:
			return false;
			
		}
	}
	
	
}
