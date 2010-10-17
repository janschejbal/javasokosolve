import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/** default entry point */
public class Solve {

	public static void main(String[] args) throws  Exception {
		if (args.length != 1) {
			System.out.println("Usage: java Solve boardnumber");
			System.exit(1);
		}
		
		int boardNumber = Integer.parseInt(args[0],10);
		Board b = BoardLoader.loadFromServer(boardNumber);
		
		LDFSSolver solver = new LDFSSolver(b);
		List<Move> solution = solver.solve(100000, 400);
		
		PlayerMove playerMover = new PlayerMove(b, solution);
		System.out.println(BoardLoader.getServerReply(boardNumber, playerMover.findPath()));

	}

}
