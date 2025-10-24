package clip.ui;

import clip.core.GameObject;
import clip.core.ID;
import clip.core.ConfigManager;

import java.awt.*;

public class Menu extends GameObject {

    private final float widthPercent, heightPercent;     // size relative to window
    private final float bottomOffsetPercent;            // distance from bottom
    private final float spacingPercent;                 // vertical spacing between buttons
    private int currentWidth, currentHeight;            // actual pixel size after scaling
    private boolean visible = true;

    public Menu(float bottomOffsetPercent, float widthPercent, float heightPercent, float spacingPercent, ID id, ConfigManager config) {
        super(0, 0, id, config); // pass config from outside
        this.bottomOffsetPercent = bottomOffsetPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
        this.spacingPercent = spacingPercent;
    }

    // Update pixel position/size based on window dimensions and order
    public void updatePosition(int windowWidth, int windowHeight, int order) {
        currentWidth = (int) (windowWidth * widthPercent);
        currentHeight = (int) (windowHeight * heightPercent);

        x = (windowWidth - currentWidth) / 2;
        y = windowHeight - (int) (bottomOffsetPercent * windowHeight)
                - (currentHeight + (int) (spacingPercent * windowHeight)) * order;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, currentWidth, currentHeight);
    }

    @Override
    public void tick() {
        // Nothing needed for static menu buttons
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void render(Graphics g) {
        // Only render debug overlay if debugMode is enabled
        if (config != null && !config.debugMode) return;

        Color fillColor = switch (id) {
            case NEW_GAME -> new Color(0, 255, 0, 120);    // semi-transparent green
            case CONTINUE -> new Color(0, 0, 255, 120);    // semi-transparent blue
            case EXIT -> new Color(255, 0, 0, 120);        // semi-transparent red
            default -> new Color(255, 255, 255, 120);
        };

        g.setColor(fillColor);
        g.fillRect(x, y, currentWidth, currentHeight);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, currentWidth, currentHeight);

        // Draw centered text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int)(currentHeight * 0.4)));

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
