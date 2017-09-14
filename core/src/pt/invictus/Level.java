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
	
	public Tile getTile(int x, int y) { return (x < 0 || y < 0 || x >= map_width || y >= map_height) ? Tile.GROUND : tiles[y*map_width+x];	}
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
	
	/*
	public boolean lineOfSight(float x1,float y1, float x2,float y2) {
		int minx = (int) Math.floor(Math.min(x1,x2));
		int maxx = (int) Math.ceil(Math.max(x1,x2));
		int miny = (int) Math.floor(Math.min(y1,y2));
		int maxy = (int) Math.ceil(Math.max(y1,y2));
		
		for(int y = miny; y < maxy; y++) {
			for(int x = minx; x < maxx; x++) {
				Tile t = getTile(x, y);
				if (t == Tile.GROUND) continue;
				
				
			}
		}
		
		return false;
	}*/
}
