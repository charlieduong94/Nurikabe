package view;
import java.awt.Graphics;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Board;

class MySliderListener implements ChangeListener{	//for Slider
	GridSlider slider = null;
	Board board = null;
	Graphics g = null;
	public MySliderListener(GridSlider s, Board b){
		slider = s;
		board = b;
	}
        @Override
	public void stateChanged(ChangeEvent arg0) {
		board.removeAll();
		slider.value = slider.getValue();
		board.setSliderValue(slider.value);
//		//board.setLayout(new GridLayout(board.sliderValue, board.sliderValue));
//		board.width = board.getWidth();					//component listener takes care of that
//		board.height = board.getHeight();
		//g = board.getGraphics();
		//board.grid.clear();
		board.createTiles();
		//board.updateTiles(board.getGraphics());
		//board.repaint();
		//board.updateUI();
                board.repaint();
                board.setVisible(true);
							//needs repaint
	}						//change listener needs it because it does not link with component
	
}