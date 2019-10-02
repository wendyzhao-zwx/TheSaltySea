package saltysea;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import static saltysea.SaltySeaGUI.playSound;
import saltysea.gameobjects.Bomb;
import saltysea.gameobjects.Clam;
import saltysea.gameobjects.Enemy;
import saltysea.gameobjects.GameObject;
import saltysea.gameobjects.Pearl;
import saltysea.gameobjects.Player;
import saltysea.gameobjects.Pufferfish;
import saltysea.gameobjects.Squid;
import saltysea.gameobjects.Starfish;
import saltysea.gameobjects.Torpedo;
import saltysea.listeners.KeyboardActions;
import saltysea.listeners.TimeListener;

public class SaltySeaPanel extends JPanel {

    private final SaltySeaGUI gui;
    private final Image image;
    private boolean gameOver;
    //private final Image cover;
    private int level;
    // GameObjects for panel to draw
    private final Player player;
    private final Torpedo torpedo;
    private final LinkedList<Enemy> enemies;
    private final LinkedList<Bomb> bombs;
    // GUI's listeners
    private final TimeListener tl;
    private final KeyboardActions ka;

    public SaltySeaPanel(SaltySeaGUI gui) throws IOException {
        this.gui = gui;
        this.image = ImageIO.read(new File("background.png")); // background
        //this.cover = ImageIO.read(new File("Images//squid ink//0.png")); // background
        this.level = 0;
        this.gameOver = false;
        this.player = new Player(gui.getGamePanelWidth() / 2 - 50, gui.getGamePanelHeight() / 2 - 50, 0, gui, this);
        this.torpedo = new Torpedo(player.getCenterX(), player.getCenterY(), 35, gui, this);
        this.enemies = new LinkedList();
        this.bombs = new LinkedList();
        this.tl = new TimeListener(gui, this);
        this.ka = new KeyboardActions(this, gui);
    }

    public void activateNextGameObject(boolean enemy) {
        if (enemy) {
            for (int i = 0; i < enemies.size(); i++) {
                if (!enemies.get(i).active()) {
                    enemies.get(i).start();
                }
            }
        } else {
            for (int i = 0; i < bombs.size(); i++) {
                if (!bombs.get(i).collected()) {
                    bombs.get(i).start();
                }
            }
        }
    }

    public void addGameObjects(int quantity, int numberActive, GameObject g, boolean randomize) {
        // used for generating the starting posistion of the game object randomly
        Random random = new Random();
        int xDisp = 0, yDisp = 0;

        if (quantity > 0) {
            for (int i = 0; i < quantity; i++) {
                if (randomize) {
                    xDisp = (random.nextInt(gui.getGamePanelWidth() / 2) + 100) * (i % 2 == 0 ? -1 : 1);
                    yDisp = (random.nextInt(gui.getGamePanelHeight() / 2) + 100) * (i % 2 == 0 ? -1 : 1);
                }

                if (g instanceof Starfish) {
                    enemies.add(new Starfish(g.getX() + xDisp, g.getY() + yDisp, g.speed, ((Starfish) g).size, gui, this));
                } else if (g instanceof Pufferfish) {
                    enemies.add(new Pufferfish(g.getX() + xDisp, g.getY() + yDisp, g.speed, gui, this));
                } else if (g instanceof Squid) {
                    enemies.add(new Squid(g.getX() + xDisp, g.getY() + yDisp, g.speed, gui, this));
                } else if (g instanceof Clam) {
                    enemies.add(new Clam(g.getX() + xDisp, g.getY() + yDisp, g.speed, gui, this));
                } else if (g instanceof Bomb) {
                    bombs.add(new Bomb(g.getX() + xDisp, g.getY() + yDisp, g.speed, gui, this));

                    if (i < numberActive) {
                        bombs.getLast().start();
                    }
                }

                if (g instanceof Enemy && i < numberActive) {
                    enemies.getLast().start();
                }
            }
        }
    }

    public boolean allEnemiesDead() {
        return enemies.isEmpty();
    }

    private void detectCollisions(GameObject obj1, GameObject obj2) {
        /*
         Situations to check for:
         - torpedo & enemy
         - player & enemy
         - player & bomb
         - player & pearl (from clam)
         */

        if (obj1.getRectangle().intersects(obj2.getRectangle()) && !gameOver()) {
            if (obj1 instanceof Player && obj2 instanceof Enemy || obj1 instanceof Enemy && obj2 instanceof Player) {
                gui.addHealth(-1); // player loses health after colliding with enemy
                player.move(-player.getX(), -player.getY());
                player.move(gui.getGamePanelWidth() / 2 - 50, gui.getGamePanelHeight() / 2 - 50);
                File sound = new File("crash.wav");
                playSound(sound);
            } else if (obj1 instanceof Player && obj2 instanceof Bomb) {
                ((Bomb) obj2).collect(); // player collects bomb
                File sound = new File("powerup.wav");
                playSound(sound);
            } else if (obj1 instanceof Bomb && obj2 instanceof Player) {
                ((Bomb) obj1).collect(); // player collects bomb
                File sound = new File("powerup.wav");
                playSound(sound);
            } else if (obj1 instanceof Player && obj2 instanceof Pearl) {
                gui.addHealth(-1);
                // Move player to the center
                player.move(-player.getX(), -player.getY());
                player.move(gui.getGamePanelWidth() / 2 - 50, gui.getGamePanelHeight() / 2 - 50);
                ((Pearl) obj2).stop();
            } else if (obj2 instanceof Torpedo && obj1 instanceof Enemy) {
                ((Enemy) obj1).setHealth(-20);
                torpedo.stop();
            } else if (obj1 instanceof Torpedo && obj2 instanceof Enemy) {
                ((Enemy) obj2).setHealth(-20);
                torpedo.stop();
            }
        }
    }

    public void endLevel() {
        gameOver = true;
        playSound(new File("endsound.wav"));
        player.move(-player.getX(), -player.getY());
        player.move(gui.getGamePanelWidth() / 2 - 50, gui.getGamePanelHeight() / 2 - 50);
        this.repaint(); // to draw the game over screen
    }

    // Returns a collected and unfired bomb to fire
    public Bomb getUnfiredBomb() {
        if (bombs.size() > 0) {
            for (int i = 0; i < bombs.size(); i++) {
                if (bombs.get(i).collected() == true && bombs.get(i).thrown() == false) {
                    for (int j = 0; j < enemies.size(); j++) {
                        if (enemies.get(j).active()) {
                            enemies.get(j).setHealth((int) -enemies.get(j).getHealth()); // kill all active enemies
                        }
                    }
                    return bombs.get(i);
                }
            }
        }

        return null;
    }

    public KeyboardActions getKeyListener() {
        return ka;
    }

    public Player getPlayer() {
        return player;
    }

    public Torpedo getTorpedo() {
        return torpedo;
    }

    // Manages the number of enemies and powerups available per level, resets GUI for every new level
    public void increaseLevel(boolean increase) {
        // represents the number of enemies that appear for the first 10 levels
        int[] largeSFQuantity = {2, 4, 4, 5, 5, 6, 6, 7, 7, 8};
        int[] pufferfishQuantity = {1, 1, 2, 2, 3, 3, 4, 4, 4, 5};
        int[] clamQuantity = {1, 1, 1, 2, 2, 2, 3, 3, 5, 6};
        int[] squidQuantity = {1, 0, 1, 1, 1, 1, 1, 1, 1, 10};
        // represents the number of bombs for the first ten levels
        int[] bombQuantity = {3, 1, 1, 1, 1, 1, 1, 2, 2, 3};
        // represents the number of enemies initially active for the first 10 levels
        int[] activeLargeSFQuantity = {2, 2, 2, 2, 2, 3, 3, 3, 4, 3};
        int[] activePufferfishQuantity = {1, 0, 0, 1, 1, 1, 1, 2, 2, 2};
        int[] activeClamQuantity = {1, 0, 1, 1, 0, 1, 1, 2, 2, 2};
        int[] activeSquidQuantity = {1, 0, 0, 0, 0, 0, 0, 1, 1, 2};
        // represents the number of bombs initially active for the first 10 levels
        int[] activeBombQuantity = {1, 1, 0, 0, 1, 1, 1, 1, 0, 0};

        // Increase level
        level += increase == (true && level <= 10) ? 1 : 0;
        // Remove all previously existing game objects from lists
        enemies.clear();

        for (Bomb b : bombs) {
            if (b.thrown() == true) {
                bombs.remove(b);
            }
        }

        // Add enemies required for the level into the enemies list
        this.addGameObjects(largeSFQuantity[Math.min(level - 1, 9)], activeLargeSFQuantity[Math.min(level - 1, 9)], new Starfish(442, 305, 10, 3, gui, this), true);
        this.addGameObjects(pufferfishQuantity[Math.min(level - 1, 9)], activePufferfishQuantity[Math.min(level - 1, 9)], new Pufferfish(442, 305, 10, gui, this), true);
        this.addGameObjects(squidQuantity[Math.min(level - 1, 9)], activeSquidQuantity[Math.min(level - 1, 9)], new Squid(442, 305, 10, gui, this), true);
        this.addGameObjects(clamQuantity[Math.min(level - 1, 9)], activeClamQuantity[Math.min(level - 1, 9)], new Clam(442, 305, 10, gui, this), true);
        // Add bombs available for collection into the bombs list
        this.addGameObjects(bombQuantity[Math.min(level - 1, 9)], activeBombQuantity[Math.min(level - 1, 9)], new Bomb(442, 305, 1, gui, this), true);

        // set up initial stats
        if (level == 1) {
            bombs.get(0).collect();
            bombs.get(1).collect();
        }

        gui.addScore(-gui.getScore());
        gui.addHealth(1);
        tl.startTimer();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.drawImage(image, 0, 0, null); // draw background
        torpedo.draw((Graphics2D) graphics);
        player.toAnimate[0] = true; // set to appear in stationary state
        player.draw((Graphics2D) graphics); // draw player

        for (int j = 0; j < bombs.size(); j++) { // it throws an exception if a for each loop is used
            if (bombs.get(j).active()) {
                bombs.get(j).toAnimate[0] = true;
                this.detectCollisions(player, bombs.get(j));
            }

            bombs.get(j).draw((Graphics2D) graphics);

            if (bombs.get(j).collected() == true && bombs.get(j).thrown() == true) {
                this.removeGameObject(bombs.get(j));
            }
        }

        // draw enemies
        for (int j = 0; j < enemies.size(); j++) {
            if (enemies.get(j).active()) {
                if (enemies.get(j).getHealth() > 0) {
                    enemies.get(j).toAnimate[0] = true; // set to appear in stationary state
                    this.detectCollisions(enemies.get(j), player);
                    this.detectCollisions(enemies.get(j), torpedo);

                    if (enemies.get(j) instanceof Clam) {
                        ((Clam) enemies.get(j)).getPearl().draw((Graphics2D) graphics);
                    }
                }

                enemies.get(j).draw((Graphics2D) graphics);

                if (enemies.get(j) instanceof Clam) {
                    this.detectCollisions(player, ((Clam) enemies.get(j)).getPearl()); // detect collisions between the player and the pearl while it is shooting
                }
            }

            // this is called after the draw method because dying effects have to be animated before enemy can be removed
            if (enemies.get(j).getHealth() <= 0) {
                this.removeGameObject(enemies.get(j));
            }
        }

        if (gameOver == true) {
            Image gameOverScreen = null;

            try {
                gameOverScreen = ImageIO.read(new File("gameover.png"));
            } catch (IOException e) {

            }

            graphics.drawImage(gameOverScreen, 0, 0, null); // draw background
        }
        //graphics.drawImage(cover, 0, 0, null); // draw background
    }

    public void removeGameObject(GameObject obj) {
        if (obj instanceof Enemy) {
            ((Enemy) obj).stop();
            enemies.remove((Enemy) obj);

            // if this enemy is not a large or medium starfish
            if ((obj instanceof Starfish) == false || ((Starfish) obj).size == 1) {
                this.activateNextGameObject(true); // activate the next enemy
            } else {
                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i) instanceof Starfish && ((Starfish) enemies.get(i)).size == ((Starfish) obj).size - 1) {
                        enemies.get(i).start();
                    }
                }
            }
        } else if (obj instanceof Bomb) {
            ((Bomb) obj).stop();
            bombs.remove((Bomb) obj);
            this.activateNextGameObject(false);
        }
    }

    public boolean gameOver() {
        return gameOver;
    }
    
    public LinkedList<Enemy> getEnemies() {
        return enemies;
    }
}
