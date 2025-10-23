package clip.ui;

import clip.core.GameManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class HUD {

    private final GameManager gameManager;

    private final Image clipIcon = new ImageIcon(
            Objects.requireNonNull(getClass().getResource("/images/clipIcon.png"))
    ).getImage();
    private final Image bamboo = new ImageIcon(
            Objects.requireNonNull(getClass().getResource("/images/bamboo.png"))
    ).getImage();

    private final Font mainFont = new Font("TimesRoman", Font.BOLD, 24);
    private final Color textColor = Color.YELLOW;

    public HUD(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void tick() {
        // Could animate HUD elements here if desired
    }

    public void render(Graphics g) {
        g.drawImage(bamboo, 0, 0, null);

        g.setFont(mainFont);
        g.setColor(textColor);

        // Pull everything from GameManager
        drawIconWithValue(g, clipIcon, 62, 70, gameManager.getClips());

        if (gameManager.getColoredUpgrade() < 100_000) {
            drawIconWithValue(g, clipIcon, 190, 115, gameManager.getColoredUpgrade());
        }

        drawIconWithValue(g, clipIcon, 83, 210,
                gameManager.getMoreUpgradePrice(),
                gameManager.getMoreUpgradeCount(),
                50, 232);

        drawIconWithValue(g, clipIcon, 194, 210,
                gameManager.getValueUpgradePrice(),
                gameManager.getValueUpgradeCount(),
                160, 232);
    }

    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY, int value) {
        g.drawImage(icon, iconX, iconY, null);
        g.drawString(String.valueOf(value), iconX + 20, iconY + 23);
    }

    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY, int price, int count, int countX, int countY) {
        g.drawImage(icon, iconX, iconY, null);
        g.drawString(String.valueOf(price), iconX + 15, iconY + 25);
        g.drawString("(" + count + ")", countX, countY);
    }
}
