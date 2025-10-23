package clip.core;

import clip.entities.Spawner;
import clip.save.SaveManager;

import java.util.Random;

// Central manager for game state, upgrades, and saving/loading
public class GameManager {
    private final Handler handler;
    private final SaveManager saveManager;
    private final Spawner spawner;
    private final Random random;

    // Game state
    private int clips;
    private int currentClipCount;
    private int maxClipCount;

    private ColorTier coloredUpgrade;
    private int valueUpgradeCount;
    private int moreUpgradeCount;

    public GameManager(Handler handler) {
        this.handler = handler;
        this.saveManager = new SaveManager("data/save.json");
        this.random = new Random();
        this.spawner = new Spawner(handler, random);

        startNewGame();
    }

    // --- Game control ---
    public void startNewGame() {
        System.out.println("Game Restarted");

        clips = 0;
        currentClipCount = 0;
        maxClipCount = 25;

        coloredUpgrade = ColorTier.NONE; // start with no upgrade
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
        // No recalculation needed — prices are dynamic now
    }

    public void saveGame() {
        saveManager.save(this);
    }

    public Spawner getSpawner() { return spawner; }
    public Random getRandom() { return random; }

    // --- Gameplay actions ---
    public void collectClip(GameObject clip) {
        int value = switch (clip.getID()) {
            case PAPERCLIP -> 1;
            case RED_PAPERCLIP -> 5;
            case GREEN_PAPERCLIP -> 25;
            case BLUE_PAPERCLIP -> 100;
            case PURPLE_PAPERCLIP -> 1000;
            case YELLOW_PAPERCLIP -> 10000;
            default -> 0;
        };

        clips += value * (valueUpgradeCount + 1);
        currentClipCount--;
        handler.removeObject(clip);
    }

    // --- Upgrade methods ---
    public void buyColoredUpgradeFromHUD() {
        ColorTier nextTier = coloredUpgrade.next();
        if (nextTier != null && clips >= nextTier.getValue()) {
            clips -= nextTier.getValue();
            coloredUpgrade = nextTier;
            System.out.println("Bought colored upgrade: " + nextTier);
        } else {
            System.out.println("Not enough clips for colored upgrade.");
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

    // --- Dynamic price calculation ---
    public int getValueUpgradePrice() {
        return 200 * (int)Math.pow(2, valueUpgradeCount);
    }

    public int getMoreUpgradePrice() {
        return 200 + 50 * (int)Math.pow(2, moreUpgradeCount);
    }

    // --- Game ticking / spawning ---
    public void tick() {
        while (currentClipCount < maxClipCount) {
            double P = 69, redP = 15, greenP = 7.85, blueP = 4.25, purpleP = 2.6, yellowP = 1;
            double totalWeight = P + redP + greenP + blueP + purpleP + yellowP;

            double clipToSpawn = random.nextDouble() * totalWeight;

            if (clipToSpawn < yellowP && coloredUpgrade.ordinal() >= ColorTier.YELLOW.ordinal()) {
                spawner.spawnClip(ID.YELLOW_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP && coloredUpgrade.ordinal() >= ColorTier.PURPLE.ordinal()) {
                spawner.spawnClip(ID.PURPLE_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP + blueP && coloredUpgrade.ordinal() >= ColorTier.BLUE.ordinal()) {
                spawner.spawnClip(ID.BLUE_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP + blueP + greenP && coloredUpgrade.ordinal() >= ColorTier.GREEN.ordinal()) {
                spawner.spawnClip(ID.GREEN_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP + blueP + greenP + redP && coloredUpgrade.ordinal() >= ColorTier.RED.ordinal()) {
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
