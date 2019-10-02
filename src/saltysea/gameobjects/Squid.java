package saltysea.gameobjects;

import saltysea.SaltySeaPanel;
import saltysea.SaltySeaGUI;

public class Squid extends Enemy {
    
    public Squid(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, speed, 500, 400, 3, gui, panel); // modify;
        // load images
        images[0] = super.loadImage("squid"); // stationary squid
        images[1] = super.loadImage("damaged squid");
        images[2] = super.loadImage("blue blood"); 
    }
}
