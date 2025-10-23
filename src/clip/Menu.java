package clip;

import java.awt.*;

public class Menu extends GameObject {
    private final int width, height;

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
        // Set button color based on ID
        switch (id) {
            case NEW_GAME -> g.setColor(Color.GREEN.darker());
            case CONTINUE -> g.setColor(Color.BLUE.darker());
            case EXIT -> g.setColor(Color.RED.darker());
            default -> g.setColor(Color.GRAY);
        }

        // Draw button rectangle
        g.fillRect(x, y, width, height);

        // Draw button border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Draw centered text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String text = switch (id) {
            case NEW_GAME -> "New Game";
            case CONTINUE -> "Continue";
            case EXIT -> "Exit";
            default -> "";
        };
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height + fm.getAscent()) / 2 - 4;
        g.drawString(text, textX, textY);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public String toString() {
        return id + ": " + x + ", " + y;
    }
}
