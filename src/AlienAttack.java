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
   private ArrayList<GameObject> objects;

   public static AlienAttack Instance()
   {
      return instance;
   }

   public AlienAttack()
   {
      // add initial game objects, like the player
      objects = new ArrayList<GameObject>();
      objects.add(new Player(
         new Euclidean(200, 200),
         new Dimension(10, 10),
         5
      ));
      // trigger logic for creating new waves of aliens
   }
   
   private void createAndShowGUI()
   {
      AlienAttackFrame aaFrame = new AlienAttackFrame(objects);
   }
   
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      instance = new AlienAttack();
      
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
             instance.createAndShowGUI(); 
         }
      });

   }
}
