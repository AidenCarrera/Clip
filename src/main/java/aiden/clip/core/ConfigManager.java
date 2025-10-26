package aiden.clip.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;

public class ConfigManager {

    private final ObjectMapper mapper;

    // --- Current config values exposed ---
    public int startClips;
    public int maxClipCount;
    public double spawnRate;
    public double upgradeCostMultiplier;
    public int redUpgradeCost;
    public int greenUpgradeCost;
    public int blueUpgradeCost;
    public int purpleUpgradeCost;
    public int yellowUpgradeCost;
    public int valueUpgradeBaseCost;
    public int moreUpgradeBaseCost;

    public double musicVolume;
    public double soundEffectsVolume;

    public double hudScale;
    public int displayWidth;
    public int displayHeight;
    public boolean fullscreen;

    public int autoSaveIntervalSeconds;
    public int maxSaveFiles;
    public boolean debugMode;
    public int clickTolerance;

    public int paperclipBaseValue;
    public double basicPaperclipWeight;
    public double redSpawnWeight;
    public double greenSpawnWeight;
    public double blueSpawnWeight;
    public double purpleSpawnWeight;
    public double yellowSpawnWeight;

    public ConfigManager() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print JSON
    }

    // --- Nested classes reflecting the JSON structure ---
    public static class GameConfig {
        public int startClips = 0;
        public int maxClipCount = 25;
        public double spawnRate = 1.5;
        public double upgradeCostMultiplier = 1.2;
    }

    public static class UpgradesConfig {
        public int redUpgradeCost = 100;
        public int greenUpgradeCost = 1000;
        public int blueUpgradeCost = 10000;
        public int purpleUpgradeCost = 150000;
        public int yellowUpgradeCost = 650000;
        public int valueUpgradeBaseCost = 100;
        public int moreUpgradeBaseCost = 50;
    }

    public static class AudioConfig {
        public double musicVolume = 0.3;
        public double soundEffectsVolume = 0.7;
    }

    public static class DisplayConfig {
        public double hudScale = 1.2;
        public int width = 2560;
        public int height = 1440;
        public boolean fullscreen = true;
    }

    public static class SystemConfig {
        public int autoSaveIntervalSeconds = 15;
        public int maxSaveFiles = 5;
        public boolean debugMode = false;
        public int clickTolerance = 5;
    }

    public static class PaperclipsConfig {
        public int baseValue = 1;
        public double rareSpawnChance = 0.05;
        public ColorSpawnWeights colorSpawnWeights = new ColorSpawnWeights();

        public static class ColorSpawnWeights {
            public double basic = 69;
            public double red = 15;
            public double green = 7.85;
            public double blue = 4.25;
            public double purple = 2.6;
            public double yellow = 1;
        }
    }

    public static class ConfigRoot {
        public GameConfig game = new GameConfig();
        public UpgradesConfig upgrades = new UpgradesConfig();
        public AudioConfig audio = new AudioConfig();
        public DisplayConfig display = new DisplayConfig();
        public SystemConfig system = new SystemConfig();
        public PaperclipsConfig paperclips = new PaperclipsConfig();
    }

    // --- Load config from classpath or use defaults ---
    public void load() {
        ConfigRoot data;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.json")) {
            if (is != null) {
                data = mapper.readValue(is, ConfigRoot.class);
                System.out.println("Config loaded from classpath resource.");
            } else {
                System.out.println("Config not found in resources, using defaults.");
                data = createDefaults();
            }
        } catch (IOException e) {
            System.out.println("Failed to read config, using defaults.");
            e.printStackTrace();
            data = createDefaults();
        }

        applyConfig(data);
        System.out.println("Config loaded successfully.");
    }

    // --- Apply config values to fields ---
    private void applyConfig(ConfigRoot data) {
        startClips = data.game.startClips;
        maxClipCount = data.game.maxClipCount;
        spawnRate = data.game.spawnRate;
        upgradeCostMultiplier = data.game.upgradeCostMultiplier;

        redUpgradeCost = data.upgrades.redUpgradeCost;
        greenUpgradeCost = data.upgrades.greenUpgradeCost;
        blueUpgradeCost = data.upgrades.blueUpgradeCost;
        purpleUpgradeCost = data.upgrades.purpleUpgradeCost;
        yellowUpgradeCost = data.upgrades.yellowUpgradeCost;
        valueUpgradeBaseCost = data.upgrades.valueUpgradeBaseCost;
        moreUpgradeBaseCost = data.upgrades.moreUpgradeBaseCost;

        musicVolume = data.audio.musicVolume;
        soundEffectsVolume = data.audio.soundEffectsVolume;

        hudScale = data.display.hudScale;
        displayWidth = data.display.width;
        displayHeight = data.display.height;
        fullscreen = data.display.fullscreen;

        autoSaveIntervalSeconds = data.system.autoSaveIntervalSeconds;
        maxSaveFiles = data.system.maxSaveFiles;
        debugMode = data.system.debugMode;
        clickTolerance = data.system.clickTolerance;

        paperclipBaseValue = data.paperclips.baseValue;
        basicPaperclipWeight = data.paperclips.colorSpawnWeights.basic;
        redSpawnWeight = data.paperclips.colorSpawnWeights.red;
        greenSpawnWeight = data.paperclips.colorSpawnWeights.green;
        blueSpawnWeight = data.paperclips.colorSpawnWeights.blue;
        purpleSpawnWeight = data.paperclips.colorSpawnWeights.purple;
        yellowSpawnWeight = data.paperclips.colorSpawnWeights.yellow;
    }

    // --- Return defaults in memory ---
    private ConfigRoot createDefaults() {
        return new ConfigRoot();
    }
}
