package clip;

import java.util.Random;

public class Spawner {
    private final Handler handler;
    private final Random random;

    private int clips;
    private int currentClipCount;
    private int maxClipCount;
    private int coloredUpgrade;
    private int valueUpgradeCount;
    private int valueUpgradePrice;
    private int moreUpgradeCount;
    private int moreUpgradePrice;

    public Spawner(Handler handler, Random random) {
        this.handler = handler;
        this.random = random;
    }

    public void tick() {
        while (currentClipCount < maxClipCount) {
            double P = 69, redP = 15, greenP = 7.85, blueP = 4.25, purpleP = 2.6, yellowP = 1;
            double totalWeight = P + redP + greenP + blueP + purpleP + yellowP;
            double clipToSpawn = random.nextDouble() * totalWeight;

            if (clipToSpawn < yellowP && coloredUpgrade == 100000) createPaperclip(ID.YELLOW_PAPERCLIP);
            else if (clipToSpawn < yellowP + purpleP && coloredUpgrade >= 50000) createPaperclip(ID.PURPLE_PAPERCLIP);
            else if (clipToSpawn < yellowP + purpleP + blueP && coloredUpgrade >= 10000) createPaperclip(ID.BLUE_PAPERCLIP);
            else if (clipToSpawn < yellowP + purpleP + blueP + greenP && coloredUpgrade >= 5000) createPaperclip(ID.GREEN_PAPERCLIP);
            else if (clipToSpawn < yellowP + purpleP + blueP + greenP + redP && coloredUpgrade >= 1000) createPaperclip(ID.RED_PAPERCLIP);
            else createPaperclip(ID.PAPERCLIP);
        }
    }

    public void createPaperclip(ID id) {
        handler.addObject(new Paperclip(
                random.nextInt(Game.WIDTH - 34 - 700) + 250,
                random.nextInt(Game.HEIGHT - 100 - 500) + 150,
                id
        ));
        addClipCount();
    }

    // --- Clip management ---
    public int getClips() { return clips; }
    public void setClips(int clips) { this.clips = clips; }
    public void addClips(int amount) { this.clips += amount; }
    public void removeClips(int amount) { this.clips -= amount; }

    public int getCurrentClipCount() { return currentClipCount; }
    public void setCurrentClipCount(int count) { this.currentClipCount = count; }
    public void addClipCount() { currentClipCount++; }
    public void lowerClipCount() { currentClipCount--; }

    public int getMaxClipCount() { return maxClipCount; }
    public void setMaxClipCount(int maxClipCount) { this.maxClipCount = maxClipCount; }
    public void addMaxClipCount() { maxClipCount++; }

    // --- Colored upgrade ---
    public void addColoredUpgrade(ID id, int price) {
        handler.addObject(new Upgrade(175, 50, id));
        coloredUpgrade = price;
    }

    public int getColoredUpgrade() { return coloredUpgrade; }
    public void setColoredUpgrade(int coloredUpgrade) { this.coloredUpgrade = coloredUpgrade; }

    // --- Value upgrade ---
    public void addValueUpgrade() {
        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE));
        valueUpgradePrice = (int) (200 * Math.pow(2, valueUpgradeCount));
    }

    public int getValueUpgradePrice() { return valueUpgradePrice; }
    public int getValueUpgradeCount() { return valueUpgradeCount; }
    public void addValueUpgradeCount() { valueUpgradeCount++; }
    public void setValueUpgradeCount(int count) { this.valueUpgradeCount = count; }


    // --- More upgrade ---
    public void addMoreUpgrade() {
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE));
        moreUpgradePrice = (int) (200 + 50 * Math.pow(2, moreUpgradeCount));
    }

    public int getMoreUpgradePrice() { return moreUpgradePrice; }
    public int getMoreUpgradeCount() { return moreUpgradeCount; }
    public void addMoreUpgradeCount() { moreUpgradeCount++; }
    public void setMoreUpgradeCount(int count) { this.moreUpgradeCount = count; }

}
