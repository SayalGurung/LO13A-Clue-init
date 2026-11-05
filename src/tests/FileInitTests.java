package tests;

/*
 * Tests that OUR config files (ClueLayout.csv / ClueSetup.txt, the space
 * station board) are loaded properly. See Tests.txt for how the test
 * cells were chosen.
 */

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.Room;

public class FileInitTests {
	// Constants used to check that the files were loaded correctly
	public static final int LEGEND_SIZE = 11;   // 9 rooms + Walkway + Unused
	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 27;
	public static final int NUM_DOORS = 18;
	public static final char[] ROOM_INITIALS = { 'O', 'G', 'L', 'S', 'M', 'C', 'H', 'A', 'R' };

	// Board is a singleton, so we only set it up once for all the tests
	private static Board board;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// initialize() loads BOTH config files
		board.initialize();
	}

	// ---------------------------------------------------------------
	// Setup file
	// ---------------------------------------------------------------

	@Test
	public void testRoomLabels() {
		// first room in the file, a few in the middle, and the last two
		// entries in the file (the two "Space" types)
		assertEquals("Observatory", board.getRoom('O').getName());
		assertEquals("Server Room", board.getRoom('S').getName());
		assertEquals("Medical Bay", board.getRoom('M').getName());
		assertEquals("Reactor", board.getRoom('R').getName());
		assertEquals("Walkway", board.getRoom('W').getName());
		assertEquals("Unused", board.getRoom('X').getName());
	}

	@Test
	public void testAllLegendEntriesLoaded() {
		// every one of the eleven letters in the legend must map to a room
		String letters = "OGLSMCHARWX";
		assertEquals(LEGEND_SIZE, letters.length());
		for (char c : letters.toCharArray())
			{
			assertNotNull(board.getRoom(c), "Missing legend entry " + c);
			assertNotNull(board.getRoom(c).getName(), "Legend entry " + c + " has no name");
		}
	}

	// ---------------------------------------------------------------
	// Layout file
	// ---------------------------------------------------------------

	@Test
	public void testBoardDimensions() {
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	// Corners of the board are the extreme cases for the grid size
	@Test
	public void testCornerCells() {
		assertEquals('X', board.getCell(0, 0).getInitial());
		assertEquals('X', board.getCell(0, NUM_COLUMNS - 1).getInitial());
		assertEquals('X', board.getCell(NUM_ROWS - 1, 0).getInitial());
		assertEquals('X', board.getCell(NUM_ROWS - 1, NUM_COLUMNS - 1).getInitial());
	}

	// ---------------------------------------------------------------
	// Doorways
	// ---------------------------------------------------------------

	// One doorway in each direction, taken from different rooms
	@Test
	public void testDoorDirections() {
		// opens LEFT into the Observatory
		BoardCell cell = board.getCell(3, 8);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
		// opens RIGHT into the Greenhouse
		cell = board.getCell(4, 9);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());
		// opens UP into the Control Room
		cell = board.getCell(18, 23);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());
		// opens DOWN into the Medical Bay
		cell = board.getCell(13, 6);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());
		// the Archives have doors on both sides of the same row
		cell = board.getCell(22, 9);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());
		cell = board.getCell(22, 8);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
	}

	// Cells that must NOT be doorways
	@Test
	public void testNotDoorways() {
		// plain walkway
		BoardCell cell = board.getCell(8, 10);
		assertEquals('W', cell.getInitial());
		assertFalse(cell.isDoorway());
		assertEquals(DoorDirection.NONE, cell.getDoorDirection());
		// walkway right next to a doorway
		cell = board.getCell(3, 9);
		assertFalse(cell.isDoorway());
		// room cell
		cell = board.getCell(5, 3);
		assertFalse(cell.isDoorway());
		// a room center is not a doorway
		cell = board.getCell(3, 4);
		assertFalse(cell.isDoorway());
		// unused space
		cell = board.getCell(0, 0);
		assertFalse(cell.isDoorway());
	}

	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++)
				if (board.getCell(row, col).isDoorway())
					numDoors++;
		assertEquals(NUM_DOORS, numDoors);
	}

	// ---------------------------------------------------------------
	// Rooms, centers, labels and secret passages
	// ---------------------------------------------------------------

	@Test
	public void testPlainRoomCell() {
		// inside the Laboratory, not special in any way
		BoardCell cell = board.getCell(6, 20);
		Room room = board.getRoom(cell);
		assertNotNull(room);
		assertEquals("Laboratory", room.getName());
		assertFalse(cell.isLabel());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isDoorway());
		assertEquals('\0', cell.getSecretPassage());
	}

	// Label cells are marked '#' in the layout file
	@Test
	public void testLabelCells() {
		// first room in the file
		BoardCell cell = board.getCell(2, 4);
		Room room = board.getRoom(cell);
		assertEquals("Observatory", room.getName());
		assertTrue(cell.isLabel());
		assertFalse(cell.isRoomCenter());
		assertSame(cell, room.getLabelCell());
		// last room in the file
		cell = board.getCell(23, 23);
		room = board.getRoom(cell);
		assertEquals("Reactor", room.getName());
		assertTrue(cell.isLabel());
		assertSame(cell, room.getLabelCell());
		// the label is below the center in the Control Room
		cell = board.getCell(14, 23);
		room = board.getRoom(cell);
		assertEquals("Control Room", room.getName());
		assertTrue(cell.isLabel());
		assertSame(cell, room.getLabelCell());
	}

	// Center cells are marked '*' in the layout file
	@Test
	public void testCenterCells() {
		// label is ABOVE the center here (opposite of the Observatory)
		BoardCell cell = board.getCell(10, 4);
		Room room = board.getRoom(cell);
		assertEquals("Server Room", room.getName());
		assertTrue(cell.isRoomCenter());
		assertFalse(cell.isLabel());
		assertSame(cell, room.getCenterCell());
		// room in the bottom-left corner
		cell = board.getCell(22, 4);
		room = board.getRoom(cell);
		assertEquals("Hangar", room.getName());
		assertTrue(cell.isRoomCenter());
		assertSame(cell, room.getCenterCell());
		// room in the middle of the bottom edge
		cell = board.getCell(22, 13);
		room = board.getRoom(cell);
		assertEquals("Archives", room.getName());
		assertTrue(cell.isRoomCenter());
		assertSame(cell, room.getCenterCell());
	}

	// Every real room needs both a center and a label
	@Test
	public void testEveryRoomHasCenterAndLabel() {
		for (char initial : ROOM_INITIALS) {
			Room room = board.getRoom(initial);
			assertNotNull(room.getCenterCell(), room.getName() + " has no center");
			assertNotNull(room.getLabelCell(), room.getName() + " has no label");
			assertEquals(initial, room.getCenterCell().getInitial());
			assertEquals(initial, room.getLabelCell().getInitial());
		}
	}

	// Secret passages: two-letter cells like "OR" (room letter + target room)
	@Test
	public void testSecretPassages() {
		// Observatory <-> Reactor
		BoardCell cell = board.getCell(2, 2);
		assertEquals("Observatory", board.getRoom(cell).getName());
		assertEquals('R', cell.getSecretPassage());
		cell = board.getCell(22, 21);
		assertEquals("Reactor", board.getRoom(cell).getName());
		assertEquals('O', cell.getSecretPassage());
		// Laboratory <-> Hangar
		cell = board.getCell(2, 21);
		assertEquals("Laboratory", board.getRoom(cell).getName());
		assertEquals('H', cell.getSecretPassage());
		cell = board.getCell(22, 2);
		assertEquals("Hangar", board.getRoom(cell).getName());
		assertEquals('L', cell.getSecretPassage());
		// a passage cell is still an ordinary room cell
		assertFalse(cell.isLabel());
		assertFalse(cell.isRoomCenter());
	}

	@Test
	public void testWalkwayAndUnused() {
		// walkway
		BoardCell cell = board.getCell(9, 20);
		Room room = board.getRoom(cell);
		assertEquals("Walkway", room.getName());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
		// the unused closet in the middle of the board
		cell = board.getCell(12, 13);
		room = board.getRoom(cell);
		assertEquals("Unused", room.getName());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
	}

	// Data sanity check: every door must be on a walkway and must open
	// directly into a room (not into another walkway or the unused space).
	@Test
	public void testDoorsOpenIntoRooms() {
		int doorsChecked = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if (!cell.isDoorway())
					continue;
				doorsChecked++;
				assertEquals('W', cell.getInitial(), "Door at " + row + "," + col + " not on a walkway");
				int r = row, c = col;
				switch (cell.getDoorDirection()) {
				case UP:    r--; break;
				case DOWN:  r++; break;
				case LEFT:  c--; break;
				case RIGHT: c++; break;
				default:    fail("Doorway with no direction at " + row + "," + col);
				}
				char target = board.getCell(r, c).getInitial();
				assertTrue("OGLSMCHAR".indexOf(target) >= 0,
						"Door at " + row + "," + col + " opens into '" + target + "'");
			}
		assertEquals(NUM_DOORS, doorsChecked);
	}
}
