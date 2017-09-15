package pt.invictus.entities.player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import pt.invictus.Level;
import pt.invictus.Main;
import pt.invictus.Util;
import pt.invictus.ai.Node;
import pt.invictus.ai.TargetSolutionMap;
import pt.invictus.entities.Bullet;
import pt.invictus.entities.Entity;
import pt.invictus.entities.LuckyBox;
import pt.invictus.entities.MoneyBag;
import pt.invictus.entities.Respawnable;

public class AIPlayer extends Player {

	public AIPlayer(Level level, int i) {
		super(level, i);
	}
	
	Entity target = null;
	Vector2 local_target = new Vector2();
	boolean pathfinding;
	
	@Override
	public void update(float delta) {
		
		target = null;
		pathfinding = false;
		if (!dead) {
		
			final Player closestAlivePlayer = (Player) findClosest(level.players, Player.class, Entity.aliveEvaluator);
			LuckyBox closestActiveLuckyBox = (LuckyBox) findClosest(level.pickups, LuckyBox.class, Respawnable.available);
			MoneyBag closestUsefulMoneyBag = null;
			if (closestAlivePlayer != null) 
				closestUsefulMoneyBag = (MoneyBag) Entity.findClosest(level.moneybags, closestAlivePlayer.x, closestAlivePlayer.y, null, null, new EntityEvaluator() {
					@Override
					public boolean skip(Entity e) {
						MoneyBag m = (MoneyBag) e;
						return m.getRespawnTimer() > 0 || Util.pointDistanceSqr(e.x, e.y, closestAlivePlayer.x, closestAlivePlayer.y) > MoneyBag.EXPLOSION_RADIUS*MoneyBag.EXPLOSION_RADIUS;
					}
				});
			
			
			
			boolean shootTarget = false;
			boolean moveTowardsTarget = false;			
			boolean clearPathToTarget = false;
			boolean clearShotToTarget = false;
			
			// Things we want to shoot
			shootTarget = true;			
			if (closestUsefulMoneyBag != null)
				target = closestUsefulMoneyBag;				
			else if (closestAlivePlayer != null)
				target = closestAlivePlayer;
			else
				shootTarget = false;
			
			// Whether we can shoot then now
			if (target != null && shootTarget) {
				float projectileRadius = Bullet.BULLET_RADIUS;
				if (item != null) projectileRadius = item.radius;
				clearShotToTarget = pathFreeToTarget(target.x, target.y, projectileRadius);
				
				// If not, we need to move towards them
				if (!clearShotToTarget) {
					shootTarget = false;
					moveTowardsTarget = true;
				}
			}			
			
			// Thing we want to move towards
			if (!clearShotToTarget && closestActiveLuckyBox != null && item_timer <= 0 && item == null && star_timer <= 0) { 
				target = closestActiveLuckyBox;
				shootTarget = false;
				moveTowardsTarget = true;
			}
			
			if (target != null) {
								
				clearPathToTarget = pathFreeToTarget(target.x, target.y, radius);
				
				
				if (!clearPathToTarget && !clearShotToTarget) {
					// AI path
					pathfinding = true;
					
					ArrayList<Node> path = new ArrayList<Node>();
					float s = Main.SIZE;
					TargetSolutionMap map = level.getTargetSolutionMap((int)(x/s), (int)(y/s));
					if (map != null) {
						Node n = map.getNode((int)(target.x/s),(int)(target.y/s));
						while(n != null) {
							path.add(n);
							n = n.parent;
						}
					}
					if (path.size() > 1) {
						Node n = path.get(path.size()-2);
						local_target.set(s*(n.x+0.5f), s*(n.y+0.5f));
					}
				}
			}
			
			boolean hasTarget = (target != null);
			float distanceToTarget = hasTarget ? Util.pointDistance(x, y, target.x, target.y) : Float.MAX_VALUE;
			
			float target_dir;
			if (!hasTarget) 
				target_dir = 0;
			else if (pathfinding)
				target_dir = Util.pointDirection(x, y, local_target.x, local_target.y);
			else
				target_dir = Util.pointDirection(x, y, target.x, target.y);
			
			float target_dir_diff = Util.angleDifference(direction, target_dir); 
			boolean headedRightWay = Math.abs(target_dir_diff) < 30*Util.degToRad;
			boolean aimedTowardsTarget = hasTarget && Math.abs(Math.sin(target_dir_diff)*distanceToTarget) < target.radius;
			
			
			look_dir = target_dir;
			look_norm = hasTarget ? 1 : 0;
			
			if (moveTowardsTarget && headedRightWay)
				trottle_val = 1;
			else
				trottle_val = 0;
			
			if (shootTarget && clearShotToTarget && aimedTowardsTarget)
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
		
		if (target != null) {
			
			float s = Main.SIZE;
			int mcx = (int)(target.x/s);
			int mcy = (int)(target.y/s);
		
			if (pathfinding) {
				renderer.setColor(Player.player_colors[index % Player.player_colors.length]);
				Util.pushMatrix(renderer.getTransformMatrix());
				renderer.scale(s,s,1);
				renderer.translate(0.5f, 0.5f, 0);
				renderer.begin(ShapeType.Filled);
				
				TargetSolutionMap map = level.getTargetSolutionMap((int)(x/s), (int)(y/s));			
				if (map != null) {
					Node n = map.getNode(mcx,mcy);
					while(n != null) {
						if (n.parent != null) Util.drawWidthLine(renderer,n.x, n.y, n.parent.x, n.parent.y,0.1f);
						n = n.parent;
					}
				}
				
				renderer.setTransformMatrix(Util.popMatrix());
				renderer.end();
				
				renderer.setColor(Color.RED);
				renderer.begin(ShapeType.Filled);
				renderer.circle(local_target.x, local_target.y, s/8);
				renderer.end();
			}
			
			// line of sight	
			float dist = Util.pointDistance(x, y, target.x, target.y);
			float dx = -Bullet.BULLET_RADIUS*(target.y-y)/dist;
			float dy = Bullet.BULLET_RADIUS*(target.x-x)/dist;
			
			renderer.begin(ShapeType.Line);
			if (pathFreeToTarget(target.x, target.y, Bullet.BULLET_RADIUS)) {
				renderer.setColor(Color.RED);
				
				renderer.line(x + dx, y + dy, target.x + dx, target.y + dy);
				renderer.line(x - dx, y - dy, target.x - dx, target.y - dy);
			}	
			else {
				renderer.setColor(Color.WHITE); 
				
				float ld = level.raycast(x + dx, y + dy, (target.x-x)/dist, (target.y-y)/dist);
				float rd = level.raycast(x - dx, y - dy, (target.x-x)/dist, (target.y-y)/dist);
				renderer.line(x + dx, y + dy, x + dx + ld*(target.x-x)/dist, y + dy + ld*(target.y-y)/dist);
				renderer.line(x - dx, y - dy, x - dx + rd*(target.x-x)/dist, y - dy + rd*(target.y-y)/dist);
			}
			renderer.end();		
			
			
		}
	}
	
	

}
