package clip.ui;

import clip.core.ColorTier;
import clip.core.GameManager;
import clip.core.ID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HUD implements MouseListener {

    private final GameManager gameManager;

    private final Image clipIcon;
    private final Image bamboo;

    private final Font mainFont = new Font("TimesRoman", Font.BOLD, 24);
    private final Color textColor = Color.YELLOW;

    private final Map<ID, Image> upgradeImages = new HashMap<>();
    private final Map<ID, Rectangle> upgradeRects = new HashMap<>();

    // Upgrade positions
    private static final int COLORED_X = 170, COLORED_Y = 70;
    private static final int VALUE_X = 170, VALUE_Y = 165;
    private static final int MORE_X = 60, MORE_Y = 165;

    // Sizes
    private static final int IMAGE_SIZE = 70;   // drawn image size
    private static final int HITBOX_SIZE = 60;  // clickable area size
    private static final int HITBOX_PADDING = 10;

    public HUD(GameManager gameManager) {
        this.gameManager = gameManager;

        clipIcon = load("/images/clipIcon.png");
        bamboo = load("/images/bamboo.png");

        // Load upgrade images
        upgradeImages.put(ID.RED_UPGRADE, load("/images/redUpgrade.png"));
        upgradeImages.put(ID.GREEN_UPGRADE, load("/images/greenUpgrade.png"));
        upgradeImages.put(ID.BLUE_UPGRADE, load("/images/blueUpgrade.png"));
        upgradeImages.put(ID.PURPLE_UPGRADE, load("/images/purpleUpgrade.png"));
        upgradeImages.put(ID.YELLOW_UPGRADE, load("/images/yellowUpgrade.png"));
        upgradeImages.put(ID.VALUE_UPGRADE, load("/images/valueUpgrade.png"));
        upgradeImages.put(ID.MORE_UPGRADE, load("/images/moreUpgrade.png"));

        // Define static rectangles for upgrades
        upgradeRects.put(ID.VALUE_UPGRADE, new Rectangle(VALUE_X, VALUE_Y, HITBOX_SIZE, HITBOX_SIZE));
        upgradeRects.put(ID.MORE_UPGRADE, new Rectangle(MORE_X, MORE_Y, HITBOX_SIZE, HITBOX_SIZE));
        // Colored upgrade rectangle will be dynamic
    }

    private Image load(String path) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
    }

    public void tick() {
        // Optional animations
    }

    public void render(Graphics g) {
        g.drawImage(bamboo, 0, 0, null);
        g.setFont(mainFont);
        g.setColor(textColor);

//        // Optional: debug hitboxes
//        g.setColor(new Color(255, 0, 0, 128));
//        for (Rectangle rect : upgradeRects.values()) {
//            g.drawRect(rect.x, rect.y, rect.width, rect.height);
//        }

        // Clip counter
        drawIconWithValue(g, clipIcon, 62, 80, gameManager.getClips());

        // Draw upgrades in unified way
        drawUpgrade(g, gameManager.getColoredUpgrade() != null ? gameManager.getColoredUpgrade().next() : ColorTier.RED,
                COLORED_X);
        drawUpgrade(g, ID.VALUE_UPGRADE, VALUE_X, VALUE_Y, gameManager.getValueUpgradePrice(), gameManager.getValueUpgradeCount());
        drawUpgrade(g, ID.MORE_UPGRADE, MORE_X, MORE_Y, gameManager.getMoreUpgradePrice(), gameManager.getMoreUpgradeCount());
    }

    private void drawUpgrade(Graphics g, ColorTier tier, int x) {
        if (tier == null || tier.getUpgradeID() == null) return;

        Image img = upgradeImages.get(tier.getUpgradeID());
        if (img == null) return;

        g.drawImage(img, x, COLORED_Y, IMAGE_SIZE, IMAGE_SIZE, null);
        drawIconWithValue(g, clipIcon, x, COLORED_Y, tier.getValue());

        upgradeRects.put(tier.getUpgradeID(), new Rectangle(x, COLORED_Y, HITBOX_SIZE, HITBOX_SIZE));
    }

    private void drawUpgrade(Graphics g, ID id, int x, int y, int price, int count) {
        Image img = upgradeImages.get(id);
        if (img == null) return;

        g.drawImage(img, x, y, IMAGE_SIZE, IMAGE_SIZE, null);
        drawIconWithValue(g, clipIcon, x, y, price, count, x - 34, y + 22);

        upgradeRects.put(id, new Rectangle(x, y, HITBOX_SIZE, HITBOX_SIZE));
    }

    private void drawIconWithValue(Graphics g, Image icon, int x, int y, int value) {
        g.drawImage(icon, x, y - 18, null);
        g.drawString(String.valueOf(value), x + 20, y + 5);
    }

    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY,
                                   int price, int count, int countX, int countY) {
        g.drawImage(icon, iconX - 10, iconY - 25, null);
        g.drawString("(" + count + ")", countX + 40, countY - 25);
        g.drawString(String.valueOf(price), iconX + 40, iconY);
    }

    // ---------------- MouseListener -----------------
    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        for (Map.Entry<ID, Rectangle> entry : upgradeRects.entrySet()) {
            if (entry.getValue() != null && entry.getValue().contains(mx, my)) {
                ID id = entry.getKey();
                switch (id) {
                    case VALUE_UPGRADE -> gameManager.buyValueUpgradeFromHUD();
                    case MORE_UPGRADE -> gameManager.buyMoreUpgradeFromHUD();
                    default -> gameManager.buyColoredUpgradeFromHUD();
                }
                return;
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
