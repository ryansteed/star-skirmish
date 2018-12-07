/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.geom.Area;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * The GameEngine manages all game controls. It manages the view and the game
 * objects . It also contains the endpoints available to the user. The
 * GameEngine uses a static properties file as well as a preferences backend for
 * configuration.
 * 
 * @see GameObject
 * @see Properties
 * @see GameController
 * @see Preferences
 * @see GameFrame
 */
public class GameEngine {
    // properties
    protected static Properties prop;
    protected static Preferences prefs;

    private GameFrame frame;
    private ArrayList<GameObject> objects;
    private ArrayList<Star> stars;
    private Player player;
    private Wave currentWave;
    private int score;
    private int highscore;
    private Timer animationTimer;

    protected static Dimension size = new Dimension(900, 900);
    protected static int vertOffset = 146;
    protected static int yBound = (int) size.getHeight() - vertOffset;

    private AbstractAction start;
    private AbstractAction pause;
    private AbstractAction resume;
    private AbstractAction hyperspace;

    private Sound mainMusic;
    private Sound introMusic;

    /**
     * Prepares the game for playing, opening with a start menu and background music.
     * Also registers game controls.
     */
    public GameEngine() {
        loadProperties();
        
        currentWave = null;
        prefs = Preferences.userRoot().node(StarSkirmish.class.getName());
        highscore = Integer.valueOf(prefs.get("highscore", "0"));
        
        setTimer();
        loadObjects();

        // All sound effects royalty-free from
        // https://downloads.khinsider.com/game-soundtracks/album/galaga-arcade
        mainMusic = new Sound("main.wav");
        introMusic = new Sound("intro.wav");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame = new GameFrame(size, highscore, Integer.valueOf(prop.getProperty("plives")));
                ArrayList<GameObject> activeObjects = new ArrayList<GameObject>(objects);
                activeObjects.addAll(stars);
                frame.update(activeObjects, 0, Integer.valueOf(prop.getProperty("plives")));
                introMusic.loop();
                registerGameControls();
            }
        });
    }

    /**
     * Sets the main animation timer.
     * 
     * @see Timer
     */
    private void setTimer() {
        animationTimer = new Timer(Integer.parseInt((prop.getProperty("cycle"))), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SwingUtilities.invokeLater(new Update());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Loads objects that appear before the game is started: player and stars.
     * Objects are stored in an instance-level array list.
     * 
     * @see Player
     * @see Star
     */
    private void loadObjects() {
        // add initial game objects, like the player
        objects = new ArrayList<GameObject>();
        // player
        Dimension playerSize = new Dimension(90, 90);
        Euclidean playerInit = new Euclidean((int) (size.getWidth() / 2 - playerSize.getWidth() / 2),
                yBound - (int) playerSize.getHeight() * 2);
        Area playerBoundary = new Area(new Rectangle(0, 0, (int) (size.getWidth() - playerSize.getWidth()),
                (int) (yBound - playerSize.getHeight())));
        player = new Player(playerInit, playerSize, playerBoundary, 5, prop);
        objects.add(player);

        loadStars();  
    }

    /**
     * Creates new stars and adds to game view. 
     * @see Star
     */
    private void loadStars() {
        stars = new ArrayList<Star>();
        // stars
        for (int i = 0; i < Integer.valueOf(prop.getProperty("stars")); i++) {
            Euclidean init = new Euclidean((int) (Math.random() * GameEngine.size.getWidth()),
                    (int) (Math.random() * GameEngine.size.getHeight()));
            stars.add(new Star(init, new Area(new Rectangle(0, 0, (int) size.getWidth(), (int) yBound))));
        }
    }

    /**
     * Resets stars for a new game. (Stars pick up speed over the course of a game).
     */
    private void resetStars() {
        for (Star star : stars) {
            star.reset();
        }
    }

    /**
     * Update is a runnable that conducts the frame-by-frame processes required to operate
     * the game, including collision checking, wave updating, and updating the game frame.
     */
    class Update implements Runnable {
        public void run() {
            // System.out.println("Cycle");
            ArrayList<GameObject> activeObjects = new ArrayList<GameObject>(objects);
            if (currentWave != null) {
                checkCollisions();
                score += currentWave.update();
                if (currentWave.cleared()) {
                    score += 1;
                    currentWave = new Wave(
                        currentWave.number + Integer.valueOf(prop.getProperty("sizeup")), 
                        currentWave.speed + Integer.valueOf(prop.getProperty("speedup"))
                    );
                }
                activeObjects.addAll(currentWave.aliens);
            }
            activeObjects.addAll(stars);
            // for (Missile missile : player.missiles) {
            //     System.out.println(missile);
            // }
            activeObjects.addAll(player.missiles);
            frame.update(activeObjects, score, player.lives);
        }
        
        /**
         * Check for collisions between aliens and the player. If collisions, take a 
         * life. End the game if the player is dead.
         */
        private void checkCollisions() {
            Iterator<Alien> iter = currentWave.iterAliens();
            while (iter.hasNext()) {
                Alien current = iter.next();
                if (current.intersects(player)) {
                    iter.remove();
                    player.takeLife();
                }
                else {
                    boolean isDead = false;
                    Iterator<Player.Missile> missileIter = player.iterMissiles();
                    while (missileIter.hasNext()) {
                        if (missileIter.next().intersects(current)) {
                            missileIter.remove();
                            isDead = current.hit(player.lives);
                        }
                    }
                    // do this outside the loop to avoid race condition
                    if (isDead) iter.remove();
                }
            }
            if (player.isDead()) {
                end();
            }
        }
    }

    /**
     * Load developer defined properties from a properties file.
     * @see Properties
     */
    private void loadProperties() {
        prop = new Properties();
        InputStream input = null;
        try {
            input = StarSkirmish.getInputStream("StarSkirmish.properties");
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Bind basic game controls to keys using custom abstract action.
     */
    private void registerGameControls() {
        start = new KeyAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void run() {
                start();
            }
        };
        frame.controller.getActionMap().put("start", start);

        pause = new KeyAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void run() {
                pause();
            }
        };
        frame.controller.getActionMap().put("pause", pause);

        resume = new KeyAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void run() {
                resume();
            }
        };
        frame.controller.getActionMap().put("resume", resume);

        hyperspace = new KeyAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void run() {
                hyperspace();
            }
        };
        frame.controller.getActionMap().put("hyperspace", hyperspace);

        frame.controller.getActionMap().put("resetHighscore", new AbstractAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setHighscore(0);
            }
        });

        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hyperspace");
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "resetHighscore");
    }

    /**
     * Class for actions on key bindings. Once triggered, disables itself until enabled
     * again by another action.
     */
    abstract class KeyAction extends AbstractAction {
        static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            run();
        }
        abstract protected void run();
    }

    /**
     * Start the game (from the start or end menu).
     */
    private void start() {
        pause.setEnabled(true);
        hyperspace.setEnabled(true);

        score = 0;
        resetStars();
        player.reset(Integer.valueOf(prop.getProperty("plives")));
        // first wave
        currentWave = new Wave(Integer.valueOf(prop.getProperty("initwavesize")), 0);
        frame.gameView();
        introMusic.stop();
        mainMusic.loop();
        animationTimer.start();
    }
    /**
     * Pause the game during play.
     */
    private void pause() {
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "resume");
        resume.setEnabled(true);
        hyperspace.setEnabled(false);
        animationTimer.stop();
        frame.menuView('p');
    }
    /**
     * Resume the game after pausing.
     */
    private void resume() {
        pause.setEnabled(true);
        frame.gameView();
        animationTimer.start();
    }

    private void hyperspace() {
        System.out.println("Easter egg: hyperspace mode!");
        for (Star star : stars) {
            star.engine.a.y = 1;
        }
    }

    /**
     * End the game and show game over menu. If a new highscore, write to a hidden
     * persistent configuration backend using Preferences API.
     * @see Preferences
     */
    public void end() {
        mainMusic.stop();
        animationTimer.stop();
        pause.setEnabled(false);
        resume.setEnabled(false);
        hyperspace.setEnabled(false);
        start.setEnabled(true);
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");
        frame.menuView('e');
        // write high score to properties file
        // http://roufid.com/write-properties-files-in-java/
        if (score > highscore) {
            Sound clearedSound = new Sound("stage.wav");
            clearedSound.play();
            setHighscore(score);
        }
        else {
            // play ending sound effect
            Sound endSound = new Sound("scream.wav");
            endSound.play();
        }
    }

    /**
     * Set the new high score and update the scoreboard.
     * @param score The new high score.
     */
    private void setHighscore(int score) {
        highscore = score;
        prefs.put("highscore", Integer.toString(highscore));
        frame.overlay.updateHighscore(highscore);
    }
    
}
