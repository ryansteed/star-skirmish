/**
 * CSCI 2113 - Project 2
 * 
 * @author Ryan Steed
 *
 */
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
    protected int score;
    protected int lives;

    public MetaOverlay(Dimension dimension, int highscore) {
        super(dimension);
        setBackground(new Color(0, 0, 0, 0));

        Lives lifeDisplay = new Lives(lives);
        lifeDisplay.setLocation(0, (int) (dimension.getHeight() - GameEngine.vertOffset - lifeDisplay.getHeight()));
        add(lifeDisplay);

        // Score scoreDisplay = new Score(score);
        score = 0;
        CurrentScore scoreDisplay = new CurrentScore();
        scoreDisplay.setLocation((int) (dimension.getWidth() / 10), (int) (scoreDisplay.getHeight()));
        add(scoreDisplay);

        Score highscoreDisplay = new Score(highscore);
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
    }
    protected void update(int score, int lives) {
        this.score = score;
        this.lives = lives;
    }

    class Lives extends JComponent {
        static final long serialVersionUID = 1L;
        protected int lives;
        private int iconWidth;

        Lives(int lives) {
            this(lives, 25);
        }

        Lives(int lives, int iconWidth) {
            this.lives = lives;
            this.iconWidth = iconWidth;
            setSize(new Dimension(iconWidth * lives * 5 / 4, iconWidth));
        }

        @Override
        public void paintComponent(Graphics g) {
            for (int i = 0; i < lives; i++) {
                Player.paintShip(g, new Point(i * (iconWidth * 5 / 4), 0), new Dimension(iconWidth, iconWidth));
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
        makeMenuText("<html>[SPACE] Start<br><br>[ESC] Pause</html>");
    }
    
    protected void pauseMenu() {
        makeMenuText("<html>[SPACE] Resume<br><br>[R] RESET</html>");
    }

    private void makeMenuText(String html) {
        removeAll();
        Label menu = new Label(html, Color.white);
        menu.setLocation((int) (size.getWidth() / 2 - menu.getWidth() / 2),
                (int) (size.getHeight() / 2 - menu.getHeight()));
        add(menu);
    }

    protected void endMenu() {
        makeMenuText("<html>End</html>");
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