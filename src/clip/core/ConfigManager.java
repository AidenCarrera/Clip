package clip.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {

    private final String path;

    // --- Defaults ---
    public int startClips = 0;
    public int maxClipCount = 25;
    public int coloredUpgradeStart = 100;
    public double spawnRate = 1.5;
    public double upgradeCostMultiplier = 1.2;

    public int redUpgradeCost = 100;
    public int greenUpgradeCost = 1000;
    public int blueUpgradeCost = 5000;
    public int purpleUpgradeCost = 50000;
    public int yellowUpgradeCost = 250000;

    public int valueUpgradeBaseCost = 100;
    public int moreUpgradeBaseCost = 50;

    public double musicVolume = 0.3;
    public double soundEffectsVolume = 0.7;

    public int displayWidth = 2560;
    public int displayHeight = 1440;
    public boolean fullscreen = true;

    public int autoSaveIntervalSeconds = 15;
    public int maxSaveFiles = 5;
    public boolean debugMode = false;

    public int paperclipBaseValue = 1;
    // public double paperclipRareSpawnChance = 0.05;
    public double basicPaperclipWeight = 69;
    public double redSpawnWeight = 15;
    public double greenSpawnWeight = 7.85;
    public double blueSpawnWeight = 4.25;
    public double purpleSpawnWeight = 2.6;
    public double yellowSpawnWeight = 1;

    public ConfigManager(String path) {
        this.path = path;
    }

    public void load() {
        try {
            String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            content = content.replaceAll("[\\{\\}\\s\"]", ""); // crude but simple strip

            String[] parts = content.split(",");
            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length < 2) continue;

                String key = kv[0];
                String val = kv[1];

                try {
                    switch (key) {
                        // game
                        case "startClips" -> startClips = Integer.parseInt(val);
                        case "maxClipCount" -> maxClipCount = Integer.parseInt(val);
                        case "coloredUpgradeStart" -> coloredUpgradeStart = Integer.parseInt(val);
                        case "spawnRate" -> spawnRate = Double.parseDouble(val);
                        case "upgradeCostMultiplier" -> upgradeCostMultiplier = Double.parseDouble(val);

                        // upgrades
                        case "redUpgradeCost" -> redUpgradeCost = Integer.parseInt(val);
                        case "greenUpgradeCost" -> greenUpgradeCost = Integer.parseInt(val);
                        case "blueUpgradeCost" -> blueUpgradeCost = Integer.parseInt(val);
                        case "purpleUpgradeCost" -> purpleUpgradeCost = Integer.parseInt(val);
                        case "yellowUpgradeCost" -> yellowUpgradeCost = Integer.parseInt(val);

                        case "valueUpgradeBaseCost" -> valueUpgradeBaseCost = Integer.parseInt(val);
                        case "moreUpgradeBaseCost" -> moreUpgradeBaseCost = Integer.parseInt(val);


                        // audio
                        case "musicVolume" -> musicVolume = Double.parseDouble(val);
                        case "soundEffectsVolume" -> soundEffectsVolume = Double.parseDouble(val);

                        // display
                        case "width" -> displayWidth = Integer.parseInt(val);
                        case "height" -> displayHeight = Integer.parseInt(val);
                        case "fullscreen" -> fullscreen = Boolean.parseBoolean(val);

                        // system
                        case "autoSaveIntervalSeconds" -> autoSaveIntervalSeconds = Integer.parseInt(val);
                        case "maxSaveFiles" -> maxSaveFiles = Integer.parseInt(val);
                        case "debugMode" -> debugMode = Boolean.parseBoolean(val);

                        // paperclips
                        case "baseValue" -> paperclipBaseValue = Integer.parseInt(val);
                        // case "rareSpawnChance" -> paperclipRareSpawnChance = Double.parseDouble(val);
                        case "basic" -> basicPaperclipWeight = Double.parseDouble(val);
                        case "red" -> redSpawnWeight = Double.parseDouble(val);
                        case "green" -> greenSpawnWeight = Double.parseDouble(val);
                        case "blue" -> blueSpawnWeight = Double.parseDouble(val);
                        case "purple" -> purpleSpawnWeight = Double.parseDouble(val);
                        case "yellow" -> yellowSpawnWeight = Double.parseDouble(val);
                    }
                } catch (Exception e) {
                    System.out.println("Failed to parse " + key + ": " + val + " — using default.");
                }
            }

            System.out.println("Config loaded successfully.");

        } catch (IOException e) {
            System.out.println("Config file not found, using defaults.");
        }
    }
}