package clip.input;

import clip.core.Game;
import clip.core.Game.STATE;
import clip.core.GameObject;
import clip.core.Handler;
import clip.core.ID;
import clip.entities.Spawner;
import clip.ui.Menu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Mouse extends GameObject {
    private final Handler handler;
    private final Spawner spawner;
    private final Game game;

    public Mouse(int x, int y, ID id, Handler handler, Spawner spawner, Game game) {
        super(x, y, id);
        this.handler = handler;
        this.spawner = spawner;
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
            handleGameCollisions();
        } else if (game.getGameState() == STATE.Menu) {
            handleMenuClicks();
        }
    }

    private void handleGameCollisions() {
        for (GameObject obj : handler.getObjects()) {
            if (!getBounds().intersects(obj.getBounds()) || id != ID.MOUSE) continue;

            switch (obj.getID()) {
                case PAPERCLIP -> collectClip(obj, 1);
                case RED_PAPERCLIP -> collectClip(obj, 5);
                case GREEN_PAPERCLIP -> collectClip(obj, 25);
                case BLUE_PAPERCLIP -> collectClip(obj, 100);
                case PURPLE_PAPERCLIP -> collectClip(obj, 1000);
                case YELLOW_PAPERCLIP -> collectClip(obj, 10000);
                case RED_UPGRADE -> buyColoredUpgrade(obj, 100, ID.GREEN_UPGRADE, 1000);
                case GREEN_UPGRADE -> buyColoredUpgrade(obj, 1000, ID.BLUE_UPGRADE, 5000);
                case BLUE_UPGRADE -> buyColoredUpgrade(obj, 5000, ID.PURPLE_UPGRADE, 10000);
                case PURPLE_UPGRADE -> buyColoredUpgrade(obj, 10000, ID.YELLOW_UPGRADE, 50000);
                case YELLOW_UPGRADE -> buyColoredUpgrade(obj, 50000, null, 100000);
                case VALUE_UPGRADE -> buyValueUpgrade(obj);
                case MORE_UPGRADE -> buyMoreUpgrade(obj);
                default -> {}
            }
        }
    }

    private void collectClip(GameObject clip, int value) {
        spawner.addClips(value * (spawner.getValueUpgradeCount() + 1));
        spawner.lowerClipCount();
        handler.removeObject(clip);
    }

    private void buyColoredUpgrade(GameObject upgrade, int cost, ID nextID, int newColoredValue) {
        if (spawner.getClips() >= cost) {
            spawner.removeClips(cost);
            if (nextID != null) spawner.addColoredUpgrade(nextID, newColoredValue);
            else spawner.setColoredUpgrade(newColoredValue);
            handler.removeObject(upgrade);
        }
    }

    private void buyValueUpgrade(GameObject obj) {
        if (spawner.getClips() >= spawner.getValueUpgradePrice()) {
            spawner.removeClips(spawner.getValueUpgradePrice());
            spawner.addValueUpgradeCount();
            spawner.addValueUpgrade();
            handler.removeObject(obj);
        }
    }

    private void buyMoreUpgrade(GameObject obj) {
        if (spawner.getClips() >= spawner.getMoreUpgradePrice()) {
            spawner.removeClips(spawner.getMoreUpgradePrice());
            spawner.addMaxClipCount();
            spawner.addMoreUpgradeCount();
            spawner.addMoreUpgrade();
            handler.removeObject(obj);
        }
    }

    private void handleMenuClicks() {
        GameObject clickedButton = null;

        // Check actual mouse coordinates
        for (GameObject obj : handler.getObjects()) {
            if (!(obj instanceof clip.ui.Menu)) continue;

            Rectangle bounds = obj.getBounds();
            if (bounds.contains(mouseX, mouseY)) {
                clickedButton = obj;

                // Perform button action
                switch (obj.getID()) {
                    case NEW_GAME -> {
                        System.out.println("New Game");
                        game.setLevelImage("/images/office.png");
                        game.setGameState(Game.STATE.Game);
                        game.getGameManager().startNewGame();  // <-- use GameManager
                    }
                    case CONTINUE -> {
                        System.out.println("Continue");
                        game.setLevelImage("/images/office.png");
                        game.setGameState(Game.STATE.Game);
                        game.getGameManager().continueGame();  // <-- use GameManager
                    }
                    case EXIT -> {
                        System.out.println("Exit");
                        System.exit(0);
                    }
                }

                break; // Only trigger one button
            }
        }

        // Only remove all menu buttons if NEW_GAME or CONTINUE was clicked
        if (clickedButton != null && (clickedButton.getID() == ID.NEW_GAME || clickedButton.getID() == ID.CONTINUE)) {
            List<GameObject> toRemove = new ArrayList<>();
            for (GameObject obj : handler.getObjects()) {
                if (obj instanceof Menu) {
                    toRemove.add(obj);
                }
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
