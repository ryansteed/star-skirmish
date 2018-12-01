/**
 * CSCI 2113 - Project 2
 * 
 * @author Ryan Steed
 *
 */
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JPanel;

public class Background extends JPanel
{
    public Background(Dimension dimension) {
        setPreferredSize(dimension);
        setBackground(Color.black);
        setLayout(null);
        setVisible(true);
    }
}