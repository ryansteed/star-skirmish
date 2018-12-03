import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Point;


class Lives extends JComponent {
    static final long serialVersionUID = 1L;
    protected int lives;
    private int iconWidth;

    Lives(int lives) {
        this(lives, 30);
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