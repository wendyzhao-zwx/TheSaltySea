package saltysea.gameobjects;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import saltysea.SaltySeaPanel;
import saltysea.SaltySeaGUI;

public class Player extends GameObject implements Runnable {

    private final Thread thread;
    private boolean run;

    public Player(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, gui, panel, 2);
        this.direction = 0;
        super.images[0] = super.loadImage("player"); // player stationary
        this.thread = new Thread(this);
        this.run = false;
        this.start();
    }

    @Override
    public void draw(Graphics2D g) {
        TexturePaint texture;

        for (int i = 0; i < images.length; i++) {
            if (toAnimate[i] == true) {
                width = images[i].getWidth();
                height = images[i].getHeight();
                rectangle.setFrame(x, y, width, height);
                texture = new TexturePaint(images[i], rectangle);
                g.rotate(Math.toRadians(direction), x + width / 2, y + height / 2);
                g.setPaint(texture);
                g.fill(rectangle);

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    System.out.println("Something went wrong");
                }

                g.rotate(-Math.toRadians(direction), x + width / 2, y + height / 2);
                toAnimate[i] = false;
            }
        }
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double theta) {
        direction += theta;

        if (direction < 0) {
            direction += 360;
        } else if (direction > 360) {
            direction -= 360;
        }
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double newSpeed) {
        speed = newSpeed;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void start() {
        run = true;

        try {
            thread.start();
        } catch (IllegalThreadStateException exception) {

        }
    }
    
    public double getCenterX() {
        return images[0].getWidth() / 2 + x - 5;
    }
    
    public double getCenterY() {
        return images[0].getHeight() / 2 + y;
    }
    
    public void stop() {
        run = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (run) {
            try {
                this.move(speed * Math.cos(Math.toRadians(direction - 90)), speed * Math.sin(Math.toRadians(direction - 90)));
                panel.repaint();
                thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }
}
