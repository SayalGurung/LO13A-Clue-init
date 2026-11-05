package clueGame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The game board (singleton). Reads a setup file (the legend of rooms and
 * spaces) and a layout file (the grid of cells) and holds the resulting
 * game data.
 *
 * Setup file lines:   Room|Space, name, initial      ("//" starts a comment)
 * Layout file cells:  initial, optionally followed by one extra character:
 *     '#' label cell, '*' room center, '^' 'v' '<' '>' doorway direction,
 *     any other character = secret passage to the room with that initial.
 */
public class Board {
	private static final String DATA_DIR = "data/";

	private int numRows;
	private int numColumns;
	private BoardCell[][] grid;
	private Map<Character, Room> roomMap;
	private String layoutConfigFile;
	private String setupConfigFile;

	/*
	 * variable and methods used for singleton pattern
	 */
	private static Board theInstance = new Board();

	// constructor is private to ensure only one can be created
	private Board() {
		super();
	}

	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}

	/*
	 * initialize the board (since we are using singleton pattern). This is the
	 * "normal" path used by the game, so it traps file problems and reports
	 * them instead of crashing.
	 */
	public void initialize() {
		try {
			loadSetupConfig();
			loadLayoutConfig();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find a config file: " + e.getMessage());
		} catch (BadConfigFormatException e) {
			System.err.println("Bad config file: " + e.getMessage());
		}
	}

	/** Set the names of the two config files (looked up in the data directory). */
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile) {
		this.layoutConfigFile = DATA_DIR + layoutConfigFile;
		this.setupConfigFile = DATA_DIR + setupConfigFile;
	}

	/**
	 * Load the setup file into the room map.
	 * Memory is (re)allocated here, not at declaration, because the Board is a
	 * singleton and may be loaded many times (e.g. by different tests).
	 */
	public void loadSetupConfig() throws BadConfigFormatException, FileNotFoundException {
		Map<Character, Room> rooms = new HashMap<>();
		for (String line : readLines(setupConfigFile)) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("//"))
				continue;
			String[] parts = line.split(",", -1);
			if (parts.length != 3)
				throw new BadConfigFormatException("Setup line must be 'type, name, initial': " + line);
			String type = parts[0].trim();
			String name = parts[1].trim();
			String initial = parts[2].trim();
			if (!type.equals("Room") && !type.equals("Space"))
				throw new BadConfigFormatException("Unknown type '" + type + "' in setup line: " + line);
			if (name.isEmpty())
				throw new BadConfigFormatException("Missing name in setup line: " + line);
			if (initial.length() != 1)
				throw new BadConfigFormatException("Initial must be one character in setup line: " + line);
			if (rooms.containsKey(initial.charAt(0)))
				throw new BadConfigFormatException("Duplicate initial '" + initial + "' in setup file");
			rooms.put(initial.charAt(0), new Room(name));
		}
		roomMap = rooms;
	}

	/**
	 * Load the layout file into the grid. Requires loadSetupConfig() to have
	 * been called first (every initial must be in the legend).
	 */
	public void loadLayoutConfig() throws BadConfigFormatException, FileNotFoundException {
		if (roomMap == null)
			throw new BadConfigFormatException("Setup file must be loaded before the layout file");

		// read the whole file first so we know the grid size
		List<String[]> rows = new ArrayList<>();
		for (String line : readLines(layoutConfigFile)) {
			if (line.trim().isEmpty())
				continue;
			rows.add(line.split(",", -1));
		}
		if (rows.isEmpty())
			throw new BadConfigFormatException("Layout file is empty: " + layoutConfigFile);
		int cols = rows.get(0).length;
		for (int r = 0; r < rows.size(); r++)
			if (rows.get(r).length != cols)
				throw new BadConfigFormatException("Row " + (r + 1) + " has " + rows.get(r).length
						+ " columns but expected " + cols);

		BoardCell[][] newGrid = new BoardCell[rows.size()][cols];
		for (int r = 0; r < rows.size(); r++)
			for (int c = 0; c < cols; c++)
				newGrid[r][c] = parseCell(r, c, rows.get(r)[c].trim());

		grid = newGrid;
		numRows = rows.size();
		numColumns = cols;
	}

	// Build one cell from its text in the layout file
	private BoardCell parseCell(int row, int col, String text) throws BadConfigFormatException {
		String where = " (row " + (row + 1) + ", column " + (col + 1) + ")";
		if (text.length() < 1 || text.length() > 2)
			throw new BadConfigFormatException("Bad cell '" + text + "'" + where);
		char initial = text.charAt(0);
		Room room = roomMap.get(initial);
		if (room == null)
			throw new BadConfigFormatException("Room '" + initial + "' is not in the setup file" + where);

		BoardCell cell = new BoardCell(row, col);
		cell.setInitial(initial);
		if (text.length() == 2) {
			char extra = text.charAt(1);
			switch (extra) {
			case '#':
				cell.setLabel(true);
				room.setLabelCell(cell);
				break;
			case '*':
				cell.setRoomCenter(true);
				room.setCenterCell(cell);
				break;
			case '^':
				cell.setDoorDirection(DoorDirection.UP);
				break;
			case 'v':
				cell.setDoorDirection(DoorDirection.DOWN);
				break;
			case '<':
				cell.setDoorDirection(DoorDirection.LEFT);
				break;
			case '>':
				cell.setDoorDirection(DoorDirection.RIGHT);
				break;
			default:
				if (!roomMap.containsKey(extra))
					throw new BadConfigFormatException("Secret passage to unknown room '" + extra + "'" + where);
				cell.setSecretPassage(extra);
			}
		}
		return cell;
	}

	// Read every line of a file; a missing file becomes FileNotFoundException
	private List<String> readLines(String fileName) throws FileNotFoundException {
		if (fileName == null)
			throw new FileNotFoundException("Config file name was never set (call setConfigFiles)");
		List<String> lines = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = in.readLine()) != null) {
				// an editor/Excel may put a byte-order mark at the start of the file
				if (lines.isEmpty() && line.startsWith("﻿"))
					line = line.substring(1);
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Cannot open " + fileName);
		} catch (IOException e) {
			throw new FileNotFoundException("Cannot read " + fileName + ": " + e.getMessage());
		}
		return lines;
	}

	public Room getRoom(char initial) {
		return roomMap.get(initial);
	}

	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}
}
