package pt.invictus.ai;

import java.util.ArrayList;
import java.util.Collections;

import pt.invictus.Level;
import pt.invictus.Tile;
import pt.invictus.Util;

public class TargetSolutionMap {
	
	Level level;
	int target_x, target_y;
	int width, height;
	Node nodes[][];
	
	Position directions[] = {
			Position.get(-1,-1), Position.get(0,-1), Position.get(1, -1),
			Position.get(-1, 0), 				   	 Position.get(1,  0),
			Position.get(-1, 1), Position.get(0, 1), Position.get(1,  1)};
	
	public Node getNode(int x, int y) { if (x < 0 || y < 0 || x >= width || y >= height) return null; else return nodes[y][x];}
	void setNode(int x, int y, Node n) { if (!(x < 0 || y < 0 || x >= width || y >= height)) nodes[y][x] = n;}
	
	
	public TargetSolutionMap(Level level, int tx, int ty) {
		this.level = level;
		this.target_x = tx;
		this.target_y = ty;
		
		this.width = level.map_width;
		this.height = level.map_height;
		
		nodes = new Node[height][width];
		
		ArrayList<Node> unvisited = new ArrayList<Node>();
				
		if (level.getTile(target_x, target_y) != Tile.GROUND) return;
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Node node = new Node(x,y,Float.MAX_VALUE,null);
				if (level.getTile(x, y) != Tile.GROUND) continue;
				setNode(x,y,node);				
				unvisited.add(node);
			}
		}		
		getNode(target_x,target_y).distance = 0;		
		
		while(!unvisited.isEmpty()) {
			Collections.sort(unvisited, Node.distanceComparator);
			Node current = unvisited.remove(0);
			
			for(Position dir : directions) {
				Node neighboor = getNode(current.x+dir.x, current.y+dir.y);
				if (level.getTile(current.x+dir.x, current.y) != Tile.GROUND) continue;
				if (level.getTile(current.x, current.y+dir.y) != Tile.GROUND) continue;
				if (neighboor == null) continue;
				
				
				float newDistance = current.distance + Util.pointDistance(0, 0, dir.x, dir.y);
				if (newDistance < neighboor.distance) {
					neighboor.distance = newDistance;
					neighboor.parent = current;
				}				
			}
		}
		
		
	}

}
