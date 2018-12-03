import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

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
        for (int i=0; i<lives; i++) {
            Player.paintShip(g, new Point(i * (iconWidth * 5 / 4), 0), new Dimension(iconWidth, iconWidth));
        }
    }
}

class Score extends JLabel {
    static final long serialVersionUID = 1L;

    Score(int score) {
        super(String.format("%d", score));
        setForeground(Color.white);
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // for (int i=0; i<ge.getAvailableFontFamilyNames().length; i++) {
        //     System.out.println(ge.getAvailableFontFamilyNames()[i]);
        // }
        setFont(new Font("Emulogic", Font.PLAIN, 20));
        setSize(getPreferredSize());
    }
}