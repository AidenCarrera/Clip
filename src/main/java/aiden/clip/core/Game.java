package aiden.clip.core;

import javax.swing.*;
import aiden.clip.audio.SoundHandler;
import aiden.clip.input.Mouse;
import aiden.clip.ui.HUD;
import aiden.clip.ui.Menu;
import aiden.clip.ui.Window;
import aiden.clip.util.BufferedImageLoader;

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
    private final double autoSaveInterval;
    private BufferedImage levelImage = null;
    private final BufferedImage dog;

    private GameState gameState = GameState.MENU;

    // Scaling factors
    private final float windowScaleX;
    private final float windowScaleY;

    // Config field now accessible anywhere
    private final ConfigManager config;

    public static void main(String[] args) {
        System.out.println("Game starting...");

        try {
            ConfigManager config = new ConfigManager();
            config.load();
            new Game(config);
            System.out.println("Game initialized successfully.");
        } catch (Throwable e) {
            e.printStackTrace();
            javax.swing.SwingUtilities.invokeLater(() -> javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Error starting game: " + e,
                    "Game Crash",
                    javax.swing.JOptionPane.ERROR_MESSAGE));
        }
    }

    public Game(ConfigManager config) {
        this.config = config;

        loader = new BufferedImageLoader();
        setLevelImage("/images/menu.png");

        // --- Detect actual screen size ---
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int actualWidth = screenSize.width;
        int actualHeight = screenSize.height;

        // --- Compute scaling factors ---
        windowScaleX = (float) actualWidth / config.displayWidth;
        windowScaleY = (float) actualHeight / config.displayHeight;

        // --- Handler ---
        handler = new Handler();
        
        // --- GameManager ---
        gameManager = new GameManager(handler, config, windowScaleX, windowScaleY);

        // --- HUD ---
        hud = new HUD(gameManager, config, windowScaleX, windowScaleY); // HUD constructor must accept scaleX/scaleY

        // --- Mouse ---
        Mouse mouse = new Mouse(0, 0, ID.MOUSE, handler, this, hud, config);
        handler.addObject(mouse);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

        // --- Window ---
        new Window(this, config);

        // --- Audio ---
        SoundHandler.runMusic(config.musicVolume, config.soundEffectsVolume);

        // --- Load dog image ---
        dog = loader.loadImage("/images/dog.png");

        // --- Menu buttons ---
        Menu newGameBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.NEW_GAME, config);
        Menu continueBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.CONTINUE, config);
        Menu exitBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.EXIT, config);

        newGameBtn.updatePosition((int) (config.displayWidth * windowScaleX),
                (int) (config.displayHeight * windowScaleY), 2);
        continueBtn.updatePosition((int) (config.displayWidth * windowScaleX),
                (int) (config.displayHeight * windowScaleY), 1);
        exitBtn.updatePosition((int) (config.displayWidth * windowScaleX),
                (int) (config.displayHeight * windowScaleY), 0);

        handler.addObject(newGameBtn);
        handler.addObject(continueBtn);
        handler.addObject(exitBtn);

        // --- Auto-save interval ---
        autoSaveInterval = config.autoSaveIntervalSeconds;

        // --- Set custom cursor ---
        Image cursorImg = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/images/cursor.png")))
                .getImage();
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Cursor"));
    }

    // --- Game state getters/setters ---
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setLevelImage(String path) {
        levelImage = loader.loadImage(path);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public float getWindowScaleX() {
        return windowScaleX;
    }

    public float getWindowScaleY() {
        return windowScaleY;
    }

    // --- Game loop ---
    private double autoSaveTimer = 0;

    public synchronized void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            if (thread != null)
                thread.join();
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

                    if (gameState == GameState.GAME) {
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
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (gameState == GameState.GAME) {
            gameManager.saveGame();
            System.out.println("Game saved on exit.");
        }
        SoundHandler.close();
        stop();
    }

    private void tick() {
        handler.tick();

        if (gameState == GameState.MENU) {
            int buttonIndex = 2;
            for (GameObject obj : handler.getObjects()) {
                if (obj instanceof Menu menuButton) {
                    menuButton.updatePosition((int) (config.displayWidth * windowScaleX),
                            (int) (config.displayHeight * windowScaleY),
                            buttonIndex--);
                }
            }
        }

        if (gameState == GameState.GAME) {
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

        if (gameState == GameState.MENU) {
            g.drawImage(dog, (int) (960 * windowScaleX), (int) (20 * windowScaleY), null);
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
