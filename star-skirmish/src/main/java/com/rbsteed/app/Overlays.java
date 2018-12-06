/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;
import javax.swing.JLabel;
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
    private Score highscoreDisplay;
    protected int score;
    private int lives;
    private Lives lifeDisplay;
    private Dimension dimension;

    public MetaOverlay(Dimension dimension, int highscore, int lives) {
        super(dimension);
        this.dimension = dimension;
        this.lives = lives;

        setBackground(new Color(0, 0, 0, 0));

        // Score scoreDisplay = new Score(score);
        score = 0;
        CurrentScore scoreDisplay = new CurrentScore();
        scoreDisplay.setLocation((int) (dimension.getWidth() / 10), (int) (scoreDisplay.getHeight()));
        add(scoreDisplay);

        highscoreDisplay = new Score(highscore);
        highscoreDisplay.setLocation(
            (int) (dimension.getWidth() / 2 - highscoreDisplay.getWidth() / 2),
            (int) (highscoreDisplay.getHeight())
        );
        add(highscoreDisplay);

        Label highscoreLabel = new Label("HIGHSCORE", Color.red);
        highscoreLabel.setLocation(
            (int) (dimension.getWidth() / 2 - highscoreLabel.getWidth() / 2),
            0
        );
        add(highscoreLabel);

        lifeDisplay = new Lives();
        lifeDisplay.setLocation(0, (int) (dimension.getHeight() - GameEngine.vertOffset - lifeDisplay.getHeight()));
        add(lifeDisplay);
    }
    protected void updateHighscore(int highscore) {
        remove(highscoreDisplay);
        highscoreDisplay = new Score(highscore);
        highscoreDisplay.setLocation((int) (dimension.getWidth() / 2 - highscoreDisplay.getWidth() / 2),
                (int) (highscoreDisplay.getHeight()));
        add(highscoreDisplay);
        highscoreDisplay.repaint();
    }

    protected void update(int score, int lives) {
        System.out.println("updating...");
        this.score = score;
        this.lives = lives;
    }

    class Lives extends JComponent {
        static final long serialVersionUID = 1L;
        private int iconWidth;

        private ShipPainter painter;

        Lives() {
            this(25);
        }

        Lives(int iconWidth) {
            System.out.println("Instantiating life display with "+lives+" lives");
            this.iconWidth = iconWidth;
            painter = new ShipPainter();
            setSize(new Dimension(iconWidth * lives * 5 / 4, iconWidth));
        }

        @Override
        public void paintComponent(Graphics g) {
            for (int i = 0; i < lives; i++) {
                painter.paint(g, new Point(i * (iconWidth * 5 / 4), 0), new Dimension(iconWidth, iconWidth));
            }
        }
    }

    class Score extends Label {
        static final long serialVersionUID = 1L;

        Score(int init) {
            super(Integer.toString(init), Color.white);
        }
        protected String formatScore(int x) {
            return String.format("%d", x);
        }
    }

    class CurrentScore extends Score {
        static final long serialVersionUID = 1L;

        CurrentScore() {
            super(0);
        }

        @Override
        public void paintComponent(Graphics g) {
            updateText();
            super.paintComponent(g);
        }
        private void updateText() {
            setText(formatScore(score));
            setSize(getPreferredSize());
        }
    }
}

class GameController extends Overlay {
    static final long serialVersionUID = 1L;
    private Dimension size;

    public GameController(Dimension dimension) {
        super(dimension);
        size = dimension;
        setBackground(new Color(0, 0, 0, 0));
    }

    protected void startMenu() {
        makeMenuText("<html>[SPACE] Start<br><br>&nbsp;[ESC] &nbsp;Pause</html>", true);
    }
    
    protected void pauseMenu() {
        makeMenuText("<html><center>[SPACE] Resume</center></html>", false);
    }

    private void makeMenuText(String html, boolean includeTitle) {
        removeAll();
        Label title = new Label(includeTitle ? "Star Skirmish" : "" , new Color(254,218,74));
        title.setLocation((int) (size.getWidth() / 2 - title.getWidth() / 2),
                (int) (size.getHeight() / 4 - title.getHeight()));
        add(title);
        Label menu = new Label(html, Color.white);
        menu.setLocation((int) (size.getWidth() / 2 - menu.getWidth() / 2),
                (int) (size.getHeight() * 3 / 8 - menu.getHeight() / 2));
        add(menu);
    }

    protected void endMenu() {
        makeMenuText("<html><center><p style='color: #2719C7'>Game Over</p>  <br><br>[SPACE] New Game</center></html>", true);
    }
}

class Label extends JLabel {
    static final long serialVersionUID = 1L;

    Label(String label, Color color) {
        setText(label);
        setFont(new Font("Emulogic", Font.PLAIN, 20));
        setForeground(color);
        setSize(getPreferredSize());
    }
}