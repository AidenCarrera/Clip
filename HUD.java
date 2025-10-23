package Intro.Projects.Clip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import java.awt.Font;

public class HUD {
    private Spawner spawner;
    private Image clipIcon = new ImageIcon(getClass().getResource("clipIcon.png")).getImage();
    private Image bamboo = new ImageIcon(getClass().getResource("bamboo.png")).getImage();
    public HUD(Spawner spawner) {
        this.spawner = spawner;
    }
    public void tick() {}
    public void render(Graphics g) {
        // Draws HUD
        g.drawImage(bamboo, 0, 0, null);
        g.drawImage(clipIcon, 62, 70, null);
        g.setColor(Color.yellow);
        g.setFont(new Font("TimesRoman", Font.BOLD, 24)); 
        g.drawString("" + spawner.getClips(), 82, 93);
        g.setFont(new Font("TimesRoman", Font.BOLD, 24));
        // Removes ColoredUpgrade text once fully upgraded
        if(spawner.getColoredUpgrade() < 100000) {
            g.drawImage(clipIcon, 190, 115, null);
            g.drawString("" + spawner.getColoredUpgrade(), 205, 140);
        }
        g.drawString("(" + spawner.getMoreUpgradeCount() + ")", 50, 232);
        g.drawImage(clipIcon, 83, 210, null);
        g.drawString("" + spawner.getMoreUpgradePrice(), 98, 235);
        g.drawString("(" + spawner.getValueUpgradeCount() + ")", 160, 232);
        g.drawImage(clipIcon, 194, 210, null);
        g.drawString("" + spawner.getValueUpgradePrice(), 208, 235);
    }
}