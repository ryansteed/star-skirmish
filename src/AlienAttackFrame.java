/**
 * CSCI 2113 - Project 2 - Alien Attack
 * 
 * @author Ryan Steed
 *
 */
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.OverlayLayout;
import java.awt.Dimension;

public class AlienAttackFrame extends JFrame
{
   static final long serialVersionUID = 1L;
   private int sizeX = 224 * 2;
   private int sizeY = 288 * 2;
   private Dimension size;

   private BackgroundOverlay stage;
   private MetaOverlay overlay;

   public AlienAttackFrame(Dimension size, ArrayList<GameObject> objects)
   {
      setTitle("Alien Attack!");
      setPreferredSize(size);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
      setLayout(new OverlayLayout(getContentPane()));
      stage = new BackgroundOverlay(new Dimension(sizeX, sizeY));
      update(objects);

      overlay = new MetaOverlay(size);
      
      add(overlay);
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
