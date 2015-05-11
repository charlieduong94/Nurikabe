package view;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import model.Board;

public class MyComponentListener implements ComponentListener {	//for component

    Board board = null;
    
    public MyComponentListener(Board b) {
        board = b;
        
    }
    
    @Override
    public void componentResized(ComponentEvent evt) {	//does not need repaint
//        board.width = board.getWidth();					//component listener takes care of that
//        board.height = board.getHeight();
        //board.setSize(new Dimension(board.getWidth(), board.getHeight()));
        //board.updateBoard(board.getGraphics());
        
        board.repaint();
        System.out.println(board.getWidth());
        System.out.println(board.getHeight());
        
    }
    
    @Override
    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void componentMoved(ComponentEvent arg0) {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void componentShown(ComponentEvent arg0) {
        
    }
}
