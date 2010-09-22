import java.util.List;

/**
 * A class to move the player around to puch the boxes
 * @author Calle
 *
 */
public class PlayerMove {
	private Board startBoard;
	private List<Move> moveSequence;
	private List<Vortex>[][] graph;
	private List<Vortex> nodequeue;

	// Initialize the class by setting variables, then start the search for the path to take
	public PlayerMove(Board startBoard, List<Move> moveSequence) {
		this.startBoard = startBoard;
		this.moveSequence = moveSequence;
		
		// To use Dijkstras we first build a matrix for the graph
		this.graph = new Vortex[this.startBoard.floor.length][this.startBoard.floor[0].length];
		//Create all vortexes in the matrix
		this.createVortexes();
		// Set the startingpositions special values
		this.graph[0][0].isRoot = true;
		this.graph[0][0].dist = 0;
		
		// start the search
		this.findPath(this.moveSequence); 
			
		
	}

	public void createVortexes() {
		this.nodequeue = new LinkedList<Vortex>();
		for (int i = 0; i < this.startBoard.floor.lenght; i++) {
			for (int j = 0; j < this.startBoard.floor[0].length; j++) {
				// If it is a reachable tile, add a vortex point there.
				// This will also automatically give us edges
				if (this.startBoard.reachable[i][j]) {
					this.graph[i][j] = new Vortex(i, j, Integer.MAX_INT, null, false);
					this.nodequeue.add(this.graph[i][j]);
				}
			}
		}
		
	}

	public String findPath(List<Move> dir) {
		String movements = "";
		this.createVortexes();
		for (Move curr : dir) {
			try {
				movements += this.localPath(curr.box);
			} catch (NotBoundException e) {
				System.out.println(e.getMessage());
				break;
			}
		}

		return movements;
	}

	// I use the NotBoundException for now, even if it's not 100% correct, it still close enough
	// in the name to be understood as the "There is no path to your goal"exception. -- aiquen
	public String localPath(Pos box) throws NotBoundException {
		String steps = "";
		while (this.nodequeue.size() > 0) {
			Vortex u = this.leastDist();
			if (u.dist == Integer.MAX_INT) {
				throw new NotBoundException("No awailable path to target tile!");
			}
			if (u.x == box.x && u.y == box.y) {
				steps = this.getPath(u);
				return steps;
			}
			if (u.x + 1 < this.graph.size())
				this.updateDist(this.graph[u.x + 1][u.y]);
			
			if (u.x - 1 > 0)
				this.updateDist(this.graph[u.x - 1][u.y]);
			
			if (u.y + 1 < this.graph[u.x].size())
				this.updateDist(this.graph[u.x][u.y + 1]);

			if (u.y - 1 > 0)
				this.updateDist(this.graph[u.x].size());
		}
	}
			
	public Vortex leastDist() {
	
	}

	public String getPath(Vortex node) {

	}
	
}
