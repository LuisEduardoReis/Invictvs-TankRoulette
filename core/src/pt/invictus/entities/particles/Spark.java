package pt.invictus.entities.particles;

import pt.invictus.Level;
import pt.invictus.Sprites;
import pt.invictus.Util;
import pt.invictus.entities.Entity;

public class Spark extends Particle {

	public Spark(Level level) {
		super(level);
		
		this.sprite = Sprites.spark;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}
	
	@Override
	public Entity addEVel(float x, float y) {
		super.addEVel(x, y);
		
		if (edx != 0 || edy != 0) {
			this.direction = Util.pointDirection(0, 0, edx, edy);
		}
		
		return this;
	}
}
