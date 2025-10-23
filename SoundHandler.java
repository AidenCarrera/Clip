package Intro.Projects.Clip;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.sound.sampled.*;

public class SoundHandler {
    private static AudioInputStream inputStream;
    private static Clip clip;

    public static void RunMusic() {
        try {
            // Load audio clip and loop
            inputStream = AudioSystem.getAudioInputStream(
                    new File(SoundHandler.class.getResource("music.wav").toURI())
            );
            clip = AudioSystem.getClip();
            clip.open(inputStream);

            // --- Set volume here (approx. 30%)
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // 30% volume -> convert to decibels
            double volume = 0.3; // 30%
            float dB = (float) (20f * Math.log10(volume));
            gainControl.setValue(dB);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (clip != null && clip.isOpen()) {
            clip.close();
        }
    }
}
