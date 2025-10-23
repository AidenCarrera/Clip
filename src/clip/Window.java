package clip;

import javax.swing.*;
import java.awt.*;

public class Window extends Canvas {
    private static final long serialVersionUID = -240840600533728354L;

    public Window(int width, int height, String title, Game game) {
        JFrame frame = new JFrame(title);

        // --- Fullscreen setup ---
        frame.setUndecorated(true); // removes title bar and borders
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // maximizes window to fill the screen

        // --- Optional fallback if fullscreen fails ---
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width / 2, height / 2));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/images/oso.png"));
            frame.setIconImage(image.getImage());
        } catch (Exception e) {
            System.out.println("Error loading oso.png");
        }

        frame.add(game);
        frame.setVisible(true);
        game.start();
    }
}