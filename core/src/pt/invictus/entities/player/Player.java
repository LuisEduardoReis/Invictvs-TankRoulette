package pt.invictus.entities.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import pt.invictus.Assets;
import pt.invictus.ColorUtils;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Tile;
import pt.invictus.Util;
import pt.invictus.entities.Bullet;
import pt.invictus.entities.Entity;
import pt.invictus.entities.particles.Explosion;
import pt.invictus.entities.particles.Shard;
import pt.invictus.items.Item;

public class Player extends Entity {

	public int index;
	
	public int lives;
	public float respawn_timer, respawn_delay;
	public float item_timer, item_delay;
	
	public float gun_timer, gun_delay;
	public float star_timer, star_delay;
	
	public Item item;
	
	public float bullet_speed;
	public float recoil;
	public float maxspeed;
	public float s_health;
	
	float look_norm, look_dir;
	float trottle_val;
	boolean fire_pressed, aux_pressed;
	
	
	public static Color[] player_colors = {
			new Color(1,0,0,1),
			new Color(0,38f/255,1,1),
			new Color(0,168f/255,92f/255,1),
			new Color(178f/255,0,1,1),
	};
	
	public Player(Level level, int i) {
		super(level);
		level.players.add(this);
		
		this.index = i;
		
		sprite = Sprites.player[i % Sprites.player.length];		
		
		levelCollisions = true;
		entityCollisions = true;
		radius = 26;
		
		item = null;
		item_timer = -1;
		item_delay = 4*0.655f + 0.1f;
		
		lives = 3;
		respawn_timer = -1;
		respawn_delay = 5;
		
		gun_timer = 0;
		gun_delay = 1;
		
		star_timer = 0;
		star_delay = 10;
		
		bullet_speed = 400;
		recoil = 150;
		maxspeed = 250;
		
		speed = 0;
		anim_speed = 0;
		
		s_health = 0;
		
		blend = new Color().set(Color.WHITE);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		damage_anim_timer = Util.stepTo(damage_anim_timer, 0, delta);
		gun_timer = Util.stepTo(gun_timer, 0, delta * (star_timer > 0 ? 2.5f : 1));
		star_timer = Util.stepTo(star_timer, 0, delta);
		
		if (level.game.start_timer <= 0) {
			if (s_health > health) s_health = Util.stepTo(s_health, health, 100*delta);
			if (s_health < health) s_health = Util.stepTo(s_health, health, 2*100*delta);
		}
		
		if (item_timer > 0) item_timer = Util.stepTo(item_timer, 0, delta);
		if (item_timer == 0) {
			item_timer = -1;
			getItem();
		}
					
		if (!dead && level.game.start_timer < 0 && level.game.victory_timer < 0) {
			float deadzone = 0.25f;
			
			float t_direction = direction;
			if (look_norm > deadzone) { 
				t_direction = look_dir;
				
				direction = Util.stepToDirection(direction, t_direction, (float) (2*Math.PI*delta)*0.5f);
			}
			
			if (Math.abs(trottle_val) > deadzone) { 
				speed = (star_timer > 0 ? 1.25f : 1) * maxspeed * Math.abs(trottle_val);
			} else
				speed = 0;
			
			
			if (speed != 0) {
				anim_speed = Math.abs(speed)/maxspeed;
			} else if (t_direction != direction)
				anim_speed = 1;
			else
				anim_speed = 0;
				
			
			if (fire_pressed) {
				// Shoot
				if (gun_timer == 0) {
					gun_timer = gun_delay;
					
					if (item != null)
						// Use item
						useItem();
					else {
						// Normal Shot
						Bullet b = (Bullet) new Bullet(level,this).setPosition((float) (x + (0.9f*radius)*Math.cos(direction)),(float) (y + (0.9f*radius)*Math.sin(direction)));
						b.setDVel(direction, bullet_speed);
						
						if (star_timer == 0) addEVel((float) (-recoil*Math.cos(direction)), (float) (-recoil*Math.sin(direction)));
						
						Main.playSound(Assets.shoot);
					}					
				}
			}
		
			
			// Button X
			if (aux_pressed) {
				if (Main.DEBUG) getItem();
			}
			
		
		} else {
			// Dead
			speed = 0;
			anim_speed = 0;
			
			if (respawn_timer > 0) 
				respawn_timer = Util.stepTo(respawn_timer, 0, delta);
			if (respawn_timer == 0 && lives > 0) {
				lives--;
				respawn();
				respawn_timer = -1;
			}
		}
		
		visible = !dead;
		entityCollisions = !dead;
		
		if (star_timer > 0) {
			ColorUtils.HSV_to_RGB(blend,(2*level.t*360)%360f, 100, 100);
			scale = 1 + 0.075f*((float)Math.sin(2*2*Math.PI*t)+1);
		} else
			blend.set(Color.WHITE);
	}
	
	public void respawn() {
		health = full_health;
		Vector2 s = level.spawns.get(Util.randomRangei(level.spawns.size()));
		setPosition(s.x, s.y);
		dead = false;
		levelCollisions = true;
		
		new Explosion(level).setDscale(4f*scale).setPosition(x, y).setBlend(player_colors[index % player_colors.length]);
		
		Main.playSound(Assets.itempickup);
	}
	
	public void spawnRandomly() {
		float sx, sy;
		do {
			sx = 1+Util.randomRangei(level.map_width-2);
			sy = 1+Util.randomRangei(level.map_height-2);
		} while(level.getTile((int)sx,(int)sy) != Tile.GROUND);
		sx = Main.SIZE*(sx+0.5f);
		sy = Main.SIZE*(sy+0.5f);
		
		setPosition(sx, sy);
		setDirection(Util.randomRangef(0, 2*(float)Math.PI));
	}

	public void useItem() {					
		item.use(this);
		
		item.quantity--;
		if (item.quantity <= 0) item = null;
	}

	public void getItem() {
		Main.playSound(Assets.itempickup);
		
		try {
			item = Item.items.get(Util.randomRangei(Item.items.size())).getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void die() {
		super.die();
		
		if (lives > 0) 
			respawn_timer = respawn_delay;
		else if (level.game.victory_timer < 0)
			level.ranking.add(this);
		
		item = null;
		item_timer = -1;
		
		for(int i = 0; i < 35; i++)
			new Shard(level, sprite.frames.get(0), 2).setPosition(x, y);
		Explosion.explosion(x,y,level,0.5f);
		
		Main.playSound(Assets.explosion);
	}
	
	@Override
	public void damage(float damage) {
		if (star_timer > 0) return;
		super.damage(damage);
		Main.playSound(Assets.hurt);
	}
	
	@Override
	public void renderDebug(ShapeRenderer renderer) {
		super.renderDebug(renderer);
	}

}
