package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Util;

public class DefaultScreen extends ScreenAdapter {

	public Main main;
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public OrthographicCamera camera;
	public Viewport viewport;
	public float t;
	public float fadein_timer;
	public float fadein_delay;
	public float fadeout_timer;
	public float fadeout_delay;
	public Runnable fadeout_action;
	public Color background_color;

	public DefaultScreen(Main main) {
		this.main = main;
		
		fadein_delay = 3;
		fadein_timer = fadein_delay;
		
		fadeout_timer = -1;
		fadeout_delay = 1;
		fadeout_action = null;
		
		background_color = Color.BLACK;
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.translate(Main.WIDTH/2, Main.HEIGHT/2);
		viewport = new FitViewport(Main.WIDTH, Main.HEIGHT, camera);
	}

	@Override
	public final void render(float delta) {
		update(delta);
		
		display();
		
		batch.begin();
			if (fadein_timer > 0) {
				batch.setColor(0,0,0,fadein_timer/fadein_delay);
				batch.draw(Assets.fillTexture,0,0,Main.WIDTH,Main.HEIGHT);
			}			
			if (fadeout_timer >= 0) {
				batch.setColor(0,0,0,1-(fadeout_timer/fadeout_delay));
				batch.draw(Assets.fillTexture,0,0,Main.WIDTH,Main.HEIGHT);
			}
		batch.end();
	}
	
	
	public void update(float delta) {
		t += delta;
		
		if (fadein_timer > 0) fadein_timer = Util.stepTo(fadein_timer, 0, delta);
		
		if (fadeout_timer > 0) fadeout_timer = Util.stepTo(fadeout_timer, 0, delta);
		
		if (fadeout_timer == 0) if (fadeout_action != null) fadeout_action.run();				
	}	
	
	public void display() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClearColor(background_color.r, background_color.g, background_color.b, background_color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		camera.update();
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);		
	}
	

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		viewport.update(width, height);
	}

}