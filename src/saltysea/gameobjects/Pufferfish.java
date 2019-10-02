package saltysea.gameobjects;

import saltysea.SaltySeaPanel;
import saltysea.SaltySeaGUI;

public class Pufferfish extends Enemy {

    public Pufferfish(double x, double y, double speed, SaltySeaGUI gui, SaltySeaPanel panel) {
        super(x, y, 30, 1000, 600, 3, gui, panel); // modify 
        images[0] = super.loadImage("pufferfish");
        images[1] = super.loadImage("damaged pufferfish");
    }
}
