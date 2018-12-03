/**
 * CSCI 2113 - Project 2
 * 
 * @author Ryan Steed
 *
 */
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JPanel;

abstract class Overlay extends JPanel {
    static final long serialVersionUID = 1L;

    public Overlay(Dimension dimension) {
        setPreferredSize(dimension);
        setLayout(null);
        setVisible(true);
    }
}

class BackgroundOverlay extends Overlay {
    static final long serialVersionUID = 1L;

    public BackgroundOverlay(Dimension dimension) {
        super(dimension);
        setBackground(Color.black);
    }
}

class MetaOverlay extends Overlay {
    static final long serialVersionUID = 1L;

    public MetaOverlay(Dimension dimension) {
        super(dimension);
        setBackground(new Color(0, 0, 0, 0));
    }
}