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
	private Vortex[] nodequeue;

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
		this.findPath(this.startBoard.player, this.moveSequence); 
			
		
	}

	public void createVortexes() {
		int nodecounter = 0;
		for (int i = 0; i < this.startBoard.floor.lenght; i++) {
			for (int j = 0; j < this.startBoard.floor[0].length; j++) {
				// If it is a reachable tile, add a vortex point there.
				// This will also automatically give us edges
				if (this.startBoard.reachable[i][j]) {
					this.graph[i][j] = new Vortex(i, j, Integer.MAX_INT, null, false);
					nodecounter++;
				}
			}
		}
		this.nodequeue = new Vortex[nodecounter];
	}

	public void findPath(Pos start, List<Move> dir) {
		for (Pos curr : dir) {
		}
	}
			
}
