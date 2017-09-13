package pt.invictus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;

import pt.invictus.screens.MenuScreen;

public class Main extends Game {

	public static boolean DEBUG = false;
	public static boolean MUSIC = true;
	public static boolean SOUND = true;
	
	public static final int SIZE = 60;
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;


	

	@Override
	public void create() {
		Assets.createAssets();
		
		start();		
	}
	
	public void start() {
		Sprites.createSprites();
		Assets.slotmachine.stop();
		setScreen(new MenuScreen(this));		
	}
	
	@Override
	public void render() {
		super.render();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) DEBUG ^= true;
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) start();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.M)) MUSIC ^= true;
		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) SOUND ^= true;
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
			if (!Gdx.graphics.isFullscreen()) 
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			else
				Gdx.graphics.setWindowedMode(Main.WIDTH/2,Main.HEIGHT/2);
		}
		
		if (MUSIC && !Assets.music.isPlaying()) Assets.music.play();
		if (!MUSIC && Assets.music.isPlaying()) Assets.music.pause();
 	}

	public static void playSound(Sound sound) { if (SOUND) sound.play(); }
	

}
