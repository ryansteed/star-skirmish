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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

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

    public GameEngine() {
        loadProperties();
        
        currentWave = null;
        prefs = Preferences.userRoot().node(StarSkirmish.class.getName());
        highscore = Integer.valueOf(prefs.get("highscore", "0"));
        
        setTimer();
        loadObjects();
        setShutdownHook();

        // All sound effects royalty-free from
        // https://downloads.khinsider.com/game-soundtracks/album/galaga-arcade
        mainMusic = new Sound("main.wav");
        introMusic = new Sound("intro.wav");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame = new GameFrame(size, highscore);
                ArrayList<GameObject> activeObjects = new ArrayList<GameObject>(objects);
                activeObjects.addAll(stars);
                frame.update(activeObjects, 0, Integer.valueOf(prop.getProperty("plives")));
                introMusic.loop();
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
                storeHighscore();
                // uses Java preferences API
                // http://www.vogella.com/tutorials/JavaPreferences/article.html
                prefs.put("highscore", Integer.toString(highscore));
            }
        }, "shutdown-thread"));
    }

    private void storeHighscore() {
        
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

        loadStars();  
    }

    private void loadStars() {
        stars = new ArrayList<Star>();
        // stars
        for (int i = 0; i < Integer.valueOf(prop.getProperty("stars")); i++) {
            Euclidean init = new Euclidean((int) (Math.random() * GameEngine.size.getWidth()),
                    (int) (Math.random() * GameEngine.size.getHeight()));
            stars.add(new Star(init, new Area(new Rectangle(0, 0, (int) size.getWidth(), (int) yBound))));
        }
    }

    private void resetStars() {
        for (Star star : stars) {
            star.reset();
        }
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
            activeObjects.addAll(stars);
            frame.update(activeObjects, score, player.lives);
        }
        
        private void checkCollisions() {
            Iterator<Alien> iter = currentWave.iterAliens();
            while (iter.hasNext()) {
                if (iter.next().intersects(player)) {
                    iter.remove();
                    player.takeLife();
                }
            }
            if (player.isDead()) {
                end();
            }
        }
    }

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

    private void registerGameControls() {
        start = new AbstractAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                pause.setEnabled(true);
                hyperspace.setEnabled(true);

                score = 0;
                resetStars();
                player.reset(Integer.valueOf(prop.getProperty("plives")));
                // first wave
                // TODO: make these parameters into properties
                currentWave = new Wave(5);
                frame.gameView();
                introMusic.stop();
                mainMusic.loop();
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
                hyperspace.setEnabled(false);

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

        hyperspace = new AbstractAction() {
            static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                System.out.println("Easter egg: hyperspace mode!");
                for (Star star : stars) {
                    star.engine.a.y = 1;
                }
            }
        };
        frame.controller.getActionMap().put("hyperspace", hyperspace);

        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start");
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
        frame.controller.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hyperspace");
    }

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
            highscore = score;
            frame.overlay.updateHighscore(highscore);
        }
        else {
            // play ending sound effect
            Sound endSound = new Sound("scream.wav");
            endSound.play();
        }
    }
    
}
