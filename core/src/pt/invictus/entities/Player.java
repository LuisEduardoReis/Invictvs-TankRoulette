package pt.invictus.entities;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import pt.invictus.Assets;
import pt.invictus.ColorUtils;
import pt.invictus.Item;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.XBox360Pad;
import pt.invictus.entities.particles.Explosion;
import pt.invictus.entities.particles.Shard;

public class Player extends Entity {

	public int index;
	public Controller controller;
	public boolean a_down, x_down, start_down;
	
	public int lives;
	public float respawn_timer, respawn_delay;
	public float item_timer, item_delay;
	
	public float gun_timer, gun_delay;
	public float star_timer, star_delay;
	
	public Item item;
	
	float bullet_speed;
	float recoil;
	float maxspeed;
	public float s_health;
	
	
	public static Color[] player_colors = {
			new Color(1,0,0,1),
			new Color(0,38f/255,1,1),
			new Color(0,168f/255,92f/255,1),
			new Color(178f/255,0,1,1),
	};
	
	public Player(Level level, Controller c, int i) {
		super(level);
		level.players.add(this);
		
		this.index = i;
		this.controller = c;
		
		sprite = Sprites.player[i];		
		
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
					
		if (controller != null && !dead && level.game.start_timer < 0 && level.game.victory_timer < 0) {
			float lax = controller.getAxis(XBox360Pad.AXIS_LEFT_X);
			float lay = -controller.getAxis(XBox360Pad.AXIS_LEFT_Y);					
			float trigger = controller.getAxis(XBox360Pad.AXIS_LEFT_TRIGGER);
			
			float deadzone = 0.25f;
			
			float t_direction = direction;
			if (Math.abs(lax*lax + lay*lay) > deadzone*deadzone) { 
				t_direction = Util.pointDirection(0, 0, lax, lay);
				
				direction = Util.stepToDirection(direction, t_direction, (float) (2*Math.PI*delta)*0.5f);
			}
			
			if (Math.abs(trigger) > deadzone) { 
				speed = (star_timer > 0 ? 1.25f : 1) * maxspeed * Math.abs(trigger);
			} else
				speed = 0;
			
			if (t_direction != direction)
				anim_speed = 1;
			else
				anim_speed = Math.abs(speed)/maxspeed;
			
			if (controller.getButton(XBox360Pad.BUTTON_A)) {
				if (a_down == false) {
					if (gun_timer == 0) {
						if (item != null) 
							useItem();
						else {
							// Normal Shot
							Bullet b = (Bullet) new Bullet(level,this).setPosition((float) (x + radius*Math.cos(direction)),(float) (y + radius*Math.sin(direction)));
							b.dx = (float) (bullet_speed*Math.cos(direction));
							b.dy = (float) (bullet_speed*Math.sin(direction));
							
							if (star_timer == 0) addEVel((float) (-recoil*Math.cos(direction)), (float) (-recoil*Math.sin(direction)));
							
							Main.playSound(Assets.shoot);
						}
						gun_timer = gun_delay;
					}
				}
				a_down = true;
			} else
				a_down = false;
			
			// Button X
			if (controller.getButton(XBox360Pad.BUTTON_X)) {
				if (x_down == false) {
					if (Main.DEBUG) getItem();
				}
				x_down = true;
			} else
				x_down = false;
		
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
		
		new Explosion(level).setDscale(4f*scale).setPosition(s.x, s.y).setBlend(player_colors[index]);
		
		Main.playSound(Assets.itempickup);
	}

	public void useItem() {
		float bspeed;
		Bullet b;
					
		switch(item.type) {
		case BOUNCE_BULLET:
			b = (Bullet) new Bullet(level,this).setPosition((float) (x + radius*Math.cos(direction)),(float) (y + radius*Math.sin(direction)));
			b.dx = (float) (bullet_speed*Math.cos(direction));
			b.dy = (float) (bullet_speed*Math.sin(direction));
			b.max_bounces = 3;
			
			addEVel((float) (-recoil*Math.cos(direction)), (float) (-recoil*Math.sin(direction)));
			
			Main.playSound(Assets.shoot);
			break;
		case TRIPLE_BULLET:
			float br = 10*Util.degToRad;
			for(int i = 0; i < 3; i++) {
				b = (Bullet) new Bullet(level,this).setPosition((float) (x + radius*Math.cos(direction)),(float) (y + radius*Math.sin(direction)));
				b.dx = (float) (bullet_speed*Math.cos(direction+br*(i-1)));
				b.dy = (float) (bullet_speed*Math.sin(direction+br*(i-1)));
			}
			addEVel((float) (-recoil*Math.cos(direction)), (float) (-recoil*Math.sin(direction)));
			
			Main.playSound(Assets.shoot);
			break;
		case BIG_BULLET:
			bspeed = 400;
			b = (Bullet) new Bullet(level,this).setPosition((float) (x + radius*Math.cos(direction)),(float) (y + radius*Math.sin(direction)));
			b.dx = (float) (bspeed*Math.cos(direction));
			b.dy = (float) (bspeed*Math.sin(direction));
			b.scale *= 2;
			b.radius *= 2;
			b.damage = 15;
			b.explosion_damage = 20;
			
			addEVel((float) (-1.5f*recoil*Math.cos(direction)), (float) (-1.5f*recoil*Math.sin(direction)));
			
			Main.playSound(Assets.shoot);
			break;			
		case BOMB:
			new Bomb(level, this).setPosition(x,y);
			
			Main.playSound(Assets.dropbomb);
			break;
		case MISSILE:
			Missile m = (Missile) new Missile(level,this).setPosition((float) (x + radius*Math.cos(direction)),(float) (y + radius*Math.sin(direction)));
			m.direction = direction;
			m.speed = bullet_speed;
			
			Main.playSound(Assets.shoot);
			break;			
		case STAR:
			star_timer = star_delay;
			Main.playSound(Assets.star);
			break;
		}
		
		item.quantity--;
		if (item.quantity <= 0) item = null;
	}

	public void getItem() {
		Main.playSound(Assets.itempickup);
		
		item = new Item(Item.Type.values()[Util.randomRangei(Item.Type.values().length)]);		
	}
	
	@Override
	public void die() {
		super.die();
		
		if (lives > 0) 
			respawn_timer = respawn_delay;
		else
			level.ranking.add(this);
		
		levelCollisions = false;
		
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

}
