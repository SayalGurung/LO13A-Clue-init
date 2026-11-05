package clueGame;

public class BoardCell {
	private int row, col;
	private char initial;
	private DoorDirection doorDirection = DoorDirection.NONE;
	private boolean roomLabel, roomCenter;
	private char secretPassage = '\0'; // this mean there is no secret passage
	
	//constructor
	public BoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}
	//getters and setters
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public char getInitial() {
		return initial;
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
	
	public void setRoomCenter (boolean roomCenter) {
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