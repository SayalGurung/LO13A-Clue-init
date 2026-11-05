package clueGame;

/**
 * One cell of the game board. Cells know their position, the initial of the
 * room (or space type) they belong to, and any special role they play:
 * doorway, room label, room center, or secret passage.
 */
public class BoardCell {
	private int row;
	private int col;
	private char initial;
	private DoorDirection doorDirection = DoorDirection.NONE;
	private boolean roomLabel = false;
	private boolean roomCenter = false;
	private char secretPassage = '\0'; // '\0' means no secret passage

	public BoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public char getInitial() {
		return initial;
	}

	public void setInitial(char initial) {
		this.initial = initial;
	}

	public boolean isDoorway() {
		return doorDirection != DoorDirection.NONE;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}

	public boolean isLabel() {
		return roomLabel;
	}

	public void setLabel(boolean roomLabel) {
		this.roomLabel = roomLabel;
	}

	public boolean isRoomCenter() {
		return roomCenter;
	}

	public void setRoomCenter(boolean roomCenter) {
		this.roomCenter = roomCenter;
	}

	public char getSecretPassage() {
		return secretPassage;
	}

	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	@Override
	public String toString() {
		return "BoardCell[" + row + "," + col + "," + initial + "]";
	}
}
