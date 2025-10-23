package clip.core;

import java.awt.*;

public abstract class GameObject {
    // Creates position and ID for objects
    protected int x, y;
    protected ID id;
    protected int mouseX, mouseY;
    public GameObject(int x, int y, ID id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();
    // Methods used on objects
    public ID getID() {
        return id;
    }
    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }
    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }
}