import java.awt.Dimension;
import java.util.Properties;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Area;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.JComponent;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;

abstract class GameObject extends JComponent {
    static final long serialVersionUID = 1L;
    protected Dimension hitbox;
    protected int speed;
    protected Physics engine;
    private Area boundary;
    
    public GameObject(Euclidean init, Dimension hitbox, int speed, Area boundary) {
        this.hitbox = hitbox;
        this.speed = speed;
        this.engine = new Physics(init);
        this.engine.setMaxSpeed(speed);
        this.boundary = boundary;
        setSize(hitbox);
        setLocation(init.x, init.y);
    }

    protected boolean intersects(GameObject other) {
        Area intersect = getIntersect(new Area(other.getHitbox()));
        return !intersect.isEmpty();
    }

    private Area getIntersect(Area other) {
        Area area = new Area(this.getHitbox());
        area.intersect(other);
        return area;
    }

    protected Shape getHitbox() {
        return new Rectangle(engine.getX(), engine.getY(), (int) hitbox.getWidth(), (int) hitbox.getHeight());
    }

    protected void update() {
        // System.out.println("GameObjects:39> Repainting");
        // engine.move();
        // setLocation(engine.getX(), engine.getY());
        Euclidean newPos = this.engine.move();
        boolean inBounds = true;
        if (boundary != null) {
            inBounds = getIntersect(boundary).equals(new Area(this.getHitbox()));
        }
        if (inBounds) {
            setLocation(newPos.x, newPos.y);
        } else {
            Rectangle bounds = boundary.getBounds();
            int rightBound = (int) (bounds.getX() + bounds.getWidth());
            if (newPos.x > rightBound) {
                engine.v.x = 0;
                engine.p.x = rightBound;
            }
            if (newPos.x < bounds.getX()) {
                engine.v.x = 0;
                engine.p.x = (int) bounds.getX();
            }
            int bottomBound = (int) (bounds.getY() + bounds.getHeight());
            if (newPos.y > bottomBound) {
                engine.v.y = 0;
                engine.p.y = bottomBound;
            }
            if (newPos.y < bounds.getY()) {
                engine.v.y = 0;
                engine.p.y = (int) bounds.getX();
            }
            setLocation(engine.getX(), engine.getY());
        }
    }
}

class Player extends GameObject {
    static final long serialVersionUID = 1L;

    Player(Euclidean init, Dimension hitbox, Area boundary, int maxAccel, Properties prop) {
        super(init, hitbox, Integer.valueOf(prop.getProperty("pspeed")), boundary);
        registerMoveActions(maxAccel);
    }
    private void registerMoveActions(int maxAccel) {
        MoveAction moveRight = new MoveAction(new Euclidean(maxAccel, 0));
        MoveAction moveLeft = new MoveAction(new Euclidean(-maxAccel, 0));
        MoveAction moveUp = new MoveAction(new Euclidean(0, -maxAccel));
        MoveAction moveDown = new MoveAction(new Euclidean(0, maxAccel));
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
        this.getActionMap().put("left", moveLeft);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
        this.getActionMap().put("right", moveRight);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
        this.getActionMap().put("up", moveUp);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
        this.getActionMap().put("down", moveDown);

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "rLeft");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "rRight");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "rUp");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "rDown");
        this.getActionMap().put("rLeft", new EnableAction(moveLeft));
        this.getActionMap().put("rRight", new EnableAction(moveRight));
        this.getActionMap().put("rUp", new EnableAction(moveUp));
        this.getActionMap().put("rDown", new EnableAction(moveDown));
    }
    class MoveAction extends AbstractAction {
        static final long serialVersionUID = 1L;
        private Euclidean a;
        MoveAction(Euclidean a) {
            this.a = a;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            // change player acceleration
            engine.a = a;
            // System.out.println("Changed a to "+a);
        }
    }
    class EnableAction extends MoveAction {
        static final long serialVersionUID = 1L;
        MoveAction toEnable;
        EnableAction(MoveAction toEnable) {
            super(new Euclidean(0, 0));
            this.toEnable = toEnable;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            this.setEnabled(true);
            toEnable.setEnabled(true);
            // System.out.println("Enabled move on release");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        // https://stackoverflow.com/questions/2509561/how-to-draw-a-filled-circle-in-java
        g.fillRect(0, 0, (int) hitbox.getWidth(), (int) hitbox.getHeight());
    }
}

class Alien extends GameObject {
    static final long serialVersionUID = 1L;
    protected int value;
    private static int[] tiers = {30, 60, 90};

    Alien(Euclidean init, int tier, Properties prop) {
        super(init, new Dimension(tiers[tier], tiers[tier]), Integer.valueOf(prop.getProperty("aspeed"+tier)), null);
        this.value = Integer.valueOf(prop.getProperty("apoints"+tier));
        engine.v.y = speed;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.green);
        // https://stackoverflow.com/questions/2509561/how-to-draw-a-filled-circle-in-java
        // Rectangle r = new Rectangle(engine.getX(), engine.getY(), (int)
        // hitbox.getWidth(), (int) hitbox.getHeight());
        // System.out.println(r);
        g.fillRect(0, 0, (int) hitbox.getWidth(), (int) hitbox.getHeight());
    }
}