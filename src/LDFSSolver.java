import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A solver based on a limited depth first search
 * @author Jan
 *
 */
public class LDFSSolver { // Limited depth first search
	
	private Board startBoard;
	
	private HashMap<MiniState,MoveTree> alreadySeen = new HashMap<MiniState,MoveTree>();
	// May prevent garbage to be collected. use weak ref?
	
	private MoveTree tree = new MoveTree(null, null); // root of the tree
	
	public LDFSSolver(Board startBoard) {
		this.startBoard = startBoard;
	}
	
	public List<Move> solve(int depthLimit, int step) {
		int currentMaxDepth = 0;
		try {
			while (currentMaxDepth < depthLimit) {
				currentMaxDepth += step;
				System.out.println("Trying to solve with max depth of " + currentMaxDepth);
				solveStep(currentMaxDepth, tree);
			}
		} catch (SolutionFoundException e) {
			return e.solution;
		}
		return null;
	}
	
	private void solveStep(int limit, MoveTree subtree) throws SolutionFoundException {
		if (subtree.isFinished() || subtree.getDepth() > limit) return;
		
		if (!subtree.seen) {
			subtree.seen = true;
			Board currentBoard = startBoard.partialClone();
			currentBoard.move(subtree.getMoveChain());
			
			
			currentBoard.calculateMaps();

			if (currentBoard.isSolved()) throw new SolutionFoundException(subtree.getMoveChain());
			
			if (currentBoard.isDeadlocked()) {
				subtree.makeFinished();
				return;
			}
			
			MiniState ministate = new MiniState(currentBoard);
			MoveTree otherSubtree = alreadySeen.get(ministate);
			
			if (otherSubtree == null ) {
				alreadySeen.put(ministate, subtree);
				subtree.children = MoveTree.wrapMoves(currentBoard.getPossibleMoves(), subtree);
			} else {
				// we already had this state
				if (otherSubtree.isFinished()) {
					// there is no solution this way, move along
					subtree.makeFinished();
					return;
				}
				if (otherSubtree.getDepth() > subtree.getDepth()) {
					// if the state was on a deeper level than now, move its children here
					subtree.children = otherSubtree.children;
					otherSubtree.children = null;
					for (MoveTree child : subtree.children) {
						child.setParent(subtree);
					}
					// take over
					otherSubtree.makeFinished();
					// and note that this is the currently best solution
					alreadySeen.put(ministate, subtree);
				} else {
					// else forget about this, other tree handles it
					subtree.makeFinished();
					return;
				}
			}
			
		}
		
		for (Iterator<MoveTree> iter = subtree.children.iterator(); iter.hasNext();) {
			MoveTree child = iter.next();
			solveStep(limit, child);
			if (child.isFinished()) {
				iter.remove();
			}
		}
		
		if (subtree.children.isEmpty()) {
			subtree.makeFinished();
			return;
		}		
		
	}
	
	private class SolutionFoundException extends Exception {
		private static final long serialVersionUID = -7670753785339780577L;
		public final List<Move> solution;
		public SolutionFoundException(List<Move> solution) {
			this.solution = solution;
		}
	}
}
