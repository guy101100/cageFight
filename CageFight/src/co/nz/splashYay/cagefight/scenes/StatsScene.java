package co.nz.splashYay.cagefight.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
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
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import android.graphics.Typeface;

public class StatsScene extends MenuScene implements IOnMenuItemClickListener
{
	
	private BaseGameActivity activity;
	private Engine engine;
	
	private GameData gd;
	
	private TextureRegion image;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private Font mFont;
	
	public StatsScene(BaseGameActivity act, Engine eng, Camera cam, GameData gd){
		this.activity = act;
		this.engine = eng;
		this.setCamera(cam);
		this.gd = gd;

		
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void loadResources()
	{
		this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20, Color.WHITE_ABGR_PACKED_INT);
		this.mFont.load();
	}

	public void createScene()
	{
		this.setPosition(0, 0);
		
		Text statsTitle = new Text(this.getCamera().getWidth() / 2, 50, this.mFont, "Game Over!", new TextOptions(HorizontalAlign.CENTER), activity.getVertexBufferObjectManager());
		statsTitle.setScale(2.0f);
		this.attachChild(statsTitle);
		
		Text goodHeader = new Text(20, 90, this.mFont, "Good Team", new TextOptions(HorizontalAlign.CENTER), activity.getVertexBufferObjectManager());
		statsTitle.setScale(1.1f);
		this.attachChild(goodHeader);
		
		Text evilHeader = new Text(20, 210, this.mFont, "Evil Team", new TextOptions(HorizontalAlign.CENTER), activity.getVertexBufferObjectManager());
		statsTitle.setScale(1.1f);
		this.attachChild(evilHeader);
		
		this.setBackgroundEnabled(false);
		
		int good = 0;
		int evil = 0;
		for(Entity ent : gd.getEntities().values())
		{
			if(ent instanceof Player)
			{
				Player p = (Player) ent;
				
				if(p.getTeam().equals(Team.ALL_TEAMS.GOOD))
				{
					Text playerInfo = new Text(30, 120 + (20 * good), this.mFont, p.getStatsString(), activity.getVertexBufferObjectManager());
					this.attachChild(playerInfo);
					good++;
				}
				else if(p.getTeam().equals(Team.ALL_TEAMS.EVIL))
				{
					Text playerInfo = new Text(30, 240 + (20 * evil), this.mFont, p.getStatsString(), activity.getVertexBufferObjectManager());
					this.attachChild(playerInfo);
					evil++;
				}
				
			}
		}
		
		this.setVisible(true);
		
		this.setOnMenuItemClickListener(this);
	}
}
