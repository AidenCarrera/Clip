package clip.core;

import clip.entities.Spawner;
import clip.entities.Upgrade;
import clip.save.SaveManager;
import clip.ui.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private final Handler handler;
    private final SaveManager saveManager;
    private final Spawner spawner;
    private final Random random;
    private final ConfigManager config;

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

    public GameManager(Handler handler, ConfigManager config) {
        this.handler = handler;
        this.config = config;
        this.saveManager = new SaveManager("data/save.json");
        this.random = new Random();
        this.spawner = new Spawner(handler, random, config);

        this.state = GameState.MENU;
        this.menuButtons = new ArrayList<>();

        showMenuButtons(); // initially show menu
    }

    // --- Menu management ---
    public void showMenuButtons() {
        if (!menuButtons.isEmpty()) return; // prevent duplicates

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

        for (Menu btn : menuButtons) {
            handler.addObject(btn);
        }
    }

    public void hideMenuButtons() {
        for (Menu btn : menuButtons) {
            handler.removeObject(btn);
        }
        menuButtons.clear();
    }

    // --- Game control ---
    public void startNewGame() {
        System.out.println("Game Restarted");

        state = GameState.GAME;

        hideMenuButtons(); // remove menu buttons when game starts

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
        if (state != GameState.GAME) return;

        while (currentClipCount < maxClipCount) {
            double P = 69;
            double redP = config.redSpawnWeight;
            double greenP = config.greenSpawnWeight;
            double blueP = config.blueSpawnWeight;
            double purpleP = config.purpleSpawnWeight;
            double yellowP = config.yellowSpawnWeight;

            double totalWeight = P + redP + greenP + blueP + purpleP + yellowP;
            double roll = random.nextDouble() * totalWeight;

            if (roll < yellowP && coloredUpgrade.ordinal() >= ColorTier.YELLOW.ordinal()) {
                spawner.spawnClip(ID.YELLOW_PAPERCLIP);
            } else if (roll < yellowP + purpleP && coloredUpgrade.ordinal() >= ColorTier.PURPLE.ordinal()) {
                spawner.spawnClip(ID.PURPLE_PAPERCLIP);
            } else if (roll < yellowP + purpleP + blueP && coloredUpgrade.ordinal() >= ColorTier.BLUE.ordinal()) {
                spawner.spawnClip(ID.BLUE_PAPERCLIP);
            } else if (roll < yellowP + purpleP + blueP + greenP && coloredUpgrade.ordinal() >= ColorTier.GREEN.ordinal()) {
                spawner.spawnClip(ID.GREEN_PAPERCLIP);
            } else if (roll < yellowP + purpleP + blueP + greenP + redP && coloredUpgrade.ordinal() >= ColorTier.RED.ordinal()) {
                spawner.spawnClip(ID.RED_PAPERCLIP);
            } else {
                spawner.spawnClip(ID.PAPERCLIP);
            }

            currentClipCount++;
        }
    }

    // --- Getters / setters for saving ---
    public int getClips() { return clips; }
    public void setClips(int clips) { this.clips = clips; }

    public int getCurrentClipCount() { return currentClipCount; }
    public void setCurrentClipCount(int count) { this.currentClipCount = count; }

    public int getMaxClipCount() { return maxClipCount; }
    public void setMaxClipCount(int maxClipCount) { this.maxClipCount = maxClipCount; }

    public ColorTier getColoredUpgrade() { return coloredUpgrade; }
    public void setColoredUpgrade(ColorTier coloredUpgrade) { this.coloredUpgrade = coloredUpgrade; }

    public int getValueUpgradeCount() { return valueUpgradeCount; }
    public void setValueUpgradeCount(int count) { this.valueUpgradeCount = count; }

    public int getMoreUpgradeCount() { return moreUpgradeCount; }
    public void setMoreUpgradeCount(int count) { this.moreUpgradeCount = count; }

    public Handler getHandler() { return handler; }
    public ConfigManager getConfig() { return config; }
    public GameState getState() { return state; }
    public void setState(GameState state) {
        this.state = state;
        if (state == GameState.MENU) showMenuButtons();
        else hideMenuButtons();
    }
}