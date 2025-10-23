package clip;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {
    private final Handler handler;

    public MouseInput(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handler.setMousePositionForMouse(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handler.setMousePositionForMouse(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handler.resetMousePosition();
    }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
