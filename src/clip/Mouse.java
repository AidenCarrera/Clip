package clip;

import clip.Game.STATE;

import java.awt.*;

public class Mouse extends GameObject {
    private Handler handler;
    private Spawner spawner;
    private BufferedImageLoader loader;
    private Game game;
    private Menu newGame, continueGame, exitGame;
    public Mouse(int x, int y, ID id, Handler handler, Spawner spawner, BufferedImageLoader loader, Game game) {
        super(x, y, id);
        this.handler = handler;
        this.spawner = spawner;
        this.game = game;
        this.loader = loader;
    }
    public Rectangle getBounds() {
        // Creates a hitbox for the mouse
        return new Rectangle(x, y, 16, 16);
    }
    public void tick() {
        x = mouseX;
        y = mouseY;
        x = Game.clamp(x, 0, Game.WIDTH - 416);
        y = Game.clamp(y, 0, Game.HEIGHT - 300);
        collision();
    }
    // Detects if the Mouse collides with Paperclips, Upgrades, or Menu buttons
    private void collision() {
        for(int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);
            if(tempObject.getID().equals(ID.PAPERCLIP)) {
                if(getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    handler.removeObject(tempObject);
                    spawner.addClips(1 * (spawner.getValueUpgradeCount() + 1));
                    spawner.lowerClipCount();
                }
            }
            if (tempObject.getID().equals(ID.RED_PAPERCLIP)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    handler.removeObject(tempObject);
                    spawner.addClips(5 * (spawner.getValueUpgradeCount() + 1));
                    spawner.lowerClipCount();
                }
            }
            if (tempObject.getID().equals(ID.GREEN_PAPERCLIP)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    handler.removeObject(tempObject);
                    spawner.addClips(25 * (spawner.getValueUpgradeCount() + 1));
                    spawner.lowerClipCount();
                }
            }
            if (tempObject.getID().equals(ID.BLUE_PAPERCLIP)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    handler.removeObject(tempObject);
                    spawner.addClips(100 * (spawner.getValueUpgradeCount() + 1));
                    spawner.lowerClipCount();
                }
            }
            if (tempObject.getID().equals(ID.PURPLE_PAPERCLIP)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    handler.removeObject(tempObject);
                    spawner.addClips(1000 * (spawner.getValueUpgradeCount() + 1));
                    spawner.lowerClipCount();
                }
            }
            if (tempObject.getID().equals(ID.YELLOW_PAPERCLIP)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    handler.removeObject(tempObject);
                    spawner.addClips(10000 * (spawner.getValueUpgradeCount() + 1));
                    spawner.lowerClipCount();
                }
            }
            if (tempObject.getID().equals(ID.RED_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if(spawner.getClips() >= 100) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(100);
                        spawner.addColoredUpgrade(ID.GREEN_UPGRADE, 1000);
                    } /* else {
                        System.out.println("Not enough Clips: " + 100);
                    } */
                }
            }
            if (tempObject.getID().equals(ID.GREEN_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if (spawner.getClips() >= 1000) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(1000);
                        spawner.addColoredUpgrade(ID.BLUE_UPGRADE, 5000);
                    } /* else {
                        System.out.println("Not enough Clips: " + 1000);
                    } */
                }
            }
            if (tempObject.getID().equals(ID.BLUE_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if (spawner.getClips() >= 5000) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(5000);
                        spawner.addColoredUpgrade(ID.PURPLE_UPGRADE, 10000);
                    } /* else {
                        System.out.println("Not enough Clips: " + 5000);
                    } */
                }
            }
            if (tempObject.getID().equals(ID.PURPLE_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if (spawner.getClips() >= 10000) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(10000);
                        spawner.addColoredUpgrade(ID.YELLOW_UPGRADE, 50000);
                    } /* else {
                        System.out.println("Not enough Clips: " + 10000);
                    } */
                }
            }
            if (tempObject.getID().equals(ID.YELLOW_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if (spawner.getClips() >= 50000) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(50000);
                        spawner.setColoredUpgrade(100000);
                    } /* else {
                        System.out.println("Not enough Clips: " + 50000);
                    } */
                }
            }
            if (tempObject.getID().equals(ID.VALUE_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if (spawner.getClips() >= spawner.getValueUpgradePrice()) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(spawner.getValueUpgradePrice());
                        spawner.addValueUpgradeCount();
                        spawner.addValueUpgrade();
                    } /* else {
                        System.out.println("Not enough Clips: " + spawner.getValueUpgradePrice());
                    } */
                }
            }
            if (tempObject.getID().equals(ID.MORE_UPGRADE)) {
                if (getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                    if (spawner.getClips() >= spawner.getMoreUpgradePrice()) {
                        handler.removeObject(tempObject);
                        spawner.removeClips(spawner.getMoreUpgradePrice());
                        spawner.addMaxClipCount();
                        spawner.addMoreUpgradeCount();
                        spawner.addMoreUpgrade();
                    } /* else {
                        System.out.println("Not enough Clips: " + spawner.getMoreUpgradePrice());
                    } */
                }
            }
            if(game.getGameState().equals(STATE.Menu)) {
                if(tempObject.getID().equals(ID.NEW_GAME)) {
                    if(getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                        handler.removeObject(newGame);
                        handler.removeObject(continueGame);
                        handler.removeObject(exitGame);
                        game.setLevelImage("/images/office.png");
                        game.setGameState(STATE.Game);
                        spawner.newGame();
                    }
                }
                if(tempObject.getID().equals(ID.CONTINUE)) {
                    if(getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                        handler.removeObject(newGame);
                        handler.removeObject(continueGame);
                        handler.removeObject(exitGame);
                        game.setLevelImage("/images/office.png");
                        game.setGameState(STATE.Game);
                        spawner.continueGame();
                    }
                }
                if(tempObject.getID().equals(ID.EXIT)) {
                    if(getBounds().intersects(tempObject.getBounds()) && id == ID.MOUSE) {
                        System.exit(0);
                    }
                }
            }
        }
    }
    // Render to debug
    public void render(Graphics g) {
        // g.fillRect(x, y, 16, 16);
    }
}