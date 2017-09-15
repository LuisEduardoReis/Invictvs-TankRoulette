package pt.invictus.entities.player;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.ai.Node;
import pt.invictus.ai.TargetSolutionMap;
import pt.invictus.entities.Bullet;
import pt.invictus.entities.Entity;
import pt.invictus.entities.LuckyBox;

public class AIPlayer extends Player {

	public AIPlayer(Level level, int i) {
		super(level, i);
	}
	
	float target_x, target_y;
	float aux_x, aux_y;
	boolean pathfinding;
	
	@Override
	public void update(float delta) {
		
		pathfinding = false;
		if (!dead) {
		
			Player closestAlivePlayer = (Player) findClosest(level.players, Player.class, Entity.aliveEvaluator);
			LuckyBox closestActiveLuckyBox = (LuckyBox) findClosest(level.pickups, LuckyBox.class, new EntityEvaluator() {
				@Override
				public boolean skip(Entity e) {
					return ((LuckyBox) e).respawn_timer > 0;
				}
			});
			
			Entity target = null;
			boolean clearPathToTarget = false;
			boolean clearShotToTarget = false;
			
			if (closestActiveLuckyBox != null && item_timer <= 0 && item == null && star_timer <= 0) 
				target = closestActiveLuckyBox;
			else if (closestAlivePlayer != null)
				target = closestAlivePlayer;		
			
			if (target != null) {
				target_x = target.x;
				target_y = target.y;
				
				clearPathToTarget = pathFreeToTarget(target_x, target_x, radius);
				clearShotToTarget = pathFreeToTarget(target_x, target_y, Bullet.BULLET_RADIUS);
				
				if (clearPathToTarget || (target instanceof Player && clearShotToTarget)) {
					// Direct path
					aux_x = target_x;
					aux_y = target_y;
				} else {
					// AI path
					pathfinding = true;
					
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
			}
			
			boolean hasTarget = (target != null);
			float distanceToTarget = Util.pointDistance(x, y, target_x, target_y);
			float target_dir = Util.pointDirection(x, y, aux_x, aux_y);
			float target_dir_diff = Util.angleDifference(direction, target_dir); 
			boolean headedRightWay = Math.abs(target_dir_diff) < 30*Util.degToRad;
			boolean aimedTowardsTarget = hasTarget && Math.abs(Math.sin(target_dir_diff)*distanceToTarget) < target.radius;
			
			
			look_dir = target_dir;
			look_norm = hasTarget ? 1 : 0;
			
			if (hasTarget)
				if (target instanceof Player && clearShotToTarget)
					trottle_val = 0;
				else if (headedRightWay)
					trottle_val = 1;
			else
				trottle_val = 0;
			
			if (hasTarget && target instanceof Player && clearShotToTarget && aimedTowardsTarget)
				fire_pressed = true;
			else 
				fire_pressed = false;
			
			aux_pressed = false;			
		}
		
		super.update(delta);
	}


	private boolean pathFreeToTarget(float tx, float ty, float r) {
		float dist = Util.pointDistance(x, y, tx, ty);
		float dx = -r*(ty-y)/dist;
		float dy = r*(tx-x)/dist;		
		
		return (dist < level.raycast(x + dx, y + dy, (tx-x)/dist, (ty-y)/dist))
			&& (dist < level.raycast(x - dx, y - dy, (tx-x)/dist, (ty-y)/dist));	
	}


	@Override
	public void renderDebug(ShapeRenderer renderer) {
		super.renderDebug(renderer);
		if (dead) return;
		
		float s = Main.SIZE;
		int mcx = (int)(target_x/s);
		int mcy = (int)(target_y/s);
		if (mcx >= 0 && mcx < level.map_width-1 && mcy >= 0 && mcy < level.map_height-1) {
		
			if (pathfinding) {
				renderer.setColor(Player.player_colors[index]);
				Util.pushMatrix(renderer.getTransformMatrix());
				renderer.scale(s,s,1);
				renderer.translate(0.5f, 0.5f, 0);
				
				TargetSolutionMap map = level.getTargetSolutionMap((int)(x/s), (int)(y/s));			
				if (map != null) {
					Node n = map.getNode(mcx,mcy);
					while(n != null) {
						if (n.parent != null) Util.drawWidthLine(renderer,n.x, n.y, n.parent.x, n.parent.y,0.1f);
						n = n.parent;
					}
				}
				
				renderer.setTransformMatrix(Util.popMatrix());
				
				renderer.setColor(Color.RED);
				renderer.circle(aux_x, aux_y, s/4);
			}
			
			// line of sight	
			float dist = Util.pointDistance(x, y, target_x, target_y);
			float dx = -Bullet.BULLET_RADIUS*(target_y-y)/dist;
			float dy = Bullet.BULLET_RADIUS*(target_x-x)/dist;
			
			if (pathFreeToTarget(target_x, target_y, Bullet.BULLET_RADIUS)) {
				renderer.setColor(Color.RED);
				
				renderer.line(x + dx, y + dy, target_x + dx, target_y + dy);
				renderer.line(x - dx, y - dy, target_x - dx, target_y - dy);
			}	
			else {
				renderer.setColor(Color.WHITE); 
				
				float ld = level.raycast(x + dx, y + dy, (target_x-x)/dist, (target_y-y)/dist);
				float rd = level.raycast(x - dx, y - dy, (target_x-x)/dist, (target_y-y)/dist);
				renderer.line(x + dx, y + dy, x + dx + ld*(target_x-x)/dist, y + dy + ld*(target_y-y)/dist);
				renderer.line(x - dx, y - dy, x - dx + rd*(target_x-x)/dist, y - dy + rd*(target_y-y)/dist);
			}
			
			
			
			
			
		}
	}
	
	

}
