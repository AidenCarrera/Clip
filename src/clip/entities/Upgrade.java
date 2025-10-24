package clip.entities;

import clip.core.GameObject;
import clip.core.ID;
import clip.core.ConfigManager;

import java.awt.*;

public class Upgrade extends GameObject {

    private static final int SIZE = 50; // fixed size for clicks

    public Upgrade(int x, int y, ID id, ConfigManager config) {
        super(x, y, id, config); // pass config to GameObject
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    @Override
    public void tick() {
        // No per-frame logic needed
    }

    @Override
    public void render(Graphics g) {
        // Do nothing; HUD handles rendering
    }
}
