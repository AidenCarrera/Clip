package clip;

import javax.swing.*;
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

    public Paperclip(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        Image img = getImageForID();
        width = img.getWidth(null);
        height = img.getHeight(null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void tick() {}

    public void render(Graphics g) {
        g.drawImage(getImageForID(), x, y, null);
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
}
