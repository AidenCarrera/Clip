package Intro.Projects.Clip;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

public class Menu extends GameObject {
    private int width, height;

    public Menu(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        width = 200;
        height = 50;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public void tick() {}
    public void render(Graphics g) {
        
//        // Undo for debugging
//        if(id == ID.NewGame){
//            g.setColor(Color.green);
//            g.fillRect(x, y, width, height);
//        }
//        if (id == ID.Continue) {
//            g.setColor(Color.blue);
//            g.fillRect(x, y, width, height);
//        }
//        if (id == ID.Exit) {
//            g.setColor(Color.red);
//            g.fillRect(x, y, width, height);
//        }
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public String toString() {
        return id + ": " + x + ", " + y;
    }
}