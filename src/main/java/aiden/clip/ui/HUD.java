package aiden.clip.ui;

import javax.swing.*;

import aiden.clip.core.ColorTier;
import aiden.clip.core.ConfigManager;
import aiden.clip.core.GameManager;
import aiden.clip.core.ID;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HUD implements MouseListener {

    private final ConfigManager config;
    private final GameManager gameManager;
    private final double scale;

    private final Image clipIcon;
    private final Image bamboo;

    private final Font mainFont;
    private final Color textColor = Color.WHITE;

    private final Map<ID, Image> upgradeImages = new HashMap<>();
    private final Map<ID, Rectangle> upgradeRects = new HashMap<>();

    // Base positions & sizes
    private static final int COLORED_X = 170, COLORED_Y = 70;
    private static final int VALUE_X = 170, VALUE_Y = 165;
    private static final int MORE_X = 60, MORE_Y = 165;
    private static final int IMAGE_SIZE = 70;
    private static final int HITBOX_SIZE = 60;

    public HUD(GameManager gameManager, ConfigManager config) {
        this.gameManager = gameManager;
        this.config = config;
        this.scale = config.hudScale;

        clipIcon = load("/images/clipIcon.png");
        bamboo = load("/images/bamboo.png");

        mainFont = new Font("TimesRoman", Font.BOLD, (int) (20 * scale));

        // Load upgrade images
        upgradeImages.put(ID.RED_UPGRADE, load("/images/redUpgrade.png"));
        upgradeImages.put(ID.GREEN_UPGRADE, load("/images/greenUpgrade.png"));
        upgradeImages.put(ID.BLUE_UPGRADE, load("/images/blueUpgrade.png"));
        upgradeImages.put(ID.PURPLE_UPGRADE, load("/images/purpleUpgrade.png"));
        upgradeImages.put(ID.YELLOW_UPGRADE, load("/images/yellowUpgrade.png"));
        upgradeImages.put(ID.VALUE_UPGRADE, load("/images/valueUpgrade.png"));
        upgradeImages.put(ID.MORE_UPGRADE, load("/images/moreUpgrade.png"));

        // Initial upgrade rectangles (centered on image)
        initHitboxes();
    }

    private void initHitboxes() {
        upgradeRects.put(ID.VALUE_UPGRADE, createHitbox(VALUE_X, VALUE_Y));
        upgradeRects.put(ID.MORE_UPGRADE, createHitbox(MORE_X, MORE_Y));

        // Colored upgrades will be dynamically added in drawColoredUpgrade
    }

    private Rectangle createHitbox(int baseX, int baseY) {
        int scaledImage = scaledSize(IMAGE_SIZE);
        int scaledHitbox = scaledSize(HITBOX_SIZE);
        int offset = (scaledImage - scaledHitbox) / 2;
        return new Rectangle(scaledX(baseX) + offset, scaledY(baseY) + offset, scaledHitbox, scaledHitbox);
    }

    private Image load(String path) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
    }

    private int scaledX(int x) { return (int) (x * scale); }
    private int scaledY(int y) { return (int) (y * scale); }
    private int scaledSize(int size) { return (int) (size * scale); }

    public void tick() {}

    public void render(Graphics g) {
        // Background
        g.drawImage(bamboo, 0, 0, (int)(bamboo.getWidth(null) * scale), (int)(bamboo.getHeight(null) * scale), null);

        g.setFont(mainFont);
        g.setColor(textColor);

        // Clip counter
        drawIconWithValue(g, clipIcon, scaledX(62), scaledY(80), gameManager.getClips());

        // Upgrades
        drawColoredUpgrade(g, COLORED_X, COLORED_Y);
        drawGenericUpgrade(g, ID.VALUE_UPGRADE, VALUE_X, VALUE_Y,
                gameManager.getValueUpgradePrice(), gameManager.getValueUpgradeCount());
        drawGenericUpgrade(g, ID.MORE_UPGRADE, MORE_X, MORE_Y,
                gameManager.getMoreUpgradePrice(), gameManager.getMoreUpgradeCount());

        if (config.debugMode) { // only show in debug mode
            g.setColor(Color.RED);
            for (Rectangle rect : upgradeRects.values()) {
                if (rect != null) {
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                }
            }
        }
    }

    private void drawColoredUpgrade(Graphics g, int x, int y) {
        ColorTier nextTier = gameManager.getColoredUpgrade() != null ? gameManager.getColoredUpgrade().next() : ColorTier.RED;
        if (nextTier == null || nextTier.getUpgradeID() == null) return;

        Image img = upgradeImages.get(nextTier.getUpgradeID());
        if (img == null) return;

        int cost = getUpgradeCost(nextTier);
        g.drawImage(img, scaledX(x), scaledY(y), scaledSize(IMAGE_SIZE), scaledSize(IMAGE_SIZE), null);

        drawIconWithValue(g, clipIcon, scaledX(x), scaledY(y), cost);

        // Update hitbox dynamically
        upgradeRects.put(nextTier.getUpgradeID(), createHitbox(x, y));
    }

    private int getUpgradeCost(ColorTier tier) {
        return switch (tier) {
            case RED -> config.redUpgradeCost;
            case GREEN -> config.greenUpgradeCost;
            case BLUE -> config.blueUpgradeCost;
            case PURPLE -> config.purpleUpgradeCost;
            case YELLOW -> config.yellowUpgradeCost;
            default -> 0;
        };
    }

    private void drawGenericUpgrade(Graphics g, ID id, int x, int y, int price, int count) {
        Image img = upgradeImages.get(id);
        if (img == null) return;

        g.drawImage(img, scaledX(x), scaledY(y), scaledSize(IMAGE_SIZE), scaledSize(IMAGE_SIZE), null);
        drawIconWithValue(g, clipIcon, scaledX(x), scaledY(y), price, count, scaledX(x - 34), scaledY(y + 22));

        // Hitbox already created in initHitboxes
    }

    private void drawIconWithValue(Graphics g, Image icon, int x, int y, int value) {
        g.drawImage(icon, x, y - scaledSize(18), null);
        g.drawString(String.valueOf(value), x + scaledSize(20), y + scaledSize(5));
    }

    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY,
                                   int price, int count, int countX, int countY) {
        g.drawImage(icon, iconX - scaledSize(5), iconY - scaledSize(20), null);

        // Keep price at original position, prepend count
        String text = "(" + count + ") " + price;
        g.drawString(text, countX + scaledSize(45), countY - scaledSize(25));  // countX/countY are where the price used to be
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
