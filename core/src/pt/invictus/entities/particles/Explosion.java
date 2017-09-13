package pt.invictus.entities.particles;

import com.badlogic.gdx.graphics.Color;

import pt.invictus.Level;
import pt.invictus.Sprites;
import pt.invictus.Util;

public class Explosion extends Particle {
	
	public Explosion(Level level) {
		super(level);
		
		this.sprite = Sprites.wave;
		this.blend = Color.ORANGE;
		
		this.dscale = 25f;
	}
	
	public static void explosion(float x, float y, Level level, float scale) {

		new Explosion(level).setDscale(10*scale).setPosition(x, y).setBlend(new Color(Color.ORANGE).mul(1,1,1,0.5f));
		new Explosion(level).setDscale(15*scale).setPosition(x, y).setBlend(new Color(Color.GRAY).mul(1,1,1,0.5f));
		new Explosion(level).setDscale(20*scale).setPosition(x, y).setBlend(new Color(Color.DARK_GRAY).mul(1,1,1,0.5f));
		
		for(int i = 0; i < 100; i++) {
			Spark e = (Spark) new Spark(level).setDscale(0).setPosition(x, y);
			
			e.scale = 0.5f*scale;
			float dark = Util.randomRangef(0.5f, 1);
			e.blend = new Color(1*dark,0.5f*dark,0,0.5f);
			e.dscale = Util.randomRangef(0, 0.5f)*scale;
			e.lifetime = Util.randomRangef(0.5f, 1f);
			float f = Util.randomRangef(1000, 2500)*scale;
			float d = Util.randomRangef(0, (float) (2*Math.PI));
			e.addEVel((float)(f*scale * Math.cos(d)),(float)(f*scale * Math.sin(d)));
			e.edf = Util.randomRangef(0.9f, 0.95f);
		}
		
	}
	
}
