package pt.invictus;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

import pt.invictus.controllers.GameController;
import pt.invictus.controllers.KeyBoardMouseController;
import pt.invictus.controllers.Xbox360Controller;
import pt.invictus.screens.*;

public class Main extends Game {

	public static boolean DEBUG = false;
	public static boolean MUSIC = true;
	public static boolean SOUND = true;
	public static boolean PARTICLES = true;
	
	public static final int SIZE = 60;
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final String TITLE = "Tank Roulette";

	public ArrayList<GameController> controllers = new ArrayList<GameController>();
	public int ai_players = 4;

	@Override
	public void create() {
		Assets.createAssets();
					
		start();		
	}
	
	public void start() {
		Sprites.createSprites();
		
		controllers.clear();
		controllers.add(new KeyBoardMouseController());
		for(Controller c : Controllers.getControllers()) 
			controllers.add(new Xbox360Controller(c));
		
		System.out.println(controllers);
		
		for(GameController controller : controllers) controller.setActive(true);
		if (controllers.size() > 1) controllers.get(0).setActive(false);		
		
		Assets.music.stop();
		
		setScreen(new MenuScreen(this));
		//setScreen(new ControllerSelectScreen(this));
		//setScreen(new GameScreen(this,"level.tmx"));
	}
	
	@Override
	public void render() {
		for(GameController c : controllers) c.update();	
		
		super.render();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) DEBUG ^= true;
		if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) PARTICLES ^= true;
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
