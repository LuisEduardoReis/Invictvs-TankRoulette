package pt.invictus.items;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.invictus.entities.Bullet;
import pt.invictus.entities.player.Player;

public abstract class Item {
		
	public static ArrayList<Item> items = new ArrayList<Item>();
	static {
		items.add(new BounceBulletItem());
		items.add(new TripleBulletItem());
		items.add(new BigBulletItem());
		items.add(new BombItem());
		items.add(new MissileItem());
		items.add(new StarItem());
	}
	
	public int quantity;
	public float radius;
	
	public Item() {
		this.quantity = 1;
		this.radius = Bullet.BULLET_RADIUS;
	}
 	
	public abstract void use(Player player);
	
	public abstract void render(SpriteBatch batch, float x, float y); 

}
