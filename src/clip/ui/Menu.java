package clip.ui;

import clip.core.GameObject;
import clip.core.ID;

import java.awt.*;

public class Menu extends GameObject {
    private final float widthPercent, heightPercent;       // size as % of window
    private final float bottomOffsetPercent;               // distance from bottom of window
    private final float spacingPercent;                    // vertical spacing between buttons
    private int currentWidth, currentHeight;         // actual pixel size after scaling
    private boolean visible = true;

    public Menu(float bottomOffsetPercent, float widthPercent, float heightPercent, float spacingPercent, ID id) {
        super(0, 0, id); // x,y will be calculated dynamically
        this.bottomOffsetPercent = bottomOffsetPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
        this.spacingPercent = spacingPercent;
    }

    // Call every frame to update size and position based on window
    public void updatePosition(int windowWidth, int windowHeight, int order) {
        // Calculate actual width/height in pixels
        currentWidth = (int)(windowWidth * widthPercent);
        currentHeight = (int)(windowHeight * heightPercent);

        // Center horizontally
        x = (windowWidth - currentWidth) / 2;

        // Position vertically from bottom, accounting for spacing and order
        y = windowHeight - (int)(bottomOffsetPercent * windowHeight)
                - (currentHeight + (int)(spacingPercent * windowHeight)) * order;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, currentWidth, currentHeight);
    }

    public void tick() {
        // nothing to do for static buttons
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void render(Graphics g) {
        if (!visible) return;
        // Set button color
        switch (id) {
            case NEW_GAME -> g.setColor(Color.GREEN.darker());
            case CONTINUE -> g.setColor(Color.BLUE.darker());
            case EXIT -> g.setColor(Color.RED.darker());
            default -> g.setColor(Color.GRAY);
        }

        g.fillRect(x, y, currentWidth, currentHeight);

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, currentWidth, currentHeight);

        // Draw text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int)(currentHeight * 0.4))); // font scales with button height
        String text = switch (id) {
            case NEW_GAME -> "New Game";
            case CONTINUE -> "Continue";
            case EXIT -> "Exit";
            default -> "";
        };
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (currentWidth - fm.stringWidth(text)) / 2;
        int textY = y + (currentHeight + fm.getAscent()) / 2 - 4;
        g.drawString(text, textX, textY);
    }

}
