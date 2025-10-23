package Intro.Projects.Clip;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Paperclip extends GameObject {
    private int width, height;
    private Image paperclip = new ImageIcon(getClass().getResource("paperclip.png")).getImage();
    private Image redPaperclip = new ImageIcon(getClass().getResource("redPaperclip.png")).getImage();
    private Image greenPaperclip = new ImageIcon(getClass().getResource("greenPaperclip.png")).getImage();
    private Image bluePaperclip = new ImageIcon(getClass().getResource("bluePaperclip.png")).getImage();
    private Image purplePaperclip = new ImageIcon(getClass().getResource("purplePaperclip.png")).getImage();
    private Image yellowPaperclip = new ImageIcon(getClass().getResource("yellowPaperclip.png")).getImage();

    public Paperclip(int x, int y, ID id, Handler handler) {
        super(x,y,id);
        // Gets image dimensions and stores as width and height
        width = paperclip.getWidth(null);
        height = paperclip.getHeight(null);
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public void tick() {}
    public void render(Graphics g) {
        switch (id) {
            case Paperclip:
                // Render rectangle for debug
                // g.drawRect(x, y, width, height);
                g.drawImage(paperclip, x, y, null);
                break;
            case RedPaperclip:
                g.drawImage(redPaperclip, x, y, null);
                break;
            case GreenPaperclip:
                g.drawImage(greenPaperclip, x, y, null);
                break;
            case BluePaperclip:
                g.drawImage(bluePaperclip, x, y, null);
                break;
            case PurplePaperclip:
                g.drawImage(purplePaperclip, x, y, null);
                break;
            case YellowPaperclip:
                g.drawImage(yellowPaperclip, x, y, null);
                break;
            default:
                break;
        }
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}