package saltysea.gameobjects;

import saltysea.SaltySeaPanel;
import saltysea.SaltySeaGUI;

public class Clam extends Enemy {
    
    private final Pearl pearl;
    
    public Clam(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, 500, 400, 3, gui, panel); 
        // Load image files required for animation        
        images[0] = super.loadImage("clam"); // stationary clam
        images[1] = super.loadImage("damaged clam"); // damaged clam
        this.pearl = new Pearl(this.getCenterX(), this.getCenterY(), 10, this, gui, panel);
        //pearl.start();
    }
    
    @Override
    public void run() {
        while (this.active()) {
            try {
                this.move(this.speed * Math.cos(Math.toRadians(direction)), this.speed * Math.sin(Math.toRadians(direction)));
                
                if (!pearl.active()) {
                    pearl.start();
                }
                
                panel.repaint();
                thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }            
    }
    
    public double getCenterX() {
        return images[0].getWidth() / 2 + x;
    }

    public double getCenterY() {
        return images[0].getHeight() / 2 + y;
    }    

    protected double getDirection() {
        return direction; 
    }
    
    public Pearl getPearl() {
        return pearl; 
    }
}
