package clip;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {
    // Handler handler;
    private Handler handler;
    public MouseInput(Handler handler) {
        this.handler = handler;
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);
            if (tempObject.getID() == ID.MOUSE) {
                // Sets hitbox to Mouse position
                tempObject.setMouseX(mouseX);
                tempObject.setMouseY(mouseY);
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);
            if (tempObject.getID() == ID.MOUSE) {
                tempObject.setMouseX(mouseX);
                tempObject.setMouseY(mouseY);
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        for (int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);
            if (tempObject.getID() == ID.MOUSE) {
                // Moves Hitbox out of Range
                tempObject.setMouseX(0);
                tempObject.setMouseY(0);
            }
        }
    }
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}