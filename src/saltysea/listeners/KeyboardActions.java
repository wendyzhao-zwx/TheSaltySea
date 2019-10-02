package saltysea.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import saltysea.SaltySeaGUI;
import static saltysea.SaltySeaGUI.playSound;
import saltysea.SaltySeaPanel;
import saltysea.gameobjects.Torpedo;

public class KeyboardActions implements KeyListener {

    private final SaltySeaPanel panel;
    private final SaltySeaGUI gui;
    private boolean gameStarted;
    private final File shoot = new File("shoot.wav");
    private final File bomb = new File("bomb.wav");

    public KeyboardActions(SaltySeaPanel panel, SaltySeaGUI gui) {
        this.panel = panel;
        this.gui = gui;
        this.gameStarted = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!panel.gameOver()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_B:
                    // throw bomb
                    try {
                        panel.getUnfiredBomb().fire();
                        playSound(bomb);
                    } catch (Exception exception) {

                    }
                    break;
                case KeyEvent.VK_SPACE:
                    // shoot
                    panel.getTorpedo().start();
                    playSound(shoot);
                    break;
                case KeyEvent.VK_LEFT:
                    // move left 
                    panel.getPlayer().setDirection(-10);
                    break;
                case KeyEvent.VK_RIGHT:
                    // move right
                    panel.getPlayer().setDirection(10);
                    break;
                case KeyEvent.VK_UP:
                    // accelerate
                    if (panel.getPlayer().getSpeed() <= 40) {
                        panel.getPlayer().setSpeed(panel.getPlayer().getSpeed() + 20);
                    }

                    panel.getPlayer().move(panel.getPlayer().getSpeed() * Math.cos(Math.toRadians(panel.getPlayer().getDirection() - 90)), panel.getPlayer().getSpeed() * Math.sin(Math.toRadians(panel.getPlayer().getDirection() - 90)));
                    break;
            }
        }

        panel.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
