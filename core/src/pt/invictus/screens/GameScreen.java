package pt.invictus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;

import pt.invictus.Assets;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.ai.Node;
import pt.invictus.ai.TargetSolutionMap;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;
import pt.invictus.entities.player.*;
import pt.invictus.items.Item;

public class GameScreen extends DefaultScreen {
	String level_name;
	
	Level level;
	boolean paused;
	float pause_index;
	
	public float start_timer, start_delay;
	public float victory_timer, victory_delay;

	public static String place_names[] = {"1st","2nd","3rd","4th"};
	
	
	public GameScreen(Main main, String level_name) {
		super(main);
		this.level_name = level_name;
		
		start_delay = 3;
		start_timer = start_delay;	
		
		victory_timer = -1;
		victory_delay = 10;
		
		background_color = Color.GRAY;
	}
	
	@Override
	public void show() {
		super.show();		
		
		level = new Level(this, level_name);
		paused = false;		
		
		int i = 0;
		int n = main.ai_players+main.controllers.size();
		for(GameController controller : main.controllers) new HumanPlayer(level, controller, i++);
		for(; i < n; i++) new AIPlayer(level, i);
		
		int so = Util.randomRangei(level.spawns.size());
		for(Player p : level.players) {
			Vector2 v = level.spawns.get((p.index+so)%level.spawns.size());
			p.setPosition(v.x, v.y);
			p.setDirection(Util.randomRangef(0, 2*(float)Math.PI));
		}
		
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (fadein_timer == 0) start_timer -= delta;
		if (victory_timer > 0) victory_timer = Util.stepTo(victory_timer, 0, delta);
			
		if (!paused) level.update(delta);
		pause_index = Util.stepTo(pause_index, paused ? 1 : 0, 2*delta);
		
		// Pause
		for(GameController controller : main.controllers) {				
			// Start
			if (controller.getKeyPressed(Key.START)) {
				if (victory_timer < 0) {
					paused ^= true;
					if (!paused) Main.playSound(Assets.itempickup);
					else Main.playSound(Assets.dropbomb);
				}
				else {
					fadeout_timer = fadeout_delay;
					fadeout_action = new Runnable() {
						@Override
						public void run() {
							Assets.slotmachine.stop();
							main.setScreen(new MenuScreen(main));
						}
					}; 
				}
			}
							
			// Back
			if (controller.getKeyPressed(Key.BACK)) {
				fadeout_timer = fadeout_delay;
				fadeout_action = new Runnable() {
					@Override
					public void run() {
						Assets.slotmachine.stop();
						main.setScreen(new MenuScreen(main));
						
					}
				}; 
				break;
			}
		}
	}
	
	static Color color = new Color();
	@Override
	public void display() {
		super.display();
		
		level.tileRenderer.setView(camera);
		level.tileRenderer.render();
		
		batch.setColor(Color.WHITE);
		batch.begin();
			level.renderEntities(batch);
		batch.end();
		
		// UI
		batch.begin();
			float s = Main.SIZE;
			for(int i = 0; i < level.players.size(); i++) {
				Player p = level.players.get(i);
				
				if (p.health > 0) {
					float dmg = p.damage_anim_timer / p.damage_anim_delay;
					color.set(1,1-dmg,1-dmg,1);
				} else
					color.set(Color.DARK_GRAY);
				
				float x = s + i*Main.WIDTH/level.players.size();
				
				// item indicator
				float ix = x + 1.5f*s;
				float iy = 2*s;
				Sprites.circle.render(batch, 0, ix, iy, 2.5f,2.5f, 0, Color.GRAY);
				Sprites.circle.render(batch, 0, ix, iy, 2.2f,2.2f, 0, Color.BLACK);
				
				
				if (p.item_timer > 0) {
					float period = Util.interpolate(p.item_timer/p.item_delay,0.25f,0.10f);
					int ii = (int)( (p.item_timer / period) % Item.items.size());
					
					Item.items.get(ii).render(batch, ix, iy);
				} else if (p.item != null)
					p.item.render(batch, ix, iy);
				
				// tank				
				p.sprite.render(batch, 0, x,2.5f*s,  2,2, 90, color);
				
				// knockout
				if (p.dead && p.lives == 0) {
					batch.setColor(Color.RED);
					Util.drawCentered(batch, Assets.rect, x,2.5f*s, 100, 20, 45, true, true);
					Util.drawCentered(batch, Assets.rect, x,2.5f*s, 100, 20, -45, true, true);
				}
				
				// health
				float w = 250;
				batch.setColor(Color.GRAY);				
				Util.drawCentered(batch, Sprites.rect.frames.get(0), x+2.5f*s-6, 2.85f*s, w+12, 46, 0, false, true);
				batch.setColor(Player.player_colors[p.index % Player.player_colors.length]);
				Util.drawCentered(batch, Sprites.rect.frames.get(0), x+2.5f*s, 2.85f*s, w*p.s_health/p.full_health, 30, 0, false, true);
				
				// lives
				for(int li = 0; li < p.lives; li++)
					p.sprite.render(batch, 0, x+(3.25f+0.75f*li)*s, 2f*s,  0.75f,0.75f, 90, Color.WHITE);
			}
			
			
			// Start
			if (start_timer > -2) {
				String message = "";
				float a;
				if (Util.between(start_timer, 2, 2.99f)) message = "3";
				if (Util.between(start_timer, 1, 2)) message = "2";
				if (Util.between(start_timer, 0, 1)) message = "1";
				if (start_timer < 0) message = "Fight!";
				
				Assets.font.getData().setScale(7);
				if (start_timer > 0) a = Util.clamp(2*(start_timer % 1),0,1);
				else a = Util.clamp(2*(1-Math.abs(1+start_timer)),0,1);
				Util.drawTitle(batch, Assets.font, message, Main.WIDTH/2,Main.HEIGHT*0.65f, a);
			}			
			
			// Pause			
			if (pause_index != 0) {
				batch.setColor(0,0,0,pause_index*0.75f);
				batch.draw(Assets.fillTexture,0,0,Main.WIDTH,Main.HEIGHT);
				Assets.font.getData().setScale(5);
				Util.drawTitle(batch, Assets.font, t % 1 > 0.5f ? "<Paused>" : " Paused ", Main.WIDTH/2,Main.HEIGHT*0.65f, pause_index);
				
				Assets.font.getData().setScale(2);
				Util.drawTitle(batch, Assets.font, "Press Start to continue.", Main.WIDTH/2,Main.HEIGHT*0.25f, pause_index);
				Assets.font.getData().setScale(1.75f);
				Util.drawTitle(batch, Assets.font, "Press Back to restart.", Main.WIDTH/2,Main.HEIGHT*0.125f, pause_index);
			}

			
			// Victory
			if (victory_timer >= 0) {
				float a = Util.clamp(1-(victory_timer/victory_delay),0,1);
				
				batch.setColor(0,0,0,0.5f*a);
				batch.draw(Assets.fillTexture, 0, 0);
				
				batch.setColor(0,0,0,0.75f*a);
				Util.drawCentered(batch,Assets.rect,Main.WIDTH/2,Main.HEIGHT*0.6f,Main.WIDTH * 0.33f, Main.HEIGHT * 0.66f, 0,true,true);				

				Assets.font.getData().setScale(4);
				Util.drawTitle(batch, Assets.font, "Results", Main.WIDTH/2,Main.HEIGHT*0.85f, a);
				
				for(int i = 0; i < level.ranking.size(); i++) {
					Player p = level.ranking.get(level.ranking.size()-1-i);
					float x = Main.WIDTH/4 + 6*s;
					float y = Main.HEIGHT*0.7f - 2*s*i - (i > 0 ? 0.5f*s : 0);
					float sc = (i == 0) ? 4 : 2.5f;
					
					if (i == 0) sc += 1*Math.sin(4*t);
					Assets.font.getData().setScale(sc); 
					Util.drawTitle(batch, Assets.font, place_names[i % place_names.length], x,y, a);
					
					p.sprite.render(batch, 0,  x + 5 *s,y-0.15f*s,  sc,sc, (i == 0 ? 90 : 0), color.set(1,1,1,a));
				}
				
				Assets.font.getData().setScale(2);
				Util.drawTitle(batch, Assets.font, "Press Start to play again", Main.WIDTH/2,Main.HEIGHT*0.15f, a);
			}
			
		batch.end();
		
		
		if (Main.DEBUG) {
			Ray ray = viewport.getPickRay(Gdx.input.getX(),Gdx.input.getY());
			
			batch.begin();
				Assets.font.setColor(Color.WHITE);
				Assets.font.getData().setScale(1);
				
				Assets.font.draw(batch,level.entities.size()+"",100,Main.HEIGHT);
				//Assets.font.draw(batch,Math.floor(ray.origin.x/s)+" "+Math.floor(ray.origin.y/s),100,Main.HEIGHT);

				Assets.font.draw(batch,Gdx.graphics.getFramesPerSecond()+"",100,Main.HEIGHT-30);
				
			batch.end();
			
			if (Gdx.input.isKeyPressed(Input.Keys.P)) {
			Util.pushMatrix(shapeRenderer.getTransformMatrix());
			shapeRenderer.scale(s,s,1);
			shapeRenderer.translate(0.5f, 0.5f, 0);
			shapeRenderer.begin(ShapeType.Filled);
				TargetSolutionMap map = level.getTargetSolutionMap((int)(ray.origin.x/s), (int)(ray.origin.y/s));
				if (map != null) {
					for(int y = 0; y < level.map_height; y++) {
						for(int x = 0; x < level.map_width; x++) {
							Node n = map.getNode(x, y);
							if (n == null) continue;
							if (n.parent == null) continue;
							float v = 1f-n.distance/30;
							shapeRenderer.setColor(0, v, 0, 1);
							Util.drawWidthLine(shapeRenderer, n.x, n.y, n.parent.x, n.parent.y, 3/s);
						}
					}
				}
			shapeRenderer.end();
			shapeRenderer.setTransformMatrix(Util.popMatrix());
			}
			
			level.renderDebug(shapeRenderer);	
		}
	}

	
}
