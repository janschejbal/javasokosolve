import java.util.LinkedList;
import java.util.List;

public class MoveTree {
	public final Move move;
	private MoveTree parent;
	private int depth;
	public LinkedList<MoveTree> children;
	
	public boolean seen;
	
	private boolean finished;
	
	public int intFlag;
	
	
	/**
	 * @param move the move represented by this node
	 * @param parent the parent node
	 */
	public MoveTree(Move move, MoveTree parent) {
		this.move = move;
		this.parent = parent;
		this.depth = parent == null ? 0 : parent.depth + 1;
	}
	
	public List<Move> getMoveChain() {
		if (parent == null) return new LinkedList<Move>();
		List<Move> result = parent.getMoveChain();
		result.add(move);
		return result;
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
	
	public void setParent(MoveTree parent) {
		this.parent = parent;
		fixDepth();
	}
	
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
		finished = true;
		children = null; // free memory!
	}

	public boolean isFinished() {
		return finished;
	}
	 
}
