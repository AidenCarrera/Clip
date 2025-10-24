package clip.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final String path;
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

    public int paperclipBaseValue;
    public double basicPaperclipWeight;
    public double redSpawnWeight;
    public double greenSpawnWeight;
    public double blueSpawnWeight;
    public double purpleSpawnWeight;
    public double yellowSpawnWeight;

    public ConfigManager(String path) {
        this.path = path;
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

    public void load() {
        File file = new File(path);
        ConfigRoot data;

        if (!file.exists()) {
            System.out.println("Config file not found, creating default config.");
            data = new ConfigRoot(); // defaults
            saveDefaults(file, data);
        } else {
            try {
                data = mapper.readValue(file, ConfigRoot.class);
            } catch (IOException e) {
                System.out.println("Failed to read config file, using defaults.");
                e.printStackTrace();
                data = new ConfigRoot();
            }
        }

        applyConfig(data);
        System.out.println("Config loaded successfully.");
    }

    private void applyConfig(ConfigRoot data) {
        // --- Apply game values ---
        startClips = data.game.startClips;
        maxClipCount = data.game.maxClipCount;
        spawnRate = data.game.spawnRate;
        upgradeCostMultiplier = data.game.upgradeCostMultiplier;

        // --- Apply upgrades ---
        redUpgradeCost = data.upgrades.redUpgradeCost;
        greenUpgradeCost = data.upgrades.greenUpgradeCost;
        blueUpgradeCost = data.upgrades.blueUpgradeCost;
        purpleUpgradeCost = data.upgrades.purpleUpgradeCost;
        yellowUpgradeCost = data.upgrades.yellowUpgradeCost;
        valueUpgradeBaseCost = data.upgrades.valueUpgradeBaseCost;
        moreUpgradeBaseCost = data.upgrades.moreUpgradeBaseCost;

        // --- Apply audio ---
        musicVolume = data.audio.musicVolume;
        soundEffectsVolume = data.audio.soundEffectsVolume;

        // --- Apply display ---
        hudScale = data.display.hudScale;
        displayWidth = data.display.width;
        displayHeight = data.display.height;
        fullscreen = data.display.fullscreen;

        // --- Apply system ---
        autoSaveIntervalSeconds = data.system.autoSaveIntervalSeconds;
        maxSaveFiles = data.system.maxSaveFiles;
        debugMode = data.system.debugMode;

        // --- Apply paperclips ---
        paperclipBaseValue = data.paperclips.baseValue;
        basicPaperclipWeight = data.paperclips.colorSpawnWeights.basic;
        redSpawnWeight = data.paperclips.colorSpawnWeights.red;
        greenSpawnWeight = data.paperclips.colorSpawnWeights.green;
        blueSpawnWeight = data.paperclips.colorSpawnWeights.blue;
        purpleSpawnWeight = data.paperclips.colorSpawnWeights.purple;
        yellowSpawnWeight = data.paperclips.colorSpawnWeights.yellow;
    }

    private void saveDefaults(File file, ConfigRoot data) {
        try {
            mapper.writeValue(file, data);
            System.out.println("Default config created at " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to write default config.");
            e.printStackTrace();
        }
    }
}
