

import java.util.Arrays;




/**
 * A class containing enough information to detect repeated situations.
 * A repeated situation is where
 * 	a) the boxes are at the same position and
 *  b) the player can reach the same area.
 * To save memory, a binary format is used (TODO).
 * @author Jan
 *
 */
@Deprecated
public class MiniStateFull {
	//private final Pos[] boxes;
	private final int[] boxpositions;
	//private final boolean[][] reachable;
	private final int hashcode;
	private int firstReachableX = -1;
	private int firstReachableY = -1;
	
	/**
	 * Captures the MiniState of a board. The board needs to have its maps calculated.
	 * @param source the board which should be captured
	 */
	public MiniStateFull(Board source) {
//		this.boxes = new Pos[source.boxes.length];
//		for (int i = 0; i < source.boxes.length; i++) {
//			this.boxes[i] = source.boxes[i].clone();
//		}
		
		this.boxpositions = new int[source.boxes.length*2];
		int storeAt = 0;
		for (int i = 0; i < source.boxes.length; i++) {
			this.boxpositions[storeAt++] = source.boxes[i].x;
			this.boxpositions[storeAt++] = source.boxes[i].y;
		}
		
		//reachable = new boolean[source.reachable.length][];
		//for (int x = 0; x < source.reachable.length; x++) {
		//	this.reachable[x] = source.reachable[x].clone();
		//}
		
		// test: save only the first reachable position. should be enough (together with the boxes!)
		for (int x = 0; x < source.reachable.length; x++) {
			for (int y = 0; y < source.reachable[0].length; y++) {
				if (source.reachable[x][y]) {
					firstReachableX = x;
					firstReachableY = y;
					break;
				}
			}
		}

		
		
		
		hashcode = Arrays.deepHashCode(source.boxes) ^ Arrays.deepHashCode(source.reachable);
		
	}
	
	@Override
	public int hashCode() {
		return hashcode;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			MiniStateFull other = (MiniStateFull)obj;
			if (hashcode != other.hashcode) return false;
			//return Arrays.deepEquals(boxes, other.boxes) && Arrays.deepEquals(reachable, other.reachable);
			return Arrays.equals(boxpositions, other.boxpositions) &&
				firstReachableX == other.firstReachableX &&
				firstReachableY == other.firstReachableY;
		} catch (Exception e) {
			return false; // nulls, other classes, ...
		}
	}
	
}