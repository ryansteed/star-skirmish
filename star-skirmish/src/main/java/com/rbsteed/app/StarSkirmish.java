package com.rbsteed.app;

import java.io.InputStream;
import java.net.URL;

public class StarSkirmish {
   /**
    * @param args
    */
   
   public static InputStream getInputStream(String fileName) {
      // System.out.println(classLoader.getResource(fileName).getFile());
      return StarSkirmish.class.getResourceAsStream("/"+fileName);
   }
   public static void main(String[] args) {
      GameEngine instance = new GameEngine();
   }
}