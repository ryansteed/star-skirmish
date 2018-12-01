/**
 * CSCI 2113 - Project 2 - Alien Attack
 * 
 * @author Ryan Steed
 *
 */
import java.util.ArrayList;
import javax.swing.JFrame;
import java.awt.Dimension;

public class AlienAttackFrame extends JFrame
{
   static final long serialVersionUID = 1L;
   private int sizeX = 224 * 2;
   private int sizeY = 288 * 2;
   private Background stage;

   public AlienAttackFrame(ArrayList<GameObject> objects)
   {
      setTitle("Alien Attack!");
      setPreferredSize(new Dimension(sizeX, sizeY));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      stage = new Background(new Dimension(sizeX, sizeY));
      for (GameObject object : objects) {
         System.out.println("Attempting to add "+object);
         stage.add(object);
      }
      add(stage);



      pack();
      setVisible(true);
   }
}
