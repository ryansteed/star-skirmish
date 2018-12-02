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
import java.io.IOException;

public class AlienAttack
{
   // properties
   private Properties prop;

   private static AlienAttack instance;

   private AlienAttackFrame frame;
   private ArrayList<GameObject> objects;
   private Wave currentWave;
   private int score;
   private Timer animationTimer;
   
   private static Dimension size = new Dimension(900, 900);
   private static int yBound = (int) size.getHeight() - 146;

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
   }

   private void loadObjects() {
      // add initial game objects, like the player
      objects = new ArrayList<GameObject>();
      // player
      Dimension playerSize = new Dimension(30, 30);
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
      Player player = new Player(playerInit, playerSize, playerBoundary, 5, prop);
      objects.add(player);
   }

   public void startGame() {
      score = 0;
      // first wave
      currentWave = new Wave(5, 3);
      animationTimer.start();
   }
   public void endGame() {
      System.out.println("Final Score: "+score);
   }

   class Update implements Runnable {
      public void run() {
         // System.out.println("Cycle");
         ArrayList<GameObject> activeObjects = new ArrayList<GameObject>(objects);
         if (currentWave != null) {
            score += currentWave.update();
            if (currentWave.cleared()) {
               // TODO: update to a new wave instead of ending game
               // noStopRequested = false;
               score += 1;
               currentWave = new Wave(currentWave.numTotal + 1, currentWave.speed + 5);
            }
            activeObjects.addAll(currentWave.aliens);
         }
         frame.update(activeObjects);
      }
   }
   
   private void createAndShowGUI()
   {
      frame = new AlienAttackFrame(size, objects);
   }

   private void loadProperties() {
      prop = new Properties();
      InputStream input = null;
      try {
         input = new FileInputStream("resources/AlienAttack.properties");
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

      protected boolean cleared() {
         return numCleared == numTotal;
      }
      protected int update() {
         int score = 0;
         // To permit removal in place
         // https://www.geeksforgeeks.org/remove-element-arraylist-java/
         Iterator iter = aliens.iterator();
         while (iter.hasNext()) {
            Alien current = (Alien) iter.next();
            // if alien past screen, delete and add points
            if (current.engine.getY() > AlienAttack.yBound) {
               score += current.value;
               numCleared += 1;
               // System.out.println("AlienAttack:164> Removing alien due to out of bounds");
               iter.remove();
            }
         }
         return score;
      }
   }
}
