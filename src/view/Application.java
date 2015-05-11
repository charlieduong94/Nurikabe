/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javax.swing.JOptionPane;

/**
 *
 * @author charlie
 */
public class Application {

    public static void main(String[] args) {
        Nurikabe n = new Nurikabe();
        
        JOptionPane popup = new JOptionPane();
        popup.setVisible(true);
        popup.showMessageDialog(n, "Design Mode! \nClick any tile on the grid to place a numbered Source.\nClick the \"Start Game\" button to begin playing!", "Welcome", 1);
    }
}
