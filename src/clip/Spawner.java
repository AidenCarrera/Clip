package clip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Spawner {
    private Handler handler;
    private Random random;
    // private String path = "C:/Users/Aiden Carrera/OneDrive - Union Public Schools, ISD 9/CompSci/Intro/Projects/Clip/save.txt";
    // private String path = "C:/Users/s081328/OneDrive - Union Public Schools, ISD 9/CompSci/Intro/Projects/Clip/save.txt";
    private String path = "C:/Users/aiden/OneDrive - Oklahoma A and M System/Programming/CompSci/Intro/Projects/Clip/save.txt";
    private int clips, currentClipCount, maxClipCount, coloredUpgrade, valueUpgradeCount, valueUpgradePrice, moreUpgradeCount, moreUpgradePrice;
    public Spawner(Handler handler, Random random) {
        this.handler = handler;
        this.random = random;
    }
    public void tick() {
        while (getCurrentClipCount() < getMaxClipCount()) {
            double P = 69;
            double redP = 15;
            double greenP = 7.85;
            double blueP = 4.25;
            double purpleP = 2.6;
            double yellowP = 1;
            double totalWeight = P + redP + greenP + blueP + purpleP + yellowP;
            double clipToSpawn = random.nextDouble() * totalWeight;
            if (clipToSpawn < yellowP && getColoredUpgrade() == 100000) {
                createPaperclip(ID.YELLOW_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP && getColoredUpgrade() >= 50000) {
                createPaperclip(ID.PURPLE_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP + blueP && getColoredUpgrade() >= 10000) {
                createPaperclip(ID.BLUE_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP + blueP + greenP && getColoredUpgrade() >= 5000) {
                createPaperclip(ID.GREEN_PAPERCLIP);
            } else if (clipToSpawn < yellowP + purpleP + blueP + greenP + redP && getColoredUpgrade() >= 1000) {
                createPaperclip(ID.RED_PAPERCLIP);
            } else {
                createPaperclip(ID.PAPERCLIP);
            }
        }
    }
    // Create Paperclip objects
    public void createPaperclip(ID id) {
        handler.addObject(new Paperclip(random.nextInt(Game.WIDTH - 34 - 700) + 250, random.nextInt(Game.HEIGHT - 100 - 500) + 150, id, handler));
        addClipCount();
    }
    // Clips
    public int getClips() {
        return clips;
    }
    public void setClips(int clips) {
        this.clips = clips;
    }
    public void addClips(int clips) {
        this.clips = getClips() + clips;
    }
    public void removeClips(int clips) {
        this.clips = getClips() - clips;
    }
    // Current Clips on screen
    public int getCurrentClipCount() {
        return currentClipCount;
    }
    public void setCurrentClipCount(int currentClipCount) {
        this.currentClipCount = currentClipCount;
    }
    public void addClipCount() {
        this.currentClipCount++;
    }
    public void lowerClipCount() {
        this.currentClipCount--;
    }
    // Maximum Clips on screen
    public int getMaxClipCount() {
        return maxClipCount;
    }
    public void setMaxClipCount(int maxClipCount) {
        this.maxClipCount = maxClipCount;
    }
    public void addMaxClipCount() {
        this.maxClipCount++;
    }
    // Adds ColoredUpgrade based on ID
    public void addColoredUpgrade(ID id, int price) {
        handler.addObject(new Upgrade(175, 50, id, handler));
        setColoredUpgrade(price);
    }
    public int getColoredUpgrade() {
        return coloredUpgrade;
    }
    public void setColoredUpgrade(int coloredUpgrade) {
        this.coloredUpgrade = coloredUpgrade;
    }
    // Adds ValueUpgrade and sets Price
    public void addValueUpgrade() {
        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE, handler));
        setValueUpgradePrice((int) (200 * Math.pow(2, getValueUpgradeCount())));
    }
    public int getValueUpgradePrice() {
        return valueUpgradePrice;
    }
    public int getValueUpgradeCount() {
        return valueUpgradeCount;
    }
    public void setValueUpgradePrice(int valueUpgradePrice) {
        this.valueUpgradePrice = valueUpgradePrice;
    }
    public void setValueUpgradeCount(int valueUpgradeCount) {
        this.valueUpgradeCount = valueUpgradeCount;
    }
    public void addValueUpgradeCount() {
        this.valueUpgradeCount++;
    }
    // Adds MoreUpgrade and sets Price
    public void addMoreUpgrade() {
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE, handler));
        setMoreUpradePrice((int) (200 + 50 * Math.pow(2, getMoreUpgradeCount())));
    }
    public int getMoreUpgradePrice() {
        return moreUpgradePrice;
    }
    private void setMoreUpradePrice(int moreUpgradePrice) {
        this.moreUpgradePrice = moreUpgradePrice;
    }
    public int getMoreUpgradeCount() {
        return moreUpgradeCount;
    }
    private void setMoreUpgradeCount(int moreUpgradeCount) {
        this.moreUpgradeCount = moreUpgradeCount;
    }
    public void addMoreUpgradeCount() {
        this.moreUpgradeCount++;
    }
    // Runs when newGame is clicked
    public void newGame() {
        System.out.println("Game Restarted");
        setClips(0);
        setCurrentClipCount(0);
        setMaxClipCount(25);
        setColoredUpgrade(100);
        addColoredUpgrade(ID.RED_UPGRADE, 100);
        addValueUpgrade();
        addMoreUpgrade();
    }
    public void continueGame() {
        setClips(getSavedClips());
        setCurrentClipCount(0);
        setMaxClipCount(getSavedMaxClips());
        setColoredUpgrade(getSavedColoredUpgrade());
        int upgrades = getSavedColoredUpgrade();
        if (upgrades == 100) {
            addColoredUpgrade(ID.RED_UPGRADE, 100);
        }
        else if(upgrades == 1000) {
            addColoredUpgrade(ID.GREEN_UPGRADE, 1000);
        }
        else if (upgrades == 5000) {
            addColoredUpgrade(ID.BLUE_UPGRADE, 5000);
        }
        else if (upgrades == 10000) {
            addColoredUpgrade(ID.PURPLE_UPGRADE, 10000);
        }
        else if (upgrades == 50000) {
            addColoredUpgrade(ID.YELLOW_UPGRADE, 50000);
        }
        setValueUpgradeCount(getSavedValueUpgrade());
        addValueUpgrade();
        setMoreUpgradeCount(getSavedMoreUpgrade());
        addMoreUpgrade();
    }
    public int getSavedClips() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            // Stores the line as a String
            String line = reader.readLine();
            reader.close();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Default value if file reading fails or the first line is empty
        return 0;
    }
    public int getSavedMaxClips() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            // Skips the line
            reader.readLine();
            String line = reader.readLine();
            reader.close();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 25; 
    }
    
    public int getSavedColoredUpgrade() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            reader.readLine();
            reader.readLine();
            String line = reader.readLine();
            reader.close();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 100;
    }
    private int getSavedValueUpgrade() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            reader.readLine();
            reader.readLine();
            reader.readLine();
            String line = reader.readLine();
            reader.close();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private int getSavedMoreUpgrade() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            String line = reader.readLine();
            reader.close();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
            // Saves variables to lines in txt file
            writer.write(clips + "\n");
            writer.write(maxClipCount + "\n");
            writer.write(coloredUpgrade + "\n");
            writer.write(valueUpgradeCount + "\n");
            writer.write(moreUpgradeCount + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}