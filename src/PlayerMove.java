import java.util.List;

/**
 * A class to move the player around to puch the boxes
 * @author Calle
 *
 */
public class PlayerMove {
	private Board startBoard;
	private List<Move> moveSequence;

	public PlayerMove(Board startBoard, List<Move> moveSequence) {
		this.startBoard = startBoard;
		this.moveSequence = moveSequence;
	}
}
