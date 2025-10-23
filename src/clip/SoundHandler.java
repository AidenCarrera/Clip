package clip;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Handles background music playback for the game.
 */
public class SoundHandler {

    // Static Clip instance for playback control
    private static Clip clip;

    /**
     * Loads and plays background music in a continuous loop.
     */
    public static void runMusic() {
        try {
            // Load the audio resource safely
            var resource = SoundHandler.class.getResource("/audio/music.wav");
            if (resource == null) {
                System.err.println("Audio file not found: /audio/music.wav");
                return;
            }

            // Use try-with-resources to automatically close the AudioInputStream
            try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(resource.toURI()))) {
                clip = AudioSystem.getClip();
                clip.open(inputStream);

                // Set volume (30%) using decibels
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (20f * Math.log10(0.3)); // 30% volume
                gainControl.setValue(dB);

                // Loop music indefinitely
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops and closes the currently playing music clip if it is open.
     */
    public static void close() {
        if (clip != null && clip.isOpen()) {
            clip.close();
        }
    }
}
