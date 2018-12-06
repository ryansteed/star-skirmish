/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.io.InputStream;

public class StarSkirmish {
   public static InputStream getInputStream(String fileName) {
      // System.out.println(classLoader.getResource(fileName).getFile());
      return StarSkirmish.class.getResourceAsStream("/"+fileName);
   }
   public static void main(String[] args) {
      new GameEngine();
   }
}