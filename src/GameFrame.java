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

public class GameFrame extends JFrame
{
   static final long serialVersionUID = 1L;

   private BackgroundOverlay stage;
   protected MetaOverlay overlay;
   protected GameController controller;

   public GameFrame(Dimension size, int highscore)
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
      controller = new GameController(size);

      add(controller);
      add(overlay);
      add(stage);
   
      menuView('s');
      pack();
      setVisible(true);
   }

   protected void gameView() {
      controller.removeAll();
   }
   
   protected void menuView(char menu) {
      System.out.println("attemtping to switch "+menu);
      switch (menu) {
         case 's': 
            System.out.println("Start menu time");
            controller.startMenu();
            break;
         case 'p':
            System.out.println("Pause menu time");
            controller.pauseMenu();
            this.repaint();
            break;
         case 'e':
            controller.endMenu();
            break;
      }
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
