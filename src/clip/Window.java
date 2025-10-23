package clip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

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

        // --- Window close handling ---
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // prevent immediate exit
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save game before exit
                game.getGameManager().saveGame();
                System.out.println("Game saved on window close.");
                System.exit(0);
            }
        });

        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

        try {
            ImageIcon image = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/oso.png")));
            frame.setIconImage(image.getImage());
        } catch (Exception e) {
            System.out.println("Error loading oso.png");
        }

        frame.add(game);
        frame.setVisible(true);
        game.start();
    }
}
