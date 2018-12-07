/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.util.Properties;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Area;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.AbstractAction;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.KeyStroke;
import java.awt.Color;

// import javax.swing.BorderFactory;

/**
 * Any object rendered on the game stage, always with motion. Manages position tracking,
 * collision assessment, and physics.
 */
abstract class GameObject extends JComponent {
    static final long serialVersionUID = 1L;
    protected Dimension hitbox;
    protected int speed;
    protected Physics engine;
    protected Area boundary;
    protected Painter painter;
    
    /**
     * Set the initial object conditions, including size, physics, and location.
     * 
     * @param init Initial position.
     * @param hitbox The container for this object used to assess collisions.
     * @param maxSpeed The maximum speed for this game object. NOT used to set speed.
     * @param boundary The area this object is allowed to inhabit.
     * 
     */
    public GameObject(Euclidean init, Dimension hitbox, int maxSpeed, Area boundary) {
        this.hitbox = hitbox;
        this.speed = maxSpeed;
        this.engine = new Physics(init);
        this.engine.setMaxSpeed(speed);
        this.boundary = boundary;
        setSize(hitbox);
        setLocation(init.x, init.y);
        // setBorder(BorderFactory.createLineBorder(Color.green));
    }

    /**
     * Check if this object intersects another using area subtraction.
     * @param other The other GameObject.
     * @return Whether or not the objects intersect.
     */
    protected boolean intersects(GameObject other) {
        Area intersect = getIntersect(new Area(other.getHitbox()));
        return !intersect.isEmpty();
    }

    private Area getIntersect(Area other) {
        Area area = new Area(this.getHitbox());
        area.intersect(other);
        return area;
    }

    /**
     * Get the hitbox for this object.
     * 
     * @return The shape of the hitbox.
     */
    protected Shape getHitbox() {
        return new Rectangle(engine.getX(), engine.getY(), (int) hitbox.getWidth(), (int) hitbox.getHeight());
    }

    /**
     * Update the position of this object. Make sure to disallow movement out of bounds.
     */
    protected void update() {
        // System.out.println("GameObjects:39> Repainting");
        // engine.move();
        // setLocation(engine.getX(), engine.getY());
        Euclidean newPos = this.engine.move();
        boolean inBounds = true;
        if (boundary != null) {
            inBounds = isInBounds();
        }
        if (inBounds) {
            setLocation(newPos.x, newPos.y);
        } else {
            handleOutOfBounds(newPos);
        }
    }

    private boolean isInBounds() {
        return getIntersect(boundary).equals(new Area(this.getHitbox()));
    }

    /**
     * If out of bounds, halt velocity component and set position to the boundary.
     * @param newPos The suggested new position (out of bounds).
     */
    protected void handleOutOfBounds(Euclidean newPos) {
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
    
    /**
     * Use this object's painter to render the object.
     * @see Painter
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        painter.paint(g, new Point(0, 0), hitbox);
    }
}

/**
 * The player, which moves on player command.
 */
class Player extends GameObject {
    static final long serialVersionUID = 1L;
    protected int lives;
    private Dimension originalSize;
    static int sizeInc = 30;
    private boolean immune;

    /**
     * Register movement actions for this player, and set initial object conditions.
     * Also assign a special painter.
     * 
     * @param init Initial player position.
     * @param hitbox The player hitbox.
     * @param boundary The boundary for the player.
     * @param maxAccel The maximum acceleration allowed for the player
     * @param prop The game properties object, to set certain player parameters.
     */
    Player(Euclidean init, Dimension hitbox, Area boundary, int maxAccel, Properties prop) {
        super(init, hitbox, Integer.valueOf(prop.getProperty("pspeed")), boundary);
        originalSize = hitbox;
        registerMoveActions(maxAccel);
        painter = new ShipPainter();
        immune = false;
    }

    /**
     * Register all move actions (down, right, left, and up).
     * @param maxAccel The maximum possible acceleration for this player.
     */
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
    /**
     * A special action for causing the player to move with a certain acceleration.
     * @see Physics
     */
    class MoveAction extends AbstractAction {
        static final long serialVersionUID = 1L;
        private Euclidean a;
        MoveAction(Euclidean a) {
            this.a = a;
        }
        
        /**
         * Set the engine acceleration and disable this action to avoid polluting the 
         * call stack.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            // change player acceleration
            engine.a = a;
            // System.out.println("Changed a to "+a);
        }
    }
    /**
     * A companion class to MoveAction that re-enables it when the key is lifted.
     * @see EnableAction
     */
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
    /**
     * Reset the player for a new game
     * @param lives The number of lives with which the player starts.
     */
    protected void reset(int lives) {
        this.lives = lives;
        this.hitbox = originalSize;
        setSize(hitbox);
        this.repaint();
    }
    
    /**
     * Take a life from the player and induce a brief immune period with animation.
     */
    protected void takeLife() {
        if (!immune) {
            lives--;
            if (lives > 0) {
                immune = true;

                int counts = 9;
                painter.setColor(new Color(254, 218, 74));
                ActionListener removeImmunity = new ActionListener() {
                    private int counter;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (counter == counts) {
                            ((Timer) e.getSource()).stop();
                            immune = false;
                            return;
                        }
                        if (counter % 2 == 1) {
                            painter.setColor(new Color(254, 218, 74));
                            hitbox = getSizeUp();
                        }
                        else {
                            painter.setColor(null);
                            if ((int) hitbox.getWidth() - sizeInc > 0) {
                                hitbox = getSizeDown();
                            }
                        }
                        setSize(hitbox);
                        repaint();
                        counter++;
                    }
                };
                Timer immunity = new Timer(Integer.valueOf(GameEngine.prop.getProperty("pimmunity")) / counts, removeImmunity);
                immunity.start();
            }
            // https://bigsoundbank.com/detail-0477-wilhelm-scream.html
            Sound deathSound = new Sound("death.wav");
            deathSound.play();
        }
    }
    private Dimension getSizeDown() {
        return new Dimension((int) hitbox.getWidth() - sizeInc, (int) hitbox.getHeight() - sizeInc);
    }
    private Dimension getSizeUp() {
        return new Dimension((int) hitbox.getWidth() + sizeInc, (int) hitbox.getHeight() + sizeInc);
    }
    protected boolean isDead() {
        return lives <= 0;
    }
}

/**
 * The enemy! Falls down from the top of the screen.
 */
class Alien extends GameObject {
    static final long serialVersionUID = 1L;
    protected int value;

    /**
     * Creates the alien with a special painter and sets a vertical velocity based on
     * the "tier" of enemy to which it belongs.
     * @param init Initial position.
     * @param tier Level of enemy (dictates size and speed).
     * @param baseSpeed Base speed assigned by wave - later waves have higher speed.
     * @param prop The game properties that dictate tier properties.
     */
    Alien(Euclidean init, int tier, int baseSpeed, Properties prop) {
        super(init, new Dimension(Wave.tiers[tier], Wave.tiers[tier]), Integer.valueOf(prop.getProperty("aspeed"+tier)), null);
        this.value = Integer.valueOf(prop.getProperty("apoints"+tier));
        engine.v.y = speed + baseSpeed;
        painter = new AlienPainter();
    }
}

/**
 * A background star that moves with random speed and has random size.
 */
class Star extends GameObject {
    static final long serialVersionUID = 1L;

    /**
     * Create a star with an initial position and a movement boundary.
     * Choose size and speed randomly within a range.
     * @param init The initial position.
     * @param boundary The boundary for the star (the size of the game view, usually).
     */
    public Star(Euclidean init, Area boundary) {
        super(init, randomDimension(), randomSpeed(), boundary);
        engine.v.y = speed;
        painter = new StarPainter();
        engine.setMaxSpeed(null);
    }

    private static int randomSpeed() {
        return new Random().nextInt(3) + 1;
    }
    private static Dimension randomDimension() {
        int size = new Random().nextInt(3) + 5;
        return new Dimension(size, size);
    }

    /**
     * Special out of bounds method to reset the star at the top of the screen with a new
     * size and speed. Speed increases over time to give the impression of increasing
     * flight speed relative to background.
     */
    @Override
    protected void handleOutOfBounds(Euclidean newPos) {
        engine.p.y = 0;
        engine.p.x = (int) (Math.random() * boundary.getBounds().getWidth());
        // new speed
        this.speed = speed + randomSpeed() / 2;
        engine.v.y = speed;
        // new size
        this.hitbox = randomDimension();
        setSize(hitbox);
        setLocation(engine.getX(), engine.getY());
    }

    /**
     * Reset this star for a new game by removing its built-up speed and choosing
     * new size and speed.
     */
    protected void reset() {
        this.speed = randomSpeed();
        this.hitbox = randomDimension();
        setSize(randomDimension());
        engine.v.y = speed;
        engine.a.y = 0;
    }

    /**
     * Awesome update method that elongates the star based on speed with
     * an inverse Lorentz transformation. This will give the impression of hyperspeed
     * at high velocities!
     */
    @Override
    protected void update() {
        super.update();
        // elongate star based on speed
        // funky lorentz transformation
        int c = 15;
        int lambda = engine.v.y / c > 1 ? engine.v.y / c : 1;
        // System.out.println(lambda);
        hitbox.setSize(
            hitbox.getWidth(), 
            hitbox.getHeight() * lambda < boundary.getBounds().getHeight() ? hitbox.getHeight() * lambda : boundary.getBounds().getHeight()
        );
        // System.out.println(hitbox);
        setSize(hitbox);
    }
}