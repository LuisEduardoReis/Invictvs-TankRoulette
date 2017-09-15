package pt.invictus.entities;

import pt.invictus.entities.Entity.EntityEvaluator;

public interface Respawnable {

	public float getRespawnTimer();
	
	public static Entity.EntityEvaluator available = new EntityEvaluator() {
		@Override
		public boolean skip(Entity e) {
			return ((Respawnable) e).getRespawnTimer() > 0;
		}
	};
}
