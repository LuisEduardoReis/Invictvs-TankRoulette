package pt.invictus.entities;

import com.badlogic.gdx.graphics.Color;

import pt.invictus.Assets;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.particles.Explosion;
import pt.invictus.entities.particles.Spark;
import pt.invictus.entities.player.Player;

public class Bomb extends Entity {

	float t, lifetime;
	
	Entity owner;	
	
	public Bomb(Level level, Entity owner) {
		super(level);
		this.owner = owner;
		
		sprite = Sprites.bomb;
		
		t = 0;
		lifetime = 2;
		
		z = -1;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		t += delta;
		if (t > lifetime) remove = true;
		direction = 0;
		
		if (Math.random() < 0.75f) {
			Spark e = (Spark) new Spark(level).setDscale(0).setPosition(x+4, y+24);
			e.scale = 0.1f*scale;
			e.blend = Color.ORANGE;
			e.dscale = Util.randomRangef(0, 0.5f)*scale;
			e.lifetime = Util.randomRangef(0.5f, 1f);
			float f = Util.randomRangef(10, 35)*scale;
			float d = Util.randomRangef(0, (float) (2*Math.PI));
			e.addEVel((float)(f*scale * Math.cos(d)),(float)(f*scale * Math.sin(d)));
			e.edf = Util.randomRangef(0.875f, 0.9f);
		}		
	}
	
	
	@Override
	public void destroy() {
		super.destroy();
	
		Explosion.explosion(x, y, level, scale);
		explosion(x,y,level,owner,1,35);
	}

	public static void explosion(float x, float y, Level level, Entity owner, float scale, int damage) {
		
		float range = scale*4*Main.SIZE;
		for(Player p : level.players) {
			if (p == owner) continue;
			
			// Effect
			
			float dist = Math.max(p.radius,Util.pointDistance(x, y, p.x, p.y));
				
			if (dist > range) continue;								
			
			float ratio = 1f - (dist/range);
			float force = 250 * scale;
			float dmg = damage * ratio;
			
			p.addEVel(force*(p.x-x)/dist, force*(p.y-y)/dist);
			p.damage(dmg);
		}
		
		Main.playSound(Assets.explosion);
	}
}
