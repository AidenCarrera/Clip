package clip;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    private static final long serialVersionUID = 1550691097823471818L;
    public static final int WIDTH = 2560, HEIGHT = 1440;
    private final double UPDATE_CAP = 1.0 / 165.0;
    private Thread thread;
    private Boolean running = false;
    private BufferedImageLoader loader;
    private Handler handler;
    private HUD hud;
    private Random random;
    private Spawner spawner;
    private Menu newGame, continueGame, exitGame;
    private BufferedImage levelImage = null, dog = null;
    public static void main(String[] args) {
        new Game();
    }
    public Game() {
        loader = new BufferedImageLoader();
        setLevelImage(loader, "/images/menu.png");
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
        new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/cursor.png"))).getImage(), new Point(0, 0), "Cursor"));
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
        System.out.println(newGame.toString());
        handler.addObject(newGame);
        handler.addObject(continueGame);
        handler.addObject(exitGame);
        handler.addObject(new Mouse(0, 0, ID.Mouse, handler, spawner, loader, this));
        this.addMouseListener(new MouseInput(handler));
        this.addMouseMotionListener(new MouseInput(handler));
    }
    public enum STATE {
        // Game states
        Menu, Game
    };
    public STATE gameState = STATE.Menu;
    public void setGameState(STATE gameState) {
        this.gameState = gameState;
    }
    // Sets background
    public void setLevelImage(BufferedImageLoader loader, String path) {
        levelImage = loader.loadImage(path);
    }
    public synchronized void start() {
        if(running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    public synchronized void stop() {
        try {
            thread.join();
            running = false;
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        this.requestFocus();
        boolean render = false;
        double firstTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0;
        double passedTime = 0;
        double unprocessedTime = 0;
        double frameTime = 0;
        int frames = 0;
        int fps = 0;
        while (running) {
            render = false;
            firstTime = System.nanoTime() / 1000000000.0;
            passedTime = firstTime - lastTime;
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
                    if(gameState == STATE.Game) {
                        spawner.save();
                    }
                }
            }
            if (render) {
                render();
                frames++;
            } 
            // Sleeps if there's nothing new to render
            else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // Closes audio and ends run loop
        SoundHandler.close();
        stop();
    }
    private void tick() {
        handler.tick();
        if(gameState == STATE.Game) {
            // Creates Paperclips and runs HUD once in Game state 
            spawner.tick();
            hud.tick();
        }
    }
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(2);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        // Draws Background
        g.drawImage(levelImage, 0, 0, getWidth(), getHeight(), null);
        if(gameState == STATE.Menu) {
            // Draws Menu Dog if on Menu
            g.drawImage(dog, 960, 20, null);
        }
        else {
            // Renders the HUD once in the game state
            hud.render(g);
        }
        handler.render(g);
        g.dispose();
        bs.show();
    }
    // Method that doesn't allow a value to below a number
    public static int clamp(int var, int min) {
        if (var <= min)
            return var = min;
        else
            return var;
    }
    // Overloaded Constructor
    // Method that doesn't allow a value to go above or below a threshold
    public static int clamp(int var, int min, int max) {
        if (var >= max)
            return var = max;
        else if (var <= min)
            return var = min;
        else
            return var;
    }
}
