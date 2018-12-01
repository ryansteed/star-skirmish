/**
 * CSCI 2113 - Project 2 - Alien Attack
 * 
 * @author Ryan Steed
 *
 */

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.awt.Dimension;

public class AlienAttack
{
   private static AlienAttack instance;

   private AlienAttackFrame frame;
   private ArrayList<GameObject> objects;
   private Wave currentWave;
   
   private static Dimension size = new Dimension(900, 900);
   private static int yBound = (int) size.getHeight() - 146;

   public static AlienAttack Instance()
   {
      return instance;
   }

   public AlienAttack()
   {
      // add initial game objects, like the player
      objects = new ArrayList<GameObject>();
      // player
      Dimension playerSize = new Dimension(30, 30);
      Euclidean playerInit = new Euclidean((int) (size.getWidth()/2 - playerSize.getWidth()/2), yBound - (int) playerSize.getHeight()*2);
      objects.add(new Player(playerInit, playerSize, 5));
      
      currentWave = null;

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            instance.createAndShowGUI();
         }
      });
   }

   public void start() {
      boolean noStopRequested = true;
      currentWave = new Wave(5, 3);
      while(noStopRequested) {
         try {
            SwingUtilities.invokeAndWait(new Update());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   class Update implements Runnable {
      public void run() {
         try {
            Thread.sleep(200);
            System.out.println("Cycle");
            ArrayList<GameObject> activeObjects = new ArrayList<GameObject>(objects);
            if (currentWave != null) {
               int scoreUpdate = currentWave.update();
               if (scoreUpdate < 0) {
                  // TODO: update to a new wave
                  currentWave = null;
               }
               activeObjects.addAll(currentWave.aliens);
            }
            frame.update(activeObjects);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   private void createAndShowGUI()
   {
      frame = new AlienAttackFrame(size, objects);
   }
   
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      instance = new AlienAttack();
      // TODO: allow the user to trigger this action
      instance.start();
   }

   class Wave {
      private int cleared;
      private int number;
      private int speed;
      protected ArrayList<Alien> aliens;

      Wave(int number, int speed) {
         this.number = number;
         this.cleared = 0;
         this.speed = speed;
         this.aliens = new ArrayList<Alien>();
         addNewAliens(number);
      }

      private void addNewAliens(int n) {
         for (int i = 0; i<n; i++) {
            Euclidean alienInit = new Euclidean(10, 10);
            // TODO: use normal dist to choose tier level based on wave difficulty
            // https://stackoverflow.com/questions/6011943/java-normal-distribution
            Alien alien = new Alien(alienInit, 0, speed);
            aliens.add(alien);
         }
      }

      private int update() {
         if (cleared == number) {
            return -1;
         }

         int score = 0;
         for (Alien alien : aliens) {
            // if alien past screen, delete and add points
            if (alien.engine.getY() > AlienAttack.yBound) {
               aliens.remove(alien);
               score += alien.value;
               cleared += 1;
            }
         }
         addNewAliens(number - cleared - aliens.size());
         return score;
      }
   }
}
