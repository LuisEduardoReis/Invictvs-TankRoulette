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
import pt.invictus.entities.Diamond;
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
	float aim_deviation = 3.5f*Util.degToRad;
	
	static Vector2 aux = new Vector2();
	
	@Override
	public void update(float delta) {
		
		target = null;
		pathfinding = false;
		if (!dead) {
		
			final Player closestAlivePlayer = (Player) findClosest(level.players, Player.class, Entity.aliveEvaluator);
			LuckyBox closestActiveLuckyBox = (LuckyBox) findClosest(level.pickups, LuckyBox.class, Respawnable.available);
			Diamond closestActiveDiamond = (Diamond) findClosest(level.pickups, Diamond.class, Respawnable.available);
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
			boolean canShootTarget = false;
			boolean canSeeTarget = false;
			
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
				canShootTarget = pathFreeToTarget(target.x, target.y, projectileRadius, aim_deviation);
				canSeeTarget = level.lineOfSight(x, y, target.x, target.y);
				
				// If not, we need to move towards them
				if (!canShootTarget) {
					shootTarget = false;
					moveTowardsTarget = true;
				}
			}			
			
			// Thing we want to move towards
			boolean wantLuckyBox = closestActiveLuckyBox != null && item_timer <= 0 && item == null && star_timer <= 0;
			boolean wantDiamond = closestActiveDiamond != null && health < full_health;
			boolean wantDiamondBadly = closestActiveDiamond != null && health <= 0.25f*full_health;

			if (!canSeeTarget && wantLuckyBox) { 
				target = closestActiveLuckyBox;
				shootTarget = false;
				moveTowardsTarget = true;
			}
			if ((!canShootTarget && wantDiamond) || wantDiamondBadly) {
				target = closestActiveDiamond;
				shootTarget = false;
				moveTowardsTarget = true;
			}
			
			// Target handling
			if (target != null) {
								
				clearPathToTarget = pathFreeToTarget(target.x, target.y, radius, 0);
				
				if (!clearPathToTarget && !canShootTarget) {
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
			
			if (shootTarget && canShootTarget && aimedTowardsTarget)
				fire_pressed = true;
			else
				fire_pressed = false;
			
			aux_pressed = false;			
		}
		
		super.update(delta);
	}


	private boolean pathFreeToTarget(float tx, float ty, float r, float a) {
		float dist = Util.pointDistance(x, y, tx, ty);
		float dx = -r*(ty-y)/dist;
		float dy = r*(tx-x)/dist;
		a *= Util.radToDeg;
		
		aux.set((tx-x)/dist, (ty-y)/dist);		
		
		if (a != 0) aux.rotate(a);
		float ld = level.raycast(x + dx, y + dy, aux.x, aux.y);
		
		if (a != 0) aux.rotate(-2*a);
		float rd = level.raycast(x - dx, y - dy, aux.x, aux.y);
		
		return (dist < ld && dist < rd);	
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
				renderer.begin(ShapeType.Filled);
				
				renderer.scale(s,s,1);
				renderer.translate(0.5f, 0.5f, 0);
				
				TargetSolutionMap map = level.getTargetSolutionMap((int)(x/s), (int)(y/s));			
				if (map != null) {
					for(Node n = map.getNode(mcx,mcy); n != null && n.parent != null; n = n.parent)
						Util.drawWidthLine(renderer,n.x, n.y, n.parent.x, n.parent.y,8/s);					
				}
				
				renderer.setTransformMatrix(Util.popMatrix());
				
				renderer.setColor(Color.RED);
				renderer.circle(local_target.x, local_target.y, s/8);
				
				renderer.end();
			}
			
			// line of sight	
			float dist = Util.pointDistance(x, y, target.x, target.y);
			float dx = -Bullet.BULLET_RADIUS*(target.y-y)/dist;
			float dy = Bullet.BULLET_RADIUS*(target.x-x)/dist;
			renderer.begin(ShapeType.Line);
			if (pathFreeToTarget(target.x, target.y, Bullet.BULLET_RADIUS, aim_deviation)) {
				renderer.setColor(Color.RED);
				
				aux.set(target.x - x, target.y - y);
				
				aux.rotate(aim_deviation*Util.radToDeg);				
				renderer.line(x + dx, y + dy, x + dx + aux.x, y + dy + aux.y);
				
				aux.rotate(-2*aim_deviation*Util.radToDeg);
				renderer.line(x - dx, y - dy, x - dx + aux.x, y - dy + aux.y);
			}	
			else {
				renderer.setColor(Color.WHITE); 
				
				float rd;
				aux.set((target.x - x)/dist, (target.y - y)/dist);
				
				aux.rotate(aim_deviation*Util.radToDeg);
				rd = level.raycast(x + dx, y + dy, aux.x, aux.y);
				renderer.line(x + dx, y + dy, x + dx + rd*aux.x, y + dy + rd*aux.y);
				
				aux.rotate(-2*aim_deviation*Util.radToDeg);
				rd = level.raycast(x - dx, y - dy, aux.x, aux.y);
				renderer.line(x - dx, y - dy, x - dx + rd*aux.x, y - dy + rd*aux.y);
			}
			renderer.setColor(Color.WHITE);
			renderer.line(x, y, target.x, target.y);
			
			renderer.end();		
			
			
		}
	}
	
	

}
