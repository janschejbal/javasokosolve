import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;



public class BoardLoader {
	
	public static Board loadFromString(String boardstring) {
		String[] lines = boardstring.replace("\r\n", "\n").split("\n");
		
		int width = lines[0].length();
		int height = lines.length;
		
		int players = 0;
		int boxcount = 0;
		
		boolean[][] floor = new boolean[width][height];
		boolean[][] boxmap = new boolean[width][height];
		Pos player = null;
		List<Pos> targets = new LinkedList<Pos>();
		
		for (byte row = 0; row < lines.length; row++) {
			String line = lines[row];
			
			if (line.length() != width) throw new RuntimeException("Invalid board - line length not constant");
			
			for (byte col = 0; col < width; col++) {
				floor[col][row] = true;
				char c = line.charAt(col);
				switch (c) {
				case '#': // wall
					floor[col][row] = false; 
					break;
				case '@': // player
					players++; 
					player = new Pos(col, row); 
					break;
				case '+': // player on target
					players++; 
					player = new Pos(col, row);
					targets.add(new Pos(col, row)); break;
				case '$': // box
					boxmap[col][row] = true;
					boxcount++;
					break;
				case '*': // box on target
					boxmap[col][row] = true;
					boxcount++;
					targets.add(new Pos(col, row));
					break;
				case '.': // target
					targets.add(new Pos(col, row));
					break;
				case ' ': // floor
					break;
				default: throw new RuntimeException("Invalid board - unknown symbol '" + c + "'");
				}
			}
		}
		
		if (boxcount != targets.size()) {
			throw new RuntimeException("Invalid boards - " +
					boxcount + " boxes but " + targets.size() + " targets");
		}
		if (players == 0) {
			throw new RuntimeException("Invalid boards - no player");
		}
		if (players > 1) {
			throw new RuntimeException("Invalid boards - multiple players");
		}
		
		Board b = new Board(width, height, boxcount);
		b.floor = floor;
		b.player = player;
		b.boxmap =  boxmap;
		b.targets = targets.toArray(new Pos[0]);
		
		return b;
	}
	
	public static Board loadFromLevelFile(String filepath, int number) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(filepath));
		String last = "";
		StringBuffer boardString = null;
		int skippedBoards = 0;
		boolean inBoard = false;
		boolean inMyBoard = false;
		
		while (true) {
			last = input.readLine();
			
			boolean isBoardLine = last != null && last.contains("#"); // ugly hack
			
			if (!inBoard && isBoardLine) {
				// start board
				inBoard = true;
				boardString = new StringBuffer();
				if (skippedBoards == number) {
					inMyBoard = true;
				}
			}
			
			if (inBoard) {
				if (isBoardLine) {
					// continue board
					if (inMyBoard) {
						boardString.append(last + "\n");
					} // else just ignore it
				} else {
					if (inMyBoard) {
						// we have our board
						return loadFromString(padBoard(boardString.toString()));
					} else {
						skippedBoards++;
						inBoard = false;
					}
				}
			}
			
		}
	}
	
	public static Board loadFromServer(int boardNumber) throws IOException {
		// some lines taken from client published in BILDA
		Socket socket = new Socket("cvap103.nada.kth.se",5555);
        InputStream inRaw = socket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inRaw));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println(boardNumber);
        
        byte[] boardbytes = new byte[4*1024];
        int bytesRead = inRaw.read(boardbytes);
        String boardString = new String(boardbytes,0,bytesRead);
        //System.out.println(boardString);
        
        out.close();
        in.close();
        socket.close();
        
        return loadFromString(boardString);
	}
	
	/**
	 * Checks if a given solution is correct.
	 * 
	 * @param level The level number (same as given to {@link #loadFromServer(int)})
	 * @param solution The solution string, for example "rrDldRRR"
	 * 
	 * @return true if the server considered the solution correct, false if it considered it wrong
	 * 
	 * @throws UnknownHostException if the host name lookup for the server fails.
	 * @throws IOException if any IO problem during communication with the server occurs.
	 * 
	 * @throws RuntimeException if the server returns an unexpected answer.
	 */
	public static boolean checkSolution(int level, String solution) throws UnknownHostException, IOException {
		String result = getServerReply(level, solution);
        
        if (result.startsWith("Good solution")) {
        	return true;
        } else if (result.startsWith("Wrong solution")) {
        	return false;
        } else {
        	throw new RuntimeException("Unknown response to solution: " + result);
        }
	}

	public static String getServerReply(int level, String solution)
			throws UnknownHostException, IOException {
		Socket socket = new Socket("cvap103.nada.kth.se",5555);
        InputStream inRaw = socket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inRaw));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println(level);
        
        byte[] boardbytes = new byte[4*1024];
        inRaw.read(boardbytes); // throw away board
  
        out.println(solution);
        
        String result = in.readLine();

        out.close();
        in.close();
        socket.close();
		return result;
	}

	private static String padBoard(String boardstring) {
		int max = Integer.MIN_VALUE;
		String[] lines = boardstring.replace("\r\n", "\n").split("\n");
		StringBuffer result = new StringBuffer();
		for (String line : lines) {
			if (line.length() > max) max = line.length();
		}
		for (String line : lines) {
			result.append(String.format("%1$-"+max+"s\n", line));
		}
		return result.toString();
	}
}
