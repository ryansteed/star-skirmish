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
   private Dimension size;

   public AlienAttackFrame(Dimension size)
   {
      this.size = size;

      // get custom Galaga font
      try {
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/emulogic.ttf")));
         System.out.println("Registered font");
      } catch (IOException|FontFormatException e) {
         e.printStackTrace();
      }

      setTitle("Alien Attack!");
      setPreferredSize(size);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
   
      setLayout(new OverlayLayout(getContentPane()));
      stage = new BackgroundOverlay(size);


      overlay = new MetaOverlay(size);
      add(overlay);
      add(stage);
   
      pack();
      setVisible(true);
   }

   protected void update(ArrayList<GameObject> objects, int score, int lives) {
      updateStage(objects);
      updateOverlay(score, lives);
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

   private void updateOverlay(int score, int lives) {
      overlay.removeAll();

      Lives lifeDisplay = new Lives(lives);
      lifeDisplay.setLocation(0, (int) (size.getHeight() - AlienAttack.vertOffset - lifeDisplay.getHeight()));
      overlay.add(lifeDisplay);

      // Score scoreDisplay = new Score(score);
      Score scoreDisplay = new Score(score);
      scoreDisplay.setLocation((int) (size.getWidth() / 10), (int) (scoreDisplay.getHeight()));
      overlay.add(scoreDisplay);
   }
}
