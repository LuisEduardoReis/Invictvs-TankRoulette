package pt.invictus.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.entities.Missile;
import pt.invictus.entities.player.Player;

public class MissileItem extends Item {

	public MissileItem() {
		quantity = 3;
		radius = Missile.MISSILE_RADIUS;
	}
	
	@Override
	public void use(Player p) {
		Missile m = new Missile(p.level,p);
		
		m.setPosition(p.x + p.radius*(float)Math.cos(p.direction), p.y + p.radius*(float)Math.sin(p.direction));
		m.direction = p.direction;
		m.speed = p.bullet_speed;
		
		Main.playSound(Assets.shoot);		
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
		float s = 3f;
		for(int i = 0; i < quantity; i++)
			Sprites.missile.render(batch, 0, x+10*(quantity-2-i), y+10*i-5, s, s, 0, Color.WHITE);
		
	}

}
