package saltysea.gameobjects;

import saltysea.SaltySeaPanel;
import java.util.Random;
import saltysea.SaltySeaGUI;

public abstract class Enemy extends GameObject implements Runnable {

    // Enemy statistics
    public int reward;
    public double health;
    // other objects
    protected final Thread thread;
    private boolean run;

    public Enemy(double x, double y, double speed, int reward, double health, int numberOfImages, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, gui, panel, numberOfImages);
        this.reward = reward;
        this.health = health;
        this.direction = new Random().nextInt(360);
        this.run = false;
        this.thread = new Thread(this);
        this.images[images.length - 1] = super.loadImage("red blood"); // when most enemies die (except for squid, blood effect will be reinitiallized in squid constructor)
    }

    public boolean active() {
        return run; // determines if the enemy appears on the screen 
    }

    public double getHealth() {
        return health;
    }

    @Override
    public void run() { // used to move the enemy 
        while (run) {
            try {
                this.move(this.speed * Math.cos(Math.toRadians(direction)), this.speed * Math.sin(Math.toRadians(direction)));
                panel.repaint();
                thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }

    public void setHealth(int increment) {
        health += increment;

        if (health <= 0) {
            gui.addScore(gui.getScore() + reward); // Increase player's score
            toAnimate[toAnimate.length - 2] = true; // animate damaged 
            toAnimate[toAnimate.length - 1] = true; // animate blood effect
        }
    }

    public void start() {
        run = true;

        try {
            thread.start();
        } catch (IllegalThreadStateException exception) {

        }
    }

    public void stop() {
        run = false;
        thread.interrupt();
    }
}
