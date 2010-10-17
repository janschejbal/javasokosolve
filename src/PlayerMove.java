import java.rmi.NotBoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * A class to move the player around to push the boxes.
 * 
 * It takes two arguments, a board to search and a list of moves for the
 * boxes to solve the board. It then uses Dijkstras algorithmen to preform
 * a graph search to find the shortest way from the player position to the
 * pushing position for every push until the list of moves is empty, meaning
 * that the board is solved.
 *
 */
public class PlayerMove {
	/** The board in its initial state **/
	private Board startBoard;
	/**  A list with moves the boxes need to preform to solve the board **/
	private List<Move> moveSequence;
	/** The graph representing the walkable tiles on the board as vortexes **/
	private Vortex[][] graph;
	/** The queue of nodes to search thrue to find the path to the box being moved **/
	private List<Vortex> nodequeue;

	/** Initialize the object *
	 * @param startBoard the initial {@link #startBoard} that is being searched
	 * @param moveSequnce the sequence of boxmoves to solve the {@link #startBoard}
	 */
	public PlayerMove(Board startBoard, List<Move> moveSequence) {
		this.startBoard = startBoard;
		this.moveSequence = moveSequence;
	}
	
	/** Populates the nodequeue with vortexes to build a graph **/
	public void createVortexes() {
		this.nodequeue = new LinkedList<Vortex>();
		for (int i = 0; i < this.startBoard.floor.length; i++) {
			for (int j = 0; j < this.startBoard.floor[0].length; j++) {
				// If it is a reachable tile, add a vortex point there.
				// This will also automatically give us edges
				if (this.startBoard.reachable[i][j]) {
					this.graph[i][j] = new Vortex(i, j, Integer.MAX_VALUE, null, false);
					this.nodequeue.add(this.graph[i][j]);
				}
			}
		}
		
	}
	/**
	 * Starts the search in the graph and keeps track of the box movement list
	 * 
	 * @return a string containing the moves the player need to perform to push the boxes to the correct positions.
	 */
	public String findPath() {
		String movements = "";
		List<Move> dir = this.moveSequence;
		for (Move curr : dir) {
			try {
				movements += this.localPath(curr);
			} catch (NotBoundException e) {
				System.out.println(e.getMessage());
				break;
			}
		}

		return movements;
	}

	/**
	 * Does the actual searching in the graph from the move given to the pushing tile
	 * @param box the box and direction that we want to push
	 * @return a string with the moves that the player needs to perform to get to the pushing tile, and then the push itself.
	 * @throws NotBoundException the excpetion thrown if we somehow walks of the board, safeguard for broken boards.
	 */
	public String localPath(Move box) throws NotBoundException {
		String steps = "";
		
		// To use Dijkstras we first build a matrix for the graph
		this.graph = new Vortex[this.startBoard.floor.length][this.startBoard.floor[0].length];
		//Make sure the maps are calculated, as it uses reachable map. Create all vortexes in the matrix
		startBoard.calculateMaps();
		this.createVortexes();
		// Set the starting positions special values
		this.graph[startBoard.player.x][startBoard.player.y].isRoot = true;
		this.graph[startBoard.player.x][startBoard.player.y].dist = 0;
		
		
		int dir = box.direction;
		int dx = 0;
		int dy = 0;
		// The inverted values for direction. Since we are pushing, we want to be on the other side of the box direction
		switch (dir) {
			case 1: dx = 0; dy = 1; break;
			case 2: dx = -1; dy = 0; break;
			case 3: dx = 0; dy = -1; break;
			case 4: dx = 1; dy = 0; break;
		}
		while (this.nodequeue.size() > 0) {
			Vortex u = this.leastDist();
			if (u.dist == Integer.MAX_VALUE) {
				throw new NotBoundException("No available path to target tile!");
			}
			if (u.x == box.box.x + dx && u.y == box.box.y + dy) {
				steps = this.getPath(u);
				switch (dir) {
					case 1: steps = steps + "U" ; break;
					case 2: steps = steps + "R" ; break;
					case 3: steps = steps + "D" ; break;
					case 4: steps = steps + "L" ; break;
				}				
				this.startBoard.move(box.box, dir);
				break;
			}
			if (u.x + 1 < this.graph.length)
				this.updateDist(this.graph[u.x + 1][u.y], u);
			
			if (u.x - 1 > 0)
				this.updateDist(this.graph[u.x - 1][u.y], u);
			
			if (u.y + 1 < this.graph[u.x].length)
				this.updateDist(this.graph[u.x][u.y + 1], u);

			if (u.y - 1 > 0)
				this.updateDist(this.graph[u.x][u.y - 1], u);
		}
		return steps;
	}
	
	/**
	 * calculates witch vortex has the least distance and returns that node
	 * @return the node with the shortest distance 
	 */
	public Vortex leastDist() {
		int dist = Integer.MAX_VALUE;
		Vortex returnNode = null;
		for (Vortex node : this.nodequeue) {
			if (node.dist < dist) {
				dist = node.dist;
				returnNode = node;
			}
		}
		nodequeue.remove(returnNode);
		return returnNode;
	}
	
	/**
	 * converts the path of vortexes into string characters representing the moves the player should make
	 * @param node the Vortex representing the tile and moving direction from that tile
	 * @return a string representing the move the player makes from the tile to another
	 */
	public String getPath(Vortex node) {
		if (node.isRoot) return "";
		
		int dx = node.x - node.parent.x;
		int dy = node.y - node.parent.y;
		
		char code = '#';
		if (dx == 1) code = 'r';
		if (dx == -1) code = 'l';
		if (dy == 1) code = 'd';
		if (dy == -1) code = 'u';
		
		return getPath(node.parent)+code;
	}
	
	/**
	 * Updates the distance traveled to reach a node if the distance is shorter then the already known distance. And if so, sets a new parent node to keep track of the way to the node.
	 * @param node the Vortex being updated should the distance be shorter
	 * @param parent the parent node, in this case, the node we arrived from
	 */
	public void updateDist(Vortex node, Vortex parent) {
		if (node == null) return; // nothing to do here
		int alt = node.dist + 1;
		if (alt < node.dist) {
			node.dist++;
			node.parent = parent;
		}

	}
}
