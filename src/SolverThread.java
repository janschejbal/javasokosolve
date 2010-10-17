import java.util.List;

/**
 * helper for SolveServerBoards. Runs the solver in a separate thread to make it easy to stop it after a time limit.
 */

public class SolverThread extends Thread {
	public volatile List<Move> solution = null;
	public volatile boolean isFinished = false;
	public final Object waitingObject = new Object();
	
	private Board board;
	
	SolverThread(Board board) {
		this.board = board;
	}
	
	@Override
	public void run() {
		LDFSSolver solver = new LDFSSolver(board);
		List<Move> tmpSolution = solver.solve(3000, 400);
		solution = tmpSolution;
		isFinished = true;
		synchronized (waitingObject) {
			waitingObject.notify();
		}
	}
	
}