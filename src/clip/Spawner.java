package clip;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Random;

public class Spawner {
    private final Handler handler;
    private final Random random;

    private final String path = "save.txt";

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
                id,
                handler
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
        handler.addObject(new Upgrade(175, 50, id, handler));
        coloredUpgrade = price;
    }

    public int getColoredUpgrade() { return coloredUpgrade; }
    public void setColoredUpgrade(int coloredUpgrade) { this.coloredUpgrade = coloredUpgrade; }

    // --- Value upgrade ---
    public void addValueUpgrade() {
        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE, handler));
        valueUpgradePrice = (int) (200 * Math.pow(2, valueUpgradeCount));
    }

    public int getValueUpgradePrice() { return valueUpgradePrice; }
    public int getValueUpgradeCount() { return valueUpgradeCount; }
    public void addValueUpgradeCount() { valueUpgradeCount++; }

    // --- More upgrade ---
    public void addMoreUpgrade() {
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE, handler));
        moreUpgradePrice = (int) (200 + 50 * Math.pow(2, moreUpgradeCount));
    }

    public int getMoreUpgradePrice() { return moreUpgradePrice; }
    public int getMoreUpgradeCount() { return moreUpgradeCount; }
    public void addMoreUpgradeCount() { moreUpgradeCount++; }

    // --- Game control ---
    public void newGame() {
        System.out.println("Game Restarted");
        clips = 0;
        currentClipCount = 0;
        maxClipCount = 25;
        coloredUpgrade = 100;
        addColoredUpgrade(ID.RED_UPGRADE, 100);
        addValueUpgrade();
        addMoreUpgrade();
    }

    public void continueGame() {
        System.out.println("Game Continued");
        List<Integer> saved = loadSaveFile();
        if (saved.size() >= 5) {
            clips = saved.get(0);
            maxClipCount = saved.get(1);
            coloredUpgrade = saved.get(2);
            valueUpgradeCount = saved.get(3);
            moreUpgradeCount = saved.get(4);

            // Restore colored upgrade object
            if (coloredUpgrade >= 100 && coloredUpgrade < 1000) addColoredUpgrade(ID.RED_UPGRADE, 100);
            else if (coloredUpgrade >= 1000 && coloredUpgrade < 5000) addColoredUpgrade(ID.GREEN_UPGRADE, 1000);
            else if (coloredUpgrade >= 5000 && coloredUpgrade < 10000) addColoredUpgrade(ID.BLUE_UPGRADE, 5000);
            else if (coloredUpgrade >= 10000 && coloredUpgrade < 50000) addColoredUpgrade(ID.PURPLE_UPGRADE, 10000);
            else if (coloredUpgrade >= 50000) addColoredUpgrade(ID.YELLOW_UPGRADE, 50000);

            addValueUpgrade();
            addMoreUpgrade();
        } else {
            newGame(); // fallback
        }
    }

    // --- Save/load helpers ---
    private List<Integer> loadSaveFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            return lines.stream().map(line -> {
                try { return Integer.parseInt(line.trim()); }
                catch (NumberFormatException e) { return 0; }
            }).toList();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            writer.write(clips + "\n");
            writer.write(maxClipCount + "\n");
            writer.write(coloredUpgrade + "\n");
            writer.write(valueUpgradeCount + "\n");
            writer.write(moreUpgradeCount + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
