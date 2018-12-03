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
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.File;

public class AlienAttackFrame extends JFrame
{
   static final long serialVersionUID = 1L;

   private BackgroundOverlay stage;
   protected MetaOverlay overlay;

   public AlienAttackFrame(Dimension size, int highscore)
   {
      // get custom Galaga font
      try {
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/emulogic.ttf")));
      } catch (IOException|FontFormatException e) {
         e.printStackTrace();
      }

      setTitle("Alien Attack!");
      setPreferredSize(size);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
   
      setLayout(new OverlayLayout(getContentPane()));
      stage = new BackgroundOverlay(size);


      overlay = new MetaOverlay(size, highscore);
      add(overlay);
      add(stage);
   
      pack();
      setVisible(true);
   }

   protected void update(ArrayList<GameObject> objects, int score, int lives) {
      updateStage(objects);
      overlay.update(score, lives);
      // System.out.println("AlienAttackFrame:38> Calling repaint");
      this.repaint();
   }

   private void updateStage(ArrayList<GameObject> objects) {
      stage.removeAll();
      for (GameObject object : objects) {
         // System.out.println("Add " + object);
         object.update();
         stage.add(object);
      }
   }
}
