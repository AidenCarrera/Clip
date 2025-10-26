package aiden.clip.entities;

import java.awt.*;

import aiden.clip.core.ConfigManager;
import aiden.clip.core.GameObject;
import aiden.clip.core.ID;

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
