package AI_Map;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class A_Star extends Maze{
	static int numExpanded = 0;
	static int pathLength = 0;
	static double totalG = 0;
	static long runtime = 0;
	static long memoryUsed = 0;
	
	static class CompareF implements Comparator<Cell>{ //**WORKING**
		public int compare(Cell c1, Cell c2) {
			// return 0 if c1 == c2, <0 if c1 < c2, >0 if c1 > c2
			return Double.compare(
					c1.getFCost(),
					c2.getFCost()
					// TODO: Break f-value ties!
//					c1.getFCost() - (38000 * c1.getGCost()),
//					c2.getFCost() - (38000 * c2.getGCost())
			);
		}
	}
	
	public static boolean search(Maze m, double weight, String heuristic, boolean verbose) throws IOException {
		// Start recording memory usage.
		long memoryBeforeRuntime = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		// Reset stats
		A_Star.numExpanded = 0;
		A_Star.pathLength = 0;
		// Start recording runtime.
		long startTime = System.currentTimeMillis(), endTime;
		// Initiate fringe, a list of vertices we want to consider expanding
		PriorityQueue<Cell> fringe = new PriorityQueue<>(1, new CompareF());
		// Initiate closed, a list of vertices we've already expanded
		ArrayList<Cell> closed = new ArrayList<>();
		// Print the maze's start-goal pair.
//		System.out.println("~~~~~~~~~~");
//		System.out.println("Start cell: (" + m.startMaze.getX() + ", " + m.startMaze.getY() + ")");
//		System.out.println("Goal cell: (" + m.goalMaze.getX() + ", " + m.goalMaze.getY() + ")");
//		System.out.println("~~~~~~~~~~");
		// Set the start cell's g value
		m.startMaze.setGCost(0);
		// Set the start cell's parent cell
		m.startMaze.setParent(m.startMaze);
		// Calculate and set the start cell's f value
		calculateHDistance(m.startMaze, m.goalMaze, weight, heuristic);
		// Insert the start cell into the fringe
		fringe.add(m.startMaze);

		while (!fringe.isEmpty()) {
			// Remove from fringe the element with the lowest f value
			Cell s = fringe.poll();
			// Print that cell's coordinates
//			System.out.println("current cell's coords: (" + s.getX() + ", " + s.getY() + ") [g value: " + s.getGCost() + "]");
			// If we've reached the goal, stop searching and report results
			if (s.getX() == m.goalMaze.getX() && s.getY() == m.goalMaze.getY()) {
				A_Star.totalG = s.getGCost();

				if (verbose)
				{
					System.out.println("~~~~~~~~~~~~~~~PATH FOUND~~~~~~~~~~~~~~~");
					System.out.println("(" + s.getX() + ", " + s.getY() + ") <- GOAL");
				}

				List<Cell> pathCells = new ArrayList<>();
				A_Star.pathLength++;
				s = s.getParent();

				while (s != m.startMaze) {
//					System.out.println("(" + s.getX() + ", " + s.getY() + ")");
					pathCells.add(s);
					A_Star.pathLength++;
					s = s.getParent();
				}

				pathCells.add(s);

				if (verbose) {
					System.out.println("[..." + (A_Star.pathLength - 1) + " hidden cells...]");
					System.out.println("(" + s.getX() + ", " + s.getY() + ") <- START");
					m.draw.setprintgraph(pathCells);
					JFrame frame = new JFrame("A* Algorithm [SOLVED]");
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					frame.add(m.draw);
					frame.pack();
					frame.setVisible(true);
					System.out.println("~~~~~~~~~~~~~~~END PATH TRACE~~~~~~~~~~~~");
				}

				// Stop recording runtime
				endTime = System.currentTimeMillis();
				A_Star.runtime = endTime-startTime;
				// Stop recording memory usage
				long memoryAfterRuntime = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				A_Star.memoryUsed = memoryAfterRuntime - memoryBeforeRuntime;

				if (verbose) {
					// Print stats
					System.out.println("RUNTIME: " + A_Star.runtime + "ms");
//					System.out.println("PATH LENGTH: " + A_Star.pathLength);
					System.out.println("PATH COST: " + A_Star.totalG);
					System.out.println("NODES EXPANDED: " + A_Star.numExpanded);
					// TODO: Fix memory stat.
					System.out.println("MEMORY USAGE: " + A_Star.memoryUsed + " bytes");

					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					int innerChoice = 0;
					while (innerChoice != 2) {
						System.out.println("******************************************************");
						System.out.println("1. Show a cell's f/g/h values");
						System.out.println("2. Go back");
						System.out.println("******************************************************");
						System.out.print("Type 1 or 2: ");
						innerChoice = Integer.parseInt(br.readLine());

						if (innerChoice == 1) { // Show f/g/h values for a user-requested cell
							int x, y;
							System.out.print("Enter x-coordinate: ");
							x = Integer.parseInt(br.readLine());
							System.out.print("Enter y-coordinate: ");
							y = Integer.parseInt(br.readLine());
							System.out.println("f-value: " + m.cells[x][y].getFCost());
							System.out.println("g-value: " + m.cells[x][y].getGCost());
							System.out.println("h-value: " + m.cells[x][y].getHDistance());
						}
					}
				}

				// Return
				return true;
			}
			// Add the cell to the closed list
			closed.add(s);
			// Initialize the cell's neighbors
			for (int i = s.getX()-1; i <= s.getX()+1; i++) {
				for (int j = s.getY()-1; j <= s.getY()+1; j++) {
					if (i<0 || i>MazeRowCount-1 || j<0 || j>MazeColCount-1 || (i==s.getX() && j==s.getY())) {
						continue; // Skip if coordinates are invalid
					}
					if (m.cells[i][j].isBlocked() == 0 || closed.contains(m.cells[i][j])) {
						continue; // Skip if coordinates are blocked or already visited
					}
					if (!fringe.contains(m.cells[i][j])) {
						m.cells[i][j].setGCost(Double.MAX_VALUE); // Set g value to infinity
						m.cells[i][j].setParent(null); // Nullify parent
					}
					// Update the neighbor's g value and parent if needed
					updateVertex(s, m.cells[i][j], fringe, m.goalMaze, weight, heuristic);
				}
			}
		}

		System.out.println("PATH NOT FOUND.");
		return false;
	}

	public static boolean seqSearch(Maze m, List<String> heuristics, double weightOne, double weightTwo, boolean verbose) throws IOException {
		// Start recording memory usage.
		long memoryBeforeRuntime = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		// Reset stats
		A_Star.numExpanded = 0;
		A_Star.pathLength = 0;
		// Start recording runtime.
		long startTime = System.currentTimeMillis(), endTime;

		// Initialize open lists.
		PriorityQueue<Cell>[] openQueues = new PriorityQueue[heuristics.size()];
		// Initialize closed lists.
		ArrayList<Cell>[] closedLists = new ArrayList[heuristics.size()];
		// Initialize maze copies.
		Maze[] mazes = new Maze[heuristics.size()];

		// Initialize start and goal cell f/g values and parent cells.
		// Insert the start cell into all open lists.
		for (int i = 0; i < heuristics.size(); i++) {
			openQueues[i] = new PriorityQueue<>(1, new CompareF());
			closedLists[i] = new ArrayList<>();
			mazes[i] = new Maze(m);
			mazes[i].startMaze.setGCost(0);
			mazes[i].goalMaze.setGCost(Double.MAX_VALUE);
			mazes[i].startMaze.setParent(null);
			mazes[i].goalMaze.setParent(null);
			calculateHDistance(mazes[i].startMaze, mazes[i].goalMaze, weightOne, heuristics.get(i));
			openQueues[i].add(mazes[i].startMaze);
		}

		while (openQueues[0].peek().getFCost() < Double.MAX_VALUE) {
			for (int i = 1; i < heuristics.size(); i++) {
				if (openQueues[i].peek().getFCost() <= weightTwo * openQueues[0].peek().getFCost()) {
//					System.out.println("The min key of fringe " + heuristics[i] + " is better than that of fringe " + heuristics[0] + "!");
					if (mazes[i].goalMaze.getGCost() <= openQueues[i].peek().getFCost()) {
//						System.out.println("The goal cell of maze " + heuristics[i] + " has an even smaller key!");
						if (mazes[i].goalMaze.getGCost() < Double.MAX_VALUE) {
							// Return path trace
							Cell current = mazes[i].goalMaze;
							A_Star.totalG = current.getGCost();

							if (verbose)
							{
								System.out.println("~~~~~~~~~~~~~~~PATH FOUND~~~~~~~~~~~~~~~");
								System.out.println("(" + current.getX() + ", " + current.getY() + ") <- GOAL");
							}

							List<Cell> pathCells = new ArrayList<>();
							A_Star.pathLength++;
							current = current.getParent();

							while (current != mazes[i].startMaze) {
								pathCells.add(current);
								A_Star.pathLength++;
								current = current.getParent();
							}

							pathCells.add(current);

							if (verbose) {
								System.out.println("[..." + (A_Star.pathLength - 1) + " hidden cells...]");
								System.out.println("(" + current.getX() + ", " + current.getY() + ") <- START");
								m.draw.setprintgraph(pathCells);
								JFrame frame = new JFrame("Sequential A* Algorithm [SOLVED]");
								frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
								frame.add(m.draw);
								frame.pack();
								frame.setVisible(true);
								System.out.println("~~~~~~~~~~~~~~~END PATH TRACE~~~~~~~~~~~~");
							}

							// Stop recording runtime
							endTime = System.currentTimeMillis();
							A_Star.runtime = endTime-startTime;
							// Stop recording memory usage
							long memoryAfterRuntime = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
							A_Star.memoryUsed = memoryAfterRuntime - memoryBeforeRuntime;

							if (verbose) {
								// Print stats
								System.out.println("RUNTIME: " + A_Star.runtime + "ms");
								System.out.println("PATH COST: " + A_Star.totalG);
								System.out.println("NODES EXPANDED: " + A_Star.numExpanded);
								// TODO: Fix memory stat.
								System.out.println("MEMORY USAGE: " + A_Star.memoryUsed + " bytes");

								BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
								int innerChoice = 0;
								while (innerChoice != 2) {
									System.out.println("******************************************************");
									System.out.println("1. Show a cell's f/g/h values");
									System.out.println("2. Go back");
									System.out.println("******************************************************");
									System.out.print("Type 1 or 2: ");
									innerChoice = Integer.parseInt(br.readLine());

									if (innerChoice == 1) { // Show f/g/h values for a user-requested cell
										int x, y;
										System.out.print("Enter x-coordinate: ");
										x = Integer.parseInt(br.readLine());
										System.out.print("Enter y-coordinate: ");
										y = Integer.parseInt(br.readLine());
										System.out.println("f-value: " + mazes[i].cells[x][y].getFCost());
										System.out.println("g-value: " + mazes[i].cells[x][y].getGCost());
										System.out.println("h-value: " + mazes[i].cells[x][y].getHDistance());
									}
								}
							}

							// Return
							return true;
						}
					} else {
						Cell s = openQueues[i].poll();
						assert s != null;
						for (int j = s.getX() - 1; j <= s.getX() + 1; j++) {
							for (int k = s.getY() - 1; k <= s.getY() + 1; k++) {
								if (j < 0 || j > MazeRowCount - 1 || k < 0 || k > MazeColCount - 1 || (j == s.getX() && k == s.getY())) {
									continue; // Skip if coordinates are invalid
								}
								if (mazes[i].cells[j][k].isBlocked() == 0 || closedLists[i].contains(mazes[i].cells[j][k])) {
									continue; // Skip if coordinates are blocked or already visited
								}
								if (!openQueues[i].contains(mazes[i].cells[j][k])) {
									mazes[i].cells[j][k].setGCost(Double.MAX_VALUE); // Set g value to infinity
									mazes[i].cells[j][k].setParent(null); // Nullify parent
								}
								if (mazes[i].cells[j][k].getGCost() > s.getGCost() + cost(s, mazes[i].cells[j][k])) {
									mazes[i].cells[j][k].setGCost(s.getGCost() + cost(s, mazes[i].cells[j][k]));
									mazes[i].cells[j][k].setParent(s);
									if (!closedLists[i].contains(mazes[i].cells[j][k])) {
										openQueues[i].remove(mazes[i].cells[j][k]);
										calculateHDistance(mazes[i].cells[j][k], mazes[i].goalMaze, weightOne, heuristics.get(i));
										openQueues[i].add(mazes[i].cells[j][k]);
										A_Star.numExpanded++;
									}
								}
							}
						}
						closedLists[i].add(s);
					}
				} else {
//					System.out.println("The min key of fringe " + heuristics[0] + " is better than that of fringe " + heuristics[i]);
					if (mazes[0].goalMaze.getGCost() <= openQueues[0].peek().getFCost()) {
//						System.out.println("The goal has an even better key!");
						if (mazes[0].goalMaze.getGCost() < Double.MAX_VALUE) {
							// Return path trace
							Cell current = mazes[0].goalMaze;
							A_Star.totalG = current.getGCost();

							if (verbose)
							{
								System.out.println("~~~~~~~~~~~~~~~PATH FOUND~~~~~~~~~~~~~~~");
								System.out.println("(" + current.getX() + ", " + current.getY() + ") <- GOAL");
							}

							List<Cell> pathCells = new ArrayList<>();
							A_Star.pathLength++;
							current = current.getParent();

							while (current != mazes[0].startMaze) {
								pathCells.add(current);
								A_Star.pathLength++;
								current = current.getParent();
							}

							pathCells.add(current);

							if (verbose) {
								System.out.println("[..." + (A_Star.pathLength - 1) + " hidden cells...]");
								System.out.println("(" + current.getX() + ", " + current.getY() + ") <- START");
								m.draw.setprintgraph(pathCells);
								JFrame frame = new JFrame("Sequential A* Algorithm [SOLVED]");
								frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
								frame.add(m.draw);
								frame.pack();
								frame.setVisible(true);
								System.out.println("~~~~~~~~~~~~~~~END PATH TRACE~~~~~~~~~~~~");
							}

							// Stop recording runtime
							endTime = System.currentTimeMillis();
							A_Star.runtime = endTime-startTime;
							// Stop recording memory usage
							long memoryAfterRuntime = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
							A_Star.memoryUsed = memoryAfterRuntime - memoryBeforeRuntime;

							if (verbose) {
								// Print stats
								System.out.println("RUNTIME: " + A_Star.runtime + "ms");
								System.out.println("PATH COST: " + A_Star.totalG);
								System.out.println("NODES EXPANDED: " + A_Star.numExpanded);
								// TODO: Fix memory stat.
								System.out.println("MEMORY USAGE: " + A_Star.memoryUsed + " bytes");

								BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
								int innerChoice = 0;
								while (innerChoice != 2) {
									System.out.println("******************************************************");
									System.out.println("1. Show a cell's f/g/h values");
									System.out.println("2. Go back");
									System.out.println("******************************************************");
									System.out.print("Type 1 or 2: ");
									innerChoice = Integer.parseInt(br.readLine());

									if (innerChoice == 1) { // Show f/g/h values for a user-requested cell
										int x, y;
										System.out.print("Enter x-coordinate: ");
										x = Integer.parseInt(br.readLine());
										System.out.print("Enter y-coordinate: ");
										y = Integer.parseInt(br.readLine());
										System.out.println("f-value: " + mazes[i].cells[x][y].getFCost());
										System.out.println("g-value: " + mazes[i].cells[x][y].getGCost());
										System.out.println("h-value: " + mazes[i].cells[x][y].getHDistance());
									}
								}
							}

							// Return
							return true;
						}
					} else {
						Cell s = openQueues[0].poll();
						for (int j = s.getX() - 1; j <= s.getX() + 1; j++) {
							for (int k = s.getY() - 1; k <= s.getY() + 1; k++) {
								if (j < 0 || j > MazeRowCount - 1 || k < 0 || k > MazeColCount - 1 || (j == s.getX() && k == s.getY())) {
									continue; // Skip if coordinates are invalid
								}
								if (mazes[0].cells[j][k].isBlocked() == 0 || closedLists[0].contains(mazes[0].cells[j][k])) {
									continue; // Skip if coordinates are blocked or already visited
								}
								if (!openQueues[0].contains(mazes[0].cells[j][k])) {
									mazes[0].cells[j][k].setGCost(Double.MAX_VALUE); // Set g value to infinity
									mazes[0].cells[j][k].setParent(null); // Nullify parent
								}
								if (mazes[0].cells[j][k].getGCost() > s.getGCost() + cost(s, mazes[0].cells[j][k])) {
									mazes[0].cells[j][k].setGCost(s.getGCost() + cost(s, mazes[0].cells[j][k]));
									mazes[0].cells[j][k].setParent(s);
									if (!closedLists[0].contains(mazes[0].cells[j][k])) {
										openQueues[0].remove(mazes[0].cells[j][k]);
										calculateHDistance(mazes[0].cells[j][k], mazes[0].goalMaze, weightOne, heuristics.get(0));
										openQueues[0].add(mazes[0].cells[j][k]);
										A_Star.numExpanded++;
//										System.out.println("Added neighbor " + mazes[0].cells[j][k].toString() + " to fringe.");
									}
								}
							}
						}
						closedLists[0].add(s);
					}
				}
			}
		}

		System.out.println("This maze is unsolvable. Exiting...");
		return false;
	}
	
	public static void updateVertex(Cell s, Cell neighbor, PriorityQueue<Cell> fringe, Cell goal, double weight, String heuristic) {
		if (s.getGCost() + cost(s, neighbor) < neighbor.getGCost()) {
			A_Star.numExpanded++;
//			System.out.print("Updated (" + neighbor.getX() + ", " + neighbor.getY() + ")'s g value from " + neighbor.getGCost());
			fringe.remove(neighbor);
			neighbor.setGCost(s.getGCost() + cost(s, neighbor));
//			System.out.println(" to " + neighbor.getGCost());
			neighbor.setParent(s);
			calculateHDistance(neighbor, goal, weight, heuristic);
			fringe.add(neighbor);
		}
	}
	
	public static double cost(Cell c1, Cell c2) {
		if (c1.getY() == c2.getY() || c1.getX() == c2.getX()) {
			// directional movement
			if (c1.ifHighway() && c2.ifHighway()) {
				// highway movement
				if (c1.ifHardToTraverse()) {
					if (c2.ifHardToTraverse()) {
						// hard to hard highway
						return 0.5;
					} else {
						// hard to easy highway
						return 0.375;
					}
				} else if (c2.ifHardToTraverse()) {
					// easy to hard highway
					return 0.375;
				} else {
					// easy to easy highway
					return 0.25;
				}
			} else {
				// non-highway, directional movement
				if (c1.ifHardToTraverse()) {
					if (c2.ifHardToTraverse()) {
						// hard to hard
						return 2.0;
					} else {
						// hard to easy
						return 1.5;
					}
				} else if (c2.ifHardToTraverse()) {
					// easy to hard
					return 1.5;
				} else {
					// easy to easy
					return 1.0;
				}
			}
		} else {
			// diagonal movement
			if (c1.ifHardToTraverse()) {
				if (c2.ifHardToTraverse()) {
					// diagonal hard to hard
					return Math.sqrt(8.0);
				} else {
					// diagonal hard to easy
					return (Math.sqrt(2.0) + Math.sqrt(8.0)) / 2.0;
				}
			} else if (c2.ifHardToTraverse()) {
				// diagonal easy to hard
				return (Math.sqrt(2.0) + Math.sqrt(8.0)) / 2.0;
			} else {
				// diagonal easy to easy
				return Math.sqrt(2.0);
			}
		}
	}

	// Calculate and set a cell's f and h values
	public static void calculateHDistance(Cell curr, Cell goal, double weight, String heuristic) {
//		System.out.println("Editing (" + curr.getX() + ", " + curr.getY() + ")...");

		boolean calculatedHDistance = false;

		if (heuristic.equals("0")) {
			// 0 heuristic
			curr.setHDistance(0.0);
			calculatedHDistance = true;
		}

		// TODO: Consider a more optimal admissible heuristic
		// Manhattan highway heuristic
		double admissibleHeuristic = 0.25 * (Math.abs(goal.getX() - curr.getX()) + Math.abs(goal.getY() - curr.getY()));

		if (heuristic.equals("A")) {
			// Manhattan highway heuristic
			curr.setHDistance(weight * admissibleHeuristic);
			calculatedHDistance = true;
		}

		if (heuristic.equals("M")) {
			// Manhattan heuristic
			curr.setHDistance(weight * (Math.abs(goal.getX() - curr.getX()) + Math.abs(goal.getY() - curr.getY())));
			calculatedHDistance = true;
		}

		if (heuristic.equals("E")) {
			// Euclidean heuristic
			curr.setHDistance(weight * (Math.sqrt(Math.pow(goal.getX() - curr.getX(), 2) + Math.pow(goal.getY() - curr.getY(), 2))));
			calculatedHDistance = true;
		}

		if (heuristic.equals("Z")) {
			// Easy Euclidean heuristic (cost if all cells on the path are regular)
			curr.setHDistance(weight * (Math.sqrt(2.0) * (Math.sqrt(Math.pow(goal.getX() - curr.getX(), 2) + Math.pow(goal.getY() - curr.getY(), 2)))));
			calculatedHDistance = true;
		}

		if (heuristic.equals("H")) {
			// Hard Euclidean heuristic (cost if all cells on the path are hard-to-traverse)
			curr.setHDistance(weight * (Math.sqrt(8) * (Math.sqrt(Math.pow(goal.getX() - curr.getX(), 2) + Math.pow(goal.getY() - curr.getY(), 2)))));
			calculatedHDistance = true;
		}

		if (!calculatedHDistance) {
			// If the heuristic is none of the above, then it's a number the user chose as a weight.
			System.out.println("INVALID HEURISTIC GIVEN.");
			return;
		}

		curr.setFCost(curr.getGCost() + curr.getHDistance());
	}
	
	
 
}
