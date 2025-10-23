package clip.save;

import clip.core.GameManager;
import clip.core.ID;
import clip.core.Handler;
import clip.entities.Upgrade;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class SaveManager {
    private final String path;

    public SaveManager(String path) {
        this.path = path;
    }

    // --- SAVE ---
    public void save(GameManager gameManager) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            writer.write(gameManager.getClips() + "\n");
            writer.write(gameManager.getMaxClipCount() + "\n");
            writer.write(gameManager.getColoredUpgrade() + "\n");
            writer.write(gameManager.getValueUpgradeCount() + "\n");
            writer.write(gameManager.getMoreUpgradeCount() + "\n");
            System.out.println("Game saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- LOAD ---
    public boolean load(GameManager gameManager) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            if (lines.size() < 5) return false;

            gameManager.setClips(parseOrDefault(lines.get(0)));
            gameManager.setMaxClipCount(parseOrDefault(lines.get(1)));
            gameManager.setColoredUpgrade(parseOrDefault(lines.get(2)));
            gameManager.setValueUpgradeCount(parseOrDefault(lines.get(3)));
            gameManager.setMoreUpgradeCount(parseOrDefault(lines.get(4)));

            rebuildUpgrades(gameManager);

            System.out.println("Game loaded.");
            return true;
        } catch (IOException e) {
            System.out.println("Save file not found. Starting new game.");
            return false;
        }
    }

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

    private int parseOrDefault(String line) {
        try {
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
