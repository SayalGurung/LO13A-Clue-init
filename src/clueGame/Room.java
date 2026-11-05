package clueGame;

//this class will hold information about rooms

public class Room {
	private String name;
	private BoardCell centerCell;
	private BoardCell labelCell;
	
	//constructor
	public Room(String name) {
		super();
		this.name = name;
	}
	
	//setters and getters
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
	
	public void setCenterCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}
	
}
