package clip.save;

import clip.core.ColorTier;
import clip.core.GameManager;
import clip.core.Handler;
import clip.core.ID;
import clip.entities.Upgrade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveManager {
    private final String path;

    public SaveManager(String path) {
        this.path = path;
    }

    // --- SAVE ---
    public void save(GameManager gameManager) {
        // Build JSON manually
        String json = "{\n" +
                "  \"clips\": " + gameManager.getClips() + ",\n" +
                "  \"maxClipCount\": " + gameManager.getMaxClipCount() + ",\n" +
                "  \"coloredUpgrade\": \"" + gameManager.getColoredUpgrade().name() + "\",\n" +
                "  \"valueUpgradeCount\": " + gameManager.getValueUpgradeCount() + ",\n" +
                "  \"moreUpgradeCount\": " + gameManager.getMoreUpgradeCount() + "\n" +
                "}";

        try {
            Files.write(Paths.get(path), json.getBytes(StandardCharsets.UTF_8));
            System.out.println("Game saved as JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- LOAD ---
    public boolean load(GameManager gameManager) {
        try {
            String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            content = content.replaceAll("[\\{\\}\\s\"]", ""); // remove braces, quotes, spaces

            String[] parts = content.split(",");
            int clips = 0;
            int maxClipCount = 25; // default
            int valueUpgradeCount = 0;
            int moreUpgradeCount = 0;
            ColorTier coloredUpgrade = ColorTier.RED; // default

            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length < 2) continue;
                String key = kv[0];
                String val = kv[1];

                try {
                    switch (key) {
                        case "clips" -> clips = Integer.parseInt(val);
                        case "maxClipCount" -> maxClipCount = Integer.parseInt(val);
                        case "coloredUpgrade" -> coloredUpgrade = ColorTier.valueOf(val);
                        case "valueUpgradeCount" -> valueUpgradeCount = Integer.parseInt(val);
                        case "moreUpgradeCount" -> moreUpgradeCount = Integer.parseInt(val);
                    }
                } catch (Exception ex) {
                    System.out.println("Failed to parse " + key + ": " + val + " — using default.");
                }
            }

            // Apply values
            gameManager.setClips(clips);
            gameManager.setMaxClipCount(maxClipCount);
            gameManager.setColoredUpgrade(coloredUpgrade);
            gameManager.setValueUpgradeCount(valueUpgradeCount);
            gameManager.setMoreUpgradeCount(moreUpgradeCount);

            rebuildUpgrades(gameManager);
            System.out.println("Game loaded from JSON.");
            return true;

        } catch (IOException e) {
            System.out.println("Save file not found. Starting new game.");
            return false;
        }
    }


    // --- Rebuild upgrades in the world ---
    private void rebuildUpgrades(GameManager gameManager) {
        Handler handler = gameManager.getHandler();
        ColorTier tier = gameManager.getColoredUpgrade();

        // Add the current colored upgrade in the world
        if (tier != null) {
            handler.addObject(new Upgrade(175, 50, tier.getUpgradeID()));
        }

        // Always rebuild value and more upgrades
        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE));
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE));
    }
}
