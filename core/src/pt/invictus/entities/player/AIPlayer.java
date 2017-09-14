package pt.invictus.entities.player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.ai.Node;
import pt.invictus.ai.TargetSolutionMap;
import pt.invictus.entities.Entity;

public class AIPlayer extends Player {

	public AIPlayer(Level level, int i) {
		super(level, i);
	}
	
	float target_x, target_y;
	float aux_x, aux_y;
	
	@Override
	public void update(float delta) {
		
		Entity target = null;
		float target_dist = Float.MAX_VALUE;
		for(Entity e : level.entities) {
			/*if (!(e instanceof LuckyBox)) continue;
			LuckyBox b = (LuckyBox) e;
			if (b.respawn_timer > 0) continue;*/
			if (!(e instanceof Player)) continue;
			if (e.dead) continue;
			if (e == this) continue;
			
			
			float dist = Util.pointDistanceSqr(x, y, e.x, e.y);
			if (dist < target_dist) {
				target_dist = dist;
				target = e;
			}
		}
		
		
		if (target != null) {
			target_x = target.x;
			target_y = target.y;
			
			ArrayList<Node> path = new ArrayList<Node>();
			float s = Main.SIZE;
			TargetSolutionMap map = level.getTargetSolutionMap((int)(x/s), (int)(y/s));
			if (map != null) {
				Node n = map.getNode((int)(target_x/s),(int)(target_y/s));
				while(n != null) {
					path.add(n);
					n = n.parent;
				}
			}
			if (path.size() > 1) {
				Node n = path.get(path.size()-2);
				aux_x = s*(n.x+0.5f);
				aux_y = s*(n.y+0.5f);
			} else {
				aux_x = target_x;
				aux_y = target_y;
			}
		}
		
		
		
		
		look_dir = Util.pointDirection(x, y, aux_x, aux_y);
		look_norm = (target == null && Math.abs(Util.angleDifference(direction, look_dir)) < 30*Util.degToRad) ? 0 : 1;
		trigger_val = (target == null || Util.pointDistance(x, y, target.x, target.y) < Main.SIZE*2) ? 0 : 1;
		
		fire_pressed = (target != null && Util.pointDistance(x, y, target.x, target.y) < Main.SIZE*3);
		aux_pressed = false;
		
		super.update(delta);
	}
	
	
	@Override
	public void renderDebug(ShapeRenderer renderer) {
		super.renderDebug(renderer);
		
		//Ray ray = level.game.viewport.getPickRay(Gdx.input.getX(),Gdx.input.getY());
		float s = Main.SIZE;
		int mcx = (int)(target_x/s);
		int mcy = (int)(target_y/s);
		if (mcx >= 0 && mcx < level.map_width-1 && mcy >= 0 && mcy < level.map_height-1) {
		
			renderer.setColor(Player.player_colors[index]);
			Util.pushMatrix(renderer.getTransformMatrix());
			renderer.scale(s,s,1);
			renderer.translate(0.5f, 0.5f, 0);
			
			TargetSolutionMap map = level.getTargetSolutionMap((int)(x/s), (int)(y/s));			
			if (map != null) {
				Node n = map.getNode(mcx,mcy);
				while(n != null) {
					if (n.parent != null) renderer.line(n.x, n.y, n.parent.x, n.parent.y);
					n = n.parent;
				}
			}
			
		
			renderer.setTransformMatrix(Util.popMatrix());
		}
		
		renderer.setColor(Color.RED);
		renderer.circle(aux_x, aux_y, s/4);
	}
	
	

}
