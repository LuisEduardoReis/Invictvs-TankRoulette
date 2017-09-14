package pt.invictus.ai;

import java.util.HashMap;

public class Position {
	
	public final int x, y;
	
	private Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	private static HashMap<Integer,HashMap<Integer, Position>> values;
	static {
		values = new HashMap<Integer, HashMap<Integer,Position>>();
	}
	
	public static Position get(int x, int y) {
		if (!values.containsKey(x)) values.put(x, new HashMap<Integer, Position>());
		HashMap<Integer, Position> yvalues = values.get(x);		
		if (!yvalues.containsKey(y)) yvalues.put(y, new Position(x,y));
		
		return yvalues.get(y);
	}

}
