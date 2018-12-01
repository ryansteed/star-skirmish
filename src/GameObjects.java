import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

class GameObject extends JComponent {
    static final long serialVersionUID = 1L;
    protected Dimension boundary;
    protected int speed;
    protected Physics engine;
    
    public GameObject(Euclidean init, Dimension boundary, int speed) {
        this.boundary = boundary;
        this.speed = speed;
        this.engine = new Physics(init);
        setSize(boundary);
        setLocation(init.x, init.y);
    }

    protected void move() {
        Euclidean newPos = this.engine.move();
        setLocation(newPos.x, newPos.y);
    }
}

class Player extends GameObject {
    static final long serialVersionUID = 1L;

    Player(Euclidean init, Dimension boundary, int speed) {
        super(init, boundary, speed);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        // https://stackoverflow.com/questions/2509561/how-to-draw-a-filled-circle-in-java
        // Rectangle r = new Rectangle(engine.getX(), engine.getY(), (int) boundary.getWidth(), (int) boundary.getHeight());
        // System.out.println(r);
        g.fillRect(0, 0, (int) boundary.getWidth(), (int) boundary.getHeight());
    }
}

class Alien extends GameObject {
    static final long serialVersionUID = 1L;
    protected int value;
    private static int[] tiers = {30, 60, 90};

    Alien(Euclidean init, int tier, int speed) {
        super(init, new Dimension(tiers[tier], tiers[tier]), speed);
        this.value = 10;
        if (tier == 1) {
            this.value = 25;
        }
        if (tier == 2) {
            this.value += 50;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.green);
        // https://stackoverflow.com/questions/2509561/how-to-draw-a-filled-circle-in-java
        // Rectangle r = new Rectangle(engine.getX(), engine.getY(), (int)
        // boundary.getWidth(), (int) boundary.getHeight());
        // System.out.println(r);
        g.fillRect(0, 0, (int) boundary.getWidth(), (int) boundary.getHeight());
    }
}