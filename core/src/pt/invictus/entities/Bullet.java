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

public class Bullet extends Entity {

	public int damage;
	public int explosion_damage;
	public int bounces;
	public int max_bounces;
	
	public Bullet(Level level, Entity owner) {
		super(level);
		this.owner = owner;
		damage = 25;
		explosion_damage = 0;
		
		sprite = Sprites.bullet;
		radius = Main.SIZE/2 * 0.25f;
		
		bounces = 0;
		max_bounces = 0;
		
		levelCollisions = true;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (x < -2*radius || y < -2*radius || x > Main.WIDTH+2*radius || y > Main.HEIGHT+2*radius) remove = true;
		
		if(dx*dx + dy*dy != 0) direction = Util.pointDirection(0, 0, dx, dy);
	}

	@Override
	public void collideLevel(float nx, float ny) {
		super.collideLevel(nx, ny);
		
		if (nx != 0) { dx = -dx; bounces++; }
		if (ny != 0) { dy = -dy; bounces++; }
		
		if (bounces > max_bounces) remove = true;
		
		for(int i = 0; i < 25; i++) {
			Spark e = (Spark) new Spark(level).setDscale(0).setPosition(x, y);
			e.scale = 0.25f*scale;
			float dark = Util.randomRangef(0.75f, 1);
			e.blend = new Color(1*dark,0.5f*dark,0,1f);
			e.dscale = Util.randomRangef(0, 0.5f)*scale;
			e.lifetime = Util.randomRangef(0.5f, 1f);
			float f = Util.randomRangef(100, 350)*scale;
			float d = Util.pointDirection(0, 0, nx, ny) + Util.randomRangef(-0.75f,0.75f);
			e.addEVel((float)(f*scale * Math.cos(d)),(float)(f*scale * Math.sin(d)));
			e.edf = Util.randomRangef(0.875f, 0.9f);
		}
		
		Main.playSound(Assets.ricochet);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		if (explosion_damage > 0) {
			Explosion.explosion(x, y, level, 0.5f);
			Bomb.explosion(x, y, level, owner, 0.5f, explosion_damage);
		}
	}
	
	
	@Override
	public void collide(Entity o) {
		super.collide(o);
		
		if (o instanceof Player && !o.dead && o != owner) {
			o.damage(damage);
			float dir = Util.pointDirection(x, y, o.x, o.y);
			float force = 250;
			o.addEVel(force*(float)Math.cos(dir), force*(float)Math.sin(dir));
			
			remove = true;
		}
	}
}
