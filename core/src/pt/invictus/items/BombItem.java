package pt.invictus.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.entities.Bomb;
import pt.invictus.entities.player.Player;

public class BombItem extends Item {

	public BombItem() {
		quantity = 3;
	}
	
	@Override
	public void use(Player p) {
		Bomb b = new Bomb(p.level, p);
		b.setPosition(p.x + (0.9f*p.radius)*(float)Math.cos(p.direction), p.y + (0.9f*p.radius)*(float)Math.sin(p.direction));
		b.addEVelDir(p.direction, 1500);
		
		
		Main.playSound(Assets.dropbomb);		
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
		for(int i = 0; i < quantity; i++)
			Sprites.bomb.render(batch, 0, x+10*(quantity-1-i), y+10*i-5, 2, 2, 0, Color.WHITE);		
	}

}
