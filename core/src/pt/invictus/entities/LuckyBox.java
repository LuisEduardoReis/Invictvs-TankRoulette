package pt.invictus.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.particles.Shard;
import pt.invictus.entities.player.Player;

public class LuckyBox extends Entity {

	public float respawn_timer;
	float respawn_delay;
	float despawn_timer, despawn_delay;
	
	
	public LuckyBox(Level level) {
		super(level);
		level.pickups.add(this);
		
		respawn_delay = 25;
		respawn_timer = Util.randomRangef(0,respawn_delay);
		
		despawn_timer = 0;
		despawn_delay = 10;
		
		sprite = Sprites.luckybox;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		float dist = Float.MAX_VALUE;
		for(Entity e : level.entities) if (e instanceof Player) dist = Math.min(dist, Util.pointDistance(x,y,e.x,e.y));
		if (dist > 3*Main.SIZE || respawn_timer <= 1)
			respawn_timer = Util.stepTo(respawn_timer, 0, delta);
		
		if (respawn_timer > 0) 
			despawn_timer = 0;
		else {
			despawn_timer += delta;
			if (despawn_timer > despawn_delay) {
				respawn_timer = respawn_delay;
			}
		}
		
		alpha = Util.clamp(1-respawn_timer,0,1) * Util.clamp(despawn_delay - despawn_timer,0,1); 
	}
	
	@Override
	public void collide(Entity o) {
		super.collide(o);
		
		if (o instanceof Player && respawn_timer == 0) {
			Player p = (Player) o;
			p.item_timer = p.item_delay;
			
			respawn_timer = respawn_delay;
			
			for(int i = 0; i < 20; i++)
				new Shard(level, sprite.frames.get(0), 1).setPosition(x, y);
			
			Main.playSound(Assets.luckybox);
		}
	}
	
	static Color color = new Color(); 
	@Override
	public void render(SpriteBatch batch) {
		
		Sprites.circle.render(batch, 0, x,y-Main.SIZE/2.5f, 1.25f,0.5f, 0, color.set(0,0,0,0.35f*alpha));
		sprite.render(batch, 0, x, y + 7*(float)(Math.sin(5*level.t)+1), scale, scale, 0, color.set(1,1,1,alpha));
		
		/*if (Main.DEBUG) {
			Assets.font.getData().setScale(1);
			Assets.font.setColor(Color.WHITE);
			Assets.font.draw(batch, "R:"+((int)respawn_timer), x,y+1.5f*Main.SIZE);
			Assets.font.draw(batch, "D:"+((int)despawn_timer), x,y+Main.SIZE);
		}*/
	}
}
