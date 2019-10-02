package saltysea.gameobjects;

import saltysea.SaltySeaPanel;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import saltysea.SaltySeaGUI;

public abstract class GameObject {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    public double speed;
    public double direction; // in degrees
    protected SaltySeaGUI gui;
    protected final SaltySeaPanel panel;
    protected final Rectangle2D.Double rectangle;
    public boolean[] toAnimate; // determines which list(s) should be animated when the object's draw method is called, false by default
    protected final BufferedImage[] images; // array containing BufferedImages arranged in a sequence for animation

    public GameObject(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel, int numberOfAnimations) {
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
        this.speed = speed;
        this.gui = gui;
        this.panel = panel;
        this.rectangle = new Rectangle2D.Double();
        this.toAnimate = new boolean[numberOfAnimations];
        this.images = new BufferedImage[numberOfAnimations];
    }

    public void draw(Graphics2D g) {
        TexturePaint texture;

        for (int i = 0; i < images.length; i++) {
            if (toAnimate[i] == true) {

                width = images[i].getWidth();
                height = images[i].getHeight();
                rectangle.setFrame(x, y, width, height); // x, y = upper left corner
                texture = new TexturePaint(images[i], rectangle);
                g.setPaint(texture);
                g.fill(rectangle);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    System.out.println("Something went wrong");
                }

                toAnimate[i] = false;
            }
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public BufferedImage loadImage(String keyWord) {
        // the variable keyWord contains a string that describes the action 
        // images will be named with the convention "keyWord" + 1, where 1 represents the first image of the animation
        // all of the images named with the string contained in the variable keyWord are contained in a folder with the same name

        try {
            return ImageIO.read(new File("Images//" + keyWord + "//0.png"));
        } catch (IOException | NullPointerException e) {
            System.out.println(keyWord + ": file not found");
        }

        return null;
    }

    public void move(double dx, double dy) {
        if (!panel.gameOver()) {
            x += dx;
            y += dy;

            // Check if game object is off-screen, changes direction and posistion if obj is offscreen
            if (x + width < 0) {
                x = gui.getGamePanelWidth();
                //direction = new Random().nextInt(360);
            }

            if (x > gui.getGamePanelWidth()) {
                x = -width;
                //direction = new Random().nextInt(360);
            }

            if (y + height < 0) {
                y = gui.getGamePanelHeight();
                //direction = new Random().nextInt(360);
            }

            if (y > gui.getGamePanelHeight()) {
                y = -height;
                //direction = new Random().nextInt(360);
            }
        }
    }

    public Rectangle2D.Double getRectangle() {
        return rectangle;
    }
}
