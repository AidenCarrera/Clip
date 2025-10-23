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
    public static final int WIDTH = 2560;
    public static final int HEIGHT = 1440;
    private static final double UPDATE_CAP = 1.0 / 165.0;

    private Thread thread;
    private boolean running = false;

    private final BufferedImageLoader loader;
    private final Handler handler;
    private final HUD hud;
    private final GameManager gameManager;

    private BufferedImage levelImage = null;
    private final BufferedImage dog;

    public enum STATE {
        Menu, Game
    }

    private STATE gameState = STATE.Menu;

    static void main() {
        new Game();
    }

    public Game() {
        loader = new BufferedImageLoader();
        setLevelImage("/images/menu.png");

        // Set custom cursor
        Image cursorImg = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/images/cursor.png"))
        ).getImage();
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Cursor"));

        handler = new Handler();

        // --- GameManager and Spawner ---
        gameManager = new GameManager(handler);

        hud = new HUD(gameManager);   // HUD

        // --- Mouse ---
        Mouse mouse = new Mouse(0, 0, ID.MOUSE, handler, this, hud);
        handler.addObject(mouse);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

        new Window(WIDTH, HEIGHT, "Paperclip Collector", this);

        SoundHandler.runMusic();
        dog = loader.loadImage("/images/dog.png");

        // --- Menu buttons ---
        Menu newGameBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.NEW_GAME);
        Menu continueBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.CONTINUE);
        Menu exitBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.EXIT);

        newGameBtn.updatePosition(Game.WIDTH, Game.HEIGHT, 2);
        continueBtn.updatePosition(Game.WIDTH, Game.HEIGHT, 1);
        exitBtn.updatePosition(Game.WIDTH, Game.HEIGHT, 0);

        // Set buttons invisible initially
        newGameBtn.setVisible(false);
        continueBtn.setVisible(false);
        exitBtn.setVisible(false);

        handler.addObject(newGameBtn);
        handler.addObject(continueBtn);
        handler.addObject(exitBtn);
    }

    // --- Game state getters/setters ---
    public STATE getGameState() { return gameState; }
    public void setGameState(STATE gameState) { this.gameState = gameState; }
    public void setLevelImage(String path) { levelImage = loader.loadImage(path); }

    public GameManager getGameManager() {
        return gameManager;
    }


    // --- Game loop ---
    private double autoSaveTimer = 0;                     // Tracks time for periodic auto-save
    private static final double AUTO_SAVE_INTERVAL = 15; // Auto-save interval in seconds

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

                    // --- Auto-save every interval ---
                    if (gameState == STATE.Game) {
                        autoSaveTimer += 1.0; // increment roughly every second
                        if (autoSaveTimer >= AUTO_SAVE_INTERVAL) {
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

        // Tick game logic
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
