package pt.invictus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	
	public static Texture spritesheet, fillTexture, controls, controls_keyboard;
	public static Texture level1, level2;
	public static TextureRegion xbox_controller, keyboardmouse_controller, computer_controller;
	public static TextureRegion rect;
	public static TextureRegion[][] spritesheet60;
	public static TextureRegion roulette;
	public static TextureRegion title;
	
	public static Music slotmachine, music;
	public static Sound itempickup, shoot, diamond, dropbomb, explosion, hurt, luckybox, moneybag, ricochet, star;
	
	public static BitmapFont font;
	
	public static void createAssets() {
		
		int s = Main.SIZE;	
		
		spritesheet = new Texture(Gdx.files.internal("spritesheet.png"));
		controls = new Texture(Gdx.files.internal("controls.png"));
		controls_keyboard = new Texture(Gdx.files.internal("controls_keyboard.png"));
		level1 = new Texture(Gdx.files.internal("level1.png"));
		level2 = new Texture(Gdx.files.internal("level2.png"));
		xbox_controller = new TextureRegion(new Texture(Gdx.files.internal("xbox_controller.png")));
		keyboardmouse_controller = new TextureRegion(new Texture(Gdx.files.internal("keyboardmouse_controller.png")));
		computer_controller = new TextureRegion(new Texture(Gdx.files.internal("computer_controller.png")));
		
		spritesheet60 = new TextureRegion[spritesheet.getWidth()/s][spritesheet.getHeight()/s];
		for(int i = 0; i < spritesheet60.length; i++) {
		for(int j = 0; j < spritesheet60[i].length; j++) {
			spritesheet60[i][j] = new TextureRegion(spritesheet, j*s, i*s, s,s);
		}}
		roulette = new TextureRegion(spritesheet, 4*s,0,5*s,5*s);
		title = new TextureRegion(spritesheet, 9*s,2*s,7*s,4*s);
		
		font = new BitmapFont(Gdx.files.internal("font.fnt"));
		
		Pixmap p = new Pixmap(Main.WIDTH, Main.HEIGHT, Format.RGBA8888);
		p.setColor(1, 1, 1, 1);
		p.fill();
		fillTexture = new Texture(p);
		p.dispose();
		
		p = new Pixmap(Main.SIZE, Main.SIZE, Format.RGBA8888);
		p.setColor(1, 1, 1, 1);
		p.fill();
		rect = new TextureRegion(new Texture(p));
		p.dispose();
		
		slotmachine = Gdx.audio.newMusic(Gdx.files.internal("sound/slotmachine.wav"));
		slotmachine.setLooping(true);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("sound/music.mp3"));
		music.setLooping(true);
		
		
		itempickup = Gdx.audio.newSound(Gdx.files.internal("sound/itempickup.wav"));
		shoot = Gdx.audio.newSound(Gdx.files.internal("sound/shoot.wav"));
		diamond = Gdx.audio.newSound(Gdx.files.internal("sound/diamond.wav"));
		dropbomb = Gdx.audio.newSound(Gdx.files.internal("sound/dropbomb.wav"));
		explosion = Gdx.audio.newSound(Gdx.files.internal("sound/explosion.wav"));
		hurt = Gdx.audio.newSound(Gdx.files.internal("sound/hurt.wav"));
		luckybox = Gdx.audio.newSound(Gdx.files.internal("sound/luckybox.wav"));
		moneybag = Gdx.audio.newSound(Gdx.files.internal("sound/moneybag.wav"));
		ricochet = Gdx.audio.newSound(Gdx.files.internal("sound/ricochet.wav"));
		star = Gdx.audio.newSound(Gdx.files.internal("sound/star.wav"));
	}
	
}
