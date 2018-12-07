/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.io.InputStream;

/**
 * A Star Wars + Galaga themed 2d space scroller. 
 * Dodge waves of enemies to achieve a high score!
 */
public class StarSkirmish {
   /**
    * Produce an input stream from a resource filename using Maven's resource holder.
    * @param fileName The relative path to the resource within the resources folder.
    * @return An input stream for the given resource.
    */
   public static InputStream getInputStream(String fileName) {
      return StarSkirmish.class.getResourceAsStream("/"+fileName);
   }
   public static void main(String[] args) {
      new GameEngine();
   }
}