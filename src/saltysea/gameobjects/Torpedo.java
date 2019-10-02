package saltysea.gameobjects;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import saltysea.SaltySeaGUI;
import saltysea.SaltySeaPanel;

public class Torpedo extends GameObject implements Runnable {

    private Thread thread;
    private boolean run;

    public Torpedo(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, gui, panel, 1);
        this.run = false;
        this.thread = new Thread(this);
        super.images[0] = super.loadImage("torpedo");
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

    @Override
    public void run() {
        while (run) {
            direction = panel.getPlayer().getDirection();
            this.move(speed * Math.cos(Math.toRadians(direction - 90)), speed * Math.sin(Math.toRadians(direction - 90)));
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
        x = panel.getPlayer().getCenterX();
        y = panel.getPlayer().getCenterY();
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        run = false;
        thread.interrupt();
    }
}
