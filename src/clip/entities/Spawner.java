package clip.entities;

import clip.core.Game;
import clip.core.Handler;
import clip.core.ID;

import java.util.Random;

public class Spawner {
    private final Handler handler;
    private final Random random;

    public Spawner(Handler handler, Random random) {
        this.handler = handler;
        this.random = random;
    }

    /**
     * Spawn a single paperclip at a random location.
     */
    public void spawnClip(ID id) {
        handler.addObject(new Paperclip(
                random.nextInt(Game.WIDTH - 734) + 250,  // width offset for margins
                random.nextInt(Game.HEIGHT - 600) + 150, // height offset for margins
                id
        ));
    }

    /**
     * Spawn multiple paperclips of the same type.
     */
    public void spawnClips(ID id, int count) {
        for (int i = 0; i < count; i++) spawnClip(id);
    }

}
