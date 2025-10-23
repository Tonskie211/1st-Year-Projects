import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.Random;
import java.io.File;
import javax.sound.sampled.*;

public class ImagenInvicta extends JFrame {

    // COLORS & FONTS (Modern Color Scheme)
    private static final Color BACKGROUND_COLOR = new Color(128, 203, 196); // Balanced teal
    private static final Color BUTTON_COLOR_PRIMARY = new Color(0, 150, 136); // teal
    private static final Color BUTTON_COLOR_EXIT = new Color(244, 67, 54);    // red
    private static final Color BUTTON_COLOR_HINT = new Color(77, 182, 172); // Medium teal for contrast
    private static final Color BUTTON_COLOR_PASTEL = new Color(156, 39, 176); //Purple
    private static final Color BOX_COLOR = Color.WHITE;
    
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 60);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 40);
    private static final Font LEVEL_FONT = new Font("Arial", Font.BOLD, 50);
    private static final Font CLUE_FONT = new Font("Arial", Font.BOLD, 30);
    private static final Font LETTER_FONT = new Font("Arial", Font.BOLD, 40);
    
    // State & Level Locking
    private int currentLevel = 1;
    private int highestUnlockedLevel = 1;
    
    // Volume settings (0.0 to 1.0)
    private float bgVolume = 0.5f;
    private float seVolume = 0.5f;
    
    // Music Clips
    private Clip bgMusicClip;
    
    public ImagenInvicta() {
        setupFrame();
        playBackgroundMusic();
        showHomeScreen();
        setVisible(true);
    }
    
    // SETUP
    private void setupFrame() {
        setTitle("Imagen Invicta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }
    
    // Helper: Set volume on a Clip using MASTER_GAIN control.
    private void setClipVolume(Clip clip, float volume) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (20 * Math.log10(volume));
            gainControl.setValue(dB);
        } catch (Exception e) {
            System.err.println("Volume control not supported: " + e.getMessage());
        }
    }
    
    // BACKGROUND MUSIC (MAIN)
    private void playBackgroundMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("music/main.wav"));
            bgMusicClip = AudioSystem.getClip();
            bgMusicClip.open(audioStream);
            setClipVolume(bgMusicClip, bgVolume);
            bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            System.err.println("Main background music could not be loaded: " + ex.getMessage());
        }
    }
    
    // LEVEL COMPLETE SOUND
    private void playLevelCompleteSound() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("music/yay.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            setClipVolume(clip, seVolume);
            clip.start();
        } catch (Exception ex) {
            System.err.println("Level complete sound could not be loaded: " + ex.getMessage());
        }
    }
    
    // WRONG ANSWER SOUND
    private void playWrongAnswerSound() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("music/wrong.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            setClipVolume(clip, seVolume);
            clip.start();
        } catch (Exception ex) {
            System.err.println("Wrong answer sound could not be loaded: " + ex.getMessage());
        }
    }
    
    // HOME SCREEN 
    private void showHomeScreen() {
        getContentPane().removeAll();
        BackgroundPanel homePanel = new BackgroundPanel("/images/Home.jpg");
        homePanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Imagen Invicta", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 100));
        buttonPanel.setOpaque(false);
        
        JButton startButton = createStyledButton("Start", BUTTON_COLOR_PRIMARY);
        startButton.setPreferredSize(new Dimension(250, 100));
        startButton.addActionListener(e -> showLevelSelectionScreen());
        
        JButton settingsButton = createStyledButton("Settings", BUTTON_COLOR_HINT);
        settingsButton.setPreferredSize(new Dimension(250, 100));
        settingsButton.addActionListener(e -> showSettingsScreen());
        
        JButton exitButton = createStyledButton("Exit", BUTTON_COLOR_EXIT);
        exitButton.setPreferredSize(new Dimension(250, 100));
        exitButton.addActionListener(e -> {
            if(bgMusicClip != null) { bgMusicClip.stop(); }
            System.exit(0);
        });
        
        buttonPanel.add(startButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);
        
        homePanel.add(titleLabel, BorderLayout.NORTH);
        homePanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(homePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // SETTINGS SCREEN
    private void showSettingsScreen() {
        getContentPane().removeAll();
        BackgroundPanel settingsPanel = new BackgroundPanel("/images/Home.jpg");
        settingsPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Background Music Volume Slider
        JSlider bgVolumeSlider = new JSlider(0, 100, (int)(bgVolume * 100));
        bgVolumeSlider.setMajorTickSpacing(25);
        bgVolumeSlider.setMinorTickSpacing(25);
        bgVolumeSlider.setPaintTicks(true);
        bgVolumeSlider.setPaintLabels(true);
        bgVolumeSlider.setForeground(Color.WHITE);
        bgVolumeSlider.setBackground(BACKGROUND_COLOR);
        bgVolumeSlider.setBorder(BorderFactory.createTitledBorder("Background Music Volume"));
        bgVolumeSlider.addChangeListener(e -> {
            bgVolume = bgVolumeSlider.getValue() / 100f;
            if (bgMusicClip != null) {
                setClipVolume(bgMusicClip, bgVolume);
            }
        });
        
        // Sound Effects Volume Slider
        JSlider seVolumeSlider = new JSlider(0, 100, (int)(seVolume * 100));
        seVolumeSlider.setMajorTickSpacing(25);
        seVolumeSlider.setMinorTickSpacing(25);
        seVolumeSlider.setPaintTicks(true);
        seVolumeSlider.setPaintLabels(true);
        seVolumeSlider.setForeground(Color.WHITE);
        seVolumeSlider.setBackground(BACKGROUND_COLOR);
        seVolumeSlider.setBorder(BorderFactory.createTitledBorder("Sound Effects Volume"));
        seVolumeSlider.addChangeListener(e -> {
            seVolume = seVolumeSlider.getValue() / 100f;
        });
        
        // Created By Label
        JPanel createdByPanel = new JPanel();
        createdByPanel.setLayout(new BoxLayout(createdByPanel, BoxLayout.Y_AXIS));
        createdByPanel.setOpaque(false); // Match centerPanel styling
        createdByPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Color labelColor = Color.WHITE;

        String[] names = {
          "Created By:",
          "Ramos, John Antonio C.",
          "Cayabyab, Kim Arnold D.",
          "Gonzales, Kim Francez J.",
          "Salvador, Randall Benedict R."
        };

        for (String name : names) {
            JLabel label = new JLabel(name, SwingConstants.CENTER);
            label.setFont(labelFont);
            label.setForeground(labelColor);
            label.setAlignmentX(Component.CENTER_ALIGNMENT); 
            createdByPanel.add(label);
        }

        
        JButton backButton = createStyledButton("Back", BUTTON_COLOR_EXIT);
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.addActionListener(e -> showHomeScreen());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(bgVolumeSlider);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(seVolumeSlider);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(createdByPanel);

        
        settingsPanel.add(topPanel, BorderLayout.NORTH);
        settingsPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(settingsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // LEVEL SELECTION SCREEN
    private void showLevelSelectionScreen() {
        getContentPane().removeAll();
        BackgroundPanel levelSelectPanel = new BackgroundPanel("/images/Home.jpg");
        levelSelectPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Select a Level", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JPanel levelPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        levelPanel.setOpaque(false);
        levelPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        for (int i = 1; i <= 10; i++) {
            final int lvl = i;
            JButton levelButton = new JButton("Level " + i);
            levelButton.setFont(new Font("Arial", Font.BOLD, 20));
            levelButton.setForeground(Color.WHITE);
            levelButton.setBackground(BUTTON_COLOR_PRIMARY);
            levelButton.setFocusPainted(false);
            if (i > highestUnlockedLevel) {
                levelButton.setEnabled(false);
            }
            levelButton.addActionListener(e -> showLevelScreen(lvl));
            levelPanel.add(levelButton);
        }
        
        JButton backButton = createStyledButton("Back", BUTTON_COLOR_EXIT);
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.addActionListener(e -> showHomeScreen());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        levelSelectPanel.add(topPanel, BorderLayout.NORTH);
        levelSelectPanel.add(levelPanel, BorderLayout.CENTER);
        
        add(levelSelectPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // LEVEL SCREEN
    private void showLevelScreen(int level) {
        getContentPane().removeAll();
        currentLevel = level;
        String word = getWordForLevel(level).toUpperCase();
        String[] imagePaths = getImagePathsForLevel(level);
        BackgroundPanel levelPanel = new BackgroundPanel("/images/Home.jpg");
        levelPanel.setLayout(new BorderLayout());
        
        JLabel levelLabel = new JLabel("Level " + level, SwingConstants.CENTER);
        levelLabel.setFont(LEVEL_FONT);
        levelLabel.setForeground(Color.WHITE);
        
        JButton backButton = createStyledButton("Back", BUTTON_COLOR_EXIT);
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.addActionListener(e -> showLevelSelectionScreen());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(levelLabel, BorderLayout.CENTER);
        
        JPanel hintsPanel = new JPanel();
        hintsPanel.setLayout(new BoxLayout(hintsPanel, BoxLayout.Y_AXIS));
        hintsPanel.setOpaque(false);
        hintsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JButton hintDefinitionBtn = createStyledButton("Definitions", BUTTON_COLOR_HINT);
        hintDefinitionBtn.setFont(new Font("Arial", Font.BOLD, 20));
        hintDefinitionBtn.setPreferredSize(new Dimension(150, 60));
        hintDefinitionBtn.addActionListener(e -> {
            UIManager.put("OptionPane.background", BUTTON_COLOR_HINT);
            UIManager.put("Panel.background", BUTTON_COLOR_HINT);
            JOptionPane.showMessageDialog(this, getDefinitionHint(level), "Hint: Definition", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton hintLetterBtn = createStyledButton("One Letter", BUTTON_COLOR_HINT);
        hintLetterBtn.setFont(new Font("Arial", Font.BOLD, 20));
        hintLetterBtn.setPreferredSize(new Dimension(150, 60));
        hintLetterBtn.addActionListener(e -> {
            int randomIndex = new Random().nextInt(word.length());
            UIManager.put("OptionPane.background", BUTTON_COLOR_HINT);
            UIManager.put("Panel.background", BUTTON_COLOR_HINT);
            JOptionPane.showMessageDialog(this,
                "One letter is: " + word.charAt(randomIndex),
                "Hint: Letter",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton hintCategoryBtn = createStyledButton("Categories", BUTTON_COLOR_HINT);
        hintCategoryBtn.setFont(new Font("Arial", Font.BOLD, 20));
        hintCategoryBtn.setPreferredSize(new Dimension(150, 60));
        hintCategoryBtn.addActionListener(e -> {
            UIManager.put("OptionPane.background", BUTTON_COLOR_HINT);
            UIManager.put("Panel.background", BUTTON_COLOR_HINT);
            JOptionPane.showMessageDialog(this,
                getCategoryHint(level),
                "Hint: Category",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        hintsPanel.add(hintDefinitionBtn);
        hintsPanel.add(Box.createVerticalStrut(20));
        hintsPanel.add(hintLetterBtn);
        hintsPanel.add(Box.createVerticalStrut(20));
        hintsPanel.add(hintCategoryBtn);
        hintsPanel.add(Box.createVerticalGlue());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        int cellSize = 270;
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 11, 11));
        gridPanel.setOpaque(false);
        for (String path : imagePaths) {
            ImagePanel imagePanel = new ImagePanel(path, cellSize);
            imagePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            gridPanel.add(imagePanel);
        }
        int gridDimension = 2 * cellSize + 10;
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setOpaque(false);
        wrapperPanel.setPreferredSize(new Dimension(gridDimension, gridDimension));
        wrapperPanel.add(gridPanel);
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(wrapperPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        
        JPanel letterBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        letterBoxPanel.setOpaque(false);
        final int wordLength = word.length();
        final JLabel[] letterBoxes = new JLabel[wordLength];
        Dimension fixedSize = new Dimension(80, 80);
        for (int i = 0; i < wordLength; i++) {
            JLabel letterBox = new JLabel("", SwingConstants.CENTER);
            letterBox.setFont(LETTER_FONT);
            letterBox.setForeground(Color.WHITE);
            letterBox.setOpaque(true);
            letterBox.setBackground(BACKGROUND_COLOR);
            letterBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            letterBox.setPreferredSize(fixedSize);
            letterBox.setMinimumSize(fixedSize);
            letterBox.setMaximumSize(fixedSize);
            letterBox.putClientProperty("letterButton", null);
            letterBoxes[i] = letterBox;
            letterBoxPanel.add(letterBox);
        }
        final int[] currentPos = {0};
        letterBoxPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(letterBoxPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        
        JPanel letterButtonPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        letterButtonPanel.setOpaque(false);
        String[] letters = generateRandomLetters(word, 10);
        for (String letter : letters) {
            JButton letterButton = new JButton(letter);
            letterButton.setFont(LETTER_FONT);
            letterButton.setBackground(BUTTON_COLOR_PRIMARY);
            letterButton.setForeground(Color.WHITE);
            letterButton.setFocusPainted(false);
            letterButton.addActionListener(e -> {
                if (currentPos[0] < wordLength) {
                    letterBoxes[currentPos[0]].setText(letter);
                    letterBoxes[currentPos[0]].putClientProperty("letterButton", letterButton);
                    currentPos[0]++;
                    letterButton.setEnabled(false);
                }
            });
            letterButtonPanel.add(letterButton);
        }
        letterButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(letterButtonPanel);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        actionPanel.setOpaque(false);
        JButton deleteButton = createStyledButton("Delete", BUTTON_COLOR_EXIT);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 30));
        deleteButton.addActionListener(e -> {
            if (currentPos[0] > 0) {
                currentPos[0]--;
                JLabel box = letterBoxes[currentPos[0]];
                box.setText("");
                JButton usedButton = (JButton) box.getClientProperty("letterButton");
                if (usedButton != null) {
                    usedButton.setEnabled(true);
                    box.putClientProperty("letterButton", null);
                }
            }
        });
        JButton submitButton = createStyledButton("Submit", BUTTON_COLOR_PRIMARY);
        submitButton.setFont(new Font("Arial", Font.BOLD, 30));
        submitButton.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (JLabel lb : letterBoxes) {
                sb.append(lb.getText());
            }
            if (sb.toString().equalsIgnoreCase(word)) {
                playLevelCompleteSound();
                if (currentLevel < 10 && currentLevel + 1 > highestUnlockedLevel) {
                    highestUnlockedLevel = currentLevel + 1;
                }
                JOptionPane.showMessageDialog(this, "Correct! Proceeding to the next level.", "Result", JOptionPane.INFORMATION_MESSAGE);
                int nextLevel = currentLevel + 1;
                if (nextLevel <= 10) {
                    showLevelScreen(nextLevel);
                } else {
                    JOptionPane.showMessageDialog(this, "You've completed all levels!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    showHomeScreen();
                }
            } else {
                playWrongAnswerSound();
                JOptionPane.showMessageDialog(this, "Incorrect. Try Again!", "Result", JOptionPane.WARNING_MESSAGE);
            }
        });
        actionPanel.add(deleteButton);
        actionPanel.add(submitButton);
        
        JPanel levelScreenPanel = new JPanel(new BorderLayout());
        levelScreenPanel.setOpaque(false);
        levelScreenPanel.add(topPanel, BorderLayout.NORTH);
        levelScreenPanel.add(hintsPanel, BorderLayout.WEST);
        levelScreenPanel.add(centerPanel, BorderLayout.CENTER);
        levelScreenPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(levelScreenPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // BUTTON FACTORY
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
    // LEVEL -> WORD MAPPING
    private String getWordForLevel(int level) {
        switch (level) {
            case 1: return "SUN";
            case 2: return "FISH";
            case 3: return "TREE";
            case 4: return "BOOK";
            case 5: return "CLOUD";
            case 6: return "BRIDGE";
            case 7: return "SHADOW";
            case 8: return "ECHO";
            case 9: return "RHINO";
            case 10: return "ABSTRACT";
            default: return "LEVEL";
        }
    }
    
    // LEVEL -> IMAGE PATHS
    private String[] getImagePathsForLevel(int level) {
        switch (level) {
            case 1:
                return new String[]{
                    "images/SUN-1.jpg",
                    "images/SUN-2.jpg",
                    "images/SUN-3.jpg",
                    "images/SUN-4.jpg"
                };
            case 2:
                return new String[]{
                    "images/FISH-1.jpg",
                    "images/FISH-2.jpg",
                    "images/FISH-3.jpg",
                    "images/FISH-4.jpg"
                };
            case 3:
                return new String[]{
                    "images/TREE-1.jpg",
                    "images/TREE-2.jpg",
                    "images/TREE-3.jpg",
                    "images/TREE-4.jpg"
                };
            case 4:
                return new String[]{
                    "images/BOOK-1.jpg",
                    "images/BOOK-2.jpg",
                    "images/BOOK-3.jpg",
                    "images/BOOK-4.jpg"
                };
            case 5:
                return new String[]{
                    "images/CLOUD-1.jpg",
                    "images/CLOUD-2.jpg",
                    "images/CLOUD-3.jpg",
                    "images/CLOUD-4.jpg"
                };
            case 6:
                return new String[]{
                    "images/BRIDGE-1.jpg",
                    "images/BRIDGE-2.jpg",
                    "images/BRIDGE-3.jpg",
                    "images/BRIDGE-4.jpg"
                };
            case 7:
                return new String[]{
                    "images/SHADOW-1.jpg",
                    "images/SHADOW-2.jpg",
                    "images/SHADOW-3.jpg",
                    "images/SHADOW-4.jpg"
                };
            case 8:
                return new String[]{
                    "images/ECHO-1.jpg",
                    "images/ECHO-2.jpg",
                    "images/ECHO-3.jpg",
                    "images/ECHO-4.jpg"
                };
            case 9:
                return new String[]{
                    "images/RHINO-1.jpg",
                    "images/RHINO-2.jpg",
                    "images/RHINO-3.jpg",
                    "images/RHINO-4.jpg"
                };
            case 10:
                return new String[]{
                    "images/ABSTRACT-1.jpg",
                    "images/ABSTRACT-2.jpg",
                    "images/ABSTRACT-3.jpg",
                    "images/ABSTRACT-4.jpg"
                };
            default:
                return new String[]{"", "", "", ""};
        }
    }
    
    // GENERATE LETTER BANK
    private String[] generateRandomLetters(String word, int totalLetters) {
        word = word.toUpperCase();
        if (word.length() >= totalLetters) {
            return word.substring(0, totalLetters).chars()
                    .mapToObj(c -> String.valueOf((char)c))
                    .toArray(String[]::new);
        }
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder pool = new StringBuilder(word);
        while (pool.length() < totalLetters) {
            char randomChar = alphabet.charAt((int) (Math.random() * alphabet.length()));
            pool.append(randomChar);
        }
        String[] letters = pool.toString().split("");
        for (int i = 0; i < letters.length; i++) {
            int swapIndex = (int) (Math.random() * letters.length);
            String temp = letters[i];
            letters[i] = letters[swapIndex];
            letters[swapIndex] = temp;
        }
        return letters;
    }
    
    // HINTS
    private String getDefinitionHint(int level) {
        switch (level) {
            case 1:  return "DEFINITION 1: The star at the center of the solar system that provides heat and light to Earth. DEFINITION 2: A symbol of brightness, warmth, and positivity.";
            case 2:  return "DEFINITION 1: An aquatic animal with gills and fins that lives in water. DEFINITION 2: A common food source, often prepared in various cuisines worldwide.";
            case 3:  return "DEFINITION 1: A tall plant with a trunk and branches that produces oxygen. DEFINITION 2: A symbol of growth and stability.";
            case 4:  return "DEFINITION 1: A set of written, printed, or blank pages bound together. DEFINITION 2: A source of knowledge, stories, and imagination.";
            case 5:  return "DEFINITION 1: A mass of condensed water vapor in the sky that can produce rain. DEFINITION 2: A storage system for digital data accessible via the internet.";
            case 6:  return "DEFINITION 1: A structure built to span a physical obstacle, such as a river or road. DEFINITION 2: A connection between two places, ideas, or people, often symbolizing unity.";
            case 7:  return "DEFINITION 1: A dark area or shape produced when an object blocks light. DEFINITION 2: A faint or indirect presence of something, often used metaphorically.";
            case 8:  return "DEFINITION 1: A sound that is reflected off a surface and heard again. DEFINITION 2: A repeated statement, idea, or event that resonates over time.";
            case 9:  return "DEFINITION 1: A large, thick-skinned herbivorous mammal with one or two horns. DEFINITION 2: A powerful and endangered species often targeted for its horn.";
            case 10: return "DEFINITION 1: A concept or idea that does not have a physical or concrete existence. DEFINITION 2: A style of art focusing on shapes and colors rather than realism.";
            default: return "No definition hint for this level.";
        }
    }
    
    private String getCategoryHint(int level) {
        switch (level) {
            case 1:  return "Category: Nature/Astronomy";
            case 2:  return "Category: Animals/Food";
            case 3:  return "Category: Nature/Environment";
            case 4:  return "Category: Objects/Education";
            case 5:  return "Category: Weather/Nature";
            case 6:  return "Category: Architecture/Engineering";
            case 7:  return "Category: Science/Optics";
            case 8:  return "Category: Physics/Sound";
            case 9:  return "Category: Animals/Wildlife Conservation";
            case 10: return "Category: Concept/Art";
            default: return "No category hint for this level.";
        }
    }
    
    // BACKGROUND PANEL CLASS
    class BackgroundPanel extends JPanel {
        private Image bgImage;
        public BackgroundPanel(String imagePath) {
            try {
                bgImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            } catch (Exception e) {
                System.err.println("Background image not found: " + imagePath);
                bgImage = null;
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(BUTTON_COLOR_PASTEL);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    // CUSTOM IMAGEPANEL (COVER EFFECT)
    class ImagePanel extends JPanel {
        private Image image;
        private int panelSize;
        public ImagePanel(String imagePath, int size) {
            this.panelSize = size;
            setLayout(new BorderLayout());
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon icon = new ImageIcon(imagePath);
                this.image = icon.getImage();
            }
            setPreferredSize(new Dimension(panelSize, panelSize));
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(panelSize, panelSize);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = image.getWidth(this);
                int imgHeight = image.getHeight(this);
                if (imgWidth < 1 || imgHeight < 1 || panelWidth < 1 || panelHeight < 1) {
                    g.drawImage(image, 0, 0, this);
                    return;
                }
                double panelAspect = (double) panelWidth / panelHeight;
                double imageAspect = (double) imgWidth / imgHeight;
                int newWidth, newHeight;
                int xOffset = 0, yOffset = 0;
                if (panelAspect > imageAspect) {
                    newWidth = panelWidth;
                    newHeight = (int) (newWidth / imageAspect);
                    yOffset = (panelHeight - newHeight) / 2;
                } else {
                    newHeight = panelHeight;
                    newWidth = (int) (newHeight * imageAspect);
                    xOffset = (panelWidth - newWidth) / 2;
                }
                g.drawImage(image, xOffset, yOffset, newWidth, newHeight, this);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImagenInvicta::new);
    }
}
