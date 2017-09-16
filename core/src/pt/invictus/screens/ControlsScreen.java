package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;

public class ControlsScreen extends DefaultScreen {
	
	public ControlsScreen(Main main) {
		super(main);
		
		fadein_delay = 2;
		fadein_timer = fadein_delay;
		
		fadeout_timer = -1;
		fadeout_delay = 1;
		
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (fadeout_timer < 0) {
			boolean start = false;
			for(GameController c : main.controllers)
				if (c.getKeyDown(Key.START) || c.getKeyDown(Key.A)) {
					start = true; break;
				}
			if (start || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				
				Main.playSound(Assets.itempickup);
				
				fadeout_timer = fadeout_delay;
				fadeout_action = new Runnable() {
					@Override
					public void run() {
						main.setScreen(new ControllerSelectScreen(main));
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
			Util.drawTitle(batch, Assets.font, "Controls", Main.WIDTH/2,Main.HEIGHT*0.85f, 1);
					
			float duration = 5;
			float phase = (t % duration)/duration;
			if (phase < 0.1)
				batch.setColor(1, 1, 1, (phase / 0.1f));
			else if (phase > 0.9)
				batch.setColor(1, 1, 1, 1-((phase-0.9f) / 0.1f));				
			else
				batch.setColor(Color.WHITE);
			
			if (t % (2*duration) < duration) {
				batch.draw(Assets.controls, 0,-50, Main.WIDTH, Main.HEIGHT);
			} else {
				batch.draw(Assets.controls_keyboard, 0,-50, Main.WIDTH, Main.HEIGHT);
			}			
			
			
			int ind = (int) ((t/Sprites.player[0].anim_delay) % 2);
			Sprites.player[0].render(batch, ind, Main.WIDTH/8, Main.HEIGHT/2, 5,5,90+t*Util.radToDeg,Color.WHITE);
			Sprites.bullet.render(batch, 0, Main.WIDTH*7/8, Main.HEIGHT*(0.5f+(t/2 % 1)), 2.5f, 2.5f, 90, Color.WHITE);
			Sprites.player[0].render(batch, ind, Main.WIDTH*7/8, Main.HEIGHT/2, 5,5,90,Color.WHITE);
			
			if (t > 2 && t % 1 < 0.5f) {
				Assets.font.getData().setScale(2);
				Util.drawTitle(batch, Assets.font, "Press Start/Space to continue", Main.WIDTH/2,Main.HEIGHT*0.125f, 1);
			}
		
		batch.end();
	}	

}
