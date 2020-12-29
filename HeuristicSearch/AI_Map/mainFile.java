package AI_Map;

import java.io.*;
import java.util.ArrayList;


public class mainFile {

	public static void main(String[] args) throws NumberFormatException, IOException {
		ArrayList<String> heuristics = new ArrayList<>();
		heuristics.add("0");
		heuristics.add("A");
		heuristics.add("M");
		heuristics.add("E");
		heuristics.add("Z");
		heuristics.add("H");

		ArrayList<Double> weights = new ArrayList<>();
		weights.add(1.0);
		weights.add(1.25);
		weights.add(2.0);

		int choice;

		Maze m = new Maze();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("******************************************************");
		System.out.println("--Heuristic Search--");
		System.out.println("1. Create new map");
		System.out.println("2. Read map from file");
		System.out.println("3. Run tests");
		System.out.println("4. Exit");
		System.out.println("******************************************************");
		System.out.print("Type 1, 2, 3, or 4: ");
		choice = Integer.parseInt(br.readLine());
	
		switch(choice) {
			case 1: // Generate a new map.
				m.genMaze();
				m.renderMaze();
				break;
			case 2: // Load a map from a file.
				boolean fileFlag = true;
				while(fileFlag) {
					try {
						System.out.print("Input map name: ");
						String filename = br.readLine() + ".txt";
						File file = new File(filename);
						BufferedReader read = new BufferedReader(new FileReader(file));

						String sCurrentLine = read.readLine();
						String[] startCoordinates = sCurrentLine.split(",");
						int startX= Integer.parseInt(startCoordinates[0]);
						int startY= Integer.parseInt(startCoordinates[1]);
						m.startMaze.setX(startX);
						m.startMaze.setY(startY);
						m.startMaze =  m.cells[startX][startY];

						sCurrentLine = read.readLine();
						String[] goalCoordinates = sCurrentLine.split(",");
						int goalX= Integer.parseInt(goalCoordinates[0]);
						int goalY= Integer.parseInt(goalCoordinates[1]);
						m.goalMaze.setX(goalX);
						m.goalMaze.setY(goalY);
						m.goalMaze =  m.cells[goalX][goalY];

						for (int i = 0; i < 8; i++) {
							sCurrentLine = read.readLine();
							String[] coordinates = sCurrentLine.split(",");
							int x= Integer.parseInt(coordinates[0]);
							int y= Integer.parseInt(coordinates[1]);
							m.eightCoordinates.add(m.cells[x][y]);
						}

						// Uncomment to enable user control over map size.
//							sCurrentLine = read.readLine();
//							String mazeSize[] = sCurrentLine.split(",");
//							Maze.MazeRowCount =  Integer.valueOf(mazeSize[0]);
//							Maze.MazeColCount =  Integer.valueOf(mazeSize[1]);

						Maze.MazeRowCount = 120;
						Maze.MazeColCount = 160;

						for (int i = 0; i < Maze.MazeRowCount; i++) {
							sCurrentLine = read.readLine();
							String[] mazeRow = sCurrentLine.split("(?!^)");
							for (int j = 0; j < Maze.MazeColCount; j++) {
								switch(mazeRow[j]) {
								case "0": // Blocked cell
									m.cells[i][j].setBlocked(0);
									m.cells[i][j].setHighway(false);
									break;
								case "1": // Regular, unblocked cell
									m.cells[i][j].setBlocked(1);
									m.cells[i][j].setHighway(false);
									break;
								case "2": // Hard to traverse cell
									m.cells[i][j].setToHardToTraverse();
									m.cells[i][j].setBlocked(1);
									m.cells[i][j].setHighway(false);
									break;
								case "a": // Unblocked highway cell
									m.cells[i][j].setBlocked(1);
									m.cells[i][j].setHighway(true);
								case "b": // Hard to traverse highway cell
									m.cells[i][j].setToHardToTraverse();
									m.cells[i][j].setBlocked(1);
									m.cells[i][j].setHighway(true);
//									case "a1":
//										setHighwayOnCell(m.cells[i][j], 1);
//										break;
//									case "a2":
//										setHighwayOnCell(m.cells[i][j], 2);
//										break;
//									case "a3":
//										setHighwayOnCell(m.cells[i][j], 3);
//										break;
//									case "a4":
//										setHighwayOnCell(m.cells[i][j], 4);
//										break;
//									case "b1":
//										setHighwayOnCell(m.cells[i][j], 1);
//										m.cells[i][j].setToHardToTraverse();
//										break;
//									case "b2":
//										setHighwayOnCell(m.cells[i][j], 2);
//										m.cells[i][j].setToHardToTraverse();
//										break;
//									case "b3":
//										setHighwayOnCell(m.cells[i][j], 3);
//										m.cells[i][j].setToHardToTraverse();
//										break;
//									case "b4":
//										setHighwayOnCell(m.cells[i][j], 4);
//										m.cells[i][j].setToHardToTraverse();
//										break;
								}
							}
						}

						m.renderMaze();
						read.close();
						fileFlag = false;
					}
					catch(Exception FileNotFoundException){
						System.out.println("Error parsing file.");
					}
				}
				break;
			case 3:
				double optimalCost;

				ArrayList<SearchStatistics> searchStats = new ArrayList<>();
				searchStats.add(new SearchStatistics("UNIFORM-COST SEARCH"));
				searchStats.add(new SearchStatistics("MANHATTAN HIGHWAY"));
				searchStats.add(new SearchStatistics("1.25-MANHATTAN HIGHWAY"));
				searchStats.add(new SearchStatistics("2-MANHATTAN HIGHWAY"));
				searchStats.add(new SearchStatistics("MANHATTAN DISTANCE"));
				searchStats.add(new SearchStatistics("1.25-MANHATTAN DISTANCE"));
				searchStats.add(new SearchStatistics("2-MANHATTAN DISTANCE"));
				searchStats.add(new SearchStatistics("EUCLIDEAN DISTANCE"));
				searchStats.add(new SearchStatistics("1.25-EUCLIDEAN DISTANCE"));
				searchStats.add(new SearchStatistics("2-EUCLIDEAN DISTANCE"));
				searchStats.add(new SearchStatistics("EASY EUCLIDEAN"));
				searchStats.add(new SearchStatistics("1.25-EASY EUCLIDEAN"));
				searchStats.add(new SearchStatistics("2-EASY EUCLIDEAN"));
				searchStats.add(new SearchStatistics("HARD EUCLIDEAN"));
				searchStats.add(new SearchStatistics("1.25-HARD EUCLIDEAN"));
				searchStats.add(new SearchStatistics("2-HARD EUCLIDEAN"));
				searchStats.add(new SearchStatistics("(1.25, 1.25)-SEQUENTIAL A*"));
				searchStats.add(new SearchStatistics("(1.25, 2)-SEQUENTIAL A*"));
				searchStats.add(new SearchStatistics("(2, 1.25)-SEQUENTIAL A*"));
				searchStats.add(new SearchStatistics("(2, 2)-SEQUENTIAL A*"));

				for(int i = 0; i < 5; i++) {
					System.out.println();
					System.out.print("BATCH #" + i + "...");
					m = new Maze();
					m.genMaze();
					int j = 0;
					while(j < 10) {
						int statsIndex = 0;
						optimalCost = Double.MAX_VALUE;
						m.regenStartGoal();

//						System.out.println("Running uniform-cost tests...");
						if (A_Star.search(m, 0.0, "0", false)) {
							optimalCost = record(searchStats, statsIndex, optimalCost);
						} else {
							System.out.println("Uniform-cost test #" + ((10 * i) + j) + "failed.");
							break;
						}
						statsIndex++;

//						System.out.println("Running A* tests...");
						for (int k = 1; k < heuristics.size(); k++) {
							for (Double weight : weights) {
								if (A_Star.search(m, weight, heuristics.get(k), false)) {
//									System.out.println("Running A* test " + statsIndex + "...");
									optimalCost = record(searchStats, statsIndex, optimalCost);
								} else {
									System.out.println("A* test #" + ((10 * i) + j) + "failed.");
									break;
								}
								statsIndex++;
							}
						}

//						System.out.println("Running Sequential A* tests...");
						for (int k = 1; k < weights.size(); k++) {
							for (int l = 1; l < weights.size(); l++) {
								if (A_Star.seqSearch(m, heuristics.subList(1, heuristics.size()), weights.get(k), weights.get(l), false)) {
									optimalCost = record(searchStats, statsIndex, optimalCost);
									statsIndex++;
								}
							}
						}

//						System.out.println("Optimal Cost: " + optimalCost);

						for (SearchStatistics searchStat : searchStats) {
							searchStat.optimality += searchStat.cost / optimalCost;
						}
						j++;
					}
					System.out.println("Done.");
				}

				System.out.println();

				for (SearchStatistics searchStat : searchStats) {
					System.out.println(searchStat.name + ": " +
							"Average runtime: " + (searchStat.runtime / 50) + "ms | " +
							"Average optimality: " + ((searchStat.optimality / 50) * 100) + "% | " +
							"Average expanded nodes: " + (searchStat.expandedNodes / 50) + " | " +
							"Average memory required: " + (searchStat.usedMemory / 50) + " bytes"
					);
				}

				System.exit(0);
				break;
			case 4:
				System.exit(0);
		}

		while (choice != 5) {
			System.out.println("******************************************************");
			System.out.println("--Heuristic Search--");
			System.out.println("1. Run Uniform-Cost Search");
			System.out.println("2. Run A*");
			System.out.println("3. Run Weighted A*");
			System.out.println("4. Run Sequential A*");
			System.out.println("5. Export map");
			System.out.println("6. Exit");
			System.out.println("******************************************************");
			System.out.print("Type 1, 2, 3, 4, 5, or 6: ");
			choice = Integer.parseInt(br.readLine());
			System.out.println("******************************************************");

			switch(choice) {
				case 1: // Uniform-Cost Search
					// Run A* with a heuristic of 0.
					System.out.println("Solving...");
					A_Star.search(m, 0.0, "0", true);
					break;
				case 2: // A*
					// Run A* with an admissible heuristic.
					System.out.println("Solving...");
					A_Star.search(m, 1.0, "A", true);
					break;
				case 3: // Weighted A*
					// Ask user for a weight and run A* with that weight.
					System.out.print("Enter a weight: ");
					double weight = Double.parseDouble(br.readLine());
					System.out.println("Solving...");
					A_Star.search(m, weight, "A", true);
					break;
				case 4: // Sequential A*
					// Run Sequential A* search with a given weight.
					System.out.print("Enter a weight: ");
					double weightOne = Double.parseDouble(br.readLine());
					System.out.print("Enter another weight: ");
					double weightTwo = Double.parseDouble(br.readLine());
					System.out.println("Solving...");
					A_Star.seqSearch(m, heuristics.subList(1, heuristics.size()), weightOne, weightTwo, true);
					break;
				case 5: // Export map to file
					m.print(null);
					break;
			}
		}

		System.exit(0);
	}

	private static double record(ArrayList<SearchStatistics> searchStats, int statsIndex, double optimalCost) {
		searchStats.get(statsIndex).runtime += A_Star.runtime;
		searchStats.get(statsIndex).cost = A_Star.totalG;
		optimalCost = Math.min(optimalCost, A_Star.totalG);
		searchStats.get(statsIndex).expandedNodes += A_Star.numExpanded;
		searchStats.get(statsIndex).usedMemory += A_Star.memoryUsed;
		return optimalCost;
	}
}

class SearchStatistics {
	public String name;
	public long runtime;
	public double cost;
	public double optimality;
	public double expandedNodes;
	public double usedMemory;

	SearchStatistics(String name) {
		this.name = name;
	}
}

