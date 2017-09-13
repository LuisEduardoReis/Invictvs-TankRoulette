package pt.invictus.entities.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pt.invictus.Level;
import pt.invictus.Sprite;
import pt.invictus.Util;

public class Shard extends Particle {

	public Shard(Level level, TextureRegion whole, float scale) {
		super(level);
		
		this.sprite = new Sprite();
		float u = whole.getU();
		float v = whole.getV();
		float w = whole.getU2() - u;
		float h = whole.getV2() - v;
		float s = Util.randomRangef(0.1f, 0.3f);
		float dx = Util.randomRangef(0, w-s*w);
		float dy = Util.randomRangef(0, h-s*h);
		TextureRegion piece = new TextureRegion(
			whole.getTexture(),
			u + dx, v + dy,
			u + dx + s*w,
			v + dy + s*h			
		);
		this.sprite.addFrame(piece);
		
		
		float dir = Util.randomRangef(0, (float)(2*Math.PI));
		float vel = Util.randomRangef(100, 300)*scale;
		addEVel(vel * (float)Math.cos(dir), vel * (float)Math.sin(dir));
	}

	
}
