package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.XBox360Pad;

public class MenuScreen extends ScreenAdapter {
	Main main;
	
	SpriteBatch batch;	
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Viewport viewport;
	
	float t;
	
	float fadein_timer, fadein_delay;
	float fadeout_timer, fadeout_delay;
	Runnable fadeout_action;
	
	public TiledMap map;
	public OrthogonalTiledMapRenderer tileRenderer;
	
	
	public MenuScreen(Main main) {
		this.main = main;
		
		t = 0;
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.translate(Main.WIDTH/2, Main.HEIGHT/2);
		viewport = new FitViewport(Main.WIDTH, Main.HEIGHT, camera);

		fadein_delay = 4;
		fadein_timer = fadein_delay;
		
		fadeout_timer = -1;
		fadeout_delay = 1;
		fadeout_action = null;
		
		map = new TmxMapLoader(new InternalFileHandleResolver()).load("title.tmx");
		tileRenderer = new OrthogonalTiledMapRenderer(map);
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
						main.setScreen(new ControlsScreen(main));
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
		
		tileRenderer.setView(camera);
		tileRenderer.render();
		
		batch.begin();
		
			// Roulettes
			batch.setColor(Color.WHITE);
			Util.drawCentered(batch, Assets.roulette, 270,Main.HEIGHT-270, 5*Main.SIZE, 5*Main.SIZE, 2*t*Util.radToDeg, true, true);
			Util.drawCentered(batch, Assets.roulette, Main.WIDTH-270,Main.HEIGHT-270, 5*Main.SIZE, 5*Main.SIZE, 2*t*Util.radToDeg, true, true);
			
			// Title
			batch.setColor(1,1,1,1);
			Util.drawCentered(batch, Assets.title, Main.WIDTH*0.4925f, Main.HEIGHT*(0.55f + Math.max(0, 2-t)), 3.5f*Assets.title.getRegionWidth(),3.5f*Assets.title.getRegionHeight(),0,true,true);
			
			
			if (t > 2 && t % 1 < 0.5f) {
				Assets.font.getData().setScale(2);
				Util.drawTitle(batch, Assets.font, "Press Start to play", Main.WIDTH/2,Main.HEIGHT*0.125f, 1);
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
