package pt.invictus.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.entities.Bullet;
import pt.invictus.entities.player.Player;

public class BigBulletItem extends Item {

	public BigBulletItem() {
		quantity = 1;
		radius = 2*Bullet.BULLET_RADIUS;
	}
	
	@Override
	public void use(Player p) {
		Bullet b = new Bullet(p.level,p);
		
		b.setPosition(p.x + p.radius*(float)Math.cos(p.direction), p.y + p.radius*(float)Math.sin(p.direction));
		b.setDVel(p.direction, p.bullet_speed);
		
		b.scale *= 2;
		b.radius *= 2;
		b.damage = 15;
		b.explosion_damage = 20;
				
		p.addEVelDir(p.direction, -1.5f*p.recoil);
			
		Main.playSound(Assets.shoot);
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
		float s = 3f;
		Sprites.bullet.render(batch, 0, x, y, 2*s, 2*s, 0, Color.WHITE);		
	}

}
