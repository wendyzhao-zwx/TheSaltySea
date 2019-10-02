package saltysea.gameobjects;

import java.util.Random;
import saltysea.SaltySeaGUI;
import saltysea.SaltySeaPanel;

public class Pearl extends GameObject implements Runnable {

    private boolean run;
    private Thread thread;
    private final Clam clam;

    public Pearl(double x, double y, double speed, Clam clam, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, gui, panel, 1);
        this.run = false;
        this.thread = new Thread(this);
        this.clam = clam;
        super.images[0] = super.loadImage("pearl");
    }

    public boolean active() {
        return run;
    }
    
    @Override
    public void run() {
        while (run) {
            this.move(speed * Math.cos(Math.toRadians(direction)), speed * Math.sin(Math.toRadians(direction)));
            this.toAnimate[0] = true;
            panel.repaint();

            try {
                thread.sleep(30);
            } catch (Exception e) {

            }

            if (x <= 0 || x >= gui.getGamePanelWidth() || y <= 0 || y >= gui.getGamePanelHeight()) {
                this.stop();
            }
        }
    }

    public void start() {
        run = true;
        x = clam.getCenterX();
        y = clam.getCenterY();
        direction = new Random().nextInt(360);
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        run = false;
        thread.interrupt();
    }
}
