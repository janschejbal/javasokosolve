public class Vortex {
		public int dist;
		public Vortex parent;
		public boolean isRoot;
		public int x, y;

	public Vortex(int x, int y, int dist, Vortex parent, boolean isRoot) {
		this.x = x;
		this.y = y;
		this.dist = dist;
		this.parent = parent;
		this.isRoot = isRoot;
	}

	public boolean getIsRoot() {
		return this.isRoot;
	}
}
