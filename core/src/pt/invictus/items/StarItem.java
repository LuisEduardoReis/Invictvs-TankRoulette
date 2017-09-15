package pt.invictus.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Main;
import pt.invictus.Sprites;
import pt.invictus.entities.player.Player;

public class StarItem extends Item {

	@Override
	public void use(Player p) {
		p.star_timer = p.star_delay;
		
		Main.playSound(Assets.star);
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
		Sprites.star.render(batch, 0, x, y, 2, 2, 0, Color.WHITE);		
	}

}
