package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;
import pt.invictus.controllers.KeyBoardMouseController;
import pt.invictus.controllers.Xbox360Controller;

public class ControllerSelectScreen extends DefaultScreen {
		
	public ControllerSelectScreen(Main main) {
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
			int active = 0;
			for(GameController c : main.controllers) {
				if (c.getKeyPressed(Key.A)) {
					c.setActive(!c.getActive()); 
				}
				if (c.getActive()) active++;
			}
			
			if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) main.ai_players++;
			if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) main.ai_players--;
			main.ai_players = (int) Util.clamp(main.ai_players, 0, 4-active);
			
			active += main.ai_players;
			
			if (active > 0) {
				boolean start = false;
				
				for(GameController c : main.controllers)
					if (c.getKeyDown(Key.START)) {	
						start = true; break;
					}
			
				if (start || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
					
					Main.playSound(Assets.itempickup);
					
					fadeout_timer = fadeout_delay;
					fadeout_action = new Runnable() {
						@Override
						public void run() {
							main.setScreen(new LevelSelectScreen(main));
						}
					}; 
				}
			}
			
		}
	}
	
	@Override
	public void display() {
		super.display();
		
		batch.begin();
		
			BitmapFont font = Assets.font;
			
			batch.setColor(0,0,0,0.5f);
			batch.draw(Assets.fillTexture,0,0);
			
			Assets.font.getData().setScale(4);
			Util.drawTitle(batch, Assets.font, "Select Players", Main.WIDTH/2,Main.HEIGHT*0.85f, 1);
			
			
			for(int i = 0; i < main.controllers.size(); i++) {
				float pos = Main.WIDTH/5.5f * (i -(main.controllers.size()-1)/2f);		
				GameController c = main.controllers.get(i);								
				
				if (c.getActive()) 
					batch.setColor(Color.WHITE);
				else 
					batch.setColor(Color.DARK_GRAY);
				
				if (c instanceof Xbox360Controller) {
					Util.drawCenteredR(batch, Assets.xbox_controller,Main.WIDTH/2+pos,Main.HEIGHT/2, 0.5f,0.5f, 0, true,true);
				}
				if (c instanceof KeyBoardMouseController) {
					Util.drawCenteredR(batch, Assets.keyboardmouse_controller,Main.WIDTH/2+pos,Main.HEIGHT/2, 0.5f,0.5f, 0, true,true);
				}				
			}
			batch.setColor(Color.WHITE);
			
			font.getData().setScale(1.5f);
			font.setColor(Color.WHITE);
			
			Util.drawTextCentered(batch, font, "Press Fire to activate/deactivate controller", Main.WIDTH/2,260);
			Util.drawTextCentered(batch, font, "Number of AI players (+/- to add/remove): " + main.ai_players, Main.WIDTH/2,160);
			
			
			int active = main.ai_players;
			for(int i = 0; i < main.controllers.size(); i++) if (main.controllers.get(i).getActive()) active++;
			if (active > 0) {
				font.getData().setScale(2.5f);
				font.setColor(Color.WHITE);
				Util.drawTextCentered(batch, font, "Press Start/Space to continue", Main.WIDTH/2,50);
			}
		
		batch.end();
	}

}
