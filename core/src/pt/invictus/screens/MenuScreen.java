package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;
import pt.invictus.entities.player.Player;

public class MenuScreen extends DefaultScreen {

	
	public TiledMap map;
	public OrthogonalTiledMapRenderer tileRenderer;
	
	
	public MenuScreen(Main main) {
		super(main);

		fadein_delay = 4;
		fadein_timer = fadein_delay;
		
		fadeout_timer = -1;
		fadeout_delay = 1;
		
		map = new TmxMapLoader(new InternalFileHandleResolver()).load("title.tmx");
		tileRenderer = new OrthogonalTiledMapRenderer(map);
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
						main.setScreen(new ControlsScreen(main));
					}
				}; 
			}
		}
	}
	
	@Override
	public void display() {
		super.display();
		
		tileRenderer.setView(camera);
		tileRenderer.render();
		
		batch.begin();
			
			batch.setColor(Color.WHITE);
		
			// Roulettes			
			Util.drawCentered(batch, Assets.roulette, 270,Main.HEIGHT-270, 5*Main.SIZE, 5*Main.SIZE, 2*t*Util.radToDeg, true, true);
			Util.drawCentered(batch, Assets.roulette, Main.WIDTH-270,Main.HEIGHT-270, 5*Main.SIZE, 5*Main.SIZE, -2*(t+1)*Util.radToDeg, true, true);
			
			// Title
			Util.drawCentered(batch, Assets.title, Main.WIDTH*0.4925f, Main.HEIGHT*(0.55f + Math.max(0, 2-t)), 3.5f*Assets.title.getRegionWidth(),3.5f*Assets.title.getRegionHeight(),0,true,true);
			
			
			// Credits
			if (t > 3) {
				Assets.font.getData().setScale(1);
				float x = Main.WIDTH/2, y = 178;
				float a = Util.clamp((t-3)/2, 0, 1);
				batch.setColor(0,0,0,0.4f*a);
				Util.drawCentered(batch, Assets.rect, Main.WIDTH/2, 105, 475, 200, 0, true,true);
				Assets.font.setColor(1,1,1,a);
				Util.drawTextCentered(batch, Assets.font, "Credits:", x, y); y-=50;
				Util.drawTextCentered(batch, Assets.font, "Luis Eduardo Reis", x, y); y-=40;
				Util.drawTextCentered(batch, Assets.font, "Raquel Magalhães Correia", x, y); y-=50;
				Util.drawTextCentered(batch, Assets.font, "Sept 2017", x, y);
			}
			// Press play
			if (t > 1 && t % 1 < 0.5f) {
				Assets.font.getData().setScale(2);
				batch.setColor(0,0,0,0.6f);
				float y = 280;
				Util.drawCentered(batch, Assets.rect, Main.WIDTH/2, y-5, 1000, 80, 0, true,true);
				Util.drawTitle(batch, Assets.font, "Press Start/Space to play", Main.WIDTH/2,y, 1);
			}
			
		batch.end();
	}	

}
