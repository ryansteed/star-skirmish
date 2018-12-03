
/**
 * CSCI 2113 - Project 2 - Alien Attack
 * 
 * @author Ryan Steed
 *
 */

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import java.awt.geom.Area;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Dimension;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class GameEngine {
    // properties
    protected static Properties prop;
    private static String propPath = "resources/AlienAttack.properties";

    private GameFrame frame;
    private ArrayList<GameObject> objects;
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

    public GameEngine() {
        loadProperties();
        currentWave = null;
        setTimer();
        loadObjects();
        setShutdownHook();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame = new GameFrame(size, highscore);
                frame.update(objects, 0, Integer.valueOf(prop.getProperty("plives")));
                registerGameControls();
            }
        });
    }

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

    private void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Storing highscore before shutdown");
                try (FileWriter inputStream = new FileWriter(propPath)) {
                    prop.setProperty("highscore", Integer.toString(highscore));
                    prop.store(inputStream, null);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "shutdown-thread"));
    }

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
    }

    class Update implements Runnable {
        public void run() {
            // System.out.println("Cycle");
            ArrayList<GameObject> activeObjects = new ArrayList<GameObject>(objects);
            if (currentWave != null) {
                checkCollisions();
                score += currentWave.update();
                if (currentWave.cleared()) {
                    score += 1;
                    // TODO: make this increment a property
                    currentWave = new Wave(currentWave.number + 1);
                }
                activeObjects.addAll(currentWave.aliens);
            }
            frame.update(activeObjects, score, player.lives);
        }
    }

    private void checkCollisions() {
        Iterator<Alien> iter = currentWave.iterAliens();
        while (iter.hasNext()) {
            if (iter.next().intersects(player)) {
                iter.remove();
                try {
                    int wait = Integer.valueOf(prop.getProperty("cycle")) * 10;
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                player.takeLife();
            }
        }
        if (player.isDead()) {
            end();
        }
    }

    private void loadProperties() {
        prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(propPath);
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
        highscore = Integer.valueOf(prop.getProperty("highscore"));
    }

    private void registerGameControls() {
        start = new AbstractAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                pause.setEnabled(true);

                score = 0;
                player.lives = Integer.valueOf(prop.getProperty("plives"));
                // first wave
                // TODO: make these parameters into properties
                currentWave = new Wave(5);
                frame.gameView();
                animationTimer.start();
            }
        };
        frame.controller.getActionMap().put("start", start);

        pause = new AbstractAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "resume");
                resume.setEnabled(true);

                animationTimer.stop();
                frame.menuView('p');
            }
        };
        frame.controller.getActionMap().put("pause", pause);

        resume = new AbstractAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                pause.setEnabled(true);

                frame.gameView();
                animationTimer.start();
            }
        };
        frame.controller.getActionMap().put("resume", resume);

        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
    }

    public void end() {
        animationTimer.stop();
        frame.menuView('e');
        // TODO: display game over
        System.out.println("Final Score: " + score);
        // write high score to properties file
        // http://roufid.com/write-properties-files-in-java/
        if (score > highscore) {
            highscore = score;
            System.out.println("Updated high score");
        }
    }
    
}
