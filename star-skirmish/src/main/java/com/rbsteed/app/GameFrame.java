/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.OverlayLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 * The container for the entire game view, including the object stage, the score overlay, and the menu overlay.
 */
public class GameFrame extends JFrame
{
   static final long serialVersionUID = 1L;

   private BackgroundOverlay stage;
   protected MetaOverlay overlay;
   protected GameController controller;

   /**
    * Sets the metadata for the game view and instantiates the components.
    * @param size The size of the game frame.
    * @param highscore The current highscore loaded from memory.
    */
   public GameFrame(Dimension size, int highscore, int lives)
   {
      // get custom Galaga font
      try {
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, StarSkirmish.getInputStream("fonts/emulogic.ttf")));
      } catch (IOException|FontFormatException e) {
         e.printStackTrace();
      }

      setTitle("Star Skirmish");
      setPreferredSize(size);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
   
      setLayout(new OverlayLayout(getContentPane()));

      stage = new BackgroundOverlay(size);
      overlay = new MetaOverlay(size, highscore, lives);
      controller = new GameController(size);

      add(controller);
      add(overlay);
      add(stage);
   
      menuView('s');
      pack();
      setVisible(true);
   }

   /**
    * Go to game view by removing all components from the menu overlay.
    */
   protected void gameView() {
      controller.removeAll();
   }
   
   /**
    * Open up the menu view.
    * 
    * @param A character key representing the correct menu to display. 's': start; 'p': pause; 'e': end.
    */
   protected void menuView(char menu) {
      switch (menu) {
         case 's': 
            controller.startMenu();
            break;
         case 'p':
            controller.pauseMenu();
            this.repaint();
            break;
         case 'e':
            controller.endMenu();
            break;
      }
   }

   /**
    * Update the game view.
    * 
    * @param objects GameObjects to include on the object stage.
    * @param score The current score.
    * @param lives The current number of player lives.
    */
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
