package pt.invictus.entities.particles;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.Assets;
import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Util;

public class Message extends Particle {

	public String message;

	public Message(Level level, String message) {
		super(level);
		
		this.sprite = null;
		this.message = message;
		
		this.lifetime = 1f;
		this.speed = Main.SIZE;
		this.direction = (float) (Math.PI/2);
		this.scale = 2f;
		
		this.x = Main.WIDTH/2;
		this.y = Main.HEIGHT/2;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		BitmapFont font = Assets.font;
		
		font.setColor(color.set(blend).mul(1, 1, 1, alpha));
		font.getData().setScale(scale);
		Util.drawTextCentered(batch, font, message, x,y);
	}

}
