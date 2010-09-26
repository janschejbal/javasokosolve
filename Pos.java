


/**
 * A class for storing positions (x/y coordinate pairs)
 * TODO: REMOVE from usage whereever possible. This class wastes LOTS of memory.
 * @author Jan
 */
public class Pos implements Cloneable {
	public byte x;
	public byte y;
	
	public Pos(byte x, byte y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @param other another position
	 * @return the ("city-block") distance to the given position 
	 */
	public int distance(Pos other) {
		return Math.abs(other.x-this.x)+Math.abs(other.y-this.y);
	}
	
	@Override
	public boolean equals(Object arg0) {
		try {
			Pos other = (Pos)arg0;
			return x == other.x && y == other.y;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return x*256*256 + y;
	}
	
	@Override
	public Pos clone() {
		return new Pos(x,y);
	}
	
	@Override
	public String toString() {
		return "["+x+";"+y+"]";
	}
}