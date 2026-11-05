package clueGame;

/**
 * Holds information about one kind of space on the board: a room, or one of
 * the non-room types (Walkway, Unused). Walkways and unused spaces are
 * treated as "rooms" so every cell can be looked up through its initial.
 */
public class Room {
	private String name;
	private BoardCell centerCell;
	private BoardCell labelCell;

	public Room(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BoardCell getCenterCell() {
		return centerCell;
	}

	public BoardCell getLabelCell() {
		return labelCell;
	}

	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}

	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}
}
