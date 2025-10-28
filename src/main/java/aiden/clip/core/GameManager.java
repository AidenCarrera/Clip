package aiden.clip.core;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aiden.clip.entities.Paperclip;
import aiden.clip.entities.Spawner;
import aiden.clip.entities.Upgrade;
import aiden.clip.save.SaveManager;
import aiden.clip.ui.Menu;

public class GameManager {

    private final Handler handler;
    private final SaveManager saveManager;
    private final Spawner spawner;
    private final Random random;
    private final ConfigManager config;

    // Window scaling factors (runtime only)
    private final float windowScaleX;
    private final float windowScaleY;

    // Game state
    private int clips;
    private int currentClipCount;
    private int maxClipCount;

    private ColorTier coloredUpgrade;
    private int valueUpgradeCount;
    private int moreUpgradeCount;

    // GameState
    private GameState state;

    // Menu buttons
    private List<Menu> menuButtons;

    public GameManager(Handler handler, ConfigManager config, float windowScaleX, float windowScaleY) {
        this.handler = handler;
        this.config = config;
        this.windowScaleX = windowScaleX;
        this.windowScaleY = windowScaleY;
        this.saveManager = new SaveManager();
        this.random = new Random();
        this.spawner = new Spawner(handler, random, config);

        this.state = GameState.MENU;
        this.menuButtons = new ArrayList<>();

        showMenuButtons(); // initially show menu
    }

    // --- Menu management ---
    public void showMenuButtons() {
        if (!menuButtons.isEmpty())
            return; // prevent duplicates

        int width = config.displayWidth;
        int height = config.displayHeight;

        Menu newGameBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.NEW_GAME, config);
        Menu continueBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.CONTINUE, config);
        Menu exitBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.EXIT, config);

        newGameBtn.updatePosition(width, height, 2);
        continueBtn.updatePosition(width, height, 1);
        exitBtn.updatePosition(width, height, 0);

        menuButtons.add(newGameBtn);
        menuButtons.add(continueBtn);
        menuButtons.add(exitBtn);

        for (Menu btn : menuButtons)
            handler.addObject(btn);
    }

    public void hideMenuButtons() {
        for (Menu btn : menuButtons)
            handler.removeObject(btn);
        menuButtons.clear();
    }

    // --- Game control ---
    public void startNewGame() {
        System.out.println("Game Restarted");
        state = GameState.GAME;

        hideMenuButtons();

        clips = config.startClips;
        currentClipCount = 0;
        maxClipCount = config.maxClipCount;

        coloredUpgrade = ColorTier.NONE;
        valueUpgradeCount = 0;
        moreUpgradeCount = 0;

        // Add initial upgrade objects
        handler.addObject(new Upgrade(175, 50, ColorTier.RED.getUpgradeID(), config));
        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE, config));
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE, config));
    }

    public void continueGame() {
        System.out.println("Loading previous game...");
        state = GameState.GAME;
        hideMenuButtons();

        if (!saveManager.load(this)) {
            System.out.println("No save found — starting new game.");
            startNewGame();
        }
    }

    public void saveGame() {
        if (coloredUpgrade == null) {
            System.out.println("No game state to save yet.");
            return;
        }
        saveManager.save(this);
    }

    // --- Gameplay actions ---
    public void collectClip(GameObject clip) {
        int baseValue = switch (clip.getID()) {
            case PAPERCLIP -> config.paperclipBaseValue;
            case RED_PAPERCLIP -> config.paperclipBaseValue * 5;
            case GREEN_PAPERCLIP -> config.paperclipBaseValue * 25;
            case BLUE_PAPERCLIP -> config.paperclipBaseValue * 100;
            case PURPLE_PAPERCLIP -> config.paperclipBaseValue * 1000;
            case YELLOW_PAPERCLIP -> config.paperclipBaseValue * 10000;
            default -> 0;
        };

        clips += baseValue * (valueUpgradeCount + 1);
        currentClipCount--;
        handler.removeObject(clip);
    }

    // --- Upgrade prices ---
    public int getColoredUpgradePrice(ColorTier tier) {
        return switch (tier) {
            case RED -> config.redUpgradeCost;
            case GREEN -> config.greenUpgradeCost;
            case BLUE -> config.blueUpgradeCost;
            case PURPLE -> config.purpleUpgradeCost;
            case YELLOW -> config.yellowUpgradeCost;
            default -> 0;
        };
    }

    public int getValueUpgradePrice() {
        return (int) (config.valueUpgradeBaseCost * Math.pow(config.upgradeCostMultiplier, valueUpgradeCount));
    }

    public int getMoreUpgradePrice() {
        return (int) (config.moreUpgradeBaseCost * Math.pow(config.upgradeCostMultiplier, moreUpgradeCount));
    }

    // --- Upgrade methods ---
    public void buyColoredUpgradeFromHUD() {
        ColorTier nextTier = coloredUpgrade.next();
        if (nextTier != null) {
            int price = getColoredUpgradePrice(nextTier);
            if (clips >= price) {
                clips -= price;
                coloredUpgrade = nextTier;
                System.out.println("Bought " + nextTier + " upgrade for " + price + " clips");
            } else {
                System.out.println("Not enough clips for " + nextTier + ". Price: " + price);
            }
        }
    }

    public void buyValueUpgradeFromHUD() {
        int price = getValueUpgradePrice();
        if (clips >= price) {
            clips -= price;
            valueUpgradeCount++;
            System.out.println("Bought value upgrade. New count: " + valueUpgradeCount);
        } else {
            System.out.println("Not enough clips for value upgrade.");
        }
    }

    public void buyMoreUpgradeFromHUD() {
        int price = getMoreUpgradePrice();
        if (clips >= price) {
            clips -= price;
            maxClipCount++;
            moreUpgradeCount++;
            System.out.println("Bought more upgrade. Max clips: " + maxClipCount);
        } else {
            System.out.println("Not enough clips for more upgrade.");
        }
    }

    // --- Game ticking / spawning ---
    public void tick() {
        if (state != GameState.GAME)
            return;

        // --- Compute HUD offset (scale applied) ---
        int hudWidth = (int) (400 * windowScaleX); // horizontal HUD margin
        int hudHeight = (int) (400 * windowScaleY); // optional vertical HUD margin

        // --- Determine max spawn bounds based on display and window scale ---
        int maxX = (int) (config.displayWidth * windowScaleX);
        int maxY = (int) (config.displayHeight * windowScaleY);

        // --- Compute paperclip size dynamically using Paperclip helper ---
        Dimension clipSize = Paperclip.getScaledSize(config, windowScaleX, windowScaleY);
        int paperclipWidth = clipSize.width;
        int paperclipHeight = clipSize.height;

        // --- Spawn bounds with margins ---
        int spawnMinX = hudWidth;
        int spawnMinY = 0; // or hudHeight if vertical HUD
        int spawnMaxX = Math.max(spawnMinX + 1, maxX - paperclipWidth);
        int spawnMaxY = Math.max(spawnMinY + 1, maxY - paperclipHeight);

        // --- Spawn paperclips while under max count ---
        while (currentClipCount < maxClipCount) {
            // --- Determine spawn type using weighted random ---
            double P = 69;
            double redP = config.redSpawnWeight;
            double greenP = config.greenSpawnWeight;
            double blueP = config.blueSpawnWeight;
            double purpleP = config.purpleSpawnWeight;
            double yellowP = config.yellowSpawnWeight;

            double totalWeight = P + redP + greenP + blueP + purpleP + yellowP;
            double roll = random.nextDouble() * totalWeight;

            ID spawnType;
            if (roll < yellowP && coloredUpgrade.ordinal() >= ColorTier.YELLOW.ordinal())
                spawnType = ID.YELLOW_PAPERCLIP;
            else if (roll < yellowP + purpleP && coloredUpgrade.ordinal() >= ColorTier.PURPLE.ordinal())
                spawnType = ID.PURPLE_PAPERCLIP;
            else if (roll < yellowP + purpleP + blueP && coloredUpgrade.ordinal() >= ColorTier.BLUE.ordinal())
                spawnType = ID.BLUE_PAPERCLIP;
            else if (roll < yellowP + purpleP + blueP + greenP && coloredUpgrade.ordinal() >= ColorTier.GREEN.ordinal())
                spawnType = ID.GREEN_PAPERCLIP;
            else if (roll < yellowP + purpleP + blueP + greenP + redP
                    && coloredUpgrade.ordinal() >= ColorTier.RED.ordinal())
                spawnType = ID.RED_PAPERCLIP;
            else
                spawnType = ID.PAPERCLIP;

            // --- Random position within safe bounds ---
            int spawnX = spawnMinX + random.nextInt(Math.max(1, spawnMaxX - spawnMinX));
            int spawnY = spawnMinY + random.nextInt(Math.max(1, spawnMaxY - spawnMinY));

            // --- Spawn the paperclip ---
            spawner.spawnClip(spawnType, spawnX, spawnY, windowScaleX, windowScaleY);

            currentClipCount++;
        }
    }


    // --- Getters / setters for saving ---
    public int getClips() {
        return clips;
    }

    public void setClips(int clips) {
        this.clips = clips;
    }

    public int getCurrentClipCount() {
        return currentClipCount;
    }

    public void setCurrentClipCount(int count) {
        this.currentClipCount = count;
    }

    public int getMaxClipCount() {
        return maxClipCount;
    }

    public void setMaxClipCount(int maxClipCount) {
        this.maxClipCount = maxClipCount;
    }

    public ColorTier getColoredUpgrade() {
        return coloredUpgrade;
    }

    public void setColoredUpgrade(ColorTier coloredUpgrade) {
        this.coloredUpgrade = coloredUpgrade;
    }

    public int getValueUpgradeCount() {
        return valueUpgradeCount;
    }

    public void setValueUpgradeCount(int count) {
        this.valueUpgradeCount = count;
    }

    public int getMoreUpgradeCount() {
        return moreUpgradeCount;
    }

    public void setMoreUpgradeCount(int count) {
        this.moreUpgradeCount = count;
    }

    public Handler getHandler() {
        return handler;
    }

    public ConfigManager getConfig() {
        return config;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        if (state == GameState.MENU)
            showMenuButtons();
        else
            hideMenuButtons();
    }

    public float getWindowScaleX() {
        return windowScaleX;
    }

    public float getWindowScaleY() {
        return windowScaleY;
    }
}
