

import java.util.Arrays;




/**
 * A class containing enough information to detect repeated situations.
 * A repeated situation is where
 * 	a) the boxes are at the same position and
 *  b) the player can reach the same area.
 * To save memory, a compact format is used.
 *
 */
public class MiniState {
	private final byte[] data;
	

	/**
	 * Captures the MiniState of a board. The board needs to have its maps calculated.
	 * @param source the board which should be captured
	 */
	public MiniState(Board source) {
		this.data = new byte[source.boxes.length*2+2];
		int storeAt = 0;
		
		for (Pos box : source.boxes) {
			this.data[storeAt++] = (byte)box.x;
			this.data[storeAt++] = (byte)box.y;
		}		
		
		// save only the first reachable position. should be enough (together with the boxes!)
		for (int x = 0; x < source.reachable.length; x++) {
			for (int y = 0; y < source.reachable[0].length; y++) {
				if (source.reachable[x][y]) {
					this.data[storeAt++] = (byte)x;
					this.data[storeAt++] = (byte)y;
					return;
				}
			}
		}
		// WARNING: REMOVE "RETURN" ABOVE BEFORE PLACING CODE HERE
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			MiniState other = (MiniState)obj;
			return Arrays.equals(data, other.data);
		} catch (Exception e) {
			return false; // nulls, other classes, ...
		}
	}
	
}