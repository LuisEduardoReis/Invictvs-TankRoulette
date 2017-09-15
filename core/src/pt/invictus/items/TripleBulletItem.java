package pt.invictus.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.Bullet;
import pt.invictus.entities.player.Player;

public class TripleBulletItem extends Item {

	@Override
	public void use(Player p) {
		float br = 10*Util.degToRad;
		
		for(int i = 0; i < 3; i++) {
			Bullet b = new Bullet(p.level,p);
			
			b.setPosition(p.x + p.radius*(float)Math.cos(p.direction), p.y + p.radius*(float)Math.sin(p.direction));
			b.setDVel(p.direction+br*(i-1), p.bullet_speed);
		}
				
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
		Util.drawTextCentered(batch, Assets.font, "Triple", x, y-25);		
	}

}
