import java.io.IOException;
import java.util.Date;
import java.util.List;


public class Tests {

	// TODO Idea: Multithreading by separating trees?
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {		
		Board b = BoardLoader.loadFromLevelFile(
//				"C:\\tools\\apps\\games\\SYASokoban\\data\\sokoban\\levels\\dimitri_and_yorick.txt",
				"C:\\tools\\apps\\games\\SYASokoban\\data\\sokoban\\levels\\default.txt",
				0);
		
		
//		Board b = BoardLoader.loadFromServer(1);

		System.out.println(b);
		System.out.println("Solving...");

		long startTime = (new Date()).getTime();
		
		LDFSSolver solver = new LDFSSolver(b);
		printSolution(b, solver.solve(1000,10));

		double neededTime = (new Date()).getTime() - startTime;
		
		System.out.format("Run took %.2f seconds (%.2f minutes)\n", neededTime/1000, neededTime/60000);
		
		// does not find solution for default level 0, so there is a bug
		// probably something excludes moves that should not be excluded
		// or does not correctly handle shorter paths to already visited nodes
	}
	
	public static void printSolution(Board startBoard, List<Move> solution) {
		System.out.println(startBoard);
		
		if (solution == null) {
			System.out.println("No solution found.");
			return;
		}

		for (Move m : solution) {
			startBoard.move(m.box, m.direction);
			System.out.println(m);
			System.out.println();
			System.out.println(startBoard);
		}
	}

}
