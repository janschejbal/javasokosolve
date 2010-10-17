import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * A class derived from SolvesServerBoards. It randomly tries to solve 100 levels and then outputs the percentage solved,
 * and the time it took to solve them in minutes.
 * 
 * (deprecated, use adapted solveServerBoards instead)
 * 
 * @author aiquen
 *
 */
@Deprecated
public class SimpleBenchmark {
	private static final int TIMEOUT = 60000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException {		
		int solved = 0;
		long beginTime = (new Date()).getTime();
		System.out.println("Solving...");
		for (int i = 1; i<50; i++) {
			solved += testBoard(i);
			System.gc();
		}
		double neededTime = (new Date()).getTime() - beginTime;
		System.out.print("Solved percentage: " + String.valueOf(solved/0.5) + "%");
		System.out.println("Time: " + String.valueOf(neededTime / 60000));
	}
	
	private static int testBoard(int number) throws IOException, InterruptedException {
		Board b = BoardLoader.loadFromServer(number);
		
		SolverThread thread = new SolverThread(b);

		long startTime = (new Date()).getTime();
		thread.start();

		while (!thread.isFinished) {
			synchronized (thread.waitingObject) {
				thread.waitingObject.wait(200);
			}
			double elapsedTime = (new Date()).getTime() - startTime;
			if (elapsedTime > TIMEOUT) {
				thread.stop();
				System.out.println("TIMEOUT: level " + String.valueOf(number));
				return 0;				
			}
		}

		List<Move> solution = thread.solution;
		if (solution == null) {
			return 0;
		}
		
		PlayerMove player = new PlayerMove(b, solution);
		String solutionString = player.findPath();

		boolean result = BoardLoader.checkSolution(number, solutionString);

		if (result){
			return 1;
		} else {
			return 0;
		}
	}
}
