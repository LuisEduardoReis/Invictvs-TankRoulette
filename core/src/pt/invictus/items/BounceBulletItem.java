package pt.invictus.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.Bullet;
import pt.invictus.entities.player.Player;

public class BounceBulletItem extends Item {

	public BounceBulletItem() {
		quantity = 5;
	}
	
	@Override
	public void use(Player p) {
		Bullet b = (Bullet) new Bullet(p.level,p);
		
		b.setPosition(p.x + p.radius*(float)Math.cos(p.direction), p.y + p.radius*(float)Math.sin(p.direction));
		b.setDVel(p.direction, p.bullet_speed);
		
		b.max_bounces = 3;
			
		p.addEVelDir(p.direction, -p.recoil);
			
		Main.playSound(Assets.shoot);		
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
		float s = 3f;
		for(int i = 0; i < quantity; i++)
			Sprites.bullet.render(batch, 0, x+10*(quantity-2-i), y+10*i, s, s, 0, Color.WHITE);
		
		Assets.font.setColor(Color.WHITE);
		Assets.font.getData().setScale(1);
		Util.drawTextCentered(batch, Assets.font, "Bounce", x, y-25);		
	}

}
