package clip.entities;

import clip.core.ConfigManager;
import clip.core.Handler;
import clip.core.ID;

import java.util.Random;

public class Spawner {
    private final Handler handler;
    private final Random random;
    private final ConfigManager config;

    public Spawner(Handler handler, Random random, ConfigManager config) {
        this.handler = handler;
        this.random = random;
        this.config = config;
    }

    /**
     * Spawn a single paperclip at a random location using config-based bounds.
     */
    public void spawnClip(ID id) {
        int marginX = (int) (config.displayWidth * 0.1);  // 10% horizontal margin
        int marginY = (int) (config.displayHeight * 0.1); // 10% vertical margin

        int x = random.nextInt(config.displayWidth - 2 * marginX) + marginX;
        int y = random.nextInt(config.displayHeight - 2 * marginY) + marginY;

        handler.addObject(new Paperclip(x, y, id, config));
    }

    /**
     * Spawn multiple paperclips of the same type.
     */
    public void spawnClips(ID id, int count) {
        for (int i = 0; i < count; i++) spawnClip(id);
    }
}
