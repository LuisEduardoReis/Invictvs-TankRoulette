package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.XBox360Pad;

public class ControlsScreen extends ScreenAdapter {
	Main main;
	
	SpriteBatch batch;	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Viewport viewport;
	
	float t;
	
	float fadein_timer, fadein_delay;
	float fadeout_timer, fadeout_delay;
	Runnable fadeout_action;
	
	
	public ControlsScreen(Main main) {
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
			boolean start = false;
			for(Controller c : Controllers.getControllers())
				if (c.getButton(XBox360Pad.BUTTON_START)) {
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
		
		
		// Render
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		
		batch.begin();
		
			Assets.font.getData().setScale(4);
			Util.drawTitle(batch, Assets.font, "Controls", Main.WIDTH/2,Main.HEIGHT*0.85f, 1);
		
			batch.setColor(Color.WHITE);
			batch.draw(Assets.controls, 0,-50, Main.WIDTH, Main.HEIGHT);
			
			int ind = (int) ((t/Sprites.player[0].anim_delay) % 2);
			Sprites.player[0].render(batch, ind, Main.WIDTH/8, Main.HEIGHT/2, 5,5,90+t*Util.radToDeg,Color.WHITE);
			Sprites.bullet.render(batch, 0, Main.WIDTH*7/8, Main.HEIGHT*(0.5f+(t/2 % 1)), 2.5f, 2.5f, 90, Color.WHITE);
			Sprites.player[0].render(batch, ind, Main.WIDTH*7/8, Main.HEIGHT/2, 5,5,90,Color.WHITE);
			
			if (t > 2 && t % 1 < 0.5f) {
				Assets.font.getData().setScale(2);
				Util.drawTitle(batch, Assets.font, "Press Start to continue", Main.WIDTH/2,Main.HEIGHT*0.125f, 1);
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
