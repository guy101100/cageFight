package co.nz.splashYay.cagefight;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.ui.activity.BaseGameActivity;

public class SoundManager {
	private BaseGameActivity activity;
	private Engine engine;
	
	private HashMap<MUSIC_TYPE, Music> musics;
	private MUSIC_TYPE currentMusic;
	private Sound[] attackSounds;
	
	
	
	public SoundManager(BaseGameActivity act, Engine eng){
		this.activity = act;
		this.engine = eng;
		
		currentMusic = MUSIC_TYPE.NONE;
		
		musics = new HashMap<SoundManager.MUSIC_TYPE, Music>();
		attackSounds = new Sound[4];
	}
	
	public enum MUSIC_TYPE {
		MENU, GAME1, GAME2, GAME3, GAME_VICTORY, GAME_OVER, NONE
	}

	public void loadSoundRes() {
		
		try {
			musics.put(MUSIC_TYPE.GAME1, MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/FF7.ogg"));
			musics.put(MUSIC_TYPE.MENU, MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/menuMusic.mp3"));
			
			attackSounds[0] = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/attack1.mp3");
			attackSounds[1] = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/attack2.mp3");
			attackSounds[2] = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/attack3.mp3");
			attackSounds[3] = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/attack4.mp3");
			
			
			for (Music music : musics.values()) {
				music.setVolume(0.5f);
				music.setLooping(true);
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			System.out.println("error 1");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("error 2");
			e.printStackTrace();
		}
	}
	
	public void playRandomAttackSound(){
		int idx = new Random().nextInt(attackSounds.length);
		attackSounds[idx].play();		
	}
	
	public void setMusic(MUSIC_TYPE music) {
		if (currentMusic != MUSIC_TYPE.NONE) {
			musics.get(currentMusic).stop();			
		} 
		currentMusic = music;
		if (music != MUSIC_TYPE.NONE) {
			musics.get(music).play();			
		} 
		
		
	}
	
	
	
		
}
