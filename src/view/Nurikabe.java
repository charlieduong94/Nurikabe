package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Block;
import model.Board;
import model.NurikabeSolver;
//Notes:
//	-- start filling more known
//  -- issue with solving, check the marked land

public class Nurikabe extends JFrame {

    int width = 500;
    int height = 500;
    Dimension size;
    GridSlider slider;
    JPanel mainPanel;
    JPanel topPanel;
    JButton startButton;
    JTextArea textArea;
    JButton importButton;
    boolean started;
    Board b;
    NurikabeSolver solver;

    public Nurikabe() {
        size = new Dimension(width, height);
        slider = new GridSlider();
        mainPanel = new JPanel();
        topPanel = new JPanel();
        startButton = new JButton("Start Game");
        textArea = new JTextArea("Design Mode");
        importButton = new JButton("Import");
        importButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Text Files", "txt");
                chooser.setFileFilter(extensionFilter);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    System.out.println("Approved");
                    /**
                     * I need to figure a way to validate these files
                     */
                    try {
                        Scanner scanner = new Scanner(new FileInputStream(chooser.getSelectedFile()));
                        String temp = scanner.nextLine().trim();
                        int sliderVal = Integer.parseInt(temp);
                        b.setSliderValue(sliderVal);
                        int y = 0;
                        Block[][] newGrid = new Block[sliderVal][sliderVal];
                        while (scanner.hasNext()) {
                            temp = scanner.nextLine();
                            String[] stringArray = temp.trim().split("\\s");
                            for (int x = 0; x < sliderVal; x++) {
                                newGrid[x][y] = new Block(((int) (x * (b.getWidth()) / sliderVal)), (int) (y * (b.getHeight() / sliderVal)),
                                        (int) (b.getWidth() / sliderVal), (int) (b.getHeight() / sliderVal), x, y);
                                int blockVal = Integer.parseInt(stringArray[x]);
                                if (blockVal != 0) {
                                    newGrid[x][y].setLandSource(blockVal);
                                    newGrid[x][y].setParent(newGrid[x][y]);
                                }
                                else{
                                    newGrid[x][y].setBlank();
                                }
                            }
                            y++;
                            System.out.println("");
                        }
                        // if nothing wrong happend
                        System.out.println("got here");
                        b.setGrid(newGrid);
                        b.repaint();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Nurikabe.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }

        });
        started = false;
        solver = null;
        b = new Board(this);
        this.setTitle("Nurikabe");
        this.setSize(size);
        this.setResizable(true);
        this.setBackground(Color.BLACK);
        this.setLayout(new BorderLayout());
        slider.setMinimum(5);
        slider.setValue(5);
        slider.setMaximum(25);
        b.setSliderValue(5);
        //mainPanel.setLayout(new BorderLayout());
        this.add(mainPanel);
        //mainPanel.add(topPanel, BorderLayout.NORTH);
        textArea.setEditable(false);
        topPanel.add(importButton);
        topPanel.add(textArea);
        topPanel.add(slider);
        topPanel.add(startButton);
        this.add(topPanel, BorderLayout.NORTH);
        this.add(b, BorderLayout.CENTER);
        //mainPanel.add(b, BorderLayout.CENTER);
        startButton.addActionListener(new MyButtonListener(this));
        slider.addChangeListener(new MySliderListener(slider, b));
        this.addComponentListener(new MyComponentListener(b));
        solver = new NurikabeSolver(b);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public JButton getImportButton() {
        return importButton;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JSlider getSlider() {
        return slider;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public NurikabeSolver getSolver() {
        return solver;
    }

}
