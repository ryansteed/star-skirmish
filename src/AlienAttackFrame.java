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
   private Dimension size;
   private Background stage;

   public AlienAttackFrame(Dimension size, ArrayList<GameObject> objects)
   {
      setTitle("Alien Attack!");
      setPreferredSize(size);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      stage = new Background(new Dimension(sizeX, sizeY));
      update(objects);
      add(stage);

      pack();
      setVisible(true);
   }

   protected void update(ArrayList<GameObject> objects) {
      stage.removeAll();

      for (GameObject object : objects) {
         // System.out.println("Add " + object);
         object.update();
         stage.add(object);
      }
      // System.out.println("AlienAttackFrame:38> Calling repaint");
      this.repaint();
   }
}
