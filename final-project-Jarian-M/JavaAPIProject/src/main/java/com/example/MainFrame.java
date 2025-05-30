package com.example;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONObject;

import javazoom.jl.player.Player;
import java.io.BufferedInputStream;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private int score; //instance variable for game score
    private int highScore; //instance variable for game high score
    private int time; //instance variable for game time
    private int currentIndex = 0; //instance int variable for index used to increment after each Pokemon is guessed correctly
    private String correct; //instance variable for correct Pokemon name

    private ArrayList<String> names = new ArrayList<String>(); //instance variable for ArrayList of Pokemon names
    private ArrayList<String> urls = new ArrayList<String>(); //instance variable for ArrayList of Pokemon image URL's
    private ArrayList<Integer> scoreList = new ArrayList<Integer>(); //instance variable for ArrayList of high scores

    private Map<String, String> pokeHigh = new HashMap<String, String>(); //instance variable for HashMap of the game's data that's saved
    private Map<String, ArrayList<Integer>> loadScores = new HashMap<String, ArrayList<Integer>>(); //instance variable for HashMap of the game's high scores that are saved after each playthrough

    private JLabel imageLabel = new JLabel(); //instance JLabel() used to show the Pokemon images on the GUI
    private JLabel scoreLabel = new JLabel(); //instance JLabel() used to show the scores on the GUI
    private JLabel timeLabel = new JLabel(); //instance JLabel() used to show the elapsed time on the GUI

    private JTextField guessField = new JTextField(20); //instance JTextField used to take in text input from the user

    private JButton submitButton = new JButton("Submit Guess"); //instance JButton used to submit user's text input
    private JButton newGameButton = new JButton("New Game"); //instance JButton used to start a new game (restart the game after it ends)
    private JButton endGameButton = new JButton("End Game"); //instance JButton used to completely end the game after the game ends (closes GUI)

    private Timer swingTimer; //instance Timer used to show the timer on the GUI (allows elapsed time to constantly update on GUI)

    private CardLayout cardLayout = new CardLayout(); //instance CardLayout used to show different panels on the GUI (main menu, game, end menu)
    private JPanel mainPanel = new JPanel(cardLayout); //instance JPanel used to show all of the panels (different screens of the game)
    private JPanel menuPanel = new JPanel(); //instance JPanel used to show the main menu of the game (added to the main panel)
    private JPanel endPanel = new JPanel(); //instance JPanel used to show the Game End screen (added to the main panel)
    private JPanel gamePanel = new JPanel(new BorderLayout()); //instance JPanel used to show the actual game (added to main panel)
    private JPanel scorePanel = new JPanel(new GridLayout(1, 3)); //instance JPanel used to show the Game Data on the GUI (score, high score, time) (added to game panel)

    private Font pokemonFont; //instance Font variable used to load the custom Pokemon Font for the game

    private Thread musicThread; //thread used to play Pokemon theme song in the background while the game is running
    private volatile boolean isMusicPlaying = true; //music starts automatically unless stopped when game ends
    private Player player = null; //instance Player variable used to play and end the Pokemon theme song

    private static MainFrame currentInstance; //static instance MainFrame variable that's needed in order to restart the game (reset game) without issues

    public static void main(String[] args) { //the actual game/GUI runner
        javax.swing.SwingUtilities.invokeLater(() -> { //runs the game/GUI
            try {
                new MainFrame();
            } catch (Exception e) { //debug print
                e.printStackTrace();
            }
        });
    }

    public MainFrame() throws Exception {
        currentInstance = this;
        File infoFile = new File("JavaApiProject/src/Data/info.txt"); //loads the game data from info.txt into the highScore, score, and time variables
        String absolutePath = infoFile.getAbsolutePath();
        pokeHigh = FileLoader.loadData(absolutePath, ":");
        if(pokeHigh != null) {
            highScore = Integer.parseInt(pokeHigh.get("highScore"));
            score = Integer.parseInt(pokeHigh.get("score"));
            time = Integer.parseInt(pokeHigh.get("time"));
        } else {
            highScore = 0;
            score = 0;
            time = 0;
        }

        File scoresFile = new File("JavaApiProject/src/Data/scores.txt"); //loads the game data from scores.txt into scoreList
        String absScorePath = scoresFile.getAbsolutePath();
        loadScores = FileLoader.loadScores(absScorePath, ":");
        if(loadScores != null && loadScores.get("scoreList") != null) {
            scoreList = loadScores.get("scoreList");
        } else {
            scoreList = new ArrayList<Integer>();
        }

        ArrayList<Integer> nums = new ArrayList<Integer>(); //initializes a new Integer ArrayList nums (used to make a list of 10 random integers for 10 random Pokemon indices)

        String jsonString = Api.getData("https://pokeapi.co/api/v2/pokemon?limit=100&offset=0");

        JSONObject json = new JSONObject(jsonString);
        JSONArray results = json.getJSONArray("results"); //initializes results to the API data under "results" from the API URL
        for(int i = 0; i < 10; i++) { //adds 10 random ints, nonrepeating, ints to the nums list
            int num = (int)(Math.random() * 100);
            while(nums.contains(num)) {
                num = (int)(Math.random() * 100);
            }
            nums.add(num);
            JSONObject item = (JSONObject) results.get(num); //initializes JSONObject item to the Pokemon from results at index num
            String name = item.getString("name"); //initializes String name to the Pokemon's name and adds it to the String ArrayList names
            names.add(name);
            String url = item.getString("url"); //initializes String url to the Pokemon's image URL and adds it to the String ArrayList urls
            urls.add(url);
        }

        pokemonFont = FontLoader.loadFont("JavaAPIProject/src/Designs/PokemonSolidNormal-xyWR.ttf", 36); //initializes pokemonFont to the loaded custom Pokemon Font using the FontLoader class and static loadFont method

        setTitle("Pokemon Guessing Game"); //initializes the title of the game
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //initializes the default close operation of the GUI to JFrame.EXIT_ON_CLOSE
        setSize(1650,1000); //initializes the size of the GUI

        setMainMenu(); //initializes the main menu
        add(mainPanel); //adds the main panel (contains all of the panels / screens of the game) to the GUI
        setGameUI(); //initializes the game screen's UI in the GUI
        setVisible(true); //initializes the visibility of the GUI (allows GUI to be seen)
        playThemeSong(); //starts the Pokemon theme song music in the background when the game starts
    }

    private void setMainMenu() {
        menuPanel.setLayout(new BorderLayout()); //initializes menuPanel and it's background color
        menuPanel.setBackground(new Color(245, 222, 179));

        JLabel logoLabel = new JLabel(); //JLabel variable that represents image of the Pokemon logo
        JLabel ballLabel = new JLabel(); //JLabel variable that represents image of the Pokeball
        try {
            ImageIcon logoIcon = new ImageIcon("JavaAPIProject/src/Designs/Pokemon-Logo-700x394.png"); //initializes logoLabel to the image of the Pokemon logo
            Image logo = logoIcon.getImage().getScaledInstance(500, 275, Image.SCALE_SMOOTH); //scales the logo image
            logoLabel.setIcon(new ImageIcon(logo));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ImageIcon ballIcon = new ImageIcon("JavaAPIProject/src/Designs/Poké_Ball_icon.png"); //initializes ballLabel to the image of the Pokeball
            Image ball = ballIcon.getImage().getScaledInstance(550, 550, Image.SCALE_SMOOTH); //scales the Pokeball image
            ballLabel.setIcon(new ImageIcon(ball));
            ballLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) { //debug print
            logoLabel.setText("Pokémon Guessing Game");
            logoLabel.setFont(pokemonFont);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JButton startButton = new JButton("Start Game"); //initializes startButton to a new JButton with String parameter "Start Game"
        startButton.setFont(pokemonFont.deriveFont(20)); //initializes startButton's design (Font, background and foreground colors)
        startButton.setBackground(new Color(60, 90, 166)); //pokemon blue
        startButton.setForeground(new Color(255, 203,5)); //pokemon yellow

        startButton.addActionListener(e -> { //initializes the action done by the startButton when it receives input (starts the game/ shows the game screen by showing the panel in mainPanel with the key "gane")
            cardLayout.show(mainPanel, "game");
            JOptionPane.showMessageDialog(this, "Fastest Time: " + time + "\nCmon! You can do better than that!"); //message of encouragement to user
            Stopwatch.start(); //starts timer
            startTimer(); //starts the GUI timer to show elapsed time
        });

        JButton resetButton = new JButton("Reset Stats"); //initializes resetButton to a new JButton with String parameter "Reset Stats"
        resetButton.setFont(pokemonFont.deriveFont(14)); //initializes resetButton's design (Font, background and foreground colors)
        resetButton.setBackground(new Color(60, 90, 166)); //pokemon blue
        resetButton.setForeground(new Color(255, 203,5)); //pokemon yellow

        resetButton.addActionListener(e -> { //initializes the action done by the resetButton when it receives input (resets all saved game data)
            highScore = 0;
            time = 0;
            scoreList = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Your Stats have been Reset!"); //message to confirm data reset
            gamePanel.remove(scorePanel); //removes scorePanel then calls loadScores so that scorePanel is updated on the GUI
            scorePanel = new JPanel(new GridLayout(1, 3));
            loadScores();
        });

        JPanel centerPanel = new JPanel(); //initializes centerPanel to a new JPanel() and sets its background color
        centerPanel.setBackground(new Color(245, 222, 179));
        centerPanel.add(startButton, BorderLayout.NORTH); //adds the Game Start buttons (startButton and resetButton)
        centerPanel.add(resetButton, BorderLayout.SOUTH);

        //initializes menuPanel (start screen)
        menuPanel.add(logoLabel, BorderLayout.NORTH); //adds the Pokemon Logo to the top of the menuPanel
        menuPanel.add(ballLabel, BorderLayout.SOUTH); //adds the Pokeball image to the bottom of menuPanel
        menuPanel.add(centerPanel, BorderLayout.CENTER); //adds centerPanel (with the buttons) to the center of menuPanel

        mainPanel.add(menuPanel, "menu"); //adds menuPanel to mainPanel with the key "menu"
    }

    private void startTimer() { //starts the timer used to show elapsed time in the GUI
        swingTimer = new Timer(1000, e -> {
            int currentTime = Stopwatch.getElapsedTimeSeconds(); //initializes int variable curretnTime to the elapsed time
            timeLabel.setText("Time: " + currentTime + " seconds"); //initializes the timeLabel variable, which will be used to show ingame time in the GUI, to the time section of the score board ("Time: " with current time in seconds)
        });
        swingTimer.start(); //starts the GUI timer
    }

    private void setGameUI() { //initializes the UI of the game screen
        setupImagePanel(); //initializes the image panel of the Pokemon images (where the Pokemon images will show up on the GUI)
        setupInputField(); //initalizes where the user will be able to input text in the game GUI (makes an input field for text input)
        loadPokemon(); //loads the Pokemon images into the GUI through image label (image label in the image panel is set to the Pokemon image)
        loadScores(); //loads the game data (high score, score, time) onto the GUI
        mainPanel.add(gamePanel, "game"); //adds gamePanel to mainPanel with the key "game"
    }

    private void playThemeSong() { //plays the Pokemon theme song in the background while the game is running (may change the song depending on my preferences)
        if(musicThread != null && musicThread.isAlive()) { //checks if the music thread is already running, if so, it wont start a new one
            return;
        }

        isMusicPlaying = true; //sets isMusicPlaying to true so that music thread can start playing music

        musicThread = new Thread(() -> { //starts a new thread to play the music in the background
            try {
                File themeSong = new File("JavaAPIProject/src/Designs/Pokemon.mp3"); //gets the theme song mp3 file
                String absolutePath = themeSong.getAbsolutePath(); //gets the absolute path of the theme song file (prevents issues with relative/local paths)

                while(isMusicPlaying) { //starts the Pokemon theme song and loops it until the game ends
                    FileInputStream theme = new FileInputStream(absolutePath); //initializes FileInputStream theme to the absolute path of the theme song file
                    BufferedInputStream music = new BufferedInputStream(theme); //initializes BufferedInputStream music to the FileInputStream theme (allows music to be played)
                    player = new Player(music); //initializes the music player to the BufferedInputStream music
                    player.play(); //plays the music using player
                }
            } catch (Exception e) { //debug print
                e.printStackTrace();
            }
        });
        
        musicThread.start(); //starts the music thread (plays the Pokemon theme song)
    }

    public void stopThemeSong() { //stops the Pokemon theme song
        isMusicPlaying = false; //sets isMusicPlaying to false so that the music thread stops playing music

        if(player != null) { //checks if player isn't null (if the music player was initialized)
            try {
                player.close(); //closes the music player (stops the music)
            } catch (Exception e) { //debug print
                e.printStackTrace();
            }
        }

        musicThread = null; //sets musicThread to null so that a new music thread can be created the next time the game is played (when the user restarts the game)
    }

    private void setupImagePanel() { //sets up space where pokemon images will go in GUI
        imageLabel.setBackground(new Color(245, 222, 179)); //sets up design (background and opacity) and place of imageLabel (where Pokemon images will go)
        imageLabel.setOpaque(true);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); //adds imageLabel to gamePanel in the center (Pokemon images will be shown in the center of the game)
        gamePanel.add(imageLabel, BorderLayout.CENTER); 
        setVisible(true); //makes Pokemon images visible
    }

    private void setupInputField() { //sets up guessing based on the user's text input
        ActionListener submitAction = e -> { //submitAction so that both submitButton and enter key can trigger it
            String userGuess = guessField.getText(); //initializes String variable userGuess to the text input in guessField by the user
            if (checkGuess(userGuess, correct)) { //checks if the user's answer is correct
                updateScore(true); //increases the user's score by 1 since the user is right
            } else {
                updateScore(false); //decreases the user's score by 1 since the user is wrong
            }
            currentIndex++; //Moves to the next Pokémon after a correct guess
            if(currentIndex < names.size()) { //prevents index out of bounds error (won't load another Pokemon image if the current index is greater than the amount of Pokemon in the names list)
                loadPokemon();
            } else {
                scoreLabel.setText("Score: " + score); //finalizes game data since game has ended (sets score and time to their final values)
                timeLabel.setText("Time: " + Stopwatch.getElapsedTimeSeconds());
                endGame(); //ends game
                return;
            }
            guessField.setText(""); //resets the text input box back to empty after the user inputs a guess
            scoreLabel.setText("Score " + score); //sets scoreLabel to the updated score after the user's guess(allows GUI to show updated score after each guess)
        };

        submitButton.addActionListener(submitAction); //submitButton triggers submitAction

        guessField.addActionListener(submitAction); //guessField (enter key) also triggers submitAction

        guessField.setFont(pokemonFont.deriveFont(18)); //sets up custom Pokemon font for the text in guessField as well as guessField's design
        guessField.setBackground(new Color(60, 90, 166));
        guessField.setForeground(new Color(255, 203, 5));

        submitButton.setFont(pokemonFont.deriveFont(18)); //sets up custom Pokemon font for the text on submitButton as well as submitButton's design
        submitButton.setBackground(new Color(60, 90, 166));
        submitButton.setForeground(new Color(255, 203, 5));

        JPanel inputPanel = new JPanel(); //initializes JPanel inputPanel to a new JPanel() and adds guessField and submitButton (the forms of input)
        inputPanel.add(guessField);
        inputPanel.add(submitButton);
        gamePanel.add(inputPanel, BorderLayout.SOUTH); //adds inputPanel to gamePanel (allows the game to have input)
    }

    private boolean checkGuess(String userGuess, String correct) { //checks the user's guess to see if it matches the correct Pokemon name
        if(userGuess.trim().equalsIgnoreCase(correct)) {
            return true;
        } else {
            return false;
        }
    }

    private void updateScore(boolean isRight) { //used to update the user's score depending on if they guess right or wrong
        if(isRight) {
            score++;
        } else {
            score--;
        }
    }

    private void loadPokemon() { //loads the Pokemon images onto the imageLabel, where they can then be shown in game in the GUI
        if (currentIndex < names.size()) { //prevents an out of bounds error (max of names.size() Pokemon images)
            correct = names.get(currentIndex); //sets the new correct name of the Pokemon for the new Pokemon
            try {
                String s = Api.getData(urls.get(currentIndex)); //initializes the String variable front_default to the image url of the Pokemon
                JSONObject obj = new JSONObject(s);
                JSONObject sprites = obj.getJSONObject("sprites");
                String front_default = sprites.getString("front_default");
                System.out.println("Image URL: " + front_default); //debug print
                System.out.println("Pokemon Name: " + correct); //test print (reveals answer)

                
                try {
                    ImageIcon icon = new ImageIcon(new java.net.URL(front_default)); //makes an actual image, scaledImage, for the Pokemon using ImageIcon icon and the Pokemon image url
                    Image scaledImage = icon.getImage().getScaledInstance(600, 600, Image.SCALE_SMOOTH); //makes image scaled to desired scale (500 by 500)
                    imageLabel.setIcon(new ImageIcon(scaledImage)); //sets imageLabel to the new Pokemon image (allows new Pokemon image to be shown in GUI)
                } catch (Exception e) {
                }
            
            } catch (Exception e) { //debug print
                e.printStackTrace();
            }
        }  
    }

    private void loadScores() { //loads the user's game data onto the GUI
        JLabel highLabel = new JLabel(); //initializes JLabel highLabel to the user's high score (with custom Pokemon font and foreground color)
        highLabel.setText("High Score: " + highScore);
        highLabel.setFont(pokemonFont.deriveFont(14));
        highLabel.setForeground(new Color(255, 203, 5));
        scorePanel.add(highLabel); //adds highLabel to scorePanel (allows high score to be shown in GUI)

        scoreLabel.setText("Score: " + score); //initializes JLabel scoreLabel to the user's score (with custom Pokemon font and foreground color)
        scoreLabel.setFont(pokemonFont.deriveFont(14));
        scoreLabel.setForeground(new Color(255, 203, 5));
        scorePanel.add(scoreLabel); //adds scoreLabel to scorePanel (allows score to be shown in GUI)

        timeLabel = new JLabel("Time: " + time + " seconds"); //initializes JLabel timeLabel to the user's time (with custom Pokemon font and foreground color)
        timeLabel.setFont(pokemonFont.deriveFont(14));
        timeLabel.setForeground(new Color(255, 203, 5));
        scorePanel.add(timeLabel); //adds timeLabel to scorePanel (allows time to be shown in GUI)

        scorePanel.setBackground(new Color(204, 0, 0)); //initializes the background color of scorePanel

        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,10)); //makes border for the scorePanel (makes user's game data look better by making border bigger)

        gamePanel.add(scorePanel, BorderLayout.NORTH); //adds scorePanel to the top of gamePanel (allows user's game data to be shown in the game)
    }

    private void endGame() { //does the processes for Game End but doesn't directly end the game (the user can choose to replay or end completely)
        Stopwatch.stop(); //stops the timer
        if(swingTimer != null){
            swingTimer.stop(); //stops the GUI timer if it was running
        }
        int finalTime = Stopwatch.getElapsedTimeSeconds(); //initializes int variable finalTime to the elapsed time (time it took to play the game in its entirety)
        scoreList.add(score); //adds the user's score to scoreList (scoreList used to calculate average and median)
        double avg = ScoreStats.getAverage(scoreList); //uses ScoreStats static method getAverage to calculate the average score from scoreList
        double median = ScoreStats.getMedian(scoreList); //uses ScoreStats static method getMedian to calculate the median score from scoreList
        JOptionPane.showMessageDialog(this, "you got them all!\nFinal Score: " + score + "\nAverage Score: " + avg + "\nMedian Score: " + median + "\nTime: " + finalTime); //tells user their End Game data
        if(score == 10 && highScore >= score) { //implements a streak into the game (if user gets the max score more than once in a row, they build a streak and highScore increases by 1)
            highScore++;
            JOptionPane.showMessageDialog(this, "You're On A Roll!\nStreak: " + (highScore - 10));
        }
        if(highScore > 10 && score < 10) { //ends the streak if the user doesn't get the max score
            highScore = 10;
            JOptionPane.showMessageDialog(this, "You Burnt the Bacon Cuh!\nStreak: " + (highScore - 10));
        }
        if (score > highScore) { //sets a new highScore if score is greater than the old high score
            highScore = score;
            JOptionPane.showMessageDialog(this, "New High Score: " + highScore);
        }
        if (time == 0 || finalTime < time) { //sets a new fastest time if finalTime is less than time (the user beats the game faster than their previous fastest run)
            time = finalTime;
        }
        score = 0; //resets score to 0 so that the user can replay
        Map<String, String> pokedex = new HashMap<>(); //initializes String String HashMap pokedex
        Map<String, ArrayList<Integer>> scores = new HashMap<>(); //initializes String Integer ArrayList HashMap scores
        pokedex.put("score", "" + score); //adds the user's game data to the pokedex HashMap (score, highScore, time)
        pokedex.put("highScore", "" + highScore);
        pokedex.put("time", "" + time);
        scores.put("scoreList", scoreList); //adds the user's list of scores to the scores HashMap
        FileSaver.saveData(pokedex); //saves the user's game data to the pokedex String String HashMap (score, highScore, time)
        FileSaver.saveScores(scores); //saves the user's new list of scores to the scores String Integer ArrayList HashMap

        setEnd(); //loads the Game End menu for the user to decide if they want to replay or end the game completely
    }

    private void setEnd() {
        endPanel.setLayout(new BorderLayout()); //initializes endPanel and it's background color
        endPanel.setBackground(new Color(245, 222, 179));

        JLabel logoLabel = new JLabel(); //JLabel variable that represents image of the Pokemon logo
        JLabel ballLabel = new JLabel(); //JLabel variable that represents image of the Pokeball
        try {
            ImageIcon logoIcon = new ImageIcon("JavaAPIProject/src/Designs/Pokemon-Logo-700x394.png"); //initializes logoLabel to the image of the Pokemon logo
            Image logo = logoIcon.getImage().getScaledInstance(500, 275, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logo));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ImageIcon ballIcon = new ImageIcon("JavaAPIProject/src/Designs/Poké_Ball_icon.png"); //initializes ballLabel to the image of the Pokeball
            Image ball = ballIcon.getImage().getScaledInstance(600, 600, Image.SCALE_SMOOTH);
            ballLabel.setIcon(new ImageIcon(ball));
            ballLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) { //debug print
            logoLabel.setText("Pokémon Guessing Game");
            logoLabel.setFont(pokemonFont);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        newGameButton.setFont(pokemonFont.deriveFont(20)); //initializes the design of newGameButton (Font, background and foreground colors)
        newGameButton.setBackground(new Color(60, 90, 166));
        newGameButton.setForeground(new Color(255, 203, 5));

        newGameButton.addActionListener(e -> { //initializes the action done by the newGameButton when it receives input (closes the current GUI window and restarts the game)
            javax.swing.SwingUtilities.invokeLater(() -> {
                if(currentInstance != null) { //checks if the game is currently running (if running, the game window is closed to prevent multiple GUI's from being active at once)
                    currentInstance.stopThemeSong(); //stops the Pokemon theme song before restarting the game
                    currentInstance.dispose(); //closes old window, makes program run cleaner (prevents multiple GUI's from being active at the same time)
                }
                try {
                    currentInstance = new MainFrame(); //restarts the game
                    currentInstance.setVisible(true);
                } catch (Exception a) { //debug print
                    a.printStackTrace();
                }
            });
        });

        endGameButton.setFont(pokemonFont.deriveFont(20)); //initializes the design of endGameButton (Font, background and foreground colors)
        endGameButton.setBackground(new Color(60, 90, 166));
        endGameButton.setForeground(new Color(255, 203, 5));

        endGameButton.addActionListener(e -> { //initializes the action done by the endGameButton when it receives input (closes the GUI entirely)
            System.exit(0);
        });

        JPanel centerPanel = new JPanel(); //initializes centerPanel to a new JPanel() and sets its background color
        centerPanel.setBackground(new Color(245, 222, 179)); 
        centerPanel.add(newGameButton, BorderLayout.NORTH); //adds the Game End buttons (newGameButton and endGameButton)
        centerPanel.add(endGameButton, BorderLayout.SOUTH);

        //initializes endPanel (end screen)
        endPanel.add(logoLabel, BorderLayout.NORTH); //adds the Pokemon Logo to the top of the endPanel
        endPanel.add(ballLabel, BorderLayout.SOUTH); //adds the Pokeball image to the bottom of endPanel
        endPanel.add(centerPanel, BorderLayout.CENTER); //adds centerPanel (with the buttons) to the center of endPanel

        mainPanel.add(endPanel, "end"); //adds endPanel to mainPanel with the key "end" (allows endPanel to be shown in the GUI)
        cardLayout.show(mainPanel, "end"); //shows endPanel (uses cardLayout to show the JPanel in mainPanel with the key "end")
    }
}