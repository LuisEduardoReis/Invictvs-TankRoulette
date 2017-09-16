package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;
import pt.invictus.controllers.Xbox360Controller;

public class LevelSelectScreen extends DefaultScreen {
		
	int index;
	String levels[] = {"level.tmx","level2.tmx"};		
	
	public LevelSelectScreen(Main main) {
		super(main);
		
		fadein_delay = 2;
		fadein_timer = fadein_delay;
		
		fadeout_timer = -1;
		fadeout_delay = 1;
		
		index = 0;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (fadeout_timer < 0) {
			boolean start = false;
			for(GameController c : main.controllers) {
			
				if (c instanceof Xbox360Controller && c.getLookNormal() > 0.25) {
					float v = (float) Math.cos(c.getLookDir(0, 0, null));
					if (v < -0.5) index = 0;
					if (v > 0.5) index = 1;
				}
				
				if (c.getKeyPressed(Key.A)) {
					index = (index+1)%2;
				}
								
				if (c instanceof Xbox360Controller && c.getKeyPressed(Key.START)) {
					start = true; break;
				}
			}
			
			if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) index = 0;
			if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) index = 1;
				
			if (start || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				
				Main.playSound(Assets.itempickup);
				
				fadeout_timer = fadeout_delay;
				fadeout_action = new Runnable() {
					@Override
					public void run() {
						main.setScreen(new GameScreen(main, levels[index % levels.length]));
					}
				}; 
			}
		}
	}
	
	@Override
	public void display() {
		super.display();		
		
		batch.begin();
		
			Assets.font.getData().setScale(4);
			Util.drawTitle(batch, Assets.font, "Select your level!", Main.WIDTH/2,Main.HEIGHT*0.9f, 1);

			float sc = 1/2.25f;
			float br = 20;
			batch.setColor(Color.WHITE);
			if (index % 2 == 0) batch.draw(Assets.rect, Main.SIZE-br, Main.HEIGHT/3.5f-br, Main.WIDTH*sc+2*br, Main.HEIGHT*sc+2*br);
			if (index % 2 == 1) batch.draw(Assets.rect, Main.WIDTH-Main.SIZE-Main.WIDTH*sc-br, Main.HEIGHT/3.5f-br, Main.WIDTH*sc+2*br, Main.HEIGHT*sc+2*br);
			
			
			batch.setColor(Color.WHITE);
			batch.draw(Assets.level1,Main.SIZE,Main.HEIGHT/3.5f,Main.WIDTH*sc, Main.HEIGHT*sc);
			batch.draw(Assets.level2,Main.WIDTH-Main.SIZE-Main.WIDTH*sc,Main.HEIGHT/3.5f,Main.WIDTH*sc, Main.HEIGHT*sc);
			
			
					
			if (t > 2 && t % 1 < 0.5f) {
				Assets.font.getData().setScale(2.5f);
				Util.drawTitle(batch, Assets.font, "Press Start/Space to continue", Main.WIDTH/2,Main.HEIGHT*0.15f, 1);
			}
			
		batch.end();
		
	}

}
