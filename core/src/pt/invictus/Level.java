package pt.invictus;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import pt.invictus.ai.TargetSolutionMap;
import pt.invictus.entities.Diamond;
import pt.invictus.entities.Entity;
import pt.invictus.entities.LuckyBox;
import pt.invictus.entities.MoneyBag;
import pt.invictus.entities.player.Player;
import pt.invictus.screens.GameScreen;

public class Level {
	public GameScreen game;
	
	public float t;
	public TiledMap map;
	public OrthogonalTiledMapRenderer tileRenderer;
	public int map_width, map_height;
	
	public Tile[] tiles;
	public TargetSolutionMap targetSolutionMap[][];
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<Entity> newEntities = new ArrayList<Entity>();
	
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Entity> pickups = new ArrayList<Entity>();
	
	
	public ArrayList<Vector2> spawns = new ArrayList<Vector2>();
	public Vector2 roulette;

	public ArrayList<Player> ranking = new ArrayList<Player>();
	
	
	public Level(GameScreen game, String level_name) {
		this.game = game;
		
		map = new TmxMapLoader(new InternalFileHandleResolver()).load(level_name);
		tileRenderer = new OrthogonalTiledMapRenderer(map);
		
		map_width = (Integer) map.getProperties().get("width");
		map_height = (Integer) map.getProperties().get("height");
		tiles = new Tile[map_width*map_height];
		
		// Load tiles
		for(int i = 0; i < tiles.length; i++) tiles[i] = Tile.GROUND;
		
		TiledMapTileLayer tiled_tiles = (TiledMapTileLayer) map.getLayers().get("main"); 
		for(int yy = 0; yy<map_height; yy++) {
			for(int xx = 0; xx<map_width; xx++) {
				TiledMapTileLayer.Cell cell = tiled_tiles.getCell(xx, yy);
				
				/*System.out.print("\t"+((cell == null) ? " " : 
					"("+cell.getTile().getProperties().get("solid")+")" 
						));*/
				if (cell != null) {
					//int cid = cell.getTile().getId()-1;
					tiles[yy*map_width+xx] = Tile.WALL;
				}
			}
			//System.out.println(); System.out.println();
		}
	
		// Load game objects
		roulette = null;
		
		for(MapObject o : map.getLayers().get("objects").getObjects()) {
			String type = (String) o.getProperties().get("type"); 
			Vector2 p = Util.getMapObjectPosition(o);
			
			if (type != null) {
				type = type.toLowerCase();
				
				if(type.equals("luckybox")) {
					new LuckyBox(this).setPosition(p.x, p.y);				
				}
				if(type.equals("moneybag")) {
					new MoneyBag(this).setPosition(p.x, p.y);
				}
				if(type.equals("diamond")) {
					new Diamond(this).setPosition(p.x, p.y);
				}
				if(type.equals("spawn")) {
					spawns.add(new Vector2(p.x, p.y));
				}
				if(type.equals("roulette")) {
					roulette = new Vector2(p.x, p.y);
				}
			}
		}
		if (spawns.size() == 0) spawns.add(new Vector2(Main.WIDTH/2,Main.HEIGHT/2));
		
		
		// Calculate AI pathfinding
		targetSolutionMap = new TargetSolutionMap[map_height][map_width];
		for(int i = 0; i < map_height; i++) {
			for(int j = 0; j < map_width; j++) {
				targetSolutionMap[i][j] = new TargetSolutionMap(this, j,i);
			}
		}
		

	} 
	
	public Tile getTile(int x, int y) { return (x < 0 || y < 0 || x >= map_width || y >= map_height) ? Tile.WALL : tiles[y*map_width+x];	}
	public void setTile(int x, int y, Tile tile) { if (x >= 0 && y >= 0 && x < map_width && y < map_height) tiles[y*map_width+x] = tile; }
	
	
	static Vector2 v = new Vector2();
	public void update(float delta) {
		
		t += delta;
		
		if (game.victory_timer < 0 && players.size() > 1) {
			int alive = 0; Player last = null;
			for(Player p : players) 
				if (!p.dead || p.lives > 0) {
					alive++;
					last = p;
				}
			if (alive <= 1) {
				if (last != null) ranking.add(last);
				game.victory_timer = game.victory_delay;
			}
		}
		
		
		for(Entity e : entities) e.preupdate(delta);		
		for(Entity e : entities) e.update(delta);
		
		//Roulette
		if (roulette != null)
		for(Entity e : entities) {
			float dist = Util.pointDistance(e.x,e.y,roulette.x,roulette.y);
			if (dist < 2.5f*Main.SIZE) {
				e.direction += delta;
				v.set(e.x,e.y);
				v.sub(roulette.x, roulette.y);
				v.rotate(delta*Util.radToDeg);
				v.add(roulette.x, roulette.y);
				e.x = v.x;
				e.y = v.y;
			}
		}
		
		entities.addAll(newEntities);
		newEntities.clear();
		
		// Entity Collisions
		for(Entity e : entities) {
			if (!e.collisions) continue;
			for(Entity o : entities) {
				if (!o.collisions) continue;
				if (e == o) continue;
				float sqrDist = Util.pointDistanceSqr(e.x, e.y, o.x, o.y);
				float radii = (e.radius + o.radius);
				
				// Collisions
				if (sqrDist <= radii*radii) e.collide(o);			
			}
		}
		
		for(int i = 0; i < entities.size(); i++) 
			if (entities.get(i).remove)
				entities.remove(i).destroy();
		
		for(Entity e : entities) e.levelCollision();
		
		Collections.sort(entities, Entity.zComparator);
		
		// Sound
		boolean slotmachine = false;
		for(Player p : players) 
			if (p.item_timer > 0.1f) {
				slotmachine = true; break; 
			}
		if (Main.SOUND) {
			if (Assets.slotmachine.isPlaying() && !slotmachine) Assets.slotmachine.stop();
			if (!Assets.slotmachine.isPlaying() && slotmachine) Assets.slotmachine.play();
		} else
			Assets.slotmachine.stop();
		
	}
	
	
	
	public void renderEntities(SpriteBatch batch) {
		Util.drawCentered(batch, Assets.roulette, roulette.x, roulette.y, 5*Main.SIZE, 5*Main.SIZE, t*Util.radToDeg, true, true);		
		
		for(Entity e : entities) e.render(batch);		
	}
	
	public void addEntity(Entity entity) { 	newEntities.add(entity); }

	public void renderDebug(ShapeRenderer shapeRenderer) {
		for(Entity e : entities) e.renderDebug(shapeRenderer);		
	}

	public TargetSolutionMap getTargetSolutionMap(int x, int y) {
		if (x < 0 || y < 0 || x >= map_width || y >= map_height) return null;
		return targetSolutionMap[y][x];
	}
	
	
	public float raycast(float rayPosX,float rayPosY, float rayDirX,float rayDirY) {
		
		rayPosX /= Main.SIZE;
		rayPosY /= Main.SIZE;
		
		//which box of the map we're in
	    int mapX = (int) (rayPosX);
	    int mapY = (int) (rayPosY);

	    //length of ray from current position to next x or y-side
	    float sideDistX;
	    float sideDistY;

	    //length of ray from one x or y-side to next x or y-side
	    float deltaDistX = (float) Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
	    float deltaDistY = (float) Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));

	    //what direction to step in x or y-direction (either +1 or -1)
	    int stepX;
	    int stepY;

	    boolean side = false; //was a NS or a EW wall hit?
	      
	    if (rayDirX < 0) {
	    	stepX = -1;
	        sideDistX = (rayPosX - mapX) * deltaDistX;
	    } else {
	        stepX = 1;
	        sideDistX = (mapX + 1.0f - rayPosX) * deltaDistX;
	    }
	    if (rayDirY < 0) {
	        stepY = -1;
	        sideDistY = (rayPosY - mapY) * deltaDistY;
	    } else {
	        stepY = 1;
	        sideDistY = (mapY + 1.0f - rayPosY) * deltaDistY;
	    }
	      
	    //perform DDA
	    int limit = 100;
	    while (true) {	    	
	    	//jump to next map square, OR in x-direction, OR in y-direction
	        if (sideDistX < sideDistY) {
	        	
	          sideDistX += deltaDistX;
	          mapX += stepX;
	          side = false;
	          
	        } else {
	        	
	          sideDistY += deltaDistY;
	          mapY += stepY;
	          side = true;
	          
	        }
	        
	        //Check if ray has hit a wall
	        if (getTile(mapX,mapY) != Tile.GROUND) break;
	        
	        //Check iteration limit
	        if (limit-- <= 0) return Float.MAX_VALUE; 
	    }
	      
	    if (!side) 
	    	return Main.SIZE * (mapX - rayPosX + (1 - stepX) / 2) / rayDirX;
	    else           
	    	return Main.SIZE * (mapY - rayPosY + (1 - stepY) / 2) / rayDirY;
	}
	
	public boolean lineOfSight(float x1, float y1, float x2, float y2) {
		float dist = Util.pointDistance(x1, y1, x2, y2);
		float raycast = raycast(x1,y1,(x2-x1)/dist, (y2-y1)/dist);
		return dist < raycast;
	}
}
