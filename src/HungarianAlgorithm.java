
/**
 * This class implements the Hungarian Algorithm for assigning boxes to targets
 * to achieve the lowest possible total cost (=distance)
 * 
 * see http://en.wikipedia.org/wiki/Hungarian_algorithm for a description of the algorithm.
 * 
 * unfortunately, it is too slow.
 * 
 */
public class HungarianAlgorithm {
	
	private int originalMatrix[][];
	private int matrix[][];
	
	/** rowAssignment[n] stores which column is assigned to row n (-1 if unassigned)*/
	private int[] rowAssignment;
	private int[] colAssignment;
	private int assignedCount;
	
	private HungarianAlgorithm(int matrix[][]) {
		this.originalMatrix = matrix;
		this.matrix = new int[matrix.length][];
		for (int i = 0; i < matrix.length; i++) {
			this.matrix[i] = matrix[i].clone();	
		}
		
		resetAssignments();
	}

	private void resetAssignments() {
		rowAssignment = new int[matrix.length];
		colAssignment = new int[matrix[0].length];
		for (int i = 0; i < rowAssignment.length; i++) {
			rowAssignment[i] = -1;
		}
		for (int i = 0; i < colAssignment.length; i++) {
			colAssignment[i] = -1;
		}
		assignedCount = 0;
	}
	
	/**
	 * Calculates the min-cost assignment for a matrix and returns the minimum cost
	 * @param matrix the matrix, as int[row][column]
	 * @return the minimum total cost
	 */
	public static int minCost(int matrix[][]) {
		
		HungarianAlgorithm algo = new HungarianAlgorithm(matrix);
		return algo.calculateMinCost();
		
	}

	private void subtractRows() {
		for (int row = 0; row < matrix.length; row++) {
			// calculate minimum
			int min = Integer.MAX_VALUE;
			for (int col = 0; col < matrix[0].length; col++) {
				if (matrix[row][col] < min) min = matrix[row][col];
			}
			// subtract
			for (int col = 0; col < matrix[0].length; col++) {
				matrix[row][col] -= min;
			}
		}
	}
	
	private void subtractCols() {
		for (int col = 0; col < matrix[0].length; col++) {
			// calculate minimum
			int min = Integer.MAX_VALUE;
			for (int row = 0; row < matrix.length; row++) {
				if (matrix[row][col] < min) min = matrix[row][col];
			}
			// subtract
			for (int row = 0; row < matrix[0].length; row++) {
				matrix[row][col] -= min;
			}
		}
	}
	
	/**
	 * Tries to assign until no more rows can be assigned
	 * @return true if all rows have been assigned (i.e. solution is found)
	 */
	private boolean assign() {
		if (assignedCount == matrix.length) return true;
		// try to assign a single node, if it fails assign any
		while (assignStepAssignSingles() || assignStepAssignAny()) {
			if (assignedCount == matrix.length) return true;
		}
		return false;
	}
	
	/**
	 * Tries to assign as many rows to columns as possible in one iteration over the matrix.
	 * Only rows with just one zero column get assigned.
	 * @return true if at least one row is assigned
	 */
	private boolean assignStepAssignSingles() {
		//System.out.println("doing single assign");
		boolean couldAssign = false;
		for (int row = 0; row < matrix.length; row++) {
			if (rowAssignment[row] != -1) continue; // already assigned
			
			int zeroCol = -1;
			for (int col = 0; col < matrix[0].length; col++) {
				if (colAssignment[col] != -1) continue;
				if (matrix[row][col] == 0) {
					if (zeroCol != -1) { // more than one zero
						zeroCol = -1;
						break;
					}
					zeroCol = col;
				}
			}
			
			if (zeroCol == -1) continue; // no zero
			
			if (colAssignment[zeroCol] == -1) {
				// row assignable, col has zero, col unassigned -> assign
				setAssignment(row, zeroCol);
				couldAssign = true;
				continue;
			}
		}
		return couldAssign;
	}

	private void setAssignment(int row, int col) {
		//System.out.println("assigned " +row + " to "+ col);
		rowAssignment[row] = col;
		colAssignment[col] = row;
		assignedCount++;
	}

	/**
	 * Tries to assign a row to a column.
	 * The first assignable row get assigned, even if there are multiple zeros.
	 * @return true if at least one row is assigned
	 */
	private boolean assignStepAssignAny() {
		//System.out.println("doing any assign");
		for (int row = 0; row < matrix.length; row++) {
			if (rowAssignment[row] != -1) continue; // already assigned
			
			for (int col = 0; col < matrix[0].length; col++) {
				if (colAssignment[col] != -1) continue;
				if (matrix[row][col] == 0) {
					setAssignment(row, col);
					return true;
				}
			}			
		}
		return false;
	}

		
	private boolean[] markedRows;
	private boolean[] markedCols;

	private void doRowMarkingStep() {
		//System.out.println("Doing row marking step");
		//printMatrix(matrix);
		
		markedRows = new boolean[matrix.length];
		markedCols = new boolean[matrix[0].length];
		
		// do the marking
		for (int row = 0; row < matrix.length; row++) {
			if (rowAssignment[row] == -1) markRow(row);
		}
		
		// now assume lines drawn through all MARKED columns and UNmarked rows.
		
		// find lowest value not hidden by a line
		int min = Integer.MAX_VALUE;
		for (int row = 0; row < matrix.length; row++) {
			if (!markedRows[row]) continue; // line!
			for (int col = 0; col < matrix[0].length; col++) {
				if (markedCols[col]) continue; // line!
				if (matrix[row][col] < min) min = matrix[row][col];
			}
		}
		
		// add/subtract
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[0].length; col++) {
				if (markedRows[row]) matrix[row][col] -= min;
				if (markedCols[col]) matrix[row][col] += min;
			}
		}
	}


	private void markRow(int row) {
		if (markedRows[row]) return;
		markedRows[row] = true;
		
		for (int col = 0; col < matrix[0].length; col++) {
			if (matrix[row][col] == 0) markCol(col);
		}
	}

	private void markCol(int col) {
		if (markedCols[col]) return;
		markedCols[col] = true;

		for (int row = 0; row < matrix.length; row++) {
			if (rowAssignment[row] == col) markRow(row);
		}
		
	}

	private int calculateMinCost() {
		while (true) { // repeat until solution found
			// Step 1
			//printMatrix(matrix);
			//System.out.println("step1");

			subtractRows();
			//if (assign()) return findSolution();
	
			// Step 2
			//printMatrix(matrix);
			//System.out.println("step2");
			//resetAssignments();
			subtractCols();
			if (assign()) return findSolution();
	
			//printMatrix(matrix);
			// Step 3+4 (initial assignments are already done)
			doRowMarkingStep();
			resetAssignments();
			if (assign()) return findSolution();
			resetAssignments();
			//System.out.println("-----------nextloop-------------");
		}
	}

	private int findSolution() {
		int solution = 0;
		for (int row = 0; row < matrix.length; row++) {
			solution += originalMatrix[row][rowAssignment[row]];
		}
		return solution;
	}

	public static void main(String[] args) {
		int[][] matrix = 
				new int[][] {
						{15,19,20,18},
						{14,15,17,14},
						{11,15,15,14},
						{21,24,26,24},
				};
		printMatrix(matrix);
		System.out.println(minCost(matrix));
	}
	
	/**
	 * Prints a matrix on stdout. Adapted from {@link Board#printMap(int[][])}.
	 * @param matrix the matrix to print
	 */
	public static void printMatrix(int[][] matrix) {
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[0].length; col++) {
				System.out.print(matrix[row][col] > 99 ? "## " : String.format("%2d ", matrix[row][col]));
			}
			System.out.println();
		}		
	}
}
