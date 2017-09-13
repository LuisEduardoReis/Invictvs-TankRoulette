package pt.invictus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Item {
	
	public static enum Type {
		BOUNCE_BULLET(5),
		TRIPLE_BULLET(3),
		BIG_BULLET(1),
		BOMB(3),
		MISSILE(3),
		STAR(1);
		
		public int quantity;
		
		Type(int quantity) {
			this.quantity = quantity;
		}
	}
	
	public Type type;
	public int quantity;
	
	public Item(Type type) {
		this.type = type;
		this.quantity = type.quantity;
	}
 	
	
	public void render(SpriteBatch batch, float x, float y) {
		render(type,quantity,batch,x,y);
	}
	
	public static void render(Type type, int quantity, SpriteBatch batch, float x, float y) {
		float s = 3f;
		switch(type) {
		case BIG_BULLET:
			Sprites.bullet.render(batch, 0, x, y, 2*s, 2*s, 0, Color.WHITE);
			break;
		case BOUNCE_BULLET:
			for(int i = 0; i < quantity; i++)
				Sprites.bullet.render(batch, 0, x+10*(quantity-2-i), y+10*i, s, s, 0, Color.WHITE);
			
			Assets.font.setColor(Color.WHITE);
			Assets.font.getData().setScale(1);
			Util.drawTextCentered(batch, Assets.font, "Bounce", x, y-25);
			break;
		case TRIPLE_BULLET:
			for(int i = 0; i < quantity; i++)
				Sprites.bullet.render(batch, 0, x+10*(quantity-2-i), y+10*i, s, s, 0, Color.WHITE);
			
			Assets.font.setColor(Color.WHITE);
			Assets.font.getData().setScale(1);
			Util.drawTextCentered(batch, Assets.font, "Triple", x, y-25);
			break;
		case BOMB:
			for(int i = 0; i < quantity; i++)
				Sprites.bomb.render(batch, 0, x+10*(quantity-1-i), y+10*i-5, 2, 2, 0, Color.WHITE);
			
			break;
		case MISSILE:
			for(int i = 0; i < quantity; i++)
				Sprites.missile.render(batch, 0, x+10*(quantity-2-i), y+10*i-5, s, s, 0, Color.WHITE);
			break;
		case STAR:
			Sprites.star.render(batch, 0, x, y, 2, 2, 0, Color.WHITE);
			break;	
		}
	}
}
