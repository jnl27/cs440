package AI_Map;

import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.*;

public class Draw extends JPanel {

	int s1, s2, t1, t2, a1, b1;
	Graphics graph;
	Cell[][] rect;

	private List<Point> fillCells;
	private List<Point> grayCells;
	private List<Point> blackCells;
	private List<Point> highwayCells;
	
	int x, y;
	private List<Cell> printgraph;

	public Draw() {

	}

	public Draw(Cell start) {
		this.x = start.getX();
		this.y = start.getY();

	}

	public void printGS(int a, int b, int c, int d) {
		s1 = a;
		s2 = b;
		t1 = c;
		t2 = d;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i < (Maze.MazeColCount-1) * 5; i = i + 5) {
			for (int j = 0; j < (Maze.MazeRowCount-1) * 5; j = j + 5) {
				g.drawRect(i, j, 10, 10);
			}
		}
	
		for (Point grayCell : grayCells) {
			int cellX = (grayCell.x * 5);
			int cellY = (grayCell.y * 5);
			g.setColor(Color.GRAY);
			g.fillRect(cellY, cellX, 5,5);
		}

		for (Point blackCell : blackCells) {
			int cellX = (blackCell.x * 5);
			int cellY = (blackCell.y * 5);
			g.setColor(Color.BLACK);
			g.fillRect(cellY, cellX, 5,5);
		}
		
		for (Point highwayCell : highwayCells) {
			int cellX = (highwayCell.x * 5);
			int cellY = (highwayCell.y * 5);
			g.setColor(Color.BLUE);
			g.fillRect(cellY, cellX, 5,5);
		}
		
		if(printgraph != null){
			for (Cell node : printgraph) {
				int cellX = (node.getX() * 5);
				int cellY = (node.getY() * 5);
	
				g.setColor(Color.orange);
				g.fillRect(cellY, cellX, 5, 5);
			}
		}
		
		g.setColor(Color.GREEN);
		g.fillRect(s2*5, s1*5,  5, 5);

		g.setColor(Color.RED);
		g.fillRect(t2*5, t1*5, 5, 5);

	}

	public List<Point> getFillCells() {
		return fillCells;
	}
	
	public List<Point> getGrayCells() {
		return grayCells;
	}
	
	public List<Point> getBlackCells() {
		return blackCells;
	}

	public void setFillCells(List<Point> fillCells) {
		this.fillCells = fillCells;
	}
	
	public void setGrayCells(List<Point> grayCells) {
		this.grayCells = grayCells;
	}
	
	public void setBlackCells(List<Point> blackCells) {
		this.blackCells = blackCells;
	}
	
	public void setHighwayCells(List<Point> highwayCells) {
		this.highwayCells = highwayCells;
	}

	public void setprintgraph(List<Cell> printgraph) { this.printgraph = printgraph; }
	
	public Dimension getPreferredSize() {
		return new Dimension(Maze.MazeRowCount*10, Maze.MazeColCount*10);
		
	}

}
