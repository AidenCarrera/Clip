package Intro.Projects.Clip;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Upgrade extends GameObject {
    private int width, height;
    private Image redUpgrade = new ImageIcon(getClass().getResource("redUpgrade.png")).getImage();
    private Image blueUpgrade = new ImageIcon(getClass().getResource("blueUpgrade.png")).getImage();
    private Image greenUpgrade = new ImageIcon(getClass().getResource("greenUpgrade.png")).getImage();
    private Image purpleUpgrade = new ImageIcon(getClass().getResource("purpleUpgrade.png")).getImage();
    private Image yellowUpgrade = new ImageIcon(getClass().getResource("yellowUpgrade.png")).getImage();
    private Image valueUpgrade = new ImageIcon(getClass().getResource("valueUpgrade.png")).getImage();
    private Image moreUpgrade = new ImageIcon(getClass().getResource("moreUpgrade.png")).getImage();
    public Upgrade(int x, int y, ID id, Handler handler) {
        super(x,y,id);
        width = redUpgrade.getWidth(null);
        height = redUpgrade.getHeight(null);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void tick() {
    }
    public void render(Graphics g) {
        switch (id) {
            case RedUpgrade:
                g.drawImage(redUpgrade, x, y, null);
                break;
            case BlueUpgrade:
                g.drawImage(blueUpgrade, x, y, null);
                break;
            case GreenUpgrade:
                g.drawImage(greenUpgrade, x, y, null);
                break;
            case PurpleUpgrade:
                g.drawImage(purpleUpgrade, x, y, null);
                break;
            case YellowUpgrade:
                g.drawImage(yellowUpgrade, x, y, null);
                break;
            case ValueUpgrade:
                g.drawImage(valueUpgrade, x, y, null);
                break;
            case MoreUpgrade:
                g.drawImage(moreUpgrade, x, y, null);
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