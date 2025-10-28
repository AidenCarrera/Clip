package aiden.clip.entities;

import javax.swing.*;
import aiden.clip.core.ConfigManager;
import aiden.clip.core.GameObject;
import aiden.clip.core.ID;
import java.awt.*;
import java.util.Objects;

public class Paperclip extends GameObject {

    private final int width;
    private final int height;

    private static final Image PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/paperclip.png"))).getImage();
    private static final Image RED_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/redPaperclip.png"))).getImage();
    private static final Image GREEN_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/greenPaperclip.png"))).getImage();
    private static final Image BLUE_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/bluePaperclip.png"))).getImage();
    private static final Image PURPLE_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/purplePaperclip.png"))).getImage();
    private static final Image YELLOW_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/yellowPaperclip.png"))).getImage();

    /**
     * @param x      X position
     * @param y      Y position
     * @param id     Paperclip type
     * @param config Config manager
     * @param scaleX Horizontal scale factor
     * @param scaleY Vertical scale factor
     */
    public Paperclip(int x, int y, ID id, ConfigManager config, double scaleX, double scaleY) {
        super(x, y, id, config);

        Image img = getImageForID();

        // Apply resolution scaling + optional clip size multiplier
        double finalScaleX = scaleX * config.clipSize;
        double finalScaleY = scaleY * config.clipSize;

        width = (int) (img.getWidth(null) * finalScaleX);
        height = (int) (img.getHeight(null) * finalScaleY);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void tick() {
    }

    public void render(Graphics g) {
        g.drawImage(getImageForID(), x, y, width, height, null);
    }

    private Image getImageForID() {
        return switch (id) {
            case PAPERCLIP -> PAPERCLIP;
            case RED_PAPERCLIP -> RED_PAPERCLIP;
            case GREEN_PAPERCLIP -> GREEN_PAPERCLIP;
            case BLUE_PAPERCLIP -> BLUE_PAPERCLIP;
            case PURPLE_PAPERCLIP -> PURPLE_PAPERCLIP;
            case YELLOW_PAPERCLIP -> YELLOW_PAPERCLIP;
            default -> PAPERCLIP;
        };
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public static Dimension getScaledSize(ConfigManager config, double scaleX, double scaleY) {
        Image img = PAPERCLIP; // use base paperclip image
        int width = (int) (img.getWidth(null) * scaleX * config.clipSize);
        int height = (int) (img.getHeight(null) * scaleY * config.clipSize);
        return new Dimension(width, height);
    }
}
