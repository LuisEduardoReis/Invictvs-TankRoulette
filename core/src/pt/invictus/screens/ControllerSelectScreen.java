package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;
import pt.invictus.controllers.KeyBoardMouseController;
import pt.invictus.controllers.Xbox360Controller;

public class ControllerSelectScreen extends ScreenAdapter {
	Main main;
	
	SpriteBatch batch;	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Viewport viewport;
	
	float t;
	
	float fadein_timer, fadein_delay;
	float fadeout_timer, fadeout_delay;
	Runnable fadeout_action;
	
	
	public ControllerSelectScreen(Main main) {
		this.main = main;
		
		t = 0;
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.translate(Main.WIDTH/2, Main.HEIGHT/2);
		viewport = new FitViewport(Main.WIDTH, Main.HEIGHT, camera);

		fadein_delay = 2;
		fadein_timer = fadein_delay;
		
		fadeout_timer = -1;
		fadeout_delay = 1;
		fadeout_action = null;
		
	}
	
	@Override
	public void render(float delta) {
		
		// Update
		t += delta;
		
		if (fadein_timer > 0) fadein_timer = Util.stepTo(fadein_timer, 0, delta);
		if (fadeout_timer > 0) fadeout_timer = Util.stepTo(fadeout_timer, 0, delta);
		if (fadeout_timer == 0) {
			if (fadeout_action != null) fadeout_action.run();
		}		
		
		if (fadeout_timer < 0) {
			int active = 0;
			for(GameController c : main.controllers) if (c.getActive()) active++;
			
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
			
			for(GameController c : main.controllers) {
				if (c.getKeyPressed(Key.A)) {
					c.setActive(!c.getActive()); 
				}
			}
		}
		
		
		// Render
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		
		batch.begin();
		
			BitmapFont font = Assets.font;
			
			batch.setColor(0,0,0,0.5f);
			batch.draw(Assets.fillTexture,0,0);
			
			Assets.font.getData().setScale(4);
			Util.drawTitle(batch, Assets.font, "Select Controllers", Main.WIDTH/2,Main.HEIGHT*0.85f, 1);
			
			
			for(int i = 0; i < main.controllers.size(); i++) {
				GameController c = main.controllers.get(i);
				float pos = Main.WIDTH/5.5f * (i -(main.controllers.size()-1)/2f);				
				
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
			Util.drawTextCentered(batch, font, "Press Fire to activate/deactivate controller", Main.WIDTH/2,230);
			
			int active = 0;
			for(int i = 0; i < main.controllers.size(); i++) if (main.controllers.get(i).getActive()) active++;
			if (active > 0) {
				font.getData().setScale(2.5f);
				font.setColor(Color.WHITE);
				Util.drawTextCentered(batch, font, "Press Start/Space to continue", Main.WIDTH/2,100);
			}
			
			if (fadein_timer > 0) {
				batch.setColor(0,0,0,fadein_timer/fadein_delay);
				batch.draw(Assets.fillTexture,0,0,Main.WIDTH,Main.HEIGHT);
			}
			if (fadeout_timer >= 0) {
				batch.setColor(0,0,0,1-(fadeout_timer/fadeout_delay));
				batch.draw(Assets.fillTexture,0,0,Main.WIDTH,Main.HEIGHT);
			}
		batch.end();
		
		super.render(delta);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		viewport.update(width, height);
	}
	

}
