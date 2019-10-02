package saltysea.gameobjects;

import saltysea.SaltySeaPanel;
import java.util.Random;
import saltysea.SaltySeaGUI;

public class Bomb extends GameObject implements Runnable {

    private boolean collected;
    private boolean thrown;
    private boolean run;
    private final Thread thread;

    public Bomb(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, gui, panel, 2);
        this.collected = false;
        this.thrown = false;
        this.run = false;
        this.thread = new Thread(this);
        // Load images for animating
        images[0] = super.loadImage("bomb"); // bomb in stationary state
        images[1] = super.loadImage("bomb explodes"); // after explosion debris cloud  
    }

    public boolean active() {
        return run; // determines if this bomb appears on the screen 
    }

    public void collect() {
        this.collected = true;
        this.gui.addBombs(1);
        this.stop();
        this.x = 50;
        this.y = 50;
    }

    public boolean collected() {
        return collected;
    }

    public void fire() {
        this.thrown = true;
        this.toAnimate[1] = true; // animate after explosion debris cloud
        this.gui.addBombs(-1); // remove a bomb
    }

    @Override
    public void run() { // creates nice floating effect for bomb
        while (run) {
            try {
                this.direction = new Random().nextInt(360);
                this.move(Math.cos(Math.toRadians(direction)), Math.sin(Math.toRadians(direction)));
                this.panel.repaint();
                thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }

    public void start() {
        this.run = true;
        this.thread.start();
    }

    public void stop() {
        this.run = false;
        this.thread.interrupt();
    }

    public boolean thrown() {
        return thrown;
    }
}
