package clip.input;

import clip.core.Game;
import clip.core.Game.STATE;
import clip.core.GameObject;
import clip.core.Handler;
import clip.core.ID;
import clip.ui.HUD;
import clip.ui.Menu;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class Mouse extends GameObject implements MouseListener, MouseMotionListener {

    private final Handler handler;
    private final Game game;
    private final HUD hud;

    private int mouseX, mouseY;

    public Mouse(int x, int y, ID id, Handler handler, Game game, HUD hud) {
        super(x, y, id);
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
        // Clamp and update mouse position
        x = Game.clamp(mouseX, 0, Game.WIDTH - 416);
        y = Game.clamp(mouseY, 0, Game.HEIGHT - 300);

        if (game.getGameState() == STATE.Game) {
            // Collect paperclips automatically
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

    private void handleMenuClicks() {
        GameObject clickedButton = null;

        for (GameObject obj : handler.getObjects()) {
            if (!(obj instanceof Menu)) continue;

            if (obj.getBounds().contains(mouseX, mouseY)) {
                clickedButton = obj;

                switch (obj.getID()) {
                    case NEW_GAME -> {
                        game.setLevelImage("/images/office.png");
                        game.setGameState(STATE.Game);
                        game.getGameManager().startNewGame();
                    }
                    case CONTINUE -> {
                        game.setLevelImage("/images/office.png");
                        game.setGameState(STATE.Game);
                        game.getGameManager().continueGame();
                    }
                    case EXIT -> System.exit(0);
                }

                break; // Only trigger one button per click
            }
        }

        if (clickedButton != null && (clickedButton.getID() == ID.NEW_GAME || clickedButton.getID() == ID.CONTINUE)) {
            List<GameObject> toRemove = new ArrayList<>();
            for (GameObject obj : handler.getObjects()) {
                if (obj instanceof Menu) toRemove.add(obj);
            }
            toRemove.forEach(handler::removeObject);
        }
    }

    private void hudMouseClick() {
        // Delegate click to HUD
        MouseEvent e = new MouseEvent(game, 0, 0, 0, mouseX, mouseY, 1, false);
        hud.mouseClicked(e);
    }

    @Override
    public void render(Graphics g) {
        // Optional debug cursor
        // g.fillRect(x, y, 16, 16);
    }

    // ---------------- MouseListener -----------------
    @Override
    public void mouseClicked(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        if (game.getGameState() == STATE.Game) {
            hudMouseClick();
        } else if (game.getGameState() == STATE.Menu) {
            handleMenuClicks();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
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
