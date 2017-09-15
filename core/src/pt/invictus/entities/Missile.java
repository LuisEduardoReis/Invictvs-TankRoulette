package pt.invictus.entities;

import pt.invictus.Level;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.particles.Explosion;
import pt.invictus.entities.player.Player;

public class Missile extends Entity {
	
	public static final float MISSILE_RADIUS = Entity.DEFAULT_RADIUS;
	float damage;

	public Missile(Level level, Entity owner) {
		super(level);
		
		this.owner = owner;
		
		sprite = Sprites.missile;
		damage = 25;
		
		levelCollisions = true;
	}

	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		Player tp = null;
		float tdist = Float.MAX_VALUE;
		for(Player p : level.players) {
			if (p == owner || p.dead) continue;
			float dist = Util.pointDistance(x, y, p.x, p.y);
			if (dist < tdist) {
				tdist = dist;
				tp = p;
			}
		}
		if (tp != null) {
			float tdir = Util.pointDirection(x, y, tp.x, tp.y);
			direction = Util.stepToDirection(direction, tdir, (float) (2*Math.PI*delta)*0.75f);
		}
	}
	
	@Override
	public void collideLevel(float nx, float ny) {
		super.collideLevel(nx, ny);
		
		remove = true;
	}
	
	@Override
	public void collide(Entity o) {
		super.collide(o);
		
		if (o instanceof Player && o != owner) {
			o.damage(damage);
			float dir = Util.pointDirection(x, y, o.x, o.y);
			float force = 250;
			o.addEVel(force*(float)Math.cos(dir), force*(float)Math.sin(dir));
			
			remove = true;
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		Explosion.explosion(x, y, level, 0.5f);
	}
}
