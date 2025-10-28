package aiden.clip.ui;

import javax.swing.*;
import aiden.clip.core.ConfigManager;
import aiden.clip.core.Game;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.Objects;

public class Window extends Canvas {
    @Serial
    private static final long serialVersionUID = -240840600533728354L;

    public Window(Game game, ConfigManager config) {
        JFrame frame = new JFrame("Nibs’ Paperclip Collector");

        // --- Detect actual screen size ---
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        frame.setUndecorated(config.fullscreen);
        frame.setResizable(!config.fullscreen);

        if (config.fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);
        }

        // --- Set canvas size ---
        game.setPreferredSize(new Dimension(width, height));
        game.setMinimumSize(new Dimension(width / 2, height / 2));
        game.setMaximumSize(new Dimension(width, height));

        // --- Window close handling ---
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (game.getGameState() == aiden.clip.core.GameState.GAME) {
                    game.getGameManager().saveGame();
                }
                System.exit(0);
            }
        });

        // --- Window icon ---
        try {
            ImageIcon image = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/oso.png")));
            frame.setIconImage(image.getImage());
        } catch (Exception e) {
            System.out.println("Error loading oso.png");
        }

        frame.add(game);
        frame.pack();
        frame.setVisible(true);

        game.start();
    }
}
