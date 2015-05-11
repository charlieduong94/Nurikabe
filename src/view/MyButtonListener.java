package view;

import view.Nurikabe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class MyButtonListener implements ActionListener {

    Nurikabe nurikabe = null;
    int i = 0;
    boolean b = false;

    public MyButtonListener(Nurikabe n) {
        nurikabe = n;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (!nurikabe.isStarted()) {
            nurikabe.setStarted(true);
            nurikabe.setText("Game Started");
            //nurikabe.start.setEnabled(false);
            nurikabe.getSlider().setEnabled(false);
            JOptionPane popup = new JOptionPane();
            popup.setVisible(true);
            popup.showMessageDialog(nurikabe, "Start Playing! \nLeft Click on a tile to fill it.\nRight Click to mark the tile as known.", "Game Started", 1);
            nurikabe.getStartButton().setText("Solve");
        } else {

            if (!b) {

                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        nurikabe.getSolver().solve();

                    }

                };
                r.run();
                b = true;

            } else {

                //nurikabe.solver.undo();
                //nurikabe.solver.checkTrappedWater();
            }

            //		nurikabe.solver.solveKnown();
        }

    }

}
