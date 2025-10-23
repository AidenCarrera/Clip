package clip;

import javax.swing.*;
import java.awt.*;

public class Upgrade extends GameObject {
    private int width, height;
    private Image redUpgrade = new ImageIcon(getClass().getResource("/images/redUpgrade.png")).getImage();
    private Image blueUpgrade = new ImageIcon(getClass().getResource("/images/blueUpgrade.png")).getImage();
    private Image greenUpgrade = new ImageIcon(getClass().getResource("/images/greenUpgrade.png")).getImage();
    private Image purpleUpgrade = new ImageIcon(getClass().getResource("/images/purpleUpgrade.png")).getImage();
    private Image yellowUpgrade = new ImageIcon(getClass().getResource("/images/yellowUpgrade.png")).getImage();
    private Image valueUpgrade = new ImageIcon(getClass().getResource("/images/valueUpgrade.png")).getImage();
    private Image moreUpgrade = new ImageIcon(getClass().getResource("/images/moreUpgrade.png")).getImage();
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
            case RED_UPGRADE:
                g.drawImage(redUpgrade, x, y, null);
                break;
            case BLUE_UPGRADE:
                g.drawImage(blueUpgrade, x, y, null);
                break;
            case GREEN_UPGRADE:
                g.drawImage(greenUpgrade, x, y, null);
                break;
            case PURPLE_UPGRADE:
                g.drawImage(purpleUpgrade, x, y, null);
                break;
            case YELLOW_UPGRADE:
                g.drawImage(yellowUpgrade, x, y, null);
                break;
            case VALUE_UPGRADE:
                g.drawImage(valueUpgrade, x, y, null);
                break;
            case MORE_UPGRADE:
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