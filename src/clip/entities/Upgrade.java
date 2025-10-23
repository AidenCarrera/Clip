package clip.entities;

import clip.core.GameObject;
import clip.core.ID;

import java.awt.*;

public class Upgrade extends GameObject {
    public Upgrade(int x, int y, ID id) {
        super(x, y, id);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 50); // size placeholder if needed for clicks
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
