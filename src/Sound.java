import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    private Clip clip;

    public Sound(String path) {
        // https://stackoverflow.com/questions/11919009/using-javax-sound-sampled-clip-to-play-loop-and-stop-mutiple-sounds-in-a-game
        try {
            File file = new File(path);
            if (file.exists()) {
                AudioInputStream sound = AudioSystem.getAudioInputStream(file);
                // load the sound into memory (a Clip)
                clip = AudioSystem.getClip();
                clip.open(sound);
            }
            else {
                throw new RuntimeException("Could not play sound " + path);
            }
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