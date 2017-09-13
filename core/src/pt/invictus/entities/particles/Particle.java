package pt.invictus.entities.particles;

import pt.invictus.Level;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.Entity;

public class Particle extends Entity {

	public float lifetime;
	public float dscale;
	
	public Particle(Level level) {
		super(level);
		
		this.sprite = Sprites.wave;
		
		this.lifetime = 1f;
		
		this.dscale = 0f;
		
		this.collisions = false;
	}
	
	public Particle setDscale(float dscale) {
		this.dscale = dscale; return this;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (t > lifetime) remove = true;
		
		scale += dscale*delta;
		
		alpha = Util.clamp(1f - (t / lifetime),0,1);
	}

}
