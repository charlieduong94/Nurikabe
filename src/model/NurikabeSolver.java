package model;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
//create a check space algorithm that looks at blanks and checks the sourceNum of every source it touches to see if
//the sources will fit,
//create an algorithm that uses arraylists or arraydeques to remember patterns that were used and avoid them
//this should reduce run times,  
//memoization
//try making solver a while loop
//while(point != null)????
//replace function call with continue

public class NurikabeSolver {

    Board board = null;
    int totalLand = 0;
    ArrayList<ArrayList<Block>> sourceChildren = new ArrayList<ArrayList<Block>>();
    ArrayList<Block> possibleLand, adjacent, markedLand, topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner,sortedSources;
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
        while (!b) {
            Block[][] tempGrid = createClone(board.grid);
            solveKnown();
            b = gridCompare(board.grid, tempGrid);
            System.out.println(b);
        }
        knownGrid = createClone(board.grid);

        System.out.println("Cloning Done");
        currentPattern.push(new ArrayDeque<Block>());
        restrictedPatterns.push(new ArrayDeque<ArrayDeque<Block>>());
        gridStack.push(new ArrayDeque<Block[][]>());
        gridStack.peek().push(knownGrid);
        sourceLoc.push(findNewSourceLoc());
        landStack.push(new ArrayDeque<ArrayDeque<Block>>());
        ArrayDeque<Block> temp = new ArrayDeque<Block>();
        findPossibleMoves(sourceLoc.peek().x, sourceLoc.peek().y, new ArrayList<Block>(), temp);
        landStack.peek().push(temp);
        solveLand(true);

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
                if (grid1[i][j].equals((Block) grid2[i][j]) == false) {
                    b = false;
                    break outerLoop;
                }

            }

        }
        return b;
    }

    public void solveKnown() {
        surroundComplete();
        checkSidesAndCorners();
        expandAllWater();
        fillKnownWater();
        checkLastLandStemPlacement();
        check2x2Area();
        board.updateBoard(board.getGraphics());
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

    public void checkLastLandStemPlacement() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                ArrayList<Block> temp = new ArrayList<Block>();
                if (board.grid[i][j].isChild() && (board.grid[i][j].isLandStem() || board.grid[i][j].isLandSource()) && !board.grid[i][j].isComplete() && board.grid[i][j].sourceNum != 0) {

                    if (board.grid[i][j].parent.sourceNum - 1 == (board.countLand(i, j, temp, board.grid[i][j].parent))) {
                        int right = checkAvailiableLandPath(i + 1, j, temp);
                        int left = checkAvailiableLandPath(i - 1, j, temp);
                        int top = checkAvailiableLandPath(i, j - 1, temp);
                        int bottom = checkAvailiableLandPath(i, j + 1, temp);
                        int count = right + left + top + bottom;
                        if (count == 2) {
                            if (top == 1 && right == 1) { //top and right

                                board.grid[i + 1][j - 1].setWater();
                            } else if (top == 1 && left == 1) { //top and left
                                board.grid[i - 1][j - 1].setWater();
                            } else if (bottom == 1 && right == 1) { //bottom and right

                                board.grid[i + 1][j + 1].setWater();
                            } else if (bottom == 1 && left == 1) { //bottom and left

                                board.grid[i - 1][j + 1].setWater();

                            }
                        }
                    }

                }
            }
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

        for (int i = 0; i < possibleLand.size(); i++) {
            int count = 0;
            if (!possibleLand.get(0).isLandSource()) {
                Block b = possibleLand.get(0);
                possibleLand.remove(0);
                for (int j = 0; j < possibleLand.size(); j++) {
                    if (possibleLand.get(j) == b) {
                        //add checkAvailiablePath here to determine if safe to add
                        count++;
                        //b.setLandStem();
                    }
                }
                if (count == 1) {
                    b.setLandStem();
                }
                possibleLand.add(b);
            } else {
                Block b = possibleLand.get(0);
                possibleLand.remove(0);
                possibleLand.add(b);
            }

        }
        possibleLand.clear();
        board.repaint();
    }

    public void markPotentialLandStems(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                //board.grid[i][j].setWater();
                markPotentialLandStemsDown(i, j - 1, m, sourceNum, count, started, parent);
                markPotentialLandStemsUp(i, j + 1, m, sourceNum, count, started, parent);
                markPotentialLandStemsLeft(i - 1, j, m, sourceNum, count, started, parent);
                markPotentialLandStemsRight(i + 1, j, m, sourceNum, count, started, parent);
                markPotentialLandStemsDownRight(i + 1, j - 1, m, sourceNum, count + 1, started, parent);
                markPotentialLandStemsUpRight(i + 1, j + 1, m, sourceNum, count + 1, started, parent);
                markPotentialLandStemsDownLeft(i - 1, j - 1, m, sourceNum, count + 1, started, parent);
                markPotentialLandStemsUpLeft(i - 1, j + 1, m, sourceNum, count + 1, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsUp(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsUp(i, j + 1, m, sourceNum, count, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsDown(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsDown(i, j - 1, m, sourceNum, count, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsLeft(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsLeft(i - 1, j, m, sourceNum, count, started, parent);

            }
            //}

        }
    }

    public void markPotentialLandStemsRight(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsRight(i + 1, j, m, sourceNum, count, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsUpRight(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsUp(i, j + 1, m, sourceNum, count, started, parent);

                markPotentialLandStemsRight(i + 1, j, m, sourceNum, count, started, parent);
                markPotentialLandStemsUpRight(i + 1, j + 1, m, sourceNum, count + 1, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsDownRight(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsDown(i, j - 1, m, sourceNum, count, started, parent);
                markPotentialLandStemsRight(i + 1, j, m, sourceNum, count, started, parent);
                markPotentialLandStemsDownRight(i + 1, j - 1, m, sourceNum, count + 1, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsUpLeft(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsUp(i, j + 1, m, sourceNum, count, started, parent);
                markPotentialLandStemsLeft(i - 1, j, m, sourceNum, count, started, parent);
                markPotentialLandStemsUpLeft(i - 1, j + 1, m, sourceNum, count + 1, started, parent);
            }
            //}

        }
    }

    public void markPotentialLandStemsDownLeft(int i, int j, ArrayList<Block> m, int sourceNum, int count, boolean started, Block parent) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {

        } else if (count < sourceNum) {

            //if(!m.contains(board.grid[i][j])){
            //markedCount = 0;
            if (board.grid[i][j].isBlank() || !started || ((board.grid[i][j].isLandSource() || board.grid[i][j].isLandStem()) && board.grid[i][j].parent == parent)) {// ((!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource())) && !m.contains(board.grid[i][j])){
                count++;
                started = true;
                m.add(board.grid[i][j]);

                markPotentialLandStemsDown(i, j - 1, m, sourceNum, count, started, parent);
                markPotentialLandStemsLeft(i - 1, j, m, sourceNum, count, started, parent);
                markPotentialLandStemsDownLeft(i - 1, j - 1, m, sourceNum, count + 1, started, parent);
            }
            //}

        }
    }

    public void surroundComplete() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isComplete() && board.grid[i][j].isLandSource()) {
                    ArrayList<Block> marked = new ArrayList<Block>();
                    fillCompleteBlockSurroundings(i, j, marked);
                }
            }
        }
        board.repaint();
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
                    markPotentialLandStems(i, j, marked, board.grid[i][j].sourceNum, 0, false, board.grid[i][j]);
//                                      
                    sourceChildren.add(marked);
                }

            }

        }
        for (int i = 0; i < sourceChildren.size(); i++) {
            for (int j = 0; j < sourceChildren.get(i).size(); j++) {

                if (!possibleLand.contains(sourceChildren.get(i).get(j))) {
                    possibleLand.add(sourceChildren.get(i).get(j));
                }

            }
        }
        board.repaint();
    }

    public void fillKnownWater() {
        retrievePotentialLand();
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (!possibleLand.contains(board.grid[i][j])) {
                    //if(!board.grid[i][j].isLandStem() || !board.grid[i][j].isLandSource()){
                    //	board.grid[i][j].setWater();
                    //}
                    if (board.grid[i][j].isBlank()) {
                        board.grid[i][j].setWater();
                    }
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
                    //checkBottomRightCorner(i+1,j+1);
                    //checkTopRightCorner(i+1,j-1);
                    //checkBottomLeftCorner(i-1,j+1);
                    //checkTopLeftCorner(i-1,j-1);
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

    public void expandAllLand() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                //if(board.grid[i][j].isWater()){
                //expandWater(i,j);
                //}
                if (board.grid[i][j].isLandStem() || board.grid[i][j].isLandSource()) {
                    if (!board.grid[i][j].isComplete() && board.grid[i][j].isChild()) {
                        ArrayList<Block> marked = new ArrayList<Block>();
                        //int current = board.countLand(i,j,marked, board.grid[i][j].parent);
                        if (board.grid[i][j].parent != null) {
                            expandLand(i, j, 1, board.grid[i][j].parent.sourceNum, board.countLand(i, j, marked, board.grid[i][j].parent), marked);
                        }
                        marked.clear();
                    }

                }
                //surroundComplete();
            }
        }
        board.update(board.getGraphics());
    }

    public void expandAllWater() {
        for (int i = 0; i < board.sliderValue; i++) {
            for (int j = 0; j < board.sliderValue; j++) {
                if (board.grid[i][j].isWater()) {
                    ArrayList<Block> marked = new ArrayList<Block>();
                    expandWater(i, j, 0, marked);

                }

                //surroundComplete();
            }
        }
        board.repaint();
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

    public void expandLand(int i, int j, int count, int max, int current, ArrayList<Block> m) {
        if (count == 0) {

        } else if (current == max) {
            for (int k = 0; k < m.size(); k++) {
                m.get(k).setCompleteness(true);
            }
        } else if (current < max) {
            if (!m.contains(board.grid[i][j])) {
                m.add(board.grid[i][j]);
            }
            if (board.grid[i][j].isBlank()) {

                board.grid[i][j].setLandStem();

            }

            count = checkAvailiableLandPath(i, j - 1, m) + checkAvailiableLandPath(i, j + 1, m) + checkAvailiableLandPath(i - 1, j, m) + checkAvailiableLandPath(i + 1, j, m);
            m.clear();
            expandLandTail(i, j, count, max, board.countLand(i, j, m, board.grid[i][j].parent), m);
        }

    }

    public void expandLandTail(int i, int j, int count, int max, int current, ArrayList<Block> m) {
        if (count == 1) { //if greater than 0, add into possible adjacent land for solver
            if (checkAvailiableLandPath(i, j - 1, m) == 1) {

                expandLand(i, j - 1, count, max, current, m);
            } else if (checkAvailiableLandPath(i, j + 1, m) == 1) {
                expandLand(i, j + 1, count, max, current, m);
            } else if (checkAvailiableLandPath(i + 1, j, m) == 1) {

                expandLand(i + 1, j, count, max, current, m);
            } else if (checkAvailiableLandPath(i - 1, j, m) == 1) {

                expandLand(i - 1, j, count, max, current, m);
            }
        }

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

    public void expandWater(int i, int j, int current, ArrayList<Block> m) {
        if (board.grid[i][j].isBlank() || board.grid[i][j].isWater()) {
            board.grid[i][j].setWater();
            m.add(board.grid[i][j]);
            int count = checkAvailiableWaterPath(i, j - 1, m) + checkAvailiableWaterPath(i, j + 1, m) + checkAvailiableWaterPath(i - 1, j, m) + checkAvailiableWaterPath(i + 1, j, m);
            expandWaterTail(i, j, count, current, m);
        }

    }

    public void expandWaterTail(int i, int j, int count, int current, ArrayList<Block> m) {
        if (count == 1) {
            if (checkAvailiableWaterPath(i, j - 1, m) == 1) {
                expandWater(i, j - 1, current, m);
            }
            if (checkAvailiableWaterPath(i, j + 1, m) == 1) {
                expandWater(i, j + 1, current, m);
            }
            if (checkAvailiableWaterPath(i + 1, j, m) == 1) {
                expandWater(i + 1, j, current, m);
            }
            if (checkAvailiableWaterPath(i - 1, j, m) == 1) {
                expandWater(i - 1, j, current, m);
            }
        }
    }

    public int checkAvailiableLandPath(int i, int j, ArrayList<Block> m) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else {
            if ((board.grid[i][j].isBlank() || (board.grid[i][j].isLandStem() && !m.contains(board.grid[i][j])))) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public int checkAvailiableWaterPath(int i, int j, ArrayList<Block> m) {
        if ((i < 0) || (i > board.sliderValue - 1) || (j < 0) || (j > board.sliderValue - 1)) {
            return 0;
        } else {
            if (board.grid[i][j].isBlank() || (board.grid[i][j].isWater() && !m.contains(board.grid[i][j]))) {
                return 1;
            } else {
                return 0;
            }
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
