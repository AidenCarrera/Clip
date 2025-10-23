package clip.entities;

import clip.core.GameObject;
import clip.core.ID;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Upgrade extends GameObject {
    private final int width, height;
    private final Image redUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/redUpgrade.png"))).getImage();
    private final Image blueUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/blueUpgrade.png"))).getImage();
    private final Image greenUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/greenUpgrade.png"))).getImage();
    private final Image purpleUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/purpleUpgrade.png"))).getImage();
    private final Image yellowUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/yellowUpgrade.png"))).getImage();
    private final Image valueUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/valueUpgrade.png"))).getImage();
    private final Image moreUpgrade = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/moreUpgrade.png"))).getImage();

    public Upgrade(int x, int y, ID id) {
        super(x, y, id);
        this.width = redUpgrade.getWidth(null);
        this.height = redUpgrade.getHeight(null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void tick() {
        // Upgrade doesn’t need to update every frame
    }

    @Override
    public void render(Graphics g) {
        switch (id) {
            case RED_UPGRADE -> g.drawImage(redUpgrade, x, y, null);
            case BLUE_UPGRADE -> g.drawImage(blueUpgrade, x, y, null);
            case GREEN_UPGRADE -> g.drawImage(greenUpgrade, x, y, null);
            case PURPLE_UPGRADE -> g.drawImage(purpleUpgrade, x, y, null);
            case YELLOW_UPGRADE -> g.drawImage(yellowUpgrade, x, y, null);
            case VALUE_UPGRADE -> g.drawImage(valueUpgrade, x, y, null);
            case MORE_UPGRADE -> g.drawImage(moreUpgrade, x, y, null);
        }
    }

}
