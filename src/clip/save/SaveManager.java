package clip.save;

import clip.core.GameManager;
import clip.core.ID;
import clip.core.Handler;
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
                "  \"coloredUpgrade\": " + gameManager.getColoredUpgrade() + ",\n" +
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
            int clips = 0, maxClipCount = 0, coloredUpgrade = 0, valueUpgradeCount = 0, moreUpgradeCount = 0;

            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length < 2) continue;
                switch (kv[0]) {
                    case "clips" -> clips = Integer.parseInt(kv[1]);
                    case "maxClipCount" -> maxClipCount = Integer.parseInt(kv[1]);
                    case "coloredUpgrade" -> coloredUpgrade = Integer.parseInt(kv[1]);
                    case "valueUpgradeCount" -> valueUpgradeCount = Integer.parseInt(kv[1]);
                    case "moreUpgradeCount" -> moreUpgradeCount = Integer.parseInt(kv[1]);
                }
            }

            // Apply to GameManager
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

        int c = gameManager.getColoredUpgrade();
        if (c >= 100 && c < 1000) handler.addObject(new Upgrade(175, 50, ID.RED_UPGRADE));
        else if (c >= 1000 && c < 5000) handler.addObject(new Upgrade(175, 50, ID.GREEN_UPGRADE));
        else if (c >= 5000 && c < 10000) handler.addObject(new Upgrade(175, 50, ID.BLUE_UPGRADE));
        else if (c >= 10000 && c < 50000) handler.addObject(new Upgrade(175, 50, ID.PURPLE_UPGRADE));
        else if (c >= 50000) handler.addObject(new Upgrade(175, 50, ID.YELLOW_UPGRADE));

        // Always rebuild value and more upgrades
        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE));
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE));
    }
}
