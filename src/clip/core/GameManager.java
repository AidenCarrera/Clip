package clip.core;

import clip.entities.Spawner;
import clip.save.SaveManager;

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

    public GameManager(Handler handler, ConfigManager config) {
        this.handler = handler;
        this.config = config;
        this.saveManager = new SaveManager("data/save.json");
        this.random = new Random();
        this.spawner = new Spawner(handler, random, config);

        // Don't start a new game here—only load or start explicitly
    }

    // --- Game control ---
    public void startNewGame() {
        System.out.println("Game Restarted");

        clips = config.startClips;
        currentClipCount = 0;
        maxClipCount = config.maxClipCount;

        coloredUpgrade = ColorTier.NONE;
        valueUpgradeCount = 0;
        moreUpgradeCount = 0;

        // Add initial upgrade objects
        handler.addObject(new clip.entities.Upgrade(175, 50, ColorTier.RED.getUpgradeID()));
        handler.addObject(new clip.entities.Upgrade(175, 145, ID.VALUE_UPGRADE));
        handler.addObject(new clip.entities.Upgrade(65, 145, ID.MORE_UPGRADE));
    }

    public void continueGame() {
        System.out.println("Loading previous game...");
        if (!saveManager.load(this)) {
            System.out.println("No save found — starting new game.");
            startNewGame();
        }
    }

    public void saveGame() {
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
}