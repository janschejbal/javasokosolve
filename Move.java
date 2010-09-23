


/**
 * A class representing information about a move
 * @author Jan
 */
public class Move {
	/** the number of the box that is moved */
	public final Pos box;
	
	/** the direction in which the box is moved (1: up, 2: right, 3: down, 4: left) */
	public final int direction;

	/**
	 * @param box the number of the box that is moved 
	 * @param direction the direction in which the box is moved (1: up, 2: right, 3: down, 4: left) 
	 */
	public Move(Pos box, int direction) {
		this.box = box;
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		String dirstr = "?";
		switch (direction) {
		case 1: dirstr = "up"; break;
		case 2: dirstr = "right"; break;
		case 3: dirstr = "down"; break;
		case 4: dirstr = "left"; break;
		}
		return "Move("+box+","+dirstr+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			Move other = (Move)obj;
			return other.box.equals(this.box) && other.direction == this.direction;
		} catch (Exception e) {
			return false; // null, wrong class, ...
		}
	}
	

}