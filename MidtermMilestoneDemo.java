import java.util.List;


public class MidtermMilestoneDemo {
	public static void main(String[] args) throws Exception {
		final int level = 2;
		Board b = BoardLoader.loadFromServer(level);
		
		LDFSSolver solver = new LDFSSolver(b);
		List<Move> solution = solver.solve(1000, 10);
		
		PlayerMove playerMover = new PlayerMove(b, solution);
		System.out.println(BoardLoader.getServerReply(level, playerMover.findPath()));

	}
}
