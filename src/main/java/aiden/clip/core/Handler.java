package aiden.clip.core;

import java.awt.*;
import java.util.LinkedList;

public class Handler {
    private final LinkedList<GameObject> objects = new LinkedList<>();
    private final LinkedList<GameObject> toAdd = new LinkedList<>();
    private final LinkedList<GameObject> toRemove = new LinkedList<>();

    // Runs this method every frame
    public synchronized void tick() {
        for (GameObject obj : objects) {
            obj.tick();
        }

        // Safely add queued objects
        if (!toAdd.isEmpty()) {
            objects.addAll(toAdd);
            toAdd.clear();
        }

        // Safely remove queued objects
        if (!toRemove.isEmpty()) {
            objects.removeAll(toRemove);
            toRemove.clear();
        }
    }

    // Renders game objects
    public synchronized void render(Graphics g) {
        for (GameObject obj : objects) {
            obj.render(g);
        }
    }

    // Queues an object to be added after current tick
    public synchronized void addObject(GameObject object) {
        toAdd.add(object);
    }

    // Queues an object to be removed after current tick
    public synchronized void removeObject(GameObject object) {
        toRemove.add(object);
    }

    // Always return a copy to avoid concurrent modification
    public synchronized LinkedList<GameObject> getObjects() {
        return new LinkedList<>(objects);
    }

    public synchronized void setMousePositionForMouse(int mouseX, int mouseY) {
        for (GameObject obj : objects) {
            if (obj.getID() == ID.MOUSE) {
                obj.setMousePosition(mouseX, mouseY);
            }
        }
    }

    public synchronized void resetMousePosition() {
        for (GameObject obj : objects) {
            if (obj.getID() == ID.MOUSE) {
                obj.setMousePosition(0, 0);
            }
        }
    }
}
