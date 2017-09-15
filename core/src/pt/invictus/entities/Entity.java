package pt.invictus.entities;

import java.util.Collection;
import java.util.Comparator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprite;
import pt.invictus.Tile;
import pt.invictus.Util;

public class Entity {

	public static final float DEFAULT_RADIUS = Main.SIZE/3;

	public Level level;
	
	public Sprite sprite;
	public Color blend, aux_color;
	public float alpha;
	public float damage_anim_timer, damage_anim_delay;
	public float bar_anim_timer, bar_anim_delay;
	public float anim_timer, anim_speed; 
	public int anim_index;
	
	public boolean visible;
	public boolean dead;
	public boolean invulnerable;
	public boolean remove;
	public boolean collisions, levelCollisions, entityCollisions, imovable;
	
	public float t;
	public float x, y, z;
	public float dx, dy;
	public float px, py;
	public float direction, speed, scale;
	public float radius;
	public float health, full_health;
	public float mass;
	
	public float edx, edy, edf;
	
	public float fade_anim_timer, fade_anim_delay;

	public Entity owner;
	
	

	public Entity(Level level) {
		this.level = level;
		level.addEntity(this);
	
		this.sprite = null;
		this.blend = Color.WHITE;
		this.aux_color = Color.WHITE;
		this.alpha = 1f;
		this.damage_anim_timer = 0;
		this.damage_anim_delay = 1f;
		this.bar_anim_timer = 0;
		this.bar_anim_delay = 3f;
		
		this.anim_index = 0;
		this.anim_timer = 0;
		this.anim_speed = 1;
		this.mass = 1;
		
		this.visible = true;
		this.dead = false;
		this.invulnerable = false;
		this.remove = false;
		this.collisions = true;
		this.levelCollisions = false;
		this.entityCollisions = false;
		this.imovable = false;
		
		this.t = 0;
		this.x = 0;
		this.y = 0;	
		this.z = 0;
		this.dx = 0;
		this.dy = 0;
		this.direction = 0;
		this.speed = 0;
		this.scale = 1;
		this.radius = DEFAULT_RADIUS;	
		this.health = 100;
		this.full_health = this.health;
		
		this.edx = 0;
		this.edy = 0;
		this.edf = 0.90f;
		
		
		this.fade_anim_timer = -1;
		this.fade_anim_delay = 1f;
		
		this.owner = null;
	}

	public void preupdate(float delta) {
		px = x;
		py = y;
	}
	
	public void update(float delta) {
		
		t += delta;
		
		damage_anim_timer = Util.stepTo(damage_anim_timer, 0, delta);
		bar_anim_timer = Util.stepTo(bar_anim_timer, 0, delta);
		
		if (fade_anim_timer > 0) fade_anim_timer = Util.stepTo(fade_anim_timer, 0, delta);
		if (fade_anim_timer == 0) remove = true;
		
		x += (Math.cos(direction)*speed + edx + dx)*delta;
		y += (Math.sin(direction)*speed + edy + dy)*delta;
	
		
		edx *= edf;
		edy *= edf;
	
		
		if (health <= 0 && !dead) die();
		
		if (sprite != null) {
			if (sprite.anim_delay > 0) {
				anim_timer += delta * anim_speed;
				while (anim_timer > sprite.anim_delay) {
					anim_index++;			
					anim_timer -= sprite.anim_delay;
				}
				if (!sprite.anim_loop && anim_index >= sprite.frames.size()) anim_index = sprite.frames.size()-1;
			} else {
				anim_index = 0;
			}
			anim_index %= sprite.frames.size();
		}
	}

	public static Color color = new Color();
	public void render(SpriteBatch batch) {		
		if (sprite != null && alpha > 0 && visible)	{
			
			color.set(dead ? Color.GRAY : blend);
			color.mul(1,1,1,alpha);
			if (damage_anim_timer > 0) {
				float s = damage_anim_timer / damage_anim_delay;
				color.mul(1,1-s,1-s,1);
			}
			if (fade_anim_timer >= 0) color.mul(1,1,1,Util.clamp(fade_anim_timer/fade_anim_delay,0,1));
			sprite.render(batch, anim_index, x, y, scale, scale, direction*Util.radToDeg, color);
		}
		
	}
	
	public void renderDebug(ShapeRenderer renderer) {
		if (collisions) {
			renderer.setColor(Color.WHITE);
			renderer.begin(ShapeType.Line);
			renderer.ellipse(this.x - this.radius, this.y - this.radius, 2*this.radius, 2*this.radius);
			renderer.end();
		}
	}
	
	public void die() {
		dead = true;
	}
	
	public void revive() {
		health = full_health;
		dead = false;
	}

	public void destroy() {
		
	}

	public void collide(Entity o) {
		if (entityCollisions && o.entityCollisions && !o.imovable && collidesWith(o)) {
			if (imovable) repelRigid(o);
			else repelForce(o,25);
		}		
	}

	public boolean collidesWith(Entity o) {
		return true; 
	}

	public void damage(float damage) {
		if (invulnerable) return;
		
		health = Util.stepTo(health, 0, damage);
		if (!dead) {
			damage_anim_timer = Math.max(damage_anim_timer, damage_anim_delay);
			bar_anim_timer = Math.max(bar_anim_timer, bar_anim_delay);
		}		
	}
	
	public void repelRigid(Entity o) {
		float dist = (radius + o.radius - Util.pointDistance(x, y, o.x, o.y));
		float dir = Util.pointDirection(x, y, o.x, o.y);
		
		o.x += dist/2 * Math.cos(dir);
		o.y += dist/2 * Math.sin(dir);
	}
	
	public void repelForce(Entity o, float force) {
		float ratio = (radius + o.radius - Util.pointDistance(x, y, o.x, o.y)) / (radius + o.radius);
		float dir = Util.pointDirection(x, y, o.x, o.y);
		
		o.x += ratio * force * Math.cos(dir);
		o.y += ratio * force * Math.sin(dir);
	}

	public Entity setPosition(float x, float y) {
		this.px = this.x = x; this.py = this.y = y; return this;		
	}
	public Entity addEVel(float x, float y) {
		this.edx += x; this.edy += y; return this;
	}
	public Entity addEVelDir(float dir, float val) {
		this.edx += (float) (val*Math.cos(dir)); 
		this.edy += (float) (val*Math.sin(dir)); 
		return this;
	}
	public Entity setBlend(Color blend) {
		this.blend = blend; return this;
	}
	public Entity setDirection(float direction) { 
		this.direction = direction; return this; 
	}
	public Entity setDVel(float dir, float val) {
		this.dx = (float) (val*Math.cos(dir));
		this.dy = (float) (val*Math.sin(dir));
		return this;
	}

	public void levelCollision() {
		if (!levelCollisions) return;
		
		int s = Main.SIZE;
		float nx = 0, ny = 0;
		
		x = Util.stepTo(px, x, radius-1);
		y = Util.stepTo(py, y, radius-1);
		
		
		// Calculate in which tile the player is in
		int xc = (int) Math.floor(this.x / s);
		int yc = (int) Math.floor(this.y / s);
		
		// Check if inside solid block to fix weird corner cases
		if (tileIsSolid(this.level.getTile(xc,yc))) {
			this.x = px;
			xc = (int) Math.floor(this.x / s);
		}
		
		// Calculate the player's position relative to the cell they're in
		float xr = this.x - (xc * s);
		float yr = this.y - (yc * s);
		
		// Check if bumping left
		if (tileIsSolid(this.level.getTile(xc-1,yc)) && xr <= this.radius) {
			xr = this.radius;
			nx = 1; ny = 0;
		}
		// Check if bumping right
		if (tileIsSolid(this.level.getTile(xc+1,yc)) && xr >= s - this.radius) {
			xr = s - this.radius;
			nx = -1; ny = 0;
		}
		// Update x. X-axis is now resolved
		this.x = (xc * s) + xr;		
		xc = (int) Math.floor(this.x / s);
		
		// Check if bumping up
		if (tileIsSolid(this.level.getTile(xc,yc-1)) && yr <= this.radius) {
			yr = this.radius;
			nx = 0; ny = 1;
		}
		
		// Check if bumping down
		if (tileIsSolid(this.level.getTile(xc,yc+1)) && yr >= s - this.radius) {
			yr = s - this.radius;	
			nx = 0; ny = -1;
		}		
		
		// Update y. Y-axis is now resolved
		this.y = (yc * s) + yr;
		
		if (nx != 0 || ny != 0) collideLevel(nx, ny);
	}
	
	public void collideLevel(float nx, float ny) {
		
	}

	public boolean tileIsSolid(Tile tile) {
		return tile == Tile.WALL;
	}
	
	public static Comparator<Entity> zComparator = new Comparator<Entity>(){
		@Override
		public int compare(Entity o1, Entity o2) {
			return Float.compare(o1.z, o2.z);
		}		
	};
	
	public interface EntityEvaluator {
		public boolean skip(Entity e);
	};
	public static EntityEvaluator aliveEvaluator = new EntityEvaluator() {
		@Override
		public boolean skip(Entity e) {
			return e.dead;
		}		
	};
	

	public static Entity findClosest(Collection<? extends Entity> entities, float x, float y, Class<? extends Entity> clazz, Entity exclude, EntityEvaluator eval) {
		Entity res = null;
		float res_dist = Float.MAX_VALUE;
		for(Entity e : entities) {
			if (exclude == e) continue;
			if (clazz != null && !clazz.isInstance(e)) continue;
			if (eval != null && eval.skip(e)) continue;			
			
			float dist = Util.pointDistanceSqr(x, y, e.x, e.y);
			if (dist < res_dist) {
				res_dist = dist;
				res = e;
			}
		}
		return res;
	}
	public static Entity findClosest(Collection<? extends Entity> entities, float x, float y, Class<? extends Entity> clazz) {
		return findClosest(entities, x,y,clazz,null, null);
	}
	
	public Entity findClosest(Collection<? extends Entity> entities, Class<? extends Entity> clazz, EntityEvaluator eval) {
		return Entity.findClosest(entities, x,y,clazz, this, eval);
	}
}