


/**
 * A class representing information about a move
 * @author Jan
 */
public class Move implements Comparable<Move> {
	/** the number of the box that is moved */
	public final Pos box;
	
	/** the direction in which the box is moved (1: up, 2: right, 3: down, 4: left) */
	public final int direction;
	
	/** the new sum of distances between boxes and their closest targets if move is performed */
	public int distancSum;

	/**
	 * @param box the number of the box that is moved 
	 * @param direction the direction in which the box is moved (1: up, 2: right, 3: down, 4: left) 
	 */
	public Move(Pos box, int direction) {
		this.box = box;
		this.direction = direction;
	}
	
	/**
	 * 
	 * @param box the number of the box that is moved 
	 * @param direction direction the direction in which the box is moved (1: up, 2: right, 3: down, 4: left)
	 * @param dist sum of distances between boxes and targets for this move
	 */
	public Move(Pos box, int direction, int dist) {
		this.box = box;
		this.direction = direction;
		this.distancSum = dist;
	}
	
	public int getDist() {
		return distancSum;
	}
	
	public void setDist(int dist) {
		this.distancSum = dist;
	}
	
	public int compareTo(Move other) {
		if (this.distancSum < other.distancSum) {
			return -1;
		}
		else if (this.distancSum == other.distancSum) {
			return 0;
		} else {
			return 1;
		}
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