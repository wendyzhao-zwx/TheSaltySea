package saltysea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class SaltySeaGUI {

    public final JFrame frame = new JFrame("The Salty Sea");
    private final File bgm = new File("bgm.wav");
    // GUI's JLabel components
    private final JLabel scoreLabel = new JLabel("000000"); // zero score
    private final JLabel healthLabel = new JLabel("\u2665\u2665"); // the number of health in 1st level - 1
    private final JLabel bombLabel = new JLabel("0");
    private final JLabel timeLabel = new JLabel("0s");
    // GUI's JPanel components
    private final JPanel startPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private final SaltySeaPanel gamePanel = new SaltySeaPanel(this);

    public SaltySeaGUI() throws IOException {

        startPanel.setPreferredSize(new Dimension(884, 684));
        startPanel.setFocusable(true);
        startPanel.requestFocusInWindow();
        ImageIcon icon = new ImageIcon("startScreen.png");
        JLabel img = new JLabel();
        img.setIcon(icon);
        startPanel.add(img);

        startPanel.addKeyListener(new KeyListener() {
            public void actionPerformed(KeyListener e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    frame.setContentPane(mainPanel);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Accessory components 
        JPanel mainDisplay = new JPanel(new GridLayout(1, 4));
        JPanel scorePanel = new JPanel();
        JPanel healthPanel = new JPanel();
        JPanel bombPanel = new JPanel();
        JPanel timePanel = new JPanel();
        JLabel scoreTitle = new JLabel("SCORE:");
        JLabel healthTitle = new JLabel("LIVES:");
        JLabel bombTitle = new JLabel("BOMBS:");
        JLabel timeTitle = new JLabel("TIME:");

        // Set up the score tracker panel
        scoreTitle.setFont(new Font("ARIAL", Font.PLAIN, 10)); // All titles are set to size 10
        scoreLabel.setFont(new Font("ARIAL", Font.PLAIN, 36)); // All other labels are set to size 36
        scorePanel.setOpaque(false); // JPanels being added to main display panel are translucent so the background colour of the main display panel is visible
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.add(scoreTitle);
        scorePanel.add(scoreLabel);

        // Set up the health tracker panel
        healthTitle.setFont(new Font("ARIAL", Font.PLAIN, 10));
        healthLabel.setFont(new Font("ARIAL", Font.PLAIN, 36));
        healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.Y_AXIS));
        healthPanel.setOpaque(false);
        healthPanel.add(healthTitle);
        healthPanel.add(healthLabel);

        // Set up the bomb tracker panel
        bombTitle.setFont(new Font("ARIAL", Font.PLAIN, 10));
        bombLabel.setFont(new Font("ARIAL", Font.PLAIN, 36));
        bombPanel.setLayout(new BoxLayout(bombPanel, BoxLayout.Y_AXIS));
        bombPanel.setOpaque(false);
        bombPanel.add(bombTitle);
        bombPanel.add(bombLabel);

        // Set up the timer tracking panel
        timeTitle.setFont(new Font("ARIAL", Font.PLAIN, 10));
        timeLabel.setFont(new Font("ARIAL", Font.PLAIN, 36));
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setOpaque(false);
        timePanel.add(timeTitle);
        timePanel.add(timeLabel);

        // Set up the main display panel
        mainDisplay.setBackground(new Color(176, 224, 230));
        mainDisplay.add(scorePanel);
        mainDisplay.add(healthPanel);
        mainDisplay.add(bombPanel);
        mainDisplay.add(timePanel);

        // Set up the game panel
        gamePanel.addKeyListener(gamePanel.getKeyListener());
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.setPreferredSize(new Dimension(this.getGamePanelWidth(), this.getGamePanelHeight()));

        // Set up the main panel
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(mainDisplay);
        mainPanel.add((JPanel) gamePanel);
        mainPanel.setPreferredSize(new Dimension(884, 685));

        // Configure the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setContentPane(startPanel);
        frame.pack();

        // Start at level 1
        gamePanel.increaseLevel(true);

        playSound(bgm);
    }

    public void addBombs(int add) {
        bombLabel.setText(String.valueOf(this.getBombs() + add));
    }

    public void addHealth(int add) {
        String health = "";

        for (int i = 0; i < add + this.getHealth(); i++) {
            health += "\u2665"; // Unicode for heart character
        }

        healthLabel.setText(health);
    }

    public void addScore(int newScore) {
        if (String.valueOf(newScore + this.getScore()).length() < 6) {
            String zeroes = "";

            for (int i = 0; i < 6 - String.valueOf(newScore).length(); i++) {
                zeroes += "0";
            }

            scoreLabel.setText(zeroes + String.valueOf(newScore));
        } else {
            scoreLabel.setText(String.valueOf(newScore));
        }
    }

    public int getBombs() {
        return Integer.parseInt(bombLabel.getText());
    }

    public int getGamePanelWidth() {
        return 884;
    }

    public int getGamePanelHeight() {
        return 610;
    }

    public int getHealth() {
        return healthLabel.getText().length();
    }

    public int getScore() {
        return Integer.parseInt(scoreLabel.getText());
    }

    public int getTime() {
        return Integer.parseInt(timeLabel.getText().substring(0, timeLabel.getText().indexOf("s")));
    }

    public void setTime(boolean restart) {
        timeLabel.setText(String.valueOf(restart ? 0 : this.getTime() + 1) + "s");
    }

    public static void playSound(File sound) {
        try {
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(AudioSystem.getAudioInputStream(sound));
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (sound.getName().equals("crash.wav") || sound.getName().equals("endsound.wav")) {
                double gain = .25D; // number between 0 and 1 (loudest)
                float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
            clip.start();
            if (sound.getName().equals("bgm.wav")) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {

        }
    }

    public void addInk(JLabel ink) {
        gamePanel.add(ink);
    }

    public static void main(String[] args) throws IOException {
        SaltySeaGUI newGame = new SaltySeaGUI();
    }
}
