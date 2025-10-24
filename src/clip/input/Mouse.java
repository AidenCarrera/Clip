package clip.input;

import clip.core.Game;
import clip.core.GameState;
import clip.core.GameObject;
import clip.core.Handler;
import clip.core.ConfigManager;
import clip.core.ID;
import clip.ui.HUD;
import clip.ui.Menu;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse extends GameObject implements MouseListener, MouseMotionListener {

    private final Handler handler;
    private final Game game;
    private final HUD hud;

    private int mouseX, mouseY;
    private int pressX, pressY; // Track press position

    public Mouse(int x, int y, ID id, Handler handler, Game game, HUD hud, ConfigManager config) {
        super(x, y, id, config); // Use GameObject's config field
        this.handler = handler;
        this.game = game;
        this.hud = hud;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 16);
    }

    @Override
    public void tick() {
        int maxX = config.displayWidth - 16;
        int maxY = config.displayHeight - 16;
        x = Game.clamp(mouseX, 0, maxX);
        y = Game.clamp(mouseY, 0, maxY);

        if (game.getGameState() == GameState.GAME) {
            collectPaperclips();
        }
    }

    private void collectPaperclips() {
        for (GameObject obj : handler.getObjects()) {
            if (obj.getID() == null) continue;
            if (!getBounds().intersects(obj.getBounds()) || id != ID.MOUSE) continue;

            switch (obj.getID()) {
                case PAPERCLIP, RED_PAPERCLIP, GREEN_PAPERCLIP, BLUE_PAPERCLIP, PURPLE_PAPERCLIP, YELLOW_PAPERCLIP ->
                        game.getGameManager().collectClip(obj);
                default -> {}
            }
        }
    }

    private void handleMenuClicks(int clickX, int clickY) {
        for (GameObject obj : handler.getObjects()) {
            if (!(obj instanceof Menu)) continue;

            if (obj.getBounds().contains(clickX, clickY)) {
                switch (obj.getID()) {
                    case NEW_GAME -> {
                        game.setLevelImage("/images/office.png");
                        game.setGameState(GameState.GAME);
                        game.getGameManager().startNewGame();
                    }
                    case CONTINUE -> {
                        game.setLevelImage("/images/office.png");
                        game.setGameState(GameState.GAME);
                        game.getGameManager().continueGame();
                    }
                    case EXIT -> System.exit(0);
                }

                // Remove menu buttons if starting/continuing game
                if (obj.getID() == ID.NEW_GAME || obj.getID() == ID.CONTINUE) {
                    handler.getObjects().removeIf(o -> o instanceof Menu);
                }
                break;
            }
        }
    }

    private void hudMouseClick(int clickX, int clickY) {
        MouseEvent e = new MouseEvent(game, 0, 0, 0, clickX, clickY, 1, false);
        hud.mouseClicked(e);
    }

    @Override
    public void render(Graphics g) {
        // Optional debug cursor
        // g.fillRect(x, y, 16, 16);
    }

    // ---------------- MouseListener -----------------
    @Override
    public void mousePressed(MouseEvent e) {
        pressX = e.getX();
        pressY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int releaseX = e.getX();
        int releaseY = e.getY();

        // Only trigger click if within tolerance from press
        if (Math.abs(releaseX - pressX) <= config.clickTolerance
                && Math.abs(releaseY - pressY) <= config.clickTolerance) {

            mouseX = releaseX;
            mouseY = releaseY;

            if (game.getGameState() == GameState.GAME) {
                hudMouseClick(mouseX, mouseY);
            } else if (game.getGameState() == GameState.MENU) {
                handleMenuClicks(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing: clicks handled through mouseReleased
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // ---------------- MouseMotionListener -----------------
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
