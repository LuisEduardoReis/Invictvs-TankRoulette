package pt.invictus.ai;

import java.util.Comparator;

public class Node {

	public int x,y;
	public float distance;
	public Node parent;
	
	public Node() {}
	
	public Node(int x, int y, float distance, Node parent) {
		this.x = x;
		this.y = y;
		this.distance = distance;
		this.parent = parent;
	}
	
	public static Comparator<Node> distanceComparator = new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
			return Float.compare(o1.distance, o2.distance);
		}
	};
	
	@Override
	public String toString() {
		return "["+x+", "+y+", "+distance+"]";
	}
}
