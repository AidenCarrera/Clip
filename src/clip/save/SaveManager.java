package clip.save;

import clip.entities.Spawner;
import clip.core.ID;

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
    public void save(Spawner spawner) {
        GameSaveData data = new GameSaveData(
                spawner.getClips(),
                spawner.getMaxClipCount(),
                spawner.getColoredUpgrade(),
                spawner.getValueUpgradeCount(),
                spawner.getMoreUpgradeCount()
        );

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            writer.write(data.clips + "\n");
            writer.write(data.maxClipCount + "\n");
            writer.write(data.coloredUpgrade + "\n");
            writer.write(data.valueUpgradeCount + "\n");
            writer.write(data.moreUpgradeCount + "\n");
            System.out.println("Game saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- LOAD ---
    public boolean load(Spawner spawner) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            if (lines.size() < 5) return false;

            GameSaveData data = new GameSaveData(
                    parseOrDefault(lines.get(0)),
                    parseOrDefault(lines.get(1)),
                    parseOrDefault(lines.get(2)),
                    parseOrDefault(lines.get(3)),
                    parseOrDefault(lines.get(4))
            );

            // Restore to Spawner
            spawner.setClips(data.clips);
            spawner.setMaxClipCount(data.maxClipCount);
            spawner.setColoredUpgrade(data.coloredUpgrade);
            spawner.setValueUpgradeCount(data.valueUpgradeCount);
            spawner.setMoreUpgradeCount(data.moreUpgradeCount);

            rebuildUpgrades(spawner);
            System.out.println("Game loaded.");
            return true;
        } catch (IOException e) {
            System.out.println("Save file not found. Starting new game.");
            return false;
        }
    }

    private void rebuildUpgrades(Spawner spawner) {
        int c = spawner.getColoredUpgrade();
        if (c >= 100 && c < 1000) spawner.addColoredUpgrade(ID.RED_UPGRADE, 100);
        else if (c >= 1000 && c < 5000) spawner.addColoredUpgrade(ID.GREEN_UPGRADE, 1000);
        else if (c >= 5000 && c < 10000) spawner.addColoredUpgrade(ID.BLUE_UPGRADE, 5000);
        else if (c >= 10000 && c < 50000) spawner.addColoredUpgrade(ID.PURPLE_UPGRADE, 10000);
        else if (c >= 50000) spawner.addColoredUpgrade(ID.YELLOW_UPGRADE, 50000);

        spawner.addValueUpgrade();
        spawner.addMoreUpgrade();
    }

    private int parseOrDefault(String line) {
        try {
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
