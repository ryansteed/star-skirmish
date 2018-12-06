/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.io.InputStream;
import java.io.BufferedInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    private Clip clip;

    public Sound(String name) {
        // https://stackoverflow.com/questions/11919009/using-javax-sound-sampled-clip-to-play-loop-and-stop-mutiple-sounds-in-a-game
        try {
            InputStream src = StarSkirmish.class.getClassLoader().getResourceAsStream(name);
            InputStream buffered = new BufferedInputStream(src);
            AudioInputStream sound = AudioSystem.getAudioInputStream(buffered);
            // load the sound into memory (a Clip)  
            clip = AudioSystem.getClip();
            clip.open(sound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void play() {
        clip.setFramePosition(0); // Must always rewind!
        clip.start();
    }
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop() {
        clip.stop();
    }
}