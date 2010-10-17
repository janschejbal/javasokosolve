import java.util.LinkedList;
import java.util.List;

/** a element (node) of the move tree.
 * each node represents one movement of a box
 *  */
public class MoveTree {
	public final Move move;
	private MoveTree parent;
	private int depth;
	private Board board;
	
	public LinkedList<MoveTree> children;
	
//	public boolean seen;
	
//	private boolean finished;
	
	public int intFlag;
	
	
	/**
	 * @param move the move represented by this node
	 * @param parent the parent node
	 */
	private MoveTree(Move move, MoveTree parent) {
		this.move = move;
		this.parent = parent;
		this.depth = parent == null ? 0 : parent.depth + 1;
	}
	
	/**
	 * Creates the root node of a move tree
	 * @param startBoard the starting board
	 */
	public MoveTree(Board startBoard) {
		this(null, null);
		this.board = startBoard;
	}
	
	public List<Move> getMoveChain() {
		if (parent == null) return new LinkedList<Move>();
		List<Move> result = parent.getMoveChain();
		result.add(move);
		return result;
	}
	
	public Board getBoard() {
		if (board != null) return board;
		
		board = parent.getBoard().partialClone();
		board.move(move.box, move.direction);
		return board;
	}
	
	public static LinkedList<MoveTree> wrapMoves(List<Move> moves, MoveTree parent) {
		LinkedList<MoveTree> result = new LinkedList<MoveTree>();
		for (Move m : moves) {
			result.add(new MoveTree(m,parent));
		}
		return result;
	}
	
	public MoveTree getParent() {
		return parent;
	}
	
	@Deprecated
	public void setParent(MoveTree parent) {
		this.parent = parent;
		fixDepth();
	}
	
	@Deprecated
	private void fixDepth() {
		int newDepth = parent == null ? 0 : parent.depth + 1;
		this.depth = newDepth;
		if (children != null) {
			for (MoveTree child : children) {
				child.fixDepth();
			}
		}
	}
	
	public int getDepth() {
		return depth;
	}

	public void makeFinished() {
		children = null; // free memory!
	}

	 
}
