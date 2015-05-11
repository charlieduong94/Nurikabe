package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
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
        //mainPanel.setLayout(new BorderLayout());
        this.add(mainPanel);
        //mainPanel.add(topPanel, BorderLayout.NORTH);
        textArea.setEditable(false);
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
