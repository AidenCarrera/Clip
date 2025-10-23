package clip.input;

import clip.core.Game;
import clip.core.Game.STATE;
import clip.core.GameObject;
import clip.core.Handler;
import clip.core.ID;
import clip.ui.Menu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Mouse extends GameObject {
    private final Handler handler;
    private final Game game;

    public Mouse(int x, int y, ID id, Handler handler, Game game) {
        super(x, y, id);
        this.handler = handler;
        this.game = game;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 16);
    }

    @Override
    public void tick() {
        // Update mouse position
        x = mouseX;
        y = mouseY;
        x = Game.clamp(x, 0, Game.WIDTH - 416);
        y = Game.clamp(y, 0, Game.HEIGHT - 300);

        if (game.getGameState() == STATE.Game) {
            handleGameInteractions();
        } else if (game.getGameState() == STATE.Menu) {
            handleMenuClicks();
        }
    }

    private void handleGameInteractions() {
        for (GameObject obj : handler.getObjects()) {
            if (!getBounds().intersects(obj.getBounds()) || id != ID.MOUSE) continue;

            // Delegate collection and upgrade logic to GameManager/Spawner
            switch (obj.getID()) {
                case PAPERCLIP, RED_PAPERCLIP, GREEN_PAPERCLIP, BLUE_PAPERCLIP, PURPLE_PAPERCLIP, YELLOW_PAPERCLIP ->
                        game.getGameManager().collectClip(obj); // <-- centralized
                case RED_UPGRADE, GREEN_UPGRADE, BLUE_UPGRADE, PURPLE_UPGRADE, YELLOW_UPGRADE ->
                        game.getGameManager().buyColoredUpgrade(obj);
                case VALUE_UPGRADE -> game.getGameManager().buyValueUpgrade(obj);
                case MORE_UPGRADE -> game.getGameManager().buyMoreUpgrade(obj);
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
                        System.out.println("New Game");
                        game.setLevelImage("/images/office.png");
                        game.setGameState(Game.STATE.Game);
                        game.getGameManager().startNewGame();
                    }
                    case CONTINUE -> {
                        System.out.println("Continue");
                        game.setLevelImage("/images/office.png");
                        game.setGameState(Game.STATE.Game);
                        game.getGameManager().continueGame();
                    }
                    case EXIT -> System.exit(0);
                }

                break; // Only trigger one button per click
            }
        }

        // Remove menu buttons after starting/continuing game
        if (clickedButton != null && (clickedButton.getID() == ID.NEW_GAME || clickedButton.getID() == ID.CONTINUE)) {
            List<GameObject> toRemove = new ArrayList<>();
            for (GameObject obj : handler.getObjects()) {
                if (obj instanceof Menu) toRemove.add(obj);
            }
            toRemove.forEach(handler::removeObject);
        }
    }

    @Override
    public void render(Graphics g) {
        // Optional debug rendering
        // g.fillRect(x, y, 16, 16);
    }
}
