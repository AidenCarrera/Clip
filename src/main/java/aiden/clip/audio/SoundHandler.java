package aiden.clip.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles background music playback for the game.
 */
public class SoundHandler {

    private static Clip musicClip;

    /**
     * Loads and plays background music in a continuous loop.
     *
     * @param musicVolume Volume for background music (0.0–1.0)
     * @param sfxVolume   Volume for sound effects (reserved for later use)
     */
    public static void runMusic(double musicVolume, double sfxVolume) {
        try {
            InputStream resourceStream = SoundHandler.class.getResourceAsStream("/audio/music.wav");
            if (resourceStream == null) {
                System.err.println("Audio file not found: /audio/music.wav");
                return;
            }

            // Wrap in BufferedInputStream to ensure mark/reset support
            try (AudioInputStream inputStream = AudioSystem
                    .getAudioInputStream(new BufferedInputStream(resourceStream))) {
                musicClip = AudioSystem.getClip();
                musicClip.open(inputStream);

                // Clamp and convert volume to decibels
                float volume = (float) Math.max(0.0, Math.min(1.0, musicVolume));
                float dB = (float) (20f * Math.log10(volume <= 0 ? 0.0001 : volume));

                if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(dB);
                } else {
                    System.err.println("Warning: MASTER_GAIN control not supported.");
                }

                // Start looping music
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();

                System.out.printf("Music started with volume %.2f dB (%.2f%%). SFX volume = %.2f%%%n",
                        dB, volume * 100, sfxVolume * 100);
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops and closes the currently playing music clip.
     */
    public static void close() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }
}
