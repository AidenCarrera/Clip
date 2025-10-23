package clip;

import java.awt.*;
import java.util.LinkedList;

// Handles stuff
public class Handler {
    LinkedList<GameObject> objects = new LinkedList<GameObject>();
    // Runs this method every frame
    public void tick() {
        for(int i = 0; i < objects.size(); i++) {
            GameObject tempObject = objects.get(i);
            tempObject.tick();
        }
    }
    // Renders game objects
    public void render(Graphics g) {
        for(int i = 0; i < objects.size(); i ++) {
            GameObject tempObject = objects.get(i);
            tempObject.render(g);
        }
    }
    // Used to create objects
    public void addObject(GameObject object) {
        this.objects.add(object);
    }
    // Used to remove objects
    public void removeObject(GameObject object) {
        this.objects.remove(object);
    }
}