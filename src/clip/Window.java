package clip;

import javax.swing.*;
import java.awt.*;

public class Window extends Canvas {
    private static final long serialVersionUID = -240840600533728354L;
    public Window(int width, int height, String title, Game game) {
        // Basic settings
        JFrame frame = new JFrame(title);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        try {
            // Sets icon of Window to osososos
            ImageIcon image = new ImageIcon(getClass().getResource("assets/images/oso.jpg"));
            frame.setIconImage(image.getImage());
        } catch (Exception e) {
            System.out.println("Error loading oso.jpg");
        }
        frame.add(game);
        frame.setVisible(true);
        game.start();
    }
}