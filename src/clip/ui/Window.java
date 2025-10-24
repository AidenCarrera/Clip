package clip.ui;

import clip.core.ConfigManager;
import clip.core.Game;

import javax.swing.*;
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

        // --- Apply display settings from config ---
        if (config.fullscreen) {
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setUndecorated(false);
            frame.setSize(config.displayWidth, config.displayHeight);
            frame.setLocationRelativeTo(null);
        }

        // --- Fallback minimums ---
        frame.setPreferredSize(new Dimension(config.displayWidth, config.displayHeight));
        frame.setMinimumSize(new Dimension(config.displayWidth / 2, config.displayHeight / 2));

        // --- Close handling ---
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (game.getGameState() == clip.core.GameState.GAME) {
                    game.getGameManager().saveGame();
                    System.out.println("Game saved on window close.");
                } else {
                    System.out.println("Closed during menu – skipping save.");
                }
                System.exit(0);
            }
        });

        frame.setResizable(!config.fullscreen);

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
