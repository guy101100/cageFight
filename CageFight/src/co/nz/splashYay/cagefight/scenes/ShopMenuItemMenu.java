package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;

import co.nz.splashYay.cagefight.UpgradeItem;

public class ShopMenuItemMenu extends MenuScene implements IOnMenuItemClickListener{
	
	private UpgradeItem upgradeItem;
	
	private final int BACK = 100;
	private final int BUY = 101;

	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private Font mFont;
	private TextureRegion upgrade;

	private ShopMenuScene sMS;

	private UpgradeItem item;

	private TextureRegion itemImage;

	private TextureRegion bg;
	
	
	
	public ShopMenuItemMenu(BaseGameActivity act, Engine eng, Camera cam, ShopMenuScene sMS, UpgradeItem item) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.sMS = sMS;
		this.item = item;
		this.setCamera(cam);
		loadRes();
		createScene();
	}
	
	public void loadRes(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/items/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
		bg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "BackgroundClient.png");
		
		upgrade = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "buySquare.jpg");
		itemImage = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, item.getImageString());
		

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, android.graphics.Color.WHITE);
		this.mFont.load();
	}
	
	public void createScene(){
		//createBackground();
		//this.setBackgroundEnabled(false);
		
		Rectangle outline = new Rectangle(25, 25, 256, 256, engine.getVertexBufferObjectManager());
		outline.setColor(Color.RED);
		this.attachChild(outline);
		
		Rectangle center = new Rectangle(35, 35, 236, 236, engine.getVertexBufferObjectManager());
		center.setColor(Color.BLACK);
		this.attachChild(center);
		
		Sprite icon = new Sprite(center.getX(), center.getY(), itemImage, this.engine.getVertexBufferObjectManager());
		icon.setSize(center.getWidth(), center.getHeight());
		this.attachChild(icon);
		
		final IMenuItem back = new ScaleMenuItemDecorator(new SpriteMenuItem(BACK, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(back);
		back.setPosition(camera.getWidth()/4- (back.getWidth()/2), camera.getHeight() - back.getHeight() - 25);
		
		final IMenuItem buyItem = new ScaleMenuItemDecorator(new SpriteMenuItem(BUY, upgrade, engine.getVertexBufferObjectManager()), 1.2f, 1);	    
		addMenuItem(buyItem);
		buyItem.setPosition(camera.getWidth() - (camera.getWidth()/4) - (buyItem.getWidth()/2), back.getY());
		
		//ipText = new Text(100, 160, this.mFont, "Server IP : ", "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
	    //joinMenu.attachChild(ipText);
		
		
		Text title = new Text(camera.getWidth()/2, 50, this.mFont, item.getName(), "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
	    title.setPosition(icon.getX() + icon.getWidth() + 25, 50);
	    title.setColor(Color.WHITE);
		this.attachChild(title);
		
		Text description = new Text(camera.getWidth()/2, 50, this.mFont, item.getDescription(), "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
		description.setPosition(title.getX(), camera.getHeight()/4);
		description.setColor(Color.WHITE);
		this.attachChild(description);
		
				
		Text cost = new Text(camera.getWidth()/2, 50, this.mFont, (item.getCost()+""), "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
		cost.setPosition(title.getX(), description.getY() + description.getHeight() + (description.getHeight()/2));
		cost.setColor(Color.WHITE);
		this.attachChild(cost);
		
		String[] statStrings = (String[]) item.getStatBonuses().toArray();
		
		for (int i = 0; i < statStrings.length; i++) {
			Text stats = new Text(camera.getWidth()/2, 50, this.mFont, statStrings[i], "Server IP : XXX.XXX.XXX.XXX Extra bit on the end".length(), activity.getVertexBufferObjectManager());
			
			if (i == 0) {
				stats.setPosition(title.getX(), cost.getY() + cost.getHeight() + (cost.getHeight()/2));
			} else {
				stats.setPosition(title.getX(), cost.getY() + ((cost.getHeight() + (cost.getHeight()/2))*i)          );
			}		
			
			stats.setColor(Color.WHITE);
			this.attachChild(stats);
		}
		
		
		
		
		
		
		
		this.setOnMenuItemClickListener(this);
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, bg, engine.getVertexBufferObjectManager())
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	
	
	
	
	
	

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case BACK:			
			sMS.clearChildScene();
			return true;
		case BUY:
			
			return true;
			
		default:
			return false;
			
		}
		
	}

}
