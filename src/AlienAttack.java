/**
 * CSCI 2113 - Project 2 - Alien Attack
 * 
 * @author Ryan Steed
 *
 */

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.geom.Area;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Dimension;
import java.util.Random;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class AlienAttack
{
   // properties
   private static Properties prop;
   private static String propPath = "resources/AlienAttack.properties";

   private static AlienAttack instance;

   private AlienAttackFrame frame;
   private ArrayList<GameObject> objects;
   private Player player;
   private Wave currentWave;
   private int score;
   private int highscore;
   private Timer animationTimer;
   
   private static Dimension size = new Dimension(900, 900);
   protected static int vertOffset = 146;
   private static int yBound = (int) size.getHeight() - vertOffset;

   public static AlienAttack Instance()
   {
      return instance;
   }

   public AlienAttack()
   {
      loadProperties();
      
      currentWave = null;

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

      loadObjects();
      
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            instance.createAndShowGUI();
         }
      });

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
      Area playerBoundary = new Area(
         new Rectangle(
            0,
            0, 
            (int) (size.getWidth()-playerSize.getWidth()), 
            (int) (yBound-playerSize.getHeight())
         )
      );
      player = new Player(playerInit, playerSize, playerBoundary, 5, prop);
      objects.add(player);
   }

   public void startGame() {
      score = 0;
      //TODO: make these lives into player copies in the bottom right
      player.lives = Integer.valueOf(prop.getProperty("plives"));
      // first wave
      currentWave = new Wave(5, 3);
      animationTimer.start();
   }
   public void endGame() {
      animationTimer.stop();
      System.out.println("Final Score: "+score);
      // write high score to properties file
      // http://roufid.com/write-properties-files-in-java/
      if (score > highscore) {
         highscore = score;
         System.out.println("Updated high score");
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
               currentWave = new Wave(currentWave.numTotal + 1, currentWave.speed + 5);
            }
            activeObjects.addAll(currentWave.aliens);
         }
         frame.update(activeObjects, score, player.lives);
      }
   }

   private void checkCollisions() {
      // Iterator iter = aliens.iterator();
      // while (iter.hasNext()) {
      //    Alien current = (Alien) iter.next();
      //    // if alien past screen, delete and add points
      //    if (currerlaynt.engine.getY() > AlienAttack.yBound) {
      //       score += current.value;
      //       numCleared += 1;
      //       // System.out.println("AlienAttack:164> Removing alien due to out of bounds");
      //       iter.remove();
      //    }
      // }

      // TODO: for each life lost, remove a life image and reduce player size
      int collisions = currentWave.checkCollisions();
      for (int i=0; i<collisions; i++) {
         player.takeLife();
      }
      if (player.lives <= 0) {
         endGame();
      }
   }
   
   private void createAndShowGUI()
   {
      frame = new AlienAttackFrame(size, highscore);
      frame.update(objects, 0, Integer.valueOf(prop.getProperty("plives")));
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
   
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      instance = new AlienAttack();
      // TODO: allow the user to trigger this action
      instance.startGame();
   }

   class Wave {
      private int numCleared;
      private int numTotal;
      private int speed;
      protected ArrayList<Alien> aliens;

      Wave(int number, int speed) {
         this.numTotal = number;
         this.numCleared = 0;
         this.speed = speed;
         this.aliens = new ArrayList<Alien>();
         addNewAliens(number);
      }

      private void addNewAliens(int n) {
         for (int i = 0; i<n; i++) {
            trySpawn(n);
         }
      }
      private void trySpawn(int n) {
         Alien alien = spawn(n);
         if (isValidSpawnPosition(alien)) {
            aliens.add(alien);
            return;
         }
         trySpawn(n);
      }
      private boolean isValidSpawnPosition(Alien spawned) {
         boolean intersection = false;
         for (Alien other : aliens) {
            if (spawned.intersects(other)) {
               intersection = true;
            }
         }
         return !intersection;
      }
      private Alien spawn(int n) {
         int initX = (int) (Math.random() * size.getWidth());
         int initY = - (int) (Math.random() * n * size.getHeight() / 10 + size.getHeight() / 10);
         Euclidean alienInit = new Euclidean(initX, initY);
         // TODO: use normal dist to choose tier level based on wave difficulty
         // https://stackoverflow.com/questions/6011943/java-normal-distribution
         return new Alien(alienInit, new Random().nextInt(3), prop);
      }
      protected int checkCollisions() {
         int collisions = 0;
         Iterator<Alien> iter = aliens.iterator();
         while (iter.hasNext()) {
            if (iter.next().intersects(player)) {
               collisions++;
               numCleared++;
               iter.remove();
            }
         }
         return collisions;
      }
      protected boolean cleared() {
         return numCleared == numTotal;
      }
      protected int update() {
         int score = 0;
         // To permit removal in place
         // https://www.geeksforgeeks.org/remove-element-arraylist-java/
         Iterator<Alien> iter = aliens.iterator();
         while (iter.hasNext()) {
            Alien current = iter.next();
            // if alien past screen, delete and add points
            if (current.engine.getY() > AlienAttack.yBound) {
               score += current.value;
               numCleared ++;
               // System.out.println("AlienAttack:164> Removing alien due to out of bounds");
               iter.remove();
            }
         }
         return score;
      }
   }
}
