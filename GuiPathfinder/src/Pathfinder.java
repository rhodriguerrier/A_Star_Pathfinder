import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pathfinder {
	
	//Function to calculate G via new path
	public double parentToChildDist(int parentX, int parentY, int childX, int childY) {
		int xDiff = Math.abs(childX - parentX);
		int yDiff = Math.abs(childY - parentY);
		double toBeSqrt = ((Math.pow(xDiff, 2)) + (Math.pow(yDiff, 2)));
		double roundedGCost = Math.round(Math.sqrt(toBeSqrt) * 100.0) / 100.0;
		return(roundedGCost);
	}
	
	//Function to calculate h cost of each new tile
	public double hCostCalculator(int currentX, int currentY, int endX, int endY) {
		int xDiff = Math.abs(endX - currentX);
		int yDiff = Math.abs(endY - currentY);
		double toBeSqrt = ((Math.pow(xDiff, 2)) + (Math.pow(yDiff, 2)));
		double roundedHCost = Math.round(Math.sqrt(toBeSqrt) * 100.0) / 100.0;
		return(roundedHCost);
	}
	
	//Function to calculate best path from start to end
	public int[][] calculatePath(int startBoxX, int startBoxY, int endBoxX, int endBoxY, int[][] wallsArray){
		boolean currentLowest = false;
		boolean endFound = false;
		boolean ignoreNeighbour = false;
		boolean ignoreNeighbourWall = false;
		boolean ignoreNeighbourClosed = false;
		List current = null;
		List tempTileHold = null;
		//Entries to these lists will have format {x, y, g cost, f value, parent x, parent y}
		List<List> openList = new ArrayList<List>();
		List<List> closedList = new ArrayList<List>();
		//Add start to openList
		double initialFValue = hCostCalculator(startBoxX, startBoxY, endBoxX, endBoxY);
		openList.add(Arrays.asList(startBoxX, startBoxY, 0, initialFValue, 0, 0));
		//-----
		
		while(openList.isEmpty() == false) {
			
			//--------------------------------------------------------------------------------------------
			//Fill current with lowest f value tile
			//At the start this will just pick starting tile

			int count = 0;
			int indexToDelete = 0;
			for(List l : openList) {
				if(current == null) {
					current = Arrays.asList(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5));
					currentLowest = false;
				}else if((Double) current.get(3) >= (Double) l.get(3)) {
					current = Arrays.asList(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5));
					currentLowest = false;
					indexToDelete = count;
				}else {
					//current is less than all because wall is hit
					//Write boolean here to look for lowest in openList if this is the case
					currentLowest = true;
				}
				count++;
			}
			
			if(currentLowest == true) {
				tempTileHold = null;
				count = 0;
				for(List l : openList) {
					if(tempTileHold == null) {
						tempTileHold = Arrays.asList(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5));
					}else if((Double) tempTileHold.get(3) >= (Double) l.get(3)) {
						tempTileHold = Arrays.asList(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5));
						indexToDelete = count;
					}
					count++;
				}
				
				current = Arrays.asList(tempTileHold.get(0), tempTileHold.get(1), tempTileHold.get(2), tempTileHold.get(3), tempTileHold.get(4), tempTileHold.get(5));
			}
			currentLowest = false;
			//Delete index of openList that has been saved into current and add to closedList
			openList.remove(indexToDelete);
			closedList.add(Arrays.asList(current.get(0), current.get(1), current.get(2),
					current.get(3), current.get(4), current.get(5)));
			
			//Check if current tile is the end point
			//If so then break out of while loop
			if((Integer) current.get(0) == endBoxX && (Integer) current.get(1) == endBoxY) {
				endFound = true;
				break;
			}
			
			//-----------------------------------------------------------------------------------------
			//Need to remove copy square from this for loop
			for(int i = -1; i <= 1; i++) {
				for(int j = -1; j <= 1; j++) {
					if(i == 0 && j == 0) {
						//do nothing
					}else {
						//Make placeHolder for neighbour being investigated
						//It get's parent's x and y but moved, 0 for now for g and f and parent's coordinate
						List placeHolder = Arrays.asList(((Integer)current.get(0) + (i)), ((Integer)current.get(1) + (j)),
								0, 0, current.get(0), current.get(1));
						
						//System.out.println(placeHolder);
						
						//If neighbour is in closed or is a wall then ignore and move on
						for(List l : closedList) {
							if((Integer) l.get(0) == placeHolder.get(0) && (Integer) l.get(1) == placeHolder.get(1)) {
								ignoreNeighbour = true;
							}
						}
						
						if(wallsArray[(Integer) placeHolder.get(0)][(Integer) placeHolder.get(1)] == 1) {
							ignoreNeighbour = true;
						}
						//System.out.println(ignoreNeighbour);
						//This count is so we can find the index if it is present in openList
						count = 0;
						if(ignoreNeighbour == false) {
							boolean inOpenList = false;
							for(List l : openList) {
								if((Integer) l.get(0) == placeHolder.get(0) && (Integer) l.get(1) == placeHolder.get(1)) {
									inOpenList = true;
									break;
								}else {
									inOpenList = false;
								}
								count++;
							}
							double childGCost = ((Number) current.get(2)).doubleValue() + parentToChildDist((Integer) current.get(0),
									(Integer) current.get(1), (Integer) placeHolder.get(0), (Integer) placeHolder.get(1));
							double childFValue = childGCost + hCostCalculator((int) placeHolder.get(0),
									(int) placeHolder.get(1), endBoxX, endBoxY);
							if(inOpenList == false) {
								openList.add(Arrays.asList(placeHolder.get(0), placeHolder.get(1),
										childGCost, childFValue, current.get(0), current.get(1)));
							}else if(inOpenList == true) {
								List tempGHold = openList.get(count);
								if(childGCost < (Double) tempGHold.get(2)) {
									//If childGCost is lower than g cost of child already in openList then
									openList.remove(count);
									openList.add(Arrays.asList(placeHolder.get(0), placeHolder.get(1),
											childGCost, childFValue, current.get(0), current.get(1)));
								}
							}
						}
					}
					ignoreNeighbour = false;
				}
			}
			//-----------------------------------------------------------------------------------------
		}
		
		//Write if statement to draw path if end found or produce message that no path found
		//------------------------------------------------------------------------------------------
		int[][] gridWithPath = new int[50][50];
		int parentFinderX;
		int parentFinderY;
		if(endFound == true) {
			boolean endParentChain = false;
			while(endParentChain == false) {
				parentFinderX = (Integer) current.get(4);
				parentFinderY = (Integer) current.get(5);
				if((Integer) current.get(0) == startBoxX && (Integer) current.get(1) == startBoxY) {
					//If current is start then do not draw, break and go on to render
					endParentChain = true;
					break;
				}else if((Integer) current.get(0) != endBoxX || (Integer) current.get(1) != endBoxY){
					//Take current, add it to new array for pathDrawn
					//Then move on to it's parent coordinate and repeat
					gridWithPath[(Integer) current.get(0)][(Integer) current.get(1)] = 1;
					for(List l : closedList) {
						if((Integer) l.get(0) == parentFinderX && (Integer) l.get(1) == parentFinderY) {
							current = Arrays.asList(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5));
						}
					}
				}else {
					for(List l : closedList) {
						if((Integer) l.get(0) == parentFinderX && (Integer) l.get(1) == parentFinderY) {
							current = Arrays.asList(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5));
						}
					}
				}
			}
		}
		//------------------------------------------------------------------------------------------
		return(gridWithPath);
	}
//---------------------------------------------------------------------------------------------
}
