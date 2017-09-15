package pt.invictus.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.particles.BankNote;
import pt.invictus.entities.particles.Explosion;
import pt.invictus.entities.particles.Shard;
import pt.invictus.entities.particles.Spark;

public class MoneyBag extends Entity implements Respawnable {
	
	public static final int EXPLOSION_RADIUS = Bomb.EXPLOSION_RADIUS;
	
	float respawn_timer, respawn_delay;

	public MoneyBag(Level level) {
		super(level);
		level.moneybags.add(this);
	
		sprite = Sprites.moneybag;
		radius = Main.SIZE/2;
		
		imovable = true;		
		
		respawn_timer = 0;
		respawn_delay = 10;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		respawn_timer = Util.stepTo(respawn_timer, 0, delta);
		
		alpha = Util.clamp(1 - respawn_timer, 0, 1);
		
		entityCollisions = (respawn_timer == 0);
		collisions = (respawn_timer == 0);
	}
	
	@Override
	public void collide(Entity o) {
		super.collide(o);
		
		if (respawn_timer == 0 && (o instanceof Bullet || o instanceof Missile)) {
			o.remove = true;
			
			respawn_timer = respawn_delay;
			
			for(int i = 0; i < 20; i++)
				new Shard(level, sprite.frames.get(0), 1).setPosition(x, y);
			
			for(int i = 0; i < 40; i++) {
				BankNote e = (BankNote) new BankNote(level).setDscale(0).setPosition(x, y);
								
				e.lifetime = Util.randomRangef(0.5f, 1f);
				float f = Util.randomRangef(250, 500)*scale;
				float d = Util.randomRangef(0, (float) (2*Math.PI));
				e.addEVel((float)(f*scale * Math.cos(d)),(float)(f*scale * Math.sin(d)));
				e.edf = Util.randomRangef(0.9f, 0.95f);
			}
			
			for(int i = 0; i < 100; i++) {
				Spark e = (Spark) new Spark(level).setDscale(0).setPosition(x, y);
				
				e.scale = 0.5f*scale;
				float dark = Util.randomRangef(0.5f, 1);
				e.blend = new Color(0,0.5f*dark,0,0.5f);
				e.dscale = Util.randomRangef(0, 0.5f)*scale;
				e.lifetime = Util.randomRangef(0.5f, 1f);
				float f = Util.randomRangef(500, 750)*scale;
				float d = Util.randomRangef(0, (float) (2*Math.PI));
				e.addEVel((float)(f*scale * Math.cos(d)),(float)(f*scale * Math.sin(d)));
				e.edf = Util.randomRangef(0.9f, 0.95f);
			}
			
			new Explosion(level).setDscale(7.5f*scale).setPosition(x, y).setBlend(new Color(0,0.5f,0,0.5f));
			new Explosion(level).setDscale(7*scale).setPosition(x, y).setBlend(new Color(0,0.5f,0,0.5f));
			
			Bomb.explosion(x, y, level, o.owner, 1, 35);
			
			Main.playSound(Assets.moneybag);
		}
 	}

	@Override
	public void render(SpriteBatch batch) {
		Sprites.circle.render(batch, 0, x,y-Main.SIZE/2.5f, 1.25f,0.5f, 0, color.set(0,0,0,0.35f*alpha));
		
		super.render(batch);	
	}

	@Override
	public float getRespawnTimer() { return respawn_timer; }
}
