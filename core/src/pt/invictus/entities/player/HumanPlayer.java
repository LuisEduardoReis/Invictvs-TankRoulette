package pt.invictus.entities.player;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pt.invictus.Level;
import pt.invictus.controllers.GameController;
import pt.invictus.controllers.GameController.Key;

public class HumanPlayer extends Player {

	public GameController controller;
	
	public HumanPlayer(Level level, GameController c, int i) {
		super(level, i);
		
		this.controller = c;		
	}

	
	@Override
	public void update(float delta) {

		if (controller != null) {
			look_dir = controller.getLookDir(x, y, level.game.viewport);
			look_norm = controller.getLookNormal();
			trottle_val = controller.getTrottleAxis();
			
			fire_pressed = controller.getKeyPressed(Key.A);
			aux_pressed = controller.getKeyPressed(Key.X);
		}
		
		super.update(delta);
	}
	
	@Override
	public void renderDebug(ShapeRenderer renderer) {
		super.renderDebug(renderer);
	}
}
