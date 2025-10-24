package clip.core;

import clip.audio.SoundHandler;
import clip.input.Mouse;
import clip.ui.HUD;
import clip.ui.Menu;
import clip.ui.Window;
import clip.util.BufferedImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.Objects;

public class Game extends Canvas implements Runnable {

    @Serial
    private static final long serialVersionUID = 1550691097823471818L;
    private static final double UPDATE_CAP = 1.0 / 165.0;

    private Thread thread;
    private boolean running = false;

    private final BufferedImageLoader loader;
    private final Handler handler;
    private final HUD hud;
    private final GameManager gameManager;
    private final double autoSaveInterval; // from config

    private BufferedImage levelImage = null;
    private final BufferedImage dog;

    public enum STATE {
        Menu, Game
    }

    private STATE gameState = STATE.Menu;

    static void main() {
        ConfigManager config = new ConfigManager("assets/config.json");
        config.load();
        new Game(config);
    }

    public Game(ConfigManager config) {

        // Set dynamic window dimensions
        int width = config.displayWidth;
        int height = config.displayHeight;

        loader = new BufferedImageLoader();
        setLevelImage("/images/menu.png");

        // --- Set custom cursor ---
        Image cursorImg = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/images/cursor.png")))
                .getImage();
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Cursor"));

        handler = new Handler();

        // --- GameManager and Spawner use config ---
        gameManager = new GameManager(handler, config);

        hud = new HUD(gameManager, config);

        // --- Mouse setup ---
        Mouse mouse = new Mouse(0, 0, ID.MOUSE, handler, this, hud, config);
        handler.addObject(mouse);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

        // --- Create window using THIS Game instance ---
        new Window(this, config);

        // --- Audio ---
        SoundHandler.runMusic(config.musicVolume, config.soundEffectsVolume);

        // --- Load dog image ---
        dog = loader.loadImage("/images/dog.png");

        // --- Menu buttons ---
        Menu newGameBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.NEW_GAME);
        Menu continueBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.CONTINUE);
        Menu exitBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.EXIT);

        newGameBtn.updatePosition(width, height, 2);
        continueBtn.updatePosition(width, height, 1);
        exitBtn.updatePosition(width, height, 0);

        newGameBtn.setVisible(false);
        continueBtn.setVisible(false);
        exitBtn.setVisible(false);

        handler.addObject(newGameBtn);
        handler.addObject(continueBtn);
        handler.addObject(exitBtn);

        // --- Auto-save interval from config ---
        autoSaveInterval = config.autoSaveIntervalSeconds;
    }

    // --- Game state getters/setters ---
    public STATE getGameState() { return gameState; }
    public void setGameState(STATE gameState) { this.gameState = gameState; }
    public void setLevelImage(String path) { levelImage = loader.loadImage(path); }

    public GameManager getGameManager() { return gameManager; }

    // --- Game loop ---
    private double autoSaveTimer = 0;

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.requestFocus();
        boolean render;
        double lastTime = System.nanoTime() / 1e9;
        double unprocessedTime = 0;
        double frameTime = 0;
        int frames = 0;
        int fps;

        while (running) {
            render = false;
            double currentTime = System.nanoTime() / 1e9;
            double passedTime = currentTime - lastTime;
            lastTime = currentTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                render = true;
                tick();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                    System.out.println(fps + " FPS");

                    // ✅ Auto-save interval from config
                    if (gameState == STATE.Game) {
                        autoSaveTimer += 1.0;
                        if (autoSaveTimer >= autoSaveInterval) {
                            gameManager.saveGame();
                            autoSaveTimer = 0;
                            System.out.println("Auto-saved game.");
                        }
                    }
                }
            }

            if (render) {
                render();
                frames++;
            } else {
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            }
        }

        // Save on exit
        if (gameState == STATE.Game) {
            gameManager.saveGame();
            System.out.println("Game saved on exit.");
        }

        SoundHandler.close();
        stop();
    }

    private void tick() {
        handler.tick();

        // Update menu button positions
        if (gameState == STATE.Menu) {
            int buttonIndex = 2;
            for (GameObject obj : handler.getObjects()) {
                if (obj instanceof Menu menuButton) {
                    menuButton.updatePosition(getWidth(), getHeight(), buttonIndex--);
                }
            }
        }

        if (gameState == STATE.Game) {
            gameManager.tick();
            hud.tick();
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(levelImage, 0, 0, getWidth(), getHeight(), null);

        if (gameState == STATE.Menu) {
            g.drawImage(dog, 960, 20, null);
        } else {
            hud.render(g);
        }

        handler.render(g);
        g.dispose();
        bs.show();
    }

    public static int clamp(int var, int min, int max) {
        return Math.max(min, Math.min(max, var));
    }
}
