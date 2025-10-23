package clip;

import java.util.Random;

// Manages game state, spawner, and saving/loading
public class GameManager {
    private final SaveManager saveManager;
    private final Spawner spawner;

    public GameManager(Handler handler) {
        Random random = new Random();
        this.spawner = new Spawner(handler, random);
        this.saveManager = new SaveManager("save.txt");
    }

    // Starts a new game and resets spawner state
    public void startNewGame() {
        System.out.println("Game Restarted");
        spawner.setClips(0);
        spawner.setCurrentClipCount(0);
        spawner.setMaxClipCount(25);
        spawner.setColoredUpgrade(100);

        spawner.addColoredUpgrade(ID.RED_UPGRADE, 100);
        spawner.addValueUpgrade();
        spawner.addMoreUpgrade();
    }

    // Continues from a save or starts new game if none found
    public void continueGame() {
        System.out.println("Loading previous game...");
        if (!saveManager.load(spawner)) {
            System.out.println("No save found — starting new game.");
            startNewGame();
        }
    }

    // Saves current game state
    public void saveGame() {
        saveManager.save(spawner);
    }

    public Spawner getSpawner() {
        return spawner;
    }
}
