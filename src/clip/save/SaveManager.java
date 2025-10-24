package clip.save;

import clip.core.ColorTier;
import clip.core.GameManager;
import clip.core.Handler;
import clip.core.ID;
import clip.entities.Upgrade;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SaveManager {

    private final String path;
    private final ObjectMapper mapper;

    public SaveManager(String path) {
        this.path = path;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // --- SAVE ---
    public void save(GameManager gameManager) {
        GameSaveData data = new GameSaveData(
                gameManager.getClips(),
                gameManager.getMaxClipCount(),
                gameManager.getColoredUpgrade().name(),
                gameManager.getValueUpgradeCount(),
                gameManager.getMoreUpgradeCount()
        );

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), data);
            System.out.println("Game saved as JSON.");
        } catch (IOException e) {
            System.err.println("Failed to save game.");
            e.printStackTrace();
        }
    }

    // --- LOAD ---
    public boolean load(GameManager gameManager) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Save file not found. Starting new game.");
            return false;
        }

        try {
            GameSaveData data = mapper.readValue(file, GameSaveData.class);

            gameManager.setClips(data.clips);
            gameManager.setMaxClipCount(data.maxClipCount);
            gameManager.setColoredUpgrade(ColorTier.valueOf(data.coloredUpgrade));
            gameManager.setValueUpgradeCount(data.valueUpgradeCount);
            gameManager.setMoreUpgradeCount(data.moreUpgradeCount);

            rebuildUpgrades(gameManager);
            System.out.println("Game loaded from JSON.");
            return true;

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to load save file. Starting new game.");
            e.printStackTrace();
            return false;
        }
    }

    private void rebuildUpgrades(GameManager gameManager) {
        Handler handler = gameManager.getHandler();
        ColorTier tier = gameManager.getColoredUpgrade();

        if (tier != null) {
            handler.addObject(new Upgrade(175, 50, tier.getUpgradeID(), gameManager.getConfig()));
        }

        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE, gameManager.getConfig()));
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE, gameManager.getConfig()));
    }
}
