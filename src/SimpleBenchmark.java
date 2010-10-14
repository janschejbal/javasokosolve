import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * A class derived from SolvesServerBoards. It randomly tries to solve 100 levels and then outputs the percentage solved,
 * and the time it took to solve them in minutes.
 * 
 * @author aiquen
 *
 */
public class SimpleBenchmark {
	private static final int TIMEOUT = 60000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException {		
		long startTime = (new Date()).getTime();
		int solved = 0;
		long beginTime = (new Date()).getTime();
		System.out.println("Solving...");
		int taken[] = new int[100];
		for (int i = 1; i<100; i++) {
			Random generator = new Random();
			int rndLevel = 1001;
			while (!Arrays.asList(taken).contains(rndLevel)){
				rndLevel = generator.nextInt(1001);
			}
			solved += testBoard(rndLevel);
			System.gc();
		}
		double neededTime = (new Date()).getTime() - beginTime;
		System.out.print("Solved percentage: " + String.valueOf(solved) + "%");
		System.out.println("Time: " + String.valueOf(neededTime / 60000));
	}
	
	private static int testBoard(int number) throws IOException, InterruptedException {
		Board b = BoardLoader.loadFromServer(number);
				
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
