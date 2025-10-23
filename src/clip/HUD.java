package clip;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class HUD {

    private final Spawner spawner;

    // Load images once, ensure they exist
    private final Image clipIcon = new ImageIcon(
            Objects.requireNonNull(getClass().getResource("/images/clipIcon.png"))
    ).getImage();
    private final Image bamboo = new ImageIcon(
            Objects.requireNonNull(getClass().getResource("/images/bamboo.png"))
    ).getImage();

    private final Font mainFont = new Font("TimesRoman", Font.BOLD, 24);
    private final Color textColor = Color.YELLOW;

    public HUD(Spawner spawner) {
        this.spawner = spawner;
    }

    public void tick() {
        // Currently empty, but can be used for animations or dynamic HUD updates
    }

    public void render(Graphics g) {
        g.drawImage(bamboo, 0, 0, null);

        g.setFont(mainFont);
        g.setColor(textColor);

        drawIconWithValue(g, clipIcon, 62, 70, spawner.getClips());

        if (spawner.getColoredUpgrade() < 100_000) {
            drawIconWithValue(g, clipIcon, 190, 115, spawner.getColoredUpgrade());
        }

        drawIconWithValue(g, clipIcon, 83, 210, spawner.getMoreUpgradePrice(), spawner.getMoreUpgradeCount(), 50, 232);
        drawIconWithValue(g, clipIcon, 194, 210, spawner.getValueUpgradePrice(), spawner.getValueUpgradeCount(), 160, 232);
    }

    /**
     * Draws an icon with a single value beside it.
     */
    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY, int value) {
        g.drawImage(icon, iconX, iconY, null);
        g.drawString(String.valueOf(value), iconX + 20, iconY + 23);
    }

    /**
     * Draws an icon with a value and a count string at specified coordinates.
     */
    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY, int price, int count, int countX, int countY) {
        g.drawImage(icon, iconX, iconY, null);
        g.drawString(String.valueOf(price), iconX + 15, iconY + 25);
        g.drawString("(" + count + ")", countX, countY);
    }
}
