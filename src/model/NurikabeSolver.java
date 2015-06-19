package model;

import java.awt.List;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
//create a check space algorithm that looks at blanks and checks the sourceNum of every source it touches to see if
//the sources will fit,
//create an algorithm that uses arraylists or arraydeques to remember patterns that were used and avoid them
//this should reduce run times,  
//memoization
//try making solver a while loop
//while(point != null)????
//replace function call with continue

public class NurikabeSolver {

    enum BlockType {

        LAND, WATER, BLANK
    }

    Board board = null;
    int totalLand = 0;
    ArrayList<ArrayList<Block>> sourceChildren = new ArrayList<ArrayList<Block>>();
    ArrayList<Block> possibleLand, adjacent, markedLand, topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner, sortedSources;
    static ArrayDeque<ArrayDeque<ArrayDeque<Block>>> landStack, restrictedPatterns;
    static ArrayDeque<ArrayDeque<Block[][]>> gridStack = new ArrayDeque<ArrayDeque<Block[][]>>();
    static ArrayDeque<Point> sourceLoc = new ArrayDeque<Point>();
    static ArrayDeque<ArrayDeque<Block>> currentPattern = new ArrayDeque<ArrayDeque<Block>>();

    //static ArrayDeque<Block> pattern = new ArrayDeque<Block>();
    public NurikabeSolver(Board b) {
        board = b;
        sourceChildren = new ArrayList<>();
        possibleLand = new ArrayList<>();
        adjacent = new ArrayList<>();
        markedLand = new ArrayList<>();
        topLeftCorner = new ArrayList<>();
        topRightCorner = new ArrayList<>();
        bottomLeftCorner = new ArrayList<>();
        bottomRightCorner = new ArrayList<>();
        sortedSources = new ArrayList<>();
        landStack = new ArrayDeque<>();
        gridStack = new ArrayDeque<>();
        sourceLoc = new ArrayDeque<>();
        restrictedPatterns = new ArrayDeque<>();
        currentPattern = new ArrayDeque<>();
    }

    public void solve() { //NEW: get the rest of the checks done for the solver, come up with a way to move from source
        // to source, find a way to grab potential land
        boolean b = false;
        Block[][] knownGrid;
        int count = 0;
        //while (!b) {
        //while(count < 2){
        Block[][] tempGrid = createClone(board.grid);
        solveKnown();
        b = gridCompare(board.grid, tempGrid);
        //}
        knownGrid = createClone(board.grid);

        /*
         currentPattern.push(new ArrayDeque<Block>());
         restrictedPatterns.push(new ArrayDeque<ArrayDeque<Block>>());
         gridStack.push(new ArrayDeque<Block[][]>());
         gridStack.peek().push(knownGrid);
         sourceLoc.push(findNewSourceLoc());
         landStack.push(new ArrayDeque<ArrayDeque<Block>>());
         ArrayDeque<Block> temp = new ArrayDeque<Block>();
         findPossibleMoves(sourceLoc.peek().x, sourceLoc.peek().y, new ArrayList<Block>(), temp);
         landStack.peek().push(temp);
         */
        //solveLand(true);
    }

    public Point findNewSourceLoc() {
        Point p = null;
        //int min = 100;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isLandSource() && !board.grid[i][j].isComplete()) {
                    /*if(board.grid[i][j].sourceNum < min){
                     min = board.grid[i][j].sourceNum;
                     p = new Point(i,j);
                     }
                     */
                    p = new Point(i, j);
                    break outerLoop;
                }
            }
        }
        return p;
    }

    public boolean checkTrappedWater() {
        boolean valid = true;
        ArrayList<Block> marked = new ArrayList<Block>();
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isWater()) {
                    countWaterAndBlanks(i, j, marked);
                    break outerLoop;
                }
            }
        }
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isWater() && !marked.contains(board.grid[i][j])) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    public void setGrid(Block[][] b) {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                board.grid[i][j].set(b[i][j]);
            }
        }
        board.updateBoard(board.getGraphics());
    }

    public void save() {
        gridStack.push(new ArrayDeque<Block[][]>());
        gridStack.peek().push(createClone(board.grid));
    }

    public void undo() {

        setGrid(gridStack.peek().peek());
        board.updateBoard(board.getGraphics());
    }

    public void solveLand(boolean valid) {

        //PROBLEM: currentPattern is getting out of hand, containing more than what it is supposed to
        boolean complete = false;
        while (!complete) {
//			try {
//				Thread.sleep(750);
//			} catch (InterruptedException e) {
//                            System.out.println("NOOOOO");
//			}

            boolean restricted = false;

            if (!landStack.isEmpty() && !landStack.peek().isEmpty() && !landStack.peek().peek().isEmpty() && !restrictedPatterns.isEmpty() && !currentPattern.peek().isEmpty()) {

                ArrayList<Block> landPattern = new ArrayList<Block>(landStack.peek().peek().clone());
                HashSet<Block> cPattern = new HashSet<Block>(currentPattern.peek().clone());

                ArrayList<ArrayList<Block>> rPattern = new ArrayList<ArrayList<Block>>();
                ArrayList<ArrayDeque<Block>> trPattern = new ArrayList<ArrayDeque<Block>>(restrictedPatterns.peek().clone());

                for (ArrayDeque<Block> myList : trPattern) {
                    ArrayList temp = new ArrayList(myList);
                    HashSet tempHash = new HashSet(temp);
                    rPattern.add(new ArrayList<Block>(tempHash));
                }

                ArrayDeque<Block> tempPattern = new ArrayDeque<Block>();

                outerLoop:
                for (int i = 0; i < rPattern.size(); i++) {
                    if (cPattern.size() == rPattern.get(i).size() && rPattern.get(i).containsAll(cPattern) && cPattern.containsAll(rPattern.get(i))) {
                        restricted = true;
                        break outerLoop;
                    }
                }

                if (!restricted) {
                    for (Block landPattern1 : landPattern) {
                        cPattern.add(landPattern1);
                        for (ArrayList<Block> rPattern1 : rPattern) {
                            if (cPattern.containsAll(rPattern1) && rPattern1.containsAll(cPattern) && cPattern.size() == rPattern1.size()) {
                                System.out.print("True!!!");
                                System.out.println("c : " + cPattern);
                                System.out.println("r : " + rPattern1);
                                tempPattern.push(landPattern1);
                                break;
                            }
                        }

                        cPattern.clear();
                        cPattern.addAll(currentPattern.peek().clone());
                    }
                    System.out.println("tempPattern: " + tempPattern);

                    landStack.peek().peek().removeAll(tempPattern);

                }

            }
            if (sourceLoc.isEmpty()) {
                complete = true;
                System.out.println("Empty");
                board.updateBoard(board.getGraphics());

            } else if (!valid) {
                System.out.println(" and Invalid Board");
                System.out.println("checkTrapped " + checkTrappedWater());
                System.out.println("lone Land Stem " + loneLandStem());
                System.out.println("checkInvalid blocks " + checkInvalidBlocks());
                System.out.println("checkIllegal 2x2 " + checkIllegal2x2());
                System.out.println("board complete" + completedBoard());
                if (!gridStack.isEmpty()) {
                    if (!gridStack.peek().isEmpty()) {
                        setGrid(gridStack.peek().peek());
                    }
                }

                if (!currentPattern.isEmpty()) {
                    if (!currentPattern.peek().isEmpty()) {
                        currentPattern.peek().pop();
                    }
                }
                if (landStack.isEmpty()) {
                    System.out.println("Empty1");
                } else if (landStack.peek().isEmpty()) {
                    System.out.println("Empty2");
                    restrictedPatterns.pop();

                    if (!currentPattern.isEmpty()) {
                        currentPattern.pop();
                    }

                    landStack.pop();
                    gridStack.pop();
                    sourceLoc.pop();
                    valid = false;
                } else if (landStack.peek().peek().isEmpty()) {
                    System.out.println("Empty3");
                    landStack.peek().pop();
                    gridStack.peek().pop();

                    valid = false;
                } else {

                    setGrid(gridStack.peek().peek());
                    System.out.println("Not Empty");

                    System.out.println("grids.peek() size = " + gridStack.peek().size());
                    System.out.println("land.peek() size = " + landStack.peek().size());
                    System.out.println("land.peek().peek() size = " + landStack.peek().peek().size());

                    currentPattern.peek().push(landStack.peek().peek().peek());

                    landStack.peek().peek().peek().setLandStem();
                    landStack.peek().peek().pop();
                    board.updateBoard(board.getGraphics());
                    valid = checkValidBoard();
                    if (valid && !board.grid[sourceLoc.peek().x][sourceLoc.peek().y].isComplete()) {
                        System.out.println("Valid Board AND placed");
                        gridStack.peek().push(createClone(board.grid));
                        ArrayDeque<Block> temp = new ArrayDeque<Block>();
                        findPossibleMoves(sourceLoc.peek().x, sourceLoc.peek().y, new ArrayList<Block>(), temp);
                        landStack.peek().push(temp);
                    }

                }

            } else if (board.grid[sourceLoc.peek().x][sourceLoc.peek().y].isComplete() && board.grid[sourceLoc.peek().x][sourceLoc.peek().y].isValid()) {
                solveKnown();
                System.out.print("Complete");
                if (valid) {
                    valid = checkValidBoard();
                }
                if (valid) {
                    System.out.println(" and valid");
                    Point point = findNewSourceLoc();
                    if (point != null) {
                        currentPattern.push(new ArrayDeque<Block>());
                        restrictedPatterns.push(new ArrayDeque<ArrayDeque<Block>>());
                        sourceLoc.push(point);
                        landStack.push(new ArrayDeque<ArrayDeque<Block>>());
                        ArrayDeque<Block> temp = new ArrayDeque<Block>();
                        findPossibleMoves(sourceLoc.peek().x, sourceLoc.peek().y, new ArrayList<Block>(), temp);
                        landStack.peek().push(temp);
                        gridStack.push(new ArrayDeque<Block[][]>());
                        gridStack.peek().push(createClone(board.grid));
                    } else {
                        System.out.println("Done");
                        complete = true;
                    }
                } else {
                    System.out.println(" and Invalid Board");
                    System.out.println("checkTrapped " + checkTrappedWater());
                    System.out.println("lone Land Stem " + loneLandStem());
                    System.out.println("checkInvalid blocks " + checkInvalidBlocks());
                    System.out.println("checkIllegal 2x2 " + checkIllegal2x2());
                    System.out.println("board complete" + completedBoard());
                    if (landStack.isEmpty()) {
                        System.out.println("Empty1");
                    } else if (landStack.peek().isEmpty()) {
                        System.out.println("Empty2");
                        restrictedPatterns.pop();

                        if (!currentPattern.isEmpty()) {
                            currentPattern.pop();
                        }

                        landStack.pop();
                        gridStack.pop();
                        sourceLoc.pop();
                        valid = false;
                    } else if (landStack.peek().peek().isEmpty()) {
                        System.out.println("Empty3");
                        landStack.peek().pop();
                        gridStack.peek().pop();

                        valid = false;
                    } else {

                        setGrid(gridStack.peek().peek());
                        System.out.println("Not Empty");

                        System.out.println("grids.peek() size = " + gridStack.peek().size());
                        System.out.println("land.peek() size = " + landStack.peek().size());
                        System.out.println("land.peek().peek() size = " + landStack.peek().peek().size());
                        currentPattern.peek().push(landStack.peek().peek().peek());
                        landStack.peek().peek().peek().setLandStem();
                        landStack.peek().peek().pop();
                        board.updateBoard(board.getGraphics());
                        valid = checkValidBoard();
                        if (valid && !board.grid[sourceLoc.peek().x][sourceLoc.peek().y].isComplete()) {
                            System.out.println("Valid Board AND placed");
                            gridStack.peek().push(createClone(board.grid));
                            ArrayDeque<Block> temp = new ArrayDeque<Block>();
                            findPossibleMoves(sourceLoc.peek().x, sourceLoc.peek().y, new ArrayList<Block>(), temp);
                            landStack.peek().push(temp);
                        }
                    }

                }

            } else {

                System.out.println("Invalid Board");
                System.out.println("checkTrapped " + checkTrappedWater());
                System.out.println("lone Land Stem " + loneLandStem());
                System.out.println("checkInvalid blocks " + checkInvalidBlocks());
                System.out.println("checkIllegal 2x2 " + checkIllegal2x2());
                System.out.println("board complete " + completedBoard());

                if (landStack.isEmpty()) {
                    System.out.println("Empty1");
                } else if (landStack.peek().isEmpty()) {
                    System.out.println("Empty2");
                    //mem edit - start
                    restrictedPatterns.pop();

                    if (!currentPattern.isEmpty()) {
                        currentPattern.pop();
                    }
                    landStack.pop();
                    gridStack.pop();
                    sourceLoc.pop();
                    valid = false;
                } else if (landStack.peek().peek().isEmpty()) {
                    System.out.println("Empty3");
                    landStack.peek().pop();
                    gridStack.peek().pop();

                    if (!currentPattern.peek().isEmpty()) {
                        currentPattern.peek().pop();

                    }
                    valid = false;
                } else {

                    setGrid(gridStack.peek().peek());
                    System.out.println("Not Empty");

                    System.out.println("grids.peek() size = " + gridStack.peek().size());
                    System.out.println("land.peek() size = " + landStack.peek().size());
                    System.out.println("land.peek().peek() size = " + landStack.peek().peek().size());
                    currentPattern.peek().push(landStack.peek().peek().peek());
                    landStack.peek().peek().peek().setLandStem();
                    landStack.peek().peek().pop();
                    board.updateBoard(board.getGraphics());
                    valid = checkValidBoard();
                    if (valid && !board.grid[sourceLoc.peek().x][sourceLoc.peek().y].isComplete()) {
                        System.out.println("Valid Board AND placed");
                        gridStack.peek().push(createClone(board.grid));
                        ArrayDeque<Block> temp = new ArrayDeque<Block>();
                        findPossibleMoves(sourceLoc.peek().x, sourceLoc.peek().y, new ArrayList<Block>(), temp);
                        landStack.peek().push(temp);
                    }

                }
            }
        }
    }

    public boolean completedBoard() {
        boolean filled = true;
        boolean complete = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].blank) {
                    filled = false;
                    break outerLoop;
                }
            }

        }
        if (filled) {
            outerLoop:
            for (int i = 0; i < board.sliderValue; i++) {
                for (int j = 0; j < board.sliderValue; j++) {
                    if (board.grid[i][j].isLandSource() && !board.grid[i][j].isComplete()) {
                        complete = false;
                        break outerLoop;
                    }
                }
            }
        } else {
            complete = true;
        }
        return complete;
    }

    public boolean loneLandStem() {
        boolean valid = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isLandStem()) {
                    if (checkVoid(i, j) == 4) {
                        valid = false;
                    }
                }

            }
        }
        return valid;
    }

    public boolean checkTrappedLand() {
        boolean valid = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && !board.grid[i][j].isComplete()) {
                    ArrayDeque<Block> temp = new ArrayDeque<Block>();
                    findPossibleMoves(i, j, new ArrayList<Block>(), temp);
                    if (temp.isEmpty()) {
                        valid = false;
                        break outerLoop;
                    }
                }
            }

        }
        return valid;
    }

    public boolean checkInvalidBlocks() {
        boolean valid = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue - 1; i++) {
            for (int j = 0; j < board.sliderValue - 1; j++) {
                if (board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) {
                    if (!board.grid[i][j].isValid()) {
                        valid = false;
                        break outerLoop;
                    }
                }
            }

        }
        return valid;
    }

    public boolean checkValidBoard() {
        return (checkTrappedLand() && checkTrappedWater() && checkIllegal2x2() && loneLandStem() && checkInvalidBlocks() && completedBoard());
    }

    public int countBlanksAndSources(int i, int j, ArrayList<Block> m, ArrayList<Block> sources) {
        if (board.grid[i][j].isBlank()) {
            m.add(board.grid[i][j]);
        } else if (board.grid[i][j].isLandSource()) {
            sources.add(board.grid[i][j]);
        }
        return countAdjacentBlanksAndSources(i - 1, j, m, sources) + countAdjacentBlanksAndSources(i + 1, j, m, sources) + countAdjacentBlanksAndSources(i, j - 1, m, sources) + countAdjacentBlanksAndSources(i, j + 1, m, sources) + 1;
    }

    public int countAdjacentBlanksAndSources(int i, int j, ArrayList<Block> m, ArrayList<Block> sources) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else if ((board.grid[i][j].isBlank() && !m.contains(board.grid[i][j])) || (board.grid[i][j].isBlank() && !sources.contains(board.grid[i][j]))) {
            return countBlanksAndSources(i, j, m, sources);
        } else {
            return 0;
        }
    }

    public boolean checkIllegal2x2() {
        boolean valid = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue - 1; i++) {
            for (int j = 0; j < board.sliderValue - 1; j++) {
                int count = 0;
                Block marked = null;
                if (board.grid[i][j].isWater()) {
                    count++;
                }
                if (board.grid[i + 1][j].isWater()) {
                    count++;
                }
                if (board.grid[i][j + 1].isWater()) {
                    count++;
                }
                if (board.grid[i + 1][j + 1].isWater()) {
                    count++;
                }
                if (count == 4) {
                    valid = false;
                    break outerLoop;
                }

            }
        }
        return valid;
    }

    public Block[][] createClone(Block[][] originalGrid) {
        Block[][] newGrid = new Block[board.sliderValue][board.sliderValue];
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                newGrid[i][j] = (Block) originalGrid[i][j].clone();
            }
        }
        return newGrid;
    }

    public boolean gridCompare(Block[][] grid1, Block[][] grid2) {
        boolean b = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (grid1[i][j].equivalentTo((Block) grid2[i][j]) == false) {
                    b = false;
                    break outerLoop;
                }

            }

        }
        return b;
    }
    int count = 0;

    public void solveKnown() {
        try {
            // this setup needs to stay the same
            fillWaters();
            board.updateBoard(board.getGraphics());Thread.sleep(1000);
            fillKnownWater();
            board.repaint();
            surroundAllComplete();
            board.updateBoard(board.getGraphics());Thread.sleep(1000);
             check2x2Area();
            board.updateBoard(board.getGraphics());Thread.sleep(1000);
            checkSidesAndCorners();
            board.updateBoard(board.getGraphics());Thread.sleep(1000);
            //Thread.sleep(1000);
            expandAllLand();
            //Thread.sleep(1000);
            board.updateBoard(board.getGraphics());Thread.sleep(1000);
            expandAllWater();
            //Thread.sleep(1000);
            board.updateBoard(board.getGraphics());Thread.sleep(1000);
            //checkLastLandStemPlacement();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(NurikabeSolver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int countWaterAndBlanks(int i, int j, ArrayList<Block> m) {
        m.add(board.grid[i][j]);
        return countAdjacentWaterAndBlanks(i - 1, j, m) + countAdjacentWaterAndBlanks(i + 1, j, m) + countAdjacentWaterAndBlanks(i, j - 1, m) + countAdjacentWaterAndBlanks(i, j + 1, m) + 1;
    }

    public int countAdjacentWaterAndBlanks(int i, int j, ArrayList<Block> m) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else if ((board.grid[i][j].isBlank() || board.grid[i][j].isWater()) && !m.contains(board.grid[i][j])) {
            return countWaterAndBlanks(i, j, m);
        } else {
            return 0;
        }
    }

    public int countWater(int i, int j, ArrayList<Block> m) {
        m.add(board.grid[i][j]);
        return countAdjacentWater(i - 1, j, m) + countAdjacentWater(i + 1, j, m) + countAdjacentWater(i, j - 1, m) + countAdjacentWater(i, j + 1, m) + 1;
    }

    public int countAdjacentWater(int i, int j, ArrayList<Block> m) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else if (board.grid[i][j].isWater() && !m.contains(board.grid[i][j])) {
            return countWater(i, j, m);
        } else {
            return 0;
        }
    }

    public boolean checkComplete() {
        boolean complete = true;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isLandSource()) {
                    if (!board.grid[i][j].isComplete()) {
                        complete = false;
                        break outerLoop;
                    }
                }
            }
        }
        return complete;
    }

    public int retrieveTotalLand() {
        int totalLand = 0;
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isLandSource()) {
                    totalLand += board.grid[i][j].sourceNum;
                }
            }
        }
        return totalLand;
    }

    public boolean checkWaterContinuity() {
        boolean correct = true;
        int totalWater = (int) (Math.pow(board.sliderValue + 1, 2) - totalLand);
        int actualWater = 0;
        outerLoop:
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isWater()) {
                    actualWater += countWater(i, j, new ArrayList<Block>());
                    break outerLoop;
                }
            }
        }
        if (actualWater != totalWater) {
            correct = false;
        }
        return correct;
    }

    public void checkLandContinuity() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isBlank()) {
                    //ArrayList<Block> temp = new ArrayList<Block>();
                    int right = checkAdjacentLandContinuity(i + 1, j);
                    int left = checkAdjacentLandContinuity(i - 1, j);
                    int top = checkAdjacentLandContinuity(i, j - 1);
                    int bottom = checkAdjacentLandContinuity(i, j + 1);
                    int count = right + left + top + bottom;
                    if (count == 2) {

                        if ((checkAdjacentLandContinuity(i + 1, j) == 1) && (checkAdjacentLandContinuity(i, j - 1) == 1)) { //top and right
							/*
                             if((board.grid[i+1][j].parent == board.grid[i][j-1].parent)){
                             board.grid[i][j].setLandStem();
                             }
                             else if((board.grid[i+1][j].isChild() && !board.grid[i][j-1].isChild()) || (!board.grid[i+1][j].isChild() && board.grid[i][j-1].isChild())){
                             board.grid[i][j].setLandStem();
                             }
                             else */
                            if (board.grid[i + 1][j].parent != board.grid[i][j - 1].parent) {
                                board.grid[i][j].setWater();
                            }
                        } else if ((checkAdjacentLandContinuity(i - 1, j) == 1) && (checkAdjacentLandContinuity(i, j - 1) == 1)) { //top and left
							/*
                             if((board.grid[i-1][j].parent == board.grid[i][j-1].parent)){
                             board.grid[i][j].setLandStem();
                             }
                             else if((board.grid[i-1][j].isChild() && !board.grid[i][j-1].isChild()) || (!board.grid[i-1][j].isChild() && board.grid[i][j-1].isChild())){
                             board.grid[i][j].setLandStem();
                             }
                             else*/ if (board.grid[i - 1][j].parent != board.grid[i][j - 1].parent) {
                                board.grid[i][j].setWater();
                            }
                        } else if ((checkAdjacentLandContinuity(i + 1, j) == 1) && (checkAdjacentLandContinuity(i, j + 1) == 1)) { //bottom and right
							/*if((board.grid[i+1][j].parent == board.grid[i][j+1].parent)){
                             board.grid[i][j].setLandStem();
                             }
                             else if((board.grid[i+1][j].isChild() && !board.grid[i][j+1].isChild()) || (!board.grid[i+1][j].isChild() && board.grid[i][j+1].isChild())){
                             board.grid[i][j].setLandStem();
                             }
                             else*/ if (board.grid[i + 1][j].parent != board.grid[i][j + 1].parent) {
                                board.grid[i][j].setWater();
                            }
                        } else if ((checkAdjacentLandContinuity(i - 1, j) == 1) && (checkAdjacentLandContinuity(i, j + 1) == 1)) { //bottom and left
							/*if((board.grid[i-1][j].parent == board.grid[i][j+1].parent)){
                             board.grid[i][j].setLandStem();
                             }
                             else if((board.grid[i-1][j].isChild() && !board.grid[i][j+1].isChild()) || (!board.grid[i+1][j].isChild() && board.grid[i][j+1].isChild())){
                             board.grid[i][j].setLandStem();
                             }
                             else */
                            if (board.grid[i - 1][j].parent != board.grid[i][j + 1].parent) {
                                board.grid[i][j].setWater();
                            }
                        } else if ((checkAdjacentLandContinuity(i + 1, j) == 1) && (checkAdjacentLandContinuity(i - 1, j) == 1)) { //right and left
							/*if((board.grid[i+1][j].parent == board.grid[i-1][j].parent)){
                             board.grid[i][j].setLandStem();
                             }
                             else if((board.grid[i+1][j].isChild() && !board.grid[i-1][j].isChild()) || (!board.grid[i+1][j].isChild() && board.grid[i-1][j].isChild())){
                             board.grid[i][j].setLandStem();
                             }
                             else */
                            if (board.grid[i + 1][j].parent != board.grid[i - 1][j].parent) {
                                board.grid[i][j].setWater();
                            }
                        } else if ((checkAdjacentLandContinuity(i, j - 1) == 1) && (checkAdjacentLandContinuity(i, j + 1) == 1)) { //top and bottom
							/*if((board.grid[i][j-1].parent == board.grid[i][j+1].parent)){
                             board.grid[i][j].setLandStem();
                             }
                             else if((board.grid[i][j-1].isChild() && !board.grid[i][j+1].isChild()) || (!board.grid[i][j].isChild() && board.grid[i][j+1].isChild())){
                             board.grid[i][j].setLandStem();
                             }
                             else*/ if (board.grid[i][j - 1].parent != board.grid[i][j + 1].parent) {
                                board.grid[i][j].setWater();
                            }
                        }
                    }
                }
            }
        }
    }

    public int checkAdjacentWaterContinuity(int i, int j) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else {
            /*
             if((board.grid[i][j].isLandStem() || board.grid[i][j].isLandSource())){
             return 4;
             }
             */
            if (board.grid[i][j].isWater()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public int checkAdjacentLandContinuity(int i, int j) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else {
            if ((board.grid[i][j].isLandStem() || board.grid[i][j].isLandSource())) {//&& !board.grid[i][j].isChild()
                return 1;
            } else {
                return 0;
            }
        }
    }

    public void fillKnownLand() {
        retrievePotentialLand();
        for (int i = 0; i < board.getSliderValue(); i++) {
            for (int j = 0; j < board.getSliderValue(); j++) {
                if (!possibleLand.contains(board.getGrid()[i][j])) {
                    Block block = board.getGrid()[i][j];
                    if (!block.isLandSource() || !block.isComplete()) {
                        block.setWater();
                    }
                }
            }
        }
        possibleLand.clear();
        board.repaint();
    }

    /**
     * Updated algorithm that marks potential land stems using bfs
     */
    public void markPotentialLandStems(Block origin) {
        ArrayList<Block> totalDiscovered = new ArrayList<Block>();
        Deque<Block> discovered = traverseBlocks(origin, BlockType.LAND, totalDiscovered);
        possibleLand.addAll(discovered);
        ArrayDeque<Block> totalSurroundingBlanks = getSurroundingBlanks(origin, totalDiscovered);
        possibleLand.addAll(totalSurroundingBlanks);
        int count = origin.getCurrentSourceVal() + 1;
        System.out.println("Origin Source num = " + origin.sourceNum);
        System.out.println(count + " is the current source val at origin " + origin.getXOnGrid() + ", " + origin.getYOnGrid());
        System.out.println(origin.getSourceNum() + " is the source num at the same origin");
        System.out.println("There are " + totalSurroundingBlanks.size() + " surrounding Blanks");
        ArrayDeque<Block> potentialBlocks = new ArrayDeque<>();
        ArrayDeque<Block> initialSet = new ArrayDeque<>(totalSurroundingBlanks);
        ArrayDeque<Block> endSet = new ArrayDeque<>();
        // marks blocks as water depending on whether the block is adjacent to the 
        ArrayList<Block> removalList = new ArrayList<>();
        for (Block b : totalSurroundingBlanks) {
            if (isAdjacentToIsland(b, origin)) {
                b.setWater();
                removalList.add(b);
            }

            board.updateBoard(board.getGraphics());
            
        }
        
        totalSurroundingBlanks.removeAll(removalList);
        potentialBlocks.addAll(totalSurroundingBlanks);
        possibleLand.addAll(totalSurroundingBlanks);
        while (count < origin.getSourceNum()) {
            while (!initialSet.isEmpty()) {
                for (Block b : getSurroundingBlocks(initialSet.removeFirst())) {
                    if ((b.isBlank() || (b.isLandStem() && b.getParent() == null))
                            && !totalSurroundingBlanks.contains(b)) {
                        if (!isAdjacentToIsland(b, origin)) {
                            totalSurroundingBlanks.add(b);
                            endSet.add(b); 
                            potentialBlocks.add(b);
                            possibleLand.add(b);
                        }
                    }
                }
            }
            initialSet.addAll(endSet);
            endSet.clear();
            System.out.println("currentCount == " + count + ", current source val =  " + origin.currentSourceVal);
            System.out.println("PotentialBlocks size = " + potentialBlocks.size());
            System.out.println("Initial Set size = " + initialSet.size());
            for (Block b : initialSet) {
                System.out.println("block in initial set at " + b.gridX + ". " + b.gridY);
            }
            count++;
        }
        // this is an issue
        // the goal is to have the puzzle find how many spaces it can move
        // if it is limited to a space that is exactly the amount of blocks it can have,
        // it will fill each of those spaces.
        System.out.println("******************************************************");
        System.out.println("******************************************************");
        System.out.println("Source Num here is " + origin.getSourceNum());
        System.out.println("current source val is " + origin.getCurrentSourceVal());
        System.out.println("potential blocks size = " + potentialBlocks.size());
        System.out.println("******************************************************");
        System.out.println("******************************************************");
        for(Block b : potentialBlocks){
            System.out.println(b.getXOnGrid() + ", " + b.getYOnGrid());
        }
        if(origin.getSourceNum() == (origin.getCurrentSourceVal() + potentialBlocks.size())){
            System.out.println("I BROKE HERE");
            for(Block b : potentialBlocks){
                if(!origin.isComplete()){
                    b.setLandStem();
                    board.updateBoard(board.getGraphics());
                    break;
                }
            }
        }
        
    }

    public ArrayDeque<Block> getAllReachableBlocks(Block origin) {
        ArrayList<Block> totalDiscovered = new ArrayList<Block>();
        Deque<Block> discovered = traverseBlocks(origin, BlockType.LAND, totalDiscovered);
        possibleLand.addAll(discovered);
        ArrayDeque<Block> totalSurroundingBlanks = getSurroundingBlanks(origin, totalDiscovered);
        possibleLand.addAll(totalSurroundingBlanks);
        int count = origin.getCurrentSourceVal() + 1;
        ArrayDeque<Block> potentialBlocks = new ArrayDeque<>();
        ArrayDeque<Block> initialSet = new ArrayDeque<>(totalSurroundingBlanks);
        ArrayDeque<Block> endSet = new ArrayDeque<>();
        // marks blocks as water depending on whether the block is adjacent to the 
        while (count < origin.getSourceNum()) {
            while (!initialSet.isEmpty()) {
                for (Block b : getSurroundingBlocks(initialSet.removeFirst())) {
                    if ((b.isLandStem() && !b.isChild()) || (b.isBlank() && !totalSurroundingBlanks.contains(b))) {
                        if (!isAdjacentToIsland(b, origin)) {
                            totalSurroundingBlanks.add(b);
                            endSet.add(b);
                            potentialBlocks.add(b);
                            possibleLand.add(b);
                        }
                    }
                }
            }
            initialSet.addAll(endSet);
            endSet.clear();
            count++;
        }
        return potentialBlocks;
    }

    public void surroundAllComplete() {
        System.out.println(board.getGrid()[0][0].isComplete() + " source num = " + board.getGrid()[0][0].sourceNum);
        ArrayList<Block> totalDiscovered = new ArrayList<>();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                Block b = board.grid[i][j];
                if (b.isComplete()) {
                    System.out.println("Surround Complete " + b.sourceNum);
                    surroundComplete(b, totalDiscovered);
                }
            }
        }
        board.repaint();
    }

    public void surroundComplete(Block origin, ArrayList<Block> totalDiscovered) {
        for (Block b : getSurroundingBlanks(origin, totalDiscovered)) {
            b.setWater();
            System.out.println("Setting to surrounding blank to water at " + b.getXOnGrid() + ", " + b.getYOnGrid());
        }
    }

    public void fillCompleteBlockSurroundings(int i, int j, ArrayList<Block> m) {
        if ((i < 0) || (i >= board.sliderValue) || (j < 0) || (j >= board.sliderValue)) {

        } else if (!board.grid[i][j].isComplete()) {
            board.grid[i][j].setWater();
        } else if (board.grid[i][j].isComplete() && !m.contains(board.grid[i][j])) {
            m.add(board.grid[i][j]);
            fillCompleteBlockSurroundings(i + 1, j, m);
            fillCompleteBlockSurroundings(i - 1, j, m);
            fillCompleteBlockSurroundings(i, j + 1, m);
            fillCompleteBlockSurroundings(i, j - 1, m);
        }
    }

    public void retrievePotentialLand() {
        possibleLand.clear();
        sourceChildren.clear();

        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isLandSource() && !board.grid[i][j].isComplete()) {
                    ArrayList<Block> marked = new ArrayList<Block>();
                    markPotentialLandStems(board.getGrid()[i][j]);
                    sourceChildren.add(marked);
                }
            }
        }

        /*
         for (int i = 0; i < sourceChildren.size(); i++) {
         for (int j = 0; j < sourceChildren.get(i).size(); j++) {

         if (!possibleLand.contains(sourceChildren.get(i).get(j))) {
         possibleLand.add(sourceChildren.get(i).get(j));
         }

         }
         }
         */
        board.repaint();
    }

    public void fillKnownWater() {
        retrievePotentialLand();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                Block block = board.grid[i][j];
                if (block.isBlank()) {
                    if (!possibleLand.contains(block)) {
                        block.setWater();
                    }
                    /*
                     int count = 0;
                     for (Block b : getSurroundingBlocks(block)) {
                     if (b.isWater()) {
                     count++;
                     }
                     }
                    
                     if (count >= 3) {
                     block.setWater();
                     }
                     */
                }
            }
        }
        /*
		
         */

        board.repaint();
        possibleLand.clear();
    }

    public void fillVoids() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isBlank()) {
                    if (checkVoid(i, j) == 3) {
                        board.grid[i][j].setWater();
                        continue;
                    }
                }
            }
        }
    }

    public void checkSidesAndCorners() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isLandSource()) {
                    checkSourceSides(i + 1, j);
                    checkSourceSides(i - 1, j);
                    checkSourceSides(i, j + 1);
                    checkSourceSides(i, j - 1);
                }

            }
        }
        topRightCorner.clear();
        topLeftCorner.clear();
        bottomLeftCorner.clear();
        bottomRightCorner.clear();
        adjacent.clear();
        board.repaint();
    }

    public void checkSourceSides(int i, int j) {
        if ((i < 0) || (i >= board.sliderValue) || (j < 0) || (j >= board.sliderValue)) {

        } else {
            if (adjacent.contains(board.grid[i][j])) {
                board.grid[i][j].setWater();
            } else {
                adjacent.add(board.grid[i][j]);
            }
        }

    }

    public void checkTopRightCorner(int i, int j) {
        if ((i < 0) || (i >= board.sliderValue) || (j < 0) || (j >= board.sliderValue)) {

        } else {
            if (!topRightCorner.contains(board.grid[i][j]) && !board.grid[i][j].isLandSource()) {
                topRightCorner.add(board.grid[i][j]);
            }
            if (bottomLeftCorner.contains(board.grid[i][j])) {
                board.grid[i][j].setWater();
            }
        }

    }

    public void checkTopLeftCorner(int i, int j) {
        if ((i < 0) || (i >= board.sliderValue) || (j < 0) || (j >= board.sliderValue)) {

        } else {
            if (!topLeftCorner.contains(board.grid[i][j]) && !board.grid[i][j].isLandSource()) {
                topLeftCorner.add(board.grid[i][j]);
            }
            if (bottomRightCorner.contains(board.grid[i][j])) {
                board.grid[i][j].setWater();
            }
        }
    }

    public void checkBottomRightCorner(int i, int j) {
        if ((i < 0) || (i >= board.sliderValue) || (j < 0) || (j >= board.sliderValue)) {

        } else {
            if (!bottomRightCorner.contains(board.grid[i][j]) && !board.grid[i][j].isLandSource()) {
                bottomRightCorner.add(board.grid[i][j]);
            }
            if (topLeftCorner.contains(board.grid[i][j])) {
                board.grid[i][j].setWater();
            }
        }
    }

    public void checkBottomLeftCorner(int i, int j) {
        if ((i < 0) || (i >= board.sliderValue) || (j < 0) || (j >= board.sliderValue)) {

        } else {
            if (!bottomLeftCorner.contains(board.grid[i][j]) && !board.grid[i][j].isLandSource()) {
                bottomLeftCorner.add(board.grid[i][j]);
            }
            if (topRightCorner.contains(board.grid[i][j])) {
                board.grid[i][j].setWater();
            }
        }
    }

    // expands land that has only one way out of a situation
    public void expandAllLand() {
        ArrayList<Block> totalDiscovered = new ArrayList<Block>();
        ArrayDeque<Block> possibleBridges = new ArrayDeque<Block>();
        ArrayDeque<ArrayList<Block>> freeLandSets = new ArrayDeque<>();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                Block temp = board.getGrid()[i][j];
                if (!totalDiscovered.contains(temp) && (temp.isLandSource() || temp.isLandStem())
                        && !temp.isComplete()) {
                    ArrayDeque<Block> surroundingBlanks = getSurroundingBlanks(temp, totalDiscovered);
                    System.out.println("SurroundingBlanks size = " + surroundingBlanks.size() + " around " + temp.gridX + ", " + temp.gridY);
                    if (surroundingBlanks.size() == 1) {
                        surroundingBlanks.getFirst().setLandStem();
                        totalDiscovered.add(surroundingBlanks.getFirst());
                    }
                }
            }
        }
        surroundAllComplete();
    }

    public void connectLand() {

        ArrayList<Block> totalDiscovered = new ArrayList<Block>();
        ArrayDeque<Block> possibleBridges = new ArrayDeque<Block>();
        ArrayList<ArrayList<Block>> freeLandSets = new ArrayList<>();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                Block temp = board.getGrid()[i][j];
                if (!totalDiscovered.contains(temp) && (temp.isLandSource())
                        && !temp.isComplete() && temp.getParent() != null) {
                    board.updateBoard(board.getGraphics());
                    System.out.println("Block at position: " + temp.getXOnGrid() + ", " + temp.getYOnGrid());
                    System.out.println("Block's source num = " + temp.getSourceNum());

                    for (Block b : getAllReachableBlocks(temp)) {
                        System.out.println("Reachable block at: " + b.getXOnGrid() + ", " + b.getYOnGrid());
                        ArrayList<Block> freeLand = getAdjacentFreeLand(b);
                        if (freeLand.size() == 1) {
                            possibleBridges.add(b);
                            freeLandSets.add(freeLand);
                        }
                    }

                }
            }
        }
        // fix this portion, issue is that 
        for (Block b : possibleBridges) {
            int adjacentCount = 0;
            for (ArrayList<Block> set : freeLandSets) {
                for (Block free : set) {
                    if (getAdjacentFreeLand(b).contains(free)) {
                        adjacentCount++;
                    }
                }
                if (adjacentCount == 1) {
                    b.setLandStem();
                }
            }
            surroundAllComplete();
        }
    }

    /**
     * Fills all blank spaces using the traverseBlanks BFS algorithm
     */
    public void fillWaters() {
        ArrayList<Block> totalDiscovered = new ArrayList<>();
        for (int i = 0; i < board.getSliderValue(); i++) {
            for (int j = 0; j < board.getSliderValue(); j++) {
                Block b = board.getGrid()[i][j];
                if (!totalDiscovered.contains(b) && b.isBlank()) {
                    Deque<Block> discovered = traverseBlocks(b, BlockType.BLANK, totalDiscovered);
                    if (discovered.size() == 1 && !isAdjacentToOtherLand(b)) {
                        b.setWater();
                    }

                }
            }
        }
    }

    /**
     * Traverses clusters of blocks of the same type.
     *
     * @param origin
     * @param blockType
     * @param totalDiscovered
     * @return
     */
    public Deque<Block> traverseBlocks(Block origin, BlockType blockType, ArrayList<Block> totalDiscovered) {
        Deque<Block> queue = new ArrayDeque<>();
        Deque<Block> discovered = new ArrayDeque<>(); //discovered landstems
        queue.add(origin);
        discovered.add(origin);
        // get origin type
        while (!queue.isEmpty()) {
            Block block = queue.removeFirst();
            ArrayDeque<Block> surroundingBlocks = getSurroundingBlocks(block);
            for (Block b : surroundingBlocks) {
                if (b != null && !discovered.contains(b)) {
                    boolean valid = false;
                    switch (blockType) {
                        case BLANK:
                            if (b.isBlank()) {
                                valid = true;
                            }
                            break;
                        case WATER:
                            if (b.isWater()) {
                                valid = true;
                            }
                            break;
                        case LAND:
                            if ((b.isLandStem() || b.isLandSource()) && b.getParent() == origin.getParent()) {
                                valid = true;
                            }
                            break;

                    }
                    if (valid) {
                        queue.add(b);
                        discovered.add(b);
                        valid = false;
                    }
                }
            }
        }
        totalDiscovered.addAll(discovered);
        return discovered;
    }

    public void expandAllWater() {
        ArrayList<Block> totalDiscovered = new ArrayList<>();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                Block temp = board.getGrid()[i][j];
                if (temp.isWater() && !totalDiscovered.contains(temp)) {
                    ArrayDeque<Block> surroundingBlocks = getSurroundingBlanks(temp, totalDiscovered);
                    if (surroundingBlocks.size() == 1 && getBlankCount() != 1) {
                        surroundingBlocks.getFirst().setWater();
                    }
                    board.updateBoard(board.getGraphics());
                }
            }
        }
    }

    public int getBlankCount() {
        ArrayList<Block> totalDiscovered = new ArrayList<>();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                Block temp = board.getGrid()[i][j];
                if (temp.isBlank()) {
                    traverseBlocks(temp, BlockType.BLANK, totalDiscovered);
                }
            }
        }
        return totalDiscovered.size();
    }

    public void findPossibleMoves(int i, int j, ArrayList<Block> m, ArrayDeque<Block> stack) {
        m.add(board.grid[i][j]);
        findPossibleMovesTail(i - 1, j, m, stack);
        findPossibleMovesTail(i + 1, j, m, stack);
        findPossibleMovesTail(i, j - 1, m, stack);
        findPossibleMovesTail(i, j + 1, m, stack);
    }

    public void findPossibleMovesTail(int i, int j, ArrayList<Block> m, ArrayDeque<Block> stack) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && !m.contains(board.grid[i][j])) {
            findPossibleMoves(i, j, m, stack);
        } else if (board.grid[i][j].isBlank()) {
            stack.push(board.grid[i][j]);
        } else {

        }
    }

    /**
     * Expands either land or water check to see if there is a block that can be
     * added. Uses Breadth First Traversal to check to see if there is only
     * outlet for either a cluster of land or water.
     *
     * @param origin
     * @param totalDiscovered
     */
    public ArrayDeque<Block> getSurroundingBlanks(Block origin, ArrayList<Block> totalDiscovered) {
        BlockType blockType;
        // may be able to just pass in a variable instead...
        if (origin.isBlank()) {
            return null;
        } else if (origin.isWater()) {
            blockType = BlockType.WATER;
        } else {
            blockType = BlockType.LAND;
        }
        Deque<Block> discovered = traverseBlocks(origin, blockType, totalDiscovered);
        ArrayDeque<Block> totalSurroundingBlanks = new ArrayDeque<>();
        if (blockType == BlockType.WATER) {
            System.out.println(discovered.size());
        }
        System.out.println("Origin: " + origin.gridX + ", " + origin.gridY);
        for (Block b : discovered) {
            System.out.println("discovered " + b.gridX + ", " + b.gridY);
        }
        while (!discovered.isEmpty()) { // for each landstem that was discovered AKA "Connected"
            Block block = discovered.removeFirst();
            ArrayDeque<Block> surroundingBlocks = getSurroundingBlocks(block);
            for (Block b : surroundingBlocks) {
                // not quite ready yet
                if (b != null && !totalSurroundingBlanks.contains(b) && b.isBlank()) {
                    totalSurroundingBlanks.add(b);
                }
            }
        }
        return totalSurroundingBlanks;
        /*
         if (totalSurroundingBlanks.size() == 1) {
         switch (blockType) {
         case WATER:
         totalSurroundingBlanks.get(0).setWater();
         break;
         case LAND:
         totalSurroundingBlanks.get(0).setLandStem();
         break;
         }
         totalDiscovered.add(totalSurroundingBlanks.get(0));
         }
         */
    }

    /**
     * Gets the surrounding blanks of a single block
     *
     * @param block
     * @return
     */
    public ArrayDeque<Block> getSurroundingBlocks(Block block) {
        ArrayDeque<Block> surroundingBlocks = new ArrayDeque<>();
        if (board.checkWithinBounds(block.getXOnGrid() - 1, block.getYOnGrid())) {
            surroundingBlocks.add(board.getGrid()[block.getXOnGrid() - 1][block.getYOnGrid()]);
        }
        if (board.checkWithinBounds(block.getXOnGrid() + 1, block.getYOnGrid())) {
            surroundingBlocks.add(board.getGrid()[block.getXOnGrid() + 1][block.getYOnGrid()]);
        }
        if (board.checkWithinBounds(block.getXOnGrid(), block.getYOnGrid() - 1)) {
            surroundingBlocks.add(board.getGrid()[block.getXOnGrid()][block.getYOnGrid() - 1]);
        }
        if (board.checkWithinBounds(block.getXOnGrid(), block.getYOnGrid() + 1)) {
            surroundingBlocks.add(board.getGrid()[block.getXOnGrid()][block.getYOnGrid() + 1]);
        }
        return surroundingBlocks;
    }

    public boolean isAdjacentToOtherLand(Block block) {
        for (Block b : getSurroundingBlocks(block)) {
            if ((b.isLandStem() || b.isLandSource())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdjacentToIsland(Block block, Block origin) {
        for (Block b : getSurroundingBlocks(block)) {
            if ((b.isLandStem() || b.isLandSource()) && b.isChild() && (b.getParent() != origin.getParent())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Block> getAdjacentFreeLand(Block block) {
        ArrayList<Block> adjacentFreeLand = new ArrayList<>();
        for (Block b : getSurroundingBlocks(block)) {
            if ((b.isLandStem() || b.isLandSource()) && !b.isChild() && b.getParent() == null && !b.isComplete()) {
                adjacentFreeLand.add(b);
            }
        }
        return adjacentFreeLand;
    }

    public int checkVoid(int i, int j) {
        return checkAdjacentWater(i - 1, j) + checkAdjacentWater(i + 1, j) + checkAdjacentWater(i, j - 1) + checkAdjacentWater(i, j + 1);
    }

    public int checkAdjacentWater(int i, int j) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else if (board.grid[i][j].isWater()) {
            return 1;
        } else {
            return 0;
        }
    }

    public void check2x2Area() {
        for (int i = 0; i < board.sliderValue - 1; i++) {
            for (int j = 0; j < board.sliderValue - 1; j++) {
                int count = 0;
                Block marked = null;
                if (board.grid[i][j].isWater()) {
                    count++;
                } else {
                    marked = board.grid[i][j];
                }
                if (board.grid[i + 1][j].isWater()) {
                    count++;
                } else {
                    marked = board.grid[i + 1][j];
                }
                if (board.grid[i][j + 1].isWater()) {
                    count++;
                } else {
                    marked = board.grid[i][j + 1];
                }
                if (board.grid[i + 1][j + 1].isWater()) {
                    count++;
                } else {
                    marked = board.grid[i + 1][j + 1];
                }
                if (count == 3) {
                    if (marked.isBlank()) {
                        marked.setLandStem();
                    }
                }

            }
        }
    }
}
