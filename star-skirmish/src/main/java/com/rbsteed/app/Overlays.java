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

/**
 * A view that overlays text and metadata on top of the main game stage.
 */
abstract class Overlay extends JPanel {
    static final long serialVersionUID = 1L;

    public Overlay(Dimension dimension) {
        setPreferredSize(dimension);
        setLayout(null);
        setVisible(true);
    }
}
/**
 * A transparent overlay.
 */
abstract class TransparentOverlay extends Overlay {
    static final long serialVersionUID = 1L;
    public TransparentOverlay(Dimension dimension) {
        super(dimension);
        setBackground(new Color(0, 0, 0, 0));
    }
}

/**
 * The vast, black void of space.
 */
class BackgroundOverlay extends Overlay {
    static final long serialVersionUID = 1L;

    public BackgroundOverlay(Dimension dimension) {
        super(dimension);
        setBackground(Color.black);
    }
}

/**
 * An overlay with the score, high score, and remaining player lives.
 * Also displays any in-game messages.
 */
class MetaOverlay extends TransparentOverlay {
    static final long serialVersionUID = 1L;
    private Score highscoreDisplay;
    protected int score;
    private int lives;
    private Lives lifeDisplay;
    private Dimension dimension;

    /**
     * Instantiate a meta overlay with current score, high score display from memory,
     * and icons representing the number of lives remaining.
     * @param dimension The size of the overlay (usually the game frame size).
     * @param highscore The current highscore.
     * @param lives The starting number of lives.
     */
    public MetaOverlay(Dimension dimension, int highscore, int lives) {
        super(dimension);
        this.dimension = dimension;
        this.lives = lives;

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
    /**
     * Update the highscore display.
     * @param highscore The current highscore.
     */
    protected void updateHighscore(int highscore) {
        remove(highscoreDisplay);
        highscoreDisplay = new Score(highscore);
        highscoreDisplay.setLocation((int) (dimension.getWidth() / 2 - highscoreDisplay.getWidth() / 2),
                (int) (highscoreDisplay.getHeight()));
        add(highscoreDisplay);
        highscoreDisplay.repaint();
    }

    /**
     * Update the overlay with a new score and number of lives remaining. Occurs every
     * frame.
     * @param score
     * @param lives
     */
    protected void update(int score, int lives) {
        this.score = score;
        this.lives = lives;
    }

    /**
     * A special component for displaying the number of lives as icons.
     */
    class Lives extends JComponent {
        static final long serialVersionUID = 1L;
        private int iconWidth;

        private ShipPainter painter;

        Lives() {
            this(25);
        }

        /**
         * Set the size to be as long as the number of icons to display, and use the
         * same painter as is used for the player.
         * @param iconWidth
         */
        Lives(int iconWidth) {
            this.iconWidth = iconWidth;
            painter = new ShipPainter();
            setSize(new Dimension(iconWidth * (lives-1) * 5 / 4, iconWidth));
        }

        @Override
        /**
         * Paint as many icons as there are lives.
         * 
         * @param g The graphics for this component.
         */
        public void paintComponent(Graphics g) {
            for (int i = 0; i < lives-1; i++) {
                painter.paint(g, new Point(i * (iconWidth * 5 / 4), 0), new Dimension(iconWidth, iconWidth));
            }
        }
    }

    /**
     * A special label for displaying scores.
     */
    class Score extends Label {
        static final long serialVersionUID = 1L;

        Score(int init) {
            super(Integer.toString(init), Color.white);
        }
        protected String formatScore(int x) {
            return String.format("%d", x);
        }
    }

    /**
     * A special singleton score display that updates with the current score.
     */
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

        /**
         * Update the size and text with the new score.
         */
        private void updateText() {
            setText(formatScore(score));
            setSize(getPreferredSize());
        }
    }
}

/**
 * A special overlay that displays the menu (and holds the bindings for
 * game controls).
 * @see GameEngine
 */
class GameController extends TransparentOverlay {
    static final long serialVersionUID = 1L;
    private Dimension size;

    public GameController(Dimension dimension) {
        super(dimension);
        size = dimension;
    }

    protected void startMenu() {
        makeMenuText("<html>[SPACE] Start<br><br>&nbsp;[ESC] &nbsp;Pause</html>", true);
    }
    
    protected void pauseMenu() {
        makeMenuText("<html><center>[SPACE] Resume</center></html>", false);
    }

    /**
     * Displays the menu text for a given menu view. Also includes the game title if
     * needed.
     * @param html The html structure of the menu text.
     * @param includeTitle Whether or not to include the game title.
     */
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

/**
 * A special label that adds color and a custom font for game text.
 */
class Label extends JLabel {
    static final long serialVersionUID = 1L;

    Label(String label, Color color) {
        setText(label);
        setFont(new Font("Emulogic", Font.PLAIN, 20));
        setForeground(color);
        setSize(getPreferredSize());
    }
}