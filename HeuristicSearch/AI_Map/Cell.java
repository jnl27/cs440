package AI_Map;
import java.util.ArrayList;


public class Cell {
	private int x;
	private int y;
	public char value;
	private double fCost;
	double search = 0;
	public double cost = 1;
	private double gCost;
	public double [] g = new double[5];
	public double [] h = new double[5];
	public double [] f = new double[5];
	private double hDistance;
	private int blocked; //int 1 = unblocked; int 0 = blocked
	private boolean hardToTraverse;
	private boolean containsHighway;
	public int highwayNumber = 0;
	public boolean visited;
	public boolean[] isVisited = {false, false, false, false, false};
	private boolean traversed;
	private boolean closed = false;
	
	public boolean isTraversed() {
		return traversed;
	}

	public void setTraversed(boolean traversed) {
		this.traversed = traversed;
	}

	public int isBlocked() {
		return blocked;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}
	

	public void setClosedFlag(boolean flag){
		this.closed = flag;
	}
	
	public boolean getClosedFlag(){
		return this.closed ;
	}

	public void setToHardToTraverse(){
		this.hardToTraverse = true;
	}
	
	public boolean ifHardToTraverse(){
		return hardToTraverse;
	}

	public boolean ifHighway(){
		return containsHighway;
	}
	
	public void setHighway(boolean containsHighway) {
		this.containsHighway = containsHighway;
	}

	private Cell parent ;
	public Cell [] prnt = new Cell[5]; 
	
	private ArrayList<Cell> neighbors ;
	


	public Cell(int x,int y){
		this.x= x;
		this.y= y;
	}

	public Cell(){
		this.x = 0;
		this.y = 0;
		this.blocked = 0;
	}
	
	public Cell(int x, int y, int blocked){
		this.x = x;
		this.y = y;
		this.blocked = blocked;
		this.neighbors = new ArrayList<>();
	}

	public Cell (Cell cellToCopy) {
		this.x = cellToCopy.x;
		this.y = cellToCopy.y;
		this.blocked = cellToCopy.blocked;
		this.containsHighway = cellToCopy.containsHighway;
		this.hardToTraverse = cellToCopy.hardToTraverse;
		this.neighbors = cellToCopy.neighbors;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public double getCost() { return cost; }
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getGCost() { return gCost; }
	public void setGCost(double gCost) {
		this.gCost = gCost;
	}
	public double getHDistance() {
		return hDistance;
	}
	public void setHDistance(double hDistance) {
		this.hDistance = hDistance;
	}

	public void setFCost(double fCost) { this.fCost = fCost; }
	public double getFCost() { return fCost; }

	public Cell getParent() {
		return parent;
	}

	public void setParent(Cell parent) {
		this.parent = parent;
	}

	public ArrayList<Cell> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Cell> neighbors) {
		this.neighbors = neighbors;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}