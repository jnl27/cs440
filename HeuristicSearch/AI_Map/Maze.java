package AI_Map;

import java.awt.Point;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

public class Maze 
{
	public static int MazeColCount = 160;
	public static int MazeRowCount = 120;
	private static ArrayList<Cell> highway = new ArrayList<>();
	ArrayList<Cell> eightCoordinates = new ArrayList<>();
	public static boolean validHighway = true;
	protected Cell[][] cells;
	public Draw draw = new Draw();
	Cell startMaze = new Cell();
	Cell goalMaze = new Cell();
	ArrayList<Cell> printgraph = new ArrayList<>();
	

	public Maze()
	{
		cells = new Cell[MazeRowCount][MazeColCount];
		
		for (int i = 0; i < MazeRowCount; i++) 
		{
			for (int j = 0; j < MazeColCount; j++) 
			{
				cells[i][j] = new Cell(i,j,1);
			}
		}
		
	}
	
	public Maze(Maze mazeToCopy){
		this.cells = new Cell[MazeRowCount][MazeColCount];
		int x, y;
		for(int i = 0; i < MazeRowCount; i++) {
			for (int j = 0; j < MazeColCount; j++) {
				this.cells[i][j] = new Cell(mazeToCopy.cells[i][j]);

				x = mazeToCopy.cells[i][j].getX();
				y = mazeToCopy.cells[i][j].getY();

				if(mazeToCopy.startMaze.getX() == x && mazeToCopy.startMaze.getY() == y) {
					this.startMaze = this.cells[i][j];
				}

				if(mazeToCopy.goalMaze.getX() == x && mazeToCopy.goalMaze.getY() == y){
					this.goalMaze = this.cells[i][j];
				}
			}
		}
	}
	
	public void genMaze()
	{
		eightCoordinates = getEightCoordinates();
		for (int i =0; i < 8; i++)
		{
			setHardToTraverse(eightCoordinates.get(i));
		}

		
		for (int highwayNumber = 1; highwayNumber <=4 ; highwayNumber++)
		{
			boolean highwayDone = buildHighway(highwayNumber);
			highway.clear();
		}
		
		int [] startCoordinates = getStartCoordinates();
		
		setStartCell(startMaze, startCoordinates[0], startCoordinates[1] );
		
		int [] goalCoordinates = getGoalCoordinates();
		
		setGoalMaze(goalMaze, goalCoordinates[0], goalCoordinates[1] );
		
		setBlockedcells();
	}

	public void regenStartGoal() {
		int [] startCoordinates = getStartCoordinates();

		setStartCell(startMaze, startCoordinates[0], startCoordinates[1] );

		int [] goalCoordinates = getGoalCoordinates();

		setGoalMaze(goalMaze, goalCoordinates[0], goalCoordinates[1] );
	}
	
	
	public void setStartCell(Cell startMaze, int x, int y){
		this.startMaze.setX(x);
		this.startMaze.setY(y);
		this.startMaze = cells [x][y];
		
	}
	
	public int[] getStartCoordinates () {
		int[] startCoordinates = {};
		boolean startGoalFound = false;
		while (!startGoalFound) {
//			System.out.println("Generating start coordinates...");
			startCoordinates =  getXYCoordinates();
			if (cells[startCoordinates[0]][startCoordinates[1]].isBlocked() == 1) {
				startGoalFound = true;
			}
		}
		return startCoordinates;
	}

	public void setGoalMaze(Cell goalMaze, int x, int y){
		
		this.goalMaze.setX(x);
		this.goalMaze.setY(y);
		this.goalMaze = cells [x][y];
	}
	
	public int[] getGoalCoordinates() {
		boolean flag = false;
		int[] goalCoordinates = {};
		int goalX, goalY; // initialized to 0 by default
		double distGoalAndStart;
		while(!flag){
//			System.out.println("Generating goal coordinates...");
			goalCoordinates =  getXYCoordinates();	
			goalX = goalCoordinates[0];
			goalY = goalCoordinates[1];
			distGoalAndStart = Math.sqrt((startMaze.getX() - goalX)*(startMaze.getX() - goalX) +(startMaze.getY() - goalY)*(startMaze.getY() - goalY));
			if( distGoalAndStart > 100 && cells[goalX][goalY].isBlocked() == 1){
				flag = true;
			} 
		}
		return goalCoordinates;
	}

	public int[] getXYCoordinates(){
		float rnd = (float)(Math.random());
		 int x, y; // initialized to 0 by default
		 if (rnd <= 0.25) {
		 		x = (int)(Math.random()*20);
		 		y = (int)(Math.random()*160); 
		 }
		 else if (rnd > 0.25 && rnd <=0.5) {
		 		x = (int)(Math.random()*120);
		 		y = (int)(Math.random()*20 + 139); 
		 }
		 else if (rnd > 0.5 && rnd <=0.75) {
		 		x = (int)(Math.random()*20 + 99);
		 		y = (int)(Math.random()*160); 
		 }
		 else {
		 		x = (int)(Math.random()*120);
		 		y = (int)(Math.random()*20); 
		 }
		return new int[]{x,y};
	}
	
	public boolean buildHighway(int highwayNumber) {
		Cell startCell = getHighwayStartCell();
		boolean highwayIncomplete = true;
		int count = 100;

		while (highwayIncomplete && count>0) {
			// TODO: Avoid infinite while loop
			count--;
			validHighway = true;
			boolean done = makeHighway(startCell);
//			System.out.println("Completing highway...");
			if (!done) {
				clear();
			}
			if (done) {
				reflectHighway(highway, highwayNumber);
				highway.clear();
				highwayIncomplete = false;
			}
		}

		// A valid highway was built!
		return true;
	}
	
	public void setBlockedcells(){
		for (int i = 0; i < MazeRowCount; i++) {
			for (int j = 0; j < MazeColCount; j++) {
				float rnd = (float) Math.random();
				if(rnd <= 0.2 && !cells[i][j].ifHighway()){
					cells[i][j].setBlocked(0);										
				}
			}
		}		
	}
	
	
	public ArrayList<Cell> getEightCoordinates()
	{

			
		for (int i=0; i<8; i++)
		{
			Cell current = cells[(int)((Math.random()*(MazeRowCount-30))+15)][(int)((Math.random()*(MazeColCount-30))+15)];
			eightCoordinates.add(current);
		}
		return eightCoordinates;
	}

	public void reflectHighway(ArrayList<Cell> highway, int highwayNumber) {
		for(int i = 0; i<=highway.size()-1;i++) {
			highway.get(i).setHighway(true);
			highway.get(i).highwayNumber = highwayNumber;
		}
	}
	

	public void setHardToTraverse(Cell current) 
	{
		int x1 = current.getX() - 15;
		int x2 = current.getX() + 15; 
		int y1 = current.getY() - 15;
		int y2 = current.getY() + 15;
		for (int i = x1; i <= x2; i++)
		{
			for (int j = y1; j <= y2; j++)
			{
				double randProb = Math.random();
				if (randProb < 0.5) {
					cells[i][j].setToHardToTraverse();
				}
			}
		}

	}
	
	
	private Cell getHighwayStartCell() {
		boolean startCellFound = false;
		int x=0,y=0;
		while (!startCellFound) {
//			System.out.println("Finding highway start cell...");
			float rnd = (float) Math.random();
			
			if(rnd<=0.25){
				 x = 0;
				 y = (int)(Math.random()*159);
			}
			else if(0.25<rnd && rnd <=0.50){
				 y = 0;
				 x = (int)(Math.random()*119);
			}
			else if(0.50<rnd && rnd <=0.75){
				 x = 119;
				 y = (int)(Math.random()*159);
			}
			else{
				 y = 159;
				 x = (int)(Math.random()*119);
			}
			if (!cells[x][y].ifHighway() &&
				!(
					cells[x][y].equals(cells[0][0]) ||
					cells[x][y].equals(cells[MazeRowCount -1][0]) ||
					cells[x][y].equals(cells[0][MazeColCount -1]) ||
					cells[x][y].equals(cells[MazeRowCount -1][MazeColCount -1])
				)
			) {
				startCellFound = true;
			}
		}
		return cells[x][y];
	}
	
	
	
	boolean makeHighway(Cell startCell) {
		boolean boundaryCondition =  true;
		
		Cell start = startCell;
		char prevDirection = 'c';
		while (boundaryCondition) {
			// TODO: Avoid infinite while loop
//			System.out.println("Making highway...");
			if (highway.size() != 0) {
				start = highway.get(highway.size() - 1);
			}
			char c = getDirection(prevDirection, startCell);
			if (c == 'r' && prevDirection == 'l' ||  c == 'l' && prevDirection == 'r' ||c == 'u' && prevDirection == 'd' ||c == 'd' && prevDirection == 'u') {
				return false;
			}
			prevDirection = c;
			boolean valid; // initially true
			switch (c) {
			case 'l' :
				valid = moveLeft(start);
				if (!valid) {
					boundaryCondition = false;
				}
				break;
			case 'r' :
				valid = moveRight(start);
				if (!valid) {
					boundaryCondition = false;
				}
				break;
			case 'u' :
				valid = moveUp(start);
				if (!valid) {
					boundaryCondition = false;
				}
				break;
			case 'd' :
				valid = moveDown(start);
				if (!valid) {
					boundaryCondition = false;
				}
				break;
				
			default:
				break;
			}
		}
		if(!validHighway) {
			clear();
			return false;
		}
		if(highway.size() < 100)
		{
			clear();
			return false;
		}
		else return highway.size() > 100;
	}
	
	void clear() {
		highway.clear();
	}
	boolean moveLeft(Cell startCell) {
		if (startCell.ifHighway())
		{
			validHighway= false;
			return false;
		}
		for (int y = startCell.getY()- 1; y >= (startCell.getY()- 20); y--) {
			if (y < 0 && highway.size() < 100 ) {
				return false;
			}
			else if (y < 0 && highway.size() > 100) {
				return false;
			}
			else if (isHighway(cells[startCell.getX()][y]) || cells[startCell.getX()][y].ifHighway()){
				validHighway = false;
				return false;
			}
			else {
				highway.add(cells[startCell.getX()][startCell.getY()]);
				highway.add(cells[startCell.getX()][y]);
//				System.out.println("Highway cell also added at (" + startCell.getX() + ", " + y + ")");
			}
			
		}
		return true;
	}
	
	boolean moveRight(Cell startCell) {
		if (startCell.ifHighway())
		{
			validHighway= false;
			return false;
		}
		for (int y = startCell.getY() + 1; y <= (startCell.getY()+ 20); y++) {
			if (y >= 160 && highway.size() < 100 ) {
				return false;
			}
			else if (y >= 160 && highway.size() > 100) {
				return false;
			}
			else if (isHighway(cells[startCell.getX()][y]) || cells[startCell.getX()][y].ifHighway()){
				validHighway = false;
				return false;
			}
			else {
				highway.add(cells [startCell.getX()][startCell.getY()]);

				highway.add(cells[startCell.getX()][y]);
//				System.out.println("Highway cell also added at (" + startCell.getX() + ", " + y + ")");
			}
			
		}
		return true;
	}
	
	boolean moveUp(Cell startCell) {
		if (startCell.ifHighway())
		{
			validHighway= false;
			return false;
		}
		for (int x = startCell.getX()- 1; x >= (startCell.getX()- 20); x--) {
			if (x < 0 && highway.size() < 100) {
				return false;
			}
			else if (x < 0 && highway.size() > 100) {
				return false;
			}
			else if (isHighway(cells[x][startCell.getY()]) || cells[x][startCell.getY()].ifHighway()){
				validHighway = false;
				return false;
			}
			else {
				highway.add(cells [startCell.getX()][startCell.getY()]);

				highway.add(cells[x][startCell.getY()]);
//				System.out.println("Highway cell also added at (" + x + ", " + startCell.getY() + ")");
			}
		}
		return true;
	}
	
	boolean moveDown(Cell startCell) {
		if (startCell.ifHighway())
		{
			validHighway= false;
			return false;
		}
		for (int x = startCell.getX() + 1; x <= (startCell.getX()+ 20); x++) {
			if (x >= 120 && highway.size() < 100 ) {
				return false;
			}
			else if (x >= 120 && highway.size() > 100) {
				return false;
			}
			else if (x <= 120 && isHighway(cells[x][startCell.getY()]) || cells[x][startCell.getY()].ifHighway()){
				validHighway = false;
				return false;
			}
			else {
				highway.add(cells [startCell.getX()][startCell.getY()]);
				highway.add(cells[x][startCell.getY()]);
//				System.out.println("Highway cell also added at (" + x + ", " + startCell.getY() + ")");
			}
		}
		return true;
	}
	
	
	char getDirection(char c, Cell startCell) {
		float rnd = (float) Math.random();
		switch (c) {
		case 'c':
			if (startCell.getX()==0) {
				return 'd';
			}
			else if (startCell.getX()==119) {
				return 'u';
			}
			else if (startCell.getY()==0) {
				return 'r';
			}
			else if (startCell.getY()==159) {
				return 'l';
			}
			else if(rnd<=0.25){
				 return 'l';
			}
			else if(0.25<rnd && rnd <=0.50){
				return 'r';
			}
			else if(0.50<rnd && rnd <=0.75){
				return 'u';
			}
			else{
				return 'd';
			}
		case 'l' : 
			if (rnd <=0.6) {
				return 'l';
			}
			else if (0.6 < rnd && rnd <= 0.8 ) {
				return 'u';
			}
			else {
				return 'd';
			}
		case 'r' : 
			if (rnd <=0.6) {
				return 'r';
			}
			else if (0.6 < rnd && rnd <= 0.8 ) {
				return 'u';
			}
			else {
				return 'd';
			}
		case 'u' : 
			if (rnd <=0.6) {
				return 'u';
			}
			else if (0.6 < rnd && rnd <= 0.8 ) {
				return 'l';
			}
			else {
				return 'r';
			}
		case 'd' : 
			if (rnd <=0.6) {
				return 'd';
			}
			else if (0.6 < rnd && rnd <= 0.8 ) {
				return 'l';
			}
			else {
				return 'r';
			}
			default:
				return 'f';
		}
	}
		
	
	//check if cell is already a highway
	boolean isHighway(Cell N) {
		for (Cell m : highway) {
			if (N.getX() == m.getX() && N.getY() == m.getY()) {
				return true;
			}
		}
		return false;
	}
	
	
	public JFrame renderMaze() {

		JFrame frame = new JFrame("A* Algorithm");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		List<Point> graycells = new ArrayList<>();
		List<Point> blackcells = new ArrayList<>();
		List<Point> highwaycells = new ArrayList<>();
		for (int i = 0; i < MazeRowCount; i++) {
			for (int j = 0; j < MazeColCount; j++) {
				if (cells[i][j].ifHighway()) {
					highwaycells.add(new Point(i, j));
				}
				else if (cells[i][j].isBlocked() == 0) {
					blackcells.add(new Point(i, j)); 
				}
				else if (cells[i][j].ifHardToTraverse()) {
					graycells.add(new Point(i, j)); 
				}
			}
		}
		draw.setGrayCells(graycells);
		draw.setBlackCells(blackcells);
		draw.setHighwayCells(highwaycells);
		draw.printGS(startMaze.getX(), startMaze.getY(), goalMaze.getX(), goalMaze.getY());
		

		frame.add(draw);
		frame.pack();
		frame.setVisible(true);
		return frame;
	}
	
	public void print(String name) {
	try {
		String filename1;
		if (name == null) {
			System.out.print("Name your maze: ");
			Scanner sc1 = new Scanner(System.in);
			filename1 = sc1.next();
		} else {
			filename1 = name;
		}
        BufferedWriter out = new BufferedWriter(new FileWriter(filename1 + ".txt"));
        out.write(startMaze.getX() + "," + startMaze.getY());
        out.newLine();
        out.write(goalMaze.getX() + "," + goalMaze.getY());
        out.newLine();
		for (Cell coordinate : eightCoordinates) {
			out.write(coordinate.getX() + "," + coordinate.getY());
			out.newLine();
		}
        for (int i = 0; i < MazeRowCount; i++) {
			for (int j = 0; j < MazeColCount; j++) {
				if (cells[i][j].isBlocked() == 0) {
					out.write("0");
					cells[i][j].setCost(Integer.MAX_VALUE);
				}
				else if (!cells[i][j].ifHighway() && !cells[i][j].ifHardToTraverse()) {
					out.write("1");
					cells[i][j].setCost(1.0);
				}
				else if (!cells[i][j].ifHighway() && cells[i][j].ifHardToTraverse()) {
					out.write("2");
					cells[i][j].setCost(2.0);
				}
				else if (cells[i][j].ifHighway() && !cells[i][j].ifHardToTraverse()) {
					out.write("a");
					cells[i][j].setCost(0.25);
				}
				else if (cells[i][j].ifHighway() && cells[i][j].ifHardToTraverse()) {
					out.write("b");
					cells[i][j].setCost(0.5);
				}
            }
			 out.write("\n");
        }
            out.close();
        System.out.println("Done.");
        } catch (IOException e) {
			System.out.println("Export failed.");
		}
    }
	
	public boolean equals(Cell a, Cell b) {
		return a.getX() == b.getX() && a.getY() == b.getY();
	}
	
	public Cell initiateNeighbors(Cell n)
	{
		
		ArrayList<Cell> nbrs = new ArrayList<>();
	
		if(n.getX() > 0 && n.getY() > 0)
			nbrs.add(cells[n.getX()-1][n.getY()-1]) ; // upper-left
		
		if(n.getX() > 0 && n.getY() < Maze.MazeColCount -1)
			nbrs.add(cells[n.getX()-1][n.getY()+1]) ; //Upper right
		
		if(n.getX() < Maze.MazeRowCount -1 && n.getY() > 0)
			nbrs.add(cells[n.getX()+1][n.getY()-1]) ; //Down left
			
		if(n.getX() < Maze.MazeRowCount -1 && n.getY() < Maze.MazeColCount -1)
			nbrs.add(cells[n.getX()+1][n.getY()+1]) ; //Down right
			
		if(n.getY() < Maze.MazeColCount -1)
			nbrs.add(cells[n.getX()][n.getY()+1]) ; //Right
		
		if(n.getY() > 0)
			nbrs.add(cells[n.getX()][n.getY()-1]) ; //L
			
		if(n.getX() > 0)
			nbrs.add(cells[n.getX()-1][n.getY()]) ;//U
		
		if(n.getX() < Maze.MazeRowCount -1)
			nbrs.add(cells[n.getX()+1][n.getY()]) ; //D
		
		n.setNeighbors(nbrs);
		
		return n;
	}
}
