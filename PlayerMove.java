import java.util.LinkedList;
import java.util.List;
import java.rmi.NotBoundException;

/**
 * A class to move the player around to push the boxes
 * @author Calle
 *
 */
public class PlayerMove {
	private Board startBoard;
	private List<Move> moveSequence;
	private Vortex[][] graph;
	private List<Vortex> nodequeue;

	// Initialize the class by setting variables, then start the search for the path to take
	public PlayerMove(Board startBoard, List<Move> moveSequence) {
		this.startBoard = startBoard;
		this.moveSequence = moveSequence;
	}

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

	// I use the NotBoundException for now, even if it's not 100% correct, it still close enough
	// in the name to be understood as the "There is no path to your goal"exception. -- aiquen
	public String localPath(Move box) throws NotBoundException {
		String steps = "";
		
		// To use Dijkstras we first build a matrix for the graph
		this.graph = new Vortex[this.startBoard.floor.length][this.startBoard.floor[0].length];
		//Create all vortexes in the matrix
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
	
	public void updateDist(Vortex node, Vortex parent) {
		if (node == null) return; // nothing to do here
		int alt = node.dist + 1;
		if (alt < node.dist) {
			node.dist++;
			node.parent = parent;
		}

	}
}
