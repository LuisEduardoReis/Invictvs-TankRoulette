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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.XBox360Pad;

public class LevelSelectScreen extends ScreenAdapter {
	Main main;
	
	SpriteBatch batch;	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Viewport viewport;
	
	float t;
	
	float fadein_timer, fadein_delay;
	float fadeout_timer, fadeout_delay;
	Runnable fadeout_action;
	
	int index;
	String levels[] = {"level.tmx","level2.tmx"};
	
	Array<Controller> controllers;
	boolean a_down[];
	
	
	public LevelSelectScreen(Main main) {
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
		
		index = 0;
		
		controllers = Controllers.getControllers();
		a_down = new boolean[controllers.size];
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
			for(int i = 0; i < controllers.size; i++) {
				Controller c = controllers.get(i);
				if (c.getButton(XBox360Pad.BUTTON_A)) {
					if (!a_down[i]) {
						index++;
					}
					a_down[i] = true;
				} else
					a_down[i] = false;
				
				if (c.getButton(XBox360Pad.BUTTON_START)) {
					start = true; break;
				}
			}
				
			if (start || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				
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
			Util.drawTitle(batch, Assets.font, "Select your level!", Main.WIDTH/2,Main.HEIGHT*0.9f, 1);

			float sc = 1/2.25f;
			float br = 20;
			batch.setColor(Color.WHITE);
			if (index % 2 == 0) batch.draw(Assets.rect, Main.SIZE-br, Main.HEIGHT/3.5f-br, Main.WIDTH*sc+2*br, Main.HEIGHT*sc+2*br);
			if (index % 2 == 1) batch.draw(Assets.rect, Main.WIDTH-Main.SIZE-Main.WIDTH*sc-br, Main.HEIGHT/3.5f-br, Main.WIDTH*sc+2*br, Main.HEIGHT*sc+2*br);
			
			
			batch.setColor(Color.WHITE);
			batch.draw(Assets.level1,Main.SIZE,Main.HEIGHT/3.5f,Main.WIDTH*sc, Main.HEIGHT*sc);
			batch.draw(Assets.level2,Main.WIDTH-Main.SIZE-Main.WIDTH*sc,Main.HEIGHT/3.5f,Main.WIDTH*sc, Main.HEIGHT*sc);
			
			
			Assets.font.getData().setScale(2);
			Util.drawTitle(batch, Assets.font, "Press A to change level", Main.WIDTH/2,Main.HEIGHT*0.2f, 1);			
					
			if (t > 2 && t % 1 < 0.5f) {
				Assets.font.getData().setScale(2.5f);
				Util.drawTitle(batch, Assets.font, "Press Start to continue", Main.WIDTH/2,Main.HEIGHT*0.1f, 1);
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
