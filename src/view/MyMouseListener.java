package view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import model.Board;

public class MyMouseListener implements MouseListener {	//for mouse

    Board board = null;
    String[] choices = {"Leave Blank", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"};

    public MyMouseListener(Board b) {
        board = b;
    }

    public void mouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub

    }

    public void mouseEntered(MouseEvent evt) {
		// TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent evt) {
		// TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent evt) {
        int x, y;
        x = (int) Math.floor((evt.getX() / (double)board.getWidth()) * board.getSliderValue());
        y = (int) Math.floor((evt.getY() / (double)board.getHeight()) * board.getSliderValue());
        System.out.println(x+ " " +y);
        if (!board.getNurikabe().isStarted()) {
            if ((x == 0) && (y > 0) && (y < board.getSliderValue() - 1)) { //checked
                if (!board.getGrid()[x + 1][y].isLandSource() && !board.getGrid()[x][y - 1].isLandSource() && !board.getGrid()[x][y + 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x == 0) && (y == 0)) { //checked
                if (!board.getGrid()[x + 1][y].isLandSource() && !board.getGrid()[x][y + 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x == 0) && (y == board.getSliderValue() - 1)) { //checked
                if (!board.getGrid()[x + 1][y].isLandSource() && !board.getGrid()[x][y - 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x == board.getSliderValue() - 1) && (y > 0) && (y < board.getSliderValue() - 1)) { //checked
                if (!board.getGrid()[x - 1][y].isLandSource() && !board.getGrid()[x][y - 1].isLandSource() && !board.getGrid()[x][y + 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x == board.getSliderValue() - 1) && (y == 0)) { // checked
                if (!board.getGrid()[x - 1][y].isLandSource() && !board.getGrid()[x][y + 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x == board.getSliderValue() - 1) && (y == board.getSliderValue() - 1)) { //checked
                if (!board.getGrid()[x - 1][y].isLandSource() && !board.getGrid()[x][y - 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x > 0) && (x < board.getSliderValue() - 1) && (y == 0)) { //checked
                if (!board.getGrid()[x - 1][y].isLandSource() && !board.getGrid()[x + 1][y].isLandSource() && !board.getGrid()[x][y + 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else if ((x > 0) && (x < board.getSliderValue() - 1) && (y == board.getSliderValue() - 1)) { //checked
                if (!board.getGrid()[x - 1][y].isLandSource() && !board.getGrid()[x + 1][y].isLandSource() && !board.getGrid()[x][y - 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            } else {
                if (!board.getGrid()[x - 1][y].isLandSource() && !board.getGrid()[x + 1][y].isLandSource() && !board.getGrid()[x][y - 1].isLandSource() && !board.getGrid()[x][y + 1].isLandSource()) {
                    createSourceDropPopup(x, y);
                } else {
                    createErrorPopup();
                }
            }
			//if(!board.getGrid()[x-1][y].isLandSource() && !board.getGrid()[x+1][y].isLandSource() && !board.getGrid()[x][y-1].isLandSource() && !board.getGrid()[x][y+1].isLandSource()){

			//}
            //else{
			//}
        } else {
            if (board.getGrid()[x][y].contains(evt.getPoint())) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (board.getGrid()[x][y].isBlank() || board.getGrid()[x][y].isLandStem()) {
                        board.getGrid()[x][y].setWater();
                    }
                } else if (SwingUtilities.isRightMouseButton(evt)) {
                    if (board.getGrid()[x][y].isBlank() || board.getGrid()[x][y].isWater()) {
                        board.getGrid()[x][y].setLandStem();

                    }
                }
            }
        }
        board.repaint();
    }

    public void mouseReleased(MouseEvent evt) {
		// TODO Auto-generated method stub

    }

    public void createSourceDropPopup(int x, int y) {
        JOptionPane popup = new JOptionPane();
        popup.setVisible(true);
        popup.setWantsInput(true);

        String selectedValue = (String) popup.showInputDialog(board, "Choose Land Source Number", "Choose", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

        //System.out.print(selectedValue);
        if (selectedValue == "Leave Blank") {
            board.getGrid()[x][y].setBlank();
        } else if (selectedValue == null) {
            //do nothing
        } else {
            int i = Integer.parseInt(selectedValue);
            System.out.println(i);
            board.getGrid()[x][y].setLandSource(i);
            board.getGrid()[x][y].setParent(board.getGrid()[x][y]);
        }
    }

    public void createErrorPopup() {
        JOptionPane popup = new JOptionPane();
        popup.setVisible(true);
        popup.showMessageDialog(board, "Error. Source cannot be placed there", "ERROR", JOptionPane.ERROR_MESSAGE);
    }

}
