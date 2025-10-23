package clip;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1550691097823471818L;
    public static final int WIDTH = 2560;
    public static final int HEIGHT = 1440;
    private static final double UPDATE_CAP = 1.0 / 165.0;

    private Thread thread;
    private boolean running = false;

    private final BufferedImageLoader loader;
    private final Handler handler;
    private final HUD hud;
    private final Random random;
    private final Spawner spawner;
    private final Menu newGame, continueGame, exitGame;

    private BufferedImage levelImage = null;
    private final BufferedImage dog;

    public enum STATE {
        Menu, Game
    }

    private STATE gameState = STATE.Menu;

    public static void main(String[] args) {
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

        random = new Random();
        handler = new Handler();
        spawner = new Spawner(handler, random);
        hud = new HUD(spawner);

        new Window(WIDTH, HEIGHT, "Paperclip Collector", this);

        SoundHandler.RunMusic();

        dog = loader.loadImage("/images/dog.png");

        newGame = new Menu(1180, 1105, ID.NewGame, handler);
        continueGame = new Menu(1180, 1222, ID.Continue, handler);
        exitGame = new Menu(1180, 1305, ID.Exit, handler);

        handler.addObject(newGame);
        handler.addObject(continueGame);
        handler.addObject(exitGame);

        MouseInput mouseInput = new MouseInput(handler);
        handler.addObject(new Mouse(0, 0, ID.Mouse, handler, spawner, loader, this));
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);
    }

    public STATE getGameState() {
        return gameState;
    }

    public void setGameState(STATE gameState) {
        this.gameState = gameState;
    }

    public void setLevelImage(String path) {
        levelImage = loader.loadImage(path);
    }

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
        int fps = 0;

        while (running) {
            render = false;
            double firstTime = System.nanoTime() / 1e9;
            double passedTime = firstTime - lastTime;
            lastTime = firstTime;

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

                    if (gameState == STATE.Game) spawner.save();
                }
            }

            if (render) {
                render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        }

        SoundHandler.close();
        stop();
    }

    private void tick() {
        handler.tick();
        if (gameState == STATE.Game) {
            spawner.tick();
            hud.tick();
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3); // triple buffering for smoother rendering
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

    // Clamp a value to a minimum
    public static int clamp(int var, int min) {
        return Math.max(var, min);
    }

    // Clamp a value between min and max
    public static int clamp(int var, int min, int max) {
        return Math.max(min, Math.min(max, var));
    }
}
