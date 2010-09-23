import java.util.Arrays;
import java.util.List;

/**
 * 
 */

public class FoundPathInfo {
	public int iteration;
	private final byte[] moveData; // yes, packing stuff into byte[]s saves a LOT of memory.
	
	public FoundPathInfo(int iteration, List<Move> moves) {
		this.iteration = iteration;
		this.moveData = compactMoves(moves);
	}
	
	public int size() {
		return moveData.length/3;
	}
	
	public boolean movesEqual(List<Move> otherMoves) {
		return Arrays.equals(moveData, compactMoves(otherMoves));
	}
	
	private byte[] compactMoves(List<Move> moves) {
		byte[] result = new byte[moves.size()*3];
		int pos = 0;
		for (Move move : moves) {
			result[pos++] = move.box.x;
			result[pos++] = move.box.y;
			result[pos++] = (byte)move.direction;
		}
		return result;
	}

}