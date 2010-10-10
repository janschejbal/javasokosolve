import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A solver based on a limited depth first search
 * @author Jan
 *
 */
public class LDFSSolver { // Limited depth first search
	
	private Board startBoard;
	private long expanded = 0;
	long lastOutput = 0;
	private HashSet<MiniState> alreadySeen = new HashSet<MiniState>();
	
	public LDFSSolver(Board startBoard) {
		this.startBoard = startBoard;
	}
	
	public List<Move> solve(int depthLimit, int step) {
		int currentMaxDepth = 0;
		try {
			while (currentMaxDepth < depthLimit) {
				currentMaxDepth += step;
				System.out.println("Trying to solve with max depth of " + currentMaxDepth);
				// always use a fresh tree, always use fresh seen map!
				alreadySeen = new HashSet<MiniState>();
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
			
			
			if (!alreadySeen.contains(ministate)) {
				alreadySeen.add(ministate);
				subtree.children = MoveTree.wrapMoves(currentBoard.getPossibleMoves(), subtree);
				
				// TODO REMOVE DEBUGGING
				expanded++;
				/*
				if ((expanded % 10000) == 0) {
					System.out.println();
					System.out.println("Expanded: " + expanded);
					System.out.println("Millis since last output: " + (System.currentTimeMillis()-lastOutput));
					lastOutput = System.currentTimeMillis();
					System.out.println(currentBoard.toString());
					System.out.println();
				}
				*/
				
				
			} else {
				// we already had this state
					subtree.makeFinished();
					return;
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
