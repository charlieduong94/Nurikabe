package model;

import view.Nurikabe;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import view.MyMouseListener;
public class Board extends JPanel {

    int sliderValue;
    double width, height;
    Nurikabe nurikabe = null;
    Block[][] grid = null;
    ArrayList<Integer> sourceNumberList; 
   ArrayList<ArrayList<Block>> sourceChildren;
    boolean initialized = false;

    public ArrayList<Integer> getSourceNumberList() {
        return sourceNumberList;
    }

    public ArrayList<ArrayList<Block>> getSourceChildren() {
        return sourceChildren;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean val) {
        initialized = val;
    }

    public Block[][] getGrid() {
        return grid;
    }

    public Nurikabe getNurikabe() {
        return nurikabe;
    }

    public int getSliderValue() {
        return sliderValue;
    }
    public void setSliderValue(int value){
        sliderValue = value;
    }
    public Board(Nurikabe n) {
        nurikabe = n;
        width = this.getWidth();
        height = this.getHeight();
        sliderValue = nurikabe.getSlider().getValue();
        sourceNumberList = new ArrayList<Integer>();
        sourceChildren = new ArrayList<ArrayList<Block>>();
        //ml = new ML(this);
        //this.setLayout(new GridLayout(sliderValue, sliderValue));
        this.addMouseListener(new MyMouseListener(this));
        this.setDoubleBuffered(true);
        createTiles();
        this.setVisible(true);
    }

    public Board(Board b) {
        nurikabe = b.nurikabe;
        width = this.getWidth();
        height = this.getHeight();
        sliderValue = nurikabe.getSlider().getValue();
        sourceNumberList = new ArrayList<Integer>();
        sourceChildren = new ArrayList<ArrayList<Block>>();
        this.grid = b.grid.clone();
		//for(int i = 0; i < sliderValue; i++){
        //	for(int j = 0; j < sliderValue; j++){
        //	this.grid[i][j] = b.grid[i][j];
        //}
        //	}	
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        width = this.getWidth();
        height = this.getHeight();
        System.out.println("painting board");
        Graphics2D g2d = (Graphics2D)g;
        updateBoard(g2d);
        redrawBoard(g2d);
        createGrid(g2d);

    }

    public void createGrid(Graphics g) {				//might not need this
        g.setColor(Color.BLACK);
        for (int i = 1; i < sliderValue; i++) {
            g.drawLine((int) (i * width / sliderValue), 0, (int) (i * width / sliderValue), (int) height);
            g.drawLine(0, (int) (i * height / sliderValue), (int) width, (int) (i * height / sliderValue));
        }
    }
    
    public void createTiles() {
        grid = new Block[sliderValue][sliderValue];
        for (int i = 0; i < sliderValue; i++) {			// row
            for (int j = 0; j < sliderValue; j++) {		// amount in row
                grid[i][j] = new Block((int) (i * (width / sliderValue)), (int) (j * (height / sliderValue)),
                        (int) (width / sliderValue), (int) (height / sliderValue));
            }
        }
    }

    public void updateBoard(Graphics g) {
        resetMarkedValidity();
        retrieveLandStatus();

        checkLandSourceCollision();
        sourceChildren.clear();
        sourceNumberList.clear();

        for (int j = 0; j < sliderValue; j++) {			// row
            for (int i = 0; i < sliderValue; i++) {		// amount in row				
                grid[i][j].x = (int) (i * width / sliderValue);
                grid[i][j].y = (int) (j * height / sliderValue);
                grid[i][j].width = (int) (width / sliderValue) + 2;
                grid[i][j].height = (int) (height / sliderValue) + 2;
                grid[i][j].paintComponent(g);
            }
        }

    }

    public void redrawBoard(Graphics g) {
        for (int j = 0; j < sliderValue; j++) {			// row
            for (int i = 0; i < sliderValue; i++) {		// amount in row				
                grid[i][j].x = (int) (i * width / sliderValue);
                grid[i][j].y = (int) (j * height / sliderValue);
                grid[i][j].width = (int) (width / sliderValue) + 2;
                grid[i][j].height = (int) (height / sliderValue) + 2;
                grid[i][j].paintComponent(g);
            }
        }

    }

    public int countLand(int i, int j, ArrayList<Block> m, Block parent) {
        grid[i][j].setParent(parent);
        m.add(grid[i][j]);
        grid[i][j].setChild(true);
        return countAdjacentLand(i - 1, j, m, parent) + countAdjacentLand(i + 1, j, m, parent) + countAdjacentLand(i, j - 1, m, parent) + countAdjacentLand(i, j + 1, m, parent) + 1;
    }

    public int countAdjacentLand(int i, int j, ArrayList<Block> m, Block parent) {
        if ((i < 0) || (i > sliderValue - 1) || (j < 0) || (j > sliderValue - 1)) {
            return 0;
        } else if (grid[i][j].isLandSource() && !m.contains(grid[i][j])) {
            return countLand(i, j, m, parent);
        } else if (grid[i][j].isLandStem() && !m.contains(grid[i][j])) {
            return countLand(i, j, m, parent);
        } else {
            return 0;
        }
    }

    public void retrieveLandStatus() {
        for (int i = 0; i < sliderValue; i++) {
            for (int j = 0; j < sliderValue; j++) {
                if (grid[i][j].isLandSource()) {
                    ArrayList<Block> marked = new ArrayList<Block>();
                    sourceNumberList.add(countLand(i, j, marked, grid[i][j]));
                    sourceChildren.add(marked);
                }

            }
        }
    }

    public void checkLandSourceCollision() {
        for (int i = 0; i < sourceChildren.size(); i++) {
            int count = 0;
            for (int j = 0; j < sourceChildren.get(i).size(); j++) {

                if (sourceChildren.get(i).get(j).isLandSource()) {
                    count++;
                    if (sourceNumberList.get(i) == sourceChildren.get(i).get(j).sourceNum) {
                        for (int k = 0; k < sourceChildren.get(i).size(); k++) {
                            sourceChildren.get(i).get(k).setCompleteness(true);
                        }

                    }
                    if (sourceNumberList.get(i) > sourceChildren.get(i).get(j).sourceNum) {
                        for (int k = 0; k < sourceChildren.get(i).size(); k++) {
                            sourceChildren.get(i).get(k).setValidity(false);
                        }
                        count = 0;

                    }
                }
                if ((count > 1)) {
                    for (int k = 0; k < sourceChildren.get(i).size(); k++) {
                        sourceChildren.get(i).get(k).setValidity(false);
                    }
                    count = 0;

                }

            }
        }
    }

    public void resetMarkedValidity() {
        /*
         for(int i = 0; i < sourceChildren.size(); i++){
         for(int j = 0; j < sourceChildren.get(i).size(); j++){
         sourceChildren.get(i).get(j).setValidity(true);
         sourceChildren.get(i).get(j).setCompleteness(false);
         }
         }
         */

        for (int i = 0; i < sliderValue; i++) {
            for (int j = 0; j < sliderValue; j++) {
                grid[i][j].setValidity(true);
                grid[i][j].setCompleteness(false);
                grid[i][j].setChild(false);
            }
        }

    }

}
