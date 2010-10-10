import java.lang.ref.WeakReference;
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
	
	private HashMap<MiniState,WeakReference<MoveTree>> alreadySeen = new HashMap<MiniState,WeakReference<MoveTree>>();
	
	public LDFSSolver(Board startBoard) {
		this.startBoard = startBoard;
	}
	
	public List<Move> solve(int depthLimit, int step) {
		int currentMaxDepth = 0;
		try {
			while (currentMaxDepth < depthLimit) {
				currentMaxDepth += step;
				//System.out.println("Trying to solve with max depth of " + currentMaxDepth);
				// always use a fresh tree, always use fresh seen map!
				alreadySeen = new HashMap<MiniState,WeakReference<MoveTree>>();
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
			WeakReference<MoveTree> otherSubtreeRef = alreadySeen.get(ministate);
			
			if (otherSubtreeRef == null ) {
				alreadySeen.put(ministate, new WeakReference<MoveTree>(subtree));
				subtree.children = MoveTree.wrapMoves(currentBoard.getPossibleMoves(), subtree);
			} else {
				// we already had this state
				
				/* commenting out complex handling for now, it is obviously broken
				 *  (as it does not find a solution in 100 steps for the first default sokoban board)
				 // what happens if current node is moved?
				
				MoveTree otherSubtree = otherSubtreeRef.get();
				if (otherSubtree == null || otherSubtree.isFinished()) {
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
					alreadySeen.put(ministate, new WeakReference<MoveTree>(subtree));
				} else {
					// else forget about this, other tree handles it
					 
				for now: just assume that the other subtree takes care of it */
					subtree.makeFinished();
					return;
				/*}*/
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