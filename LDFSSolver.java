import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * A solver based on a limited depth first search
 * @author Jan
 *
 */
public class LDFSSolver { // Limited depth first search
	
	private Board startBoard;
	
	private HashMap<MiniState, FoundPathInfo> alreadySeen = new HashMap<MiniState, FoundPathInfo>();
	
	public LDFSSolver(Board startBoard) {
		this.startBoard = startBoard;
	}
	
	public List<Move> solve(int depthLimit, int step) {
		int currentMaxDepth = 0;
		try {
			while (currentMaxDepth < depthLimit) {
				currentMaxDepth += step;
				System.out.println("Trying to solve with max depth of " + currentMaxDepth);
				System.gc();
				solveStep(currentMaxDepth, new MoveTree(null, null)); 
			}
		} catch (SolutionFoundException e) {
			return e.solution;
		}
		return null;
	}
	
	private void solveStep(int limit, MoveTree subtree) throws SolutionFoundException {
		if (subtree.isFinished()) {
			System.out.println("re-visited solved node. this should not happen");
			return;
		}
		
		if (subtree.getDepth() > limit) {
			subtree.makeFinished();
			return;
		}
		
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
			FoundPathInfo otherPath = alreadySeen.get(ministate);
			
			
			if (otherPath != null && otherPath.size() < subtree.getDepth()) {
				// we already had this state on a LOWER level
				// (same does not count, otherwise nothing could be done in the second run
				// unless the "already seen" list is deleted)
				subtree.makeFinished();
				return;
			} else if (otherPath != null && otherPath.size() == subtree.getDepth()) {
				// check if stored path == current path. if not, this is not the shortest way.
				if (otherPath.movesEqual(subtree.getMoveChain())) {
					// we are on a good way
					subtree.children = MoveTree.wrapMoves(currentBoard.getPossibleMoves(), subtree);
				} else {
					// we are not on the shortest path
					subtree.makeFinished();
					return;
				}
			} else if (otherPath != null && otherPath.size() > subtree.getDepth()) {
				// we found a new shortest path. store it for next iteration
				alreadySeen.put(ministate, new FoundPathInfo(limit, subtree.getMoveChain()));
				if (otherPath.iteration == limit) {
					// if we already handled this state in this iteration, return
					subtree.makeFinished();
					return;
				} else {
					// if we have not done it in this iteration (only in a former one): do it!
					otherPath.iteration = limit;
					subtree.children = MoveTree.wrapMoves(currentBoard.getPossibleMoves(), subtree);
				}
			} else {
				alreadySeen.put(ministate, new FoundPathInfo(limit, subtree.getMoveChain()));
				subtree.children = MoveTree.wrapMoves(currentBoard.getPossibleMoves(), subtree);
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
