package saltysea.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import saltysea.SaltySeaGUI;
import static saltysea.SaltySeaGUI.playSound;
import saltysea.SaltySeaPanel;
import saltysea.gameobjects.Enemy;
import saltysea.gameobjects.Squid;

public class TimeListener implements ActionListener {

    private final SaltySeaGUI gui;
    private final SaltySeaPanel panel;
    private final Timer timer;
    private int inkDuration;
    private final JLabel ink;
    private final File splash = new File("splash.wav");

    public TimeListener(SaltySeaGUI gui, SaltySeaPanel panel) {
        this.gui = gui;
        this.panel = panel;
        this.timer = new Timer(1000, this);
        this.inkDuration = 2;
        this.ink = new JLabel(new ImageIcon("ink.png"));
    }

    public void startTimer() {
        gui.setTime(true);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gui.setTime(false); // Increases the time by one second
        panel.requestFocus();
        panel.getPlayer().setSpeed(Math.min(Math.max(panel.getPlayer().getSpeed() - 5, 0), 40));

        if (gui.getScore() >= 10000 && gui.getScore() % 10000 == 0) { // increases health for every 10000 points
            gui.addHealth(1);
            gui.addScore(gui.getScore() - 10000);
        }

        if (panel.allEnemiesDead()) {
            timer.stop();
            panel.increaseLevel(panel.allEnemiesDead() && gui.getHealth() > 0); // The level is successfully completed if all enemies are dead before the player
        }

        if (gui.getHealth() == 0) {
            timer.stop();
            panel.endLevel();
        }
        for (Enemy enemy : panel.getEnemies()) {
            if (gui.getHealth() == 0) {
                ink.setVisible(false);
            }
            if (enemy instanceof Squid) {
                gui.addInk(ink);
                if (inkDuration == 0) {
                    ink.setVisible(true);
                    playSound(splash);
                    //inkDuration++;
                } else if (inkDuration > 1) {
                    ink.setVisible(false);
                    //inkDuration++;
                    if (inkDuration == 9) {
                        inkDuration = -1;
                    }
                }
                inkDuration++;
            }
        }
    }
}
