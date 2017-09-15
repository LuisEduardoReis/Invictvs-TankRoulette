package pt.invictus.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.particles.Explosion;
import pt.invictus.entities.player.Player;

public class Diamond extends Entity implements Respawnable {

	float respawn_timer, respawn_delay;
	float despawn_timer, despawn_delay;
	
	
	public Diamond(Level level) {
		super(level);
		level.pickups.add(this);
		
		respawn_delay = 60;
		respawn_timer = Util.randomRangef(0,respawn_delay);
		
		despawn_timer = 0;
		despawn_delay = 10;
		
		sprite = Sprites.diamond;
		
		scale = 1.5f;
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
		
		collisions = (respawn_timer == 0);
		
		alpha = Util.clamp(1-respawn_timer,0,1) * Util.clamp(despawn_delay - despawn_timer,0,1); 
	}
	
	@Override
	public void collide(Entity o) {
		super.collide(o);
		
		if (o instanceof Player && respawn_timer == 0) {
			Player p = (Player) o;			
			respawn_timer = respawn_delay;
			
			p.health = p.full_health;
			
			new Explosion(level).setDscale(2.5f*scale).setPosition(x, y).setBlend(new Color(0.5f,1,1,0.5f));
			
			Main.playSound(Assets.diamond);
		}
	}
	
	static Color color = new Color(); 
	@Override
	public void render(SpriteBatch batch) {
		
		Sprites.circle.render(batch, 0, x,y-Main.SIZE/2.5f, 1f,0.4f, 0, color.set(0,0,0,0.35f*alpha));
		sprite.render(batch, 0, x, y + 7*(float)(Math.sin(5*level.t)+1), scale*(float)Math.sin(2*level.t), scale, 0, color.set(1,1,1,alpha));
		
		/* if (Main.DEBUG) {
			Assets.font.draw(batch, "R:"+((int)respawn_timer), x,y+1.5f*Main.SIZE);
			Assets.font.draw(batch, "D:"+((int)despawn_timer), x,y+Main.SIZE);
		}*/
	}

	@Override
	public float getRespawnTimer() { return respawn_timer; }
}
