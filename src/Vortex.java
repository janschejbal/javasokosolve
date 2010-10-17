/**
 * Data structure for PlayerMove.
 * 
 * A class representing a node in the graph.
 * 
 * The Vortex keep track of the distanse from the start to the Vortex.
 * It also keep track of what node we arrived from to this node.
 * Since we start somewhere, every node also knows if it is the root node or not.
 * And last, it keep tracks of the coordinates on the board.
 * @author aiquen
 *
 */
public class Vortex {
		/** the distance from the root node to this one */
		public int dist;
		/** the node we came from to get to this node */
		public Vortex parent;
		/** flag to check if this is the root node or not */
		public boolean isRoot;
		/** the coordinates on the board */
		public int x, y;
		
	/**
	 * Creates a new node in the graph. Theres no need to keep track of its edges since we know it's position on the board.
	 * @param x the x-coordinate on the board
	 * @param y the y-coordinate on the board
	 * @param dist the distance from the root node to this one
	 * @param parent the nood we came from to get to this node
	 * @param isRoot a flag to check if this is the root node
	 */
	public Vortex(int x, int y, int dist, Vortex parent, boolean isRoot) {
		this.x = x;
		this.y = y;
		this.dist = dist;
		this.parent = parent;
		this.isRoot = isRoot;
	}
	/**
	 * check if this is the root node or not
	 * @return a boolean that is true if this is the root node. Otherwise it returns false.
	 */
	public boolean getIsRoot() {
		return this.isRoot;
	}
}
