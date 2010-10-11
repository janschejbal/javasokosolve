import java.io.IOException;
import java.util.Date;
import java.util.List;


public class SolveServerBoards {
	
	private static final int TIMEOUT = 30000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException {		
		for (int i = 1; i<1000; i++) {
			testBoard(i);
			System.gc();
		}
	}
	
	private static void testBoard(int number) throws IOException, InterruptedException {
		Board b = BoardLoader.loadFromServer(number);
		System.out.format("Solving board number %3d... ", number);
				
		SolverThread thread = new SolverThread(b);

		long startTime = (new Date()).getTime();
		thread.start();

		while (!thread.isFinished) {
			synchronized (thread.waitingObject) {
				thread.waitingObject.wait(100);
			}
			double elapsedTime = (new Date()).getTime() - startTime;
			if (elapsedTime > TIMEOUT) {
				thread.stop();
				System.out.format("TIMEOUT (%.2f seconds = %.2f minutes)\n", elapsedTime/1000, elapsedTime/60000);
				return;				
			}
		}
		double neededTime = (new Date()).getTime() - startTime;

		List<Move> solution = thread.solution;
		if (solution == null) {
			System.out.format(" NO SOLUTION FOUND (%.2f seconds = %.2f minutes)\n", neededTime/1000, neededTime/60000);
			return;
		}
		
		PlayerMove player = new PlayerMove(b, solution);
		String solutionString = player.findPath();

		boolean result = BoardLoader.checkSolution(number, solutionString);
		//System.out.println(solutionString);
		System.out.print(result? "ok" : "WRONG");
		
		System.out.format(" (%.2f seconds = %.2f minutes)\n", neededTime/1000, neededTime/60000);
	}

}
