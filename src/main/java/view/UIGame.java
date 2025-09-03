package view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.util.Duration;
import logic.Gameplay;
import logic.ImageHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main game interface that manages the gameplay visualization and user interactions.
 * Handles real-time camera feed, token placement detection, score tracking,
 * and game state management.
 */
public class UIGame extends Application {
	private boolean endOfTurn = false;
	private Pane centralPane;
	private Text scoreText1;
	private Text scoreText2;
	private TextArea scoreTextArea;
	private Text instructionsText; // Ajouter cette variable membre
	private static ImageHandler handler;
	private Gameplay gameplay;
	private Timeline timeline;
	private Timeline checkEndTurn;
	private Stage primaryStage;
	private int[] lastPosition;
	private int counterCheck;
	private int maxCheck;
	private int rounds;
	private static final Logger logger = Logger.getLogger(UIMenu.class.getName());

	/**
	 * Initializes and starts the game interface with webcam feed and UI components.
	 *
	 * @param primaryStage The primary stage for displaying the game interface
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			if (handler == null) {
				handler = new ImageHandler();
			}
			Scene scene = getScene(primaryStage);
			primaryStage.setTitle("Game - UI");
			primaryStage.setScene(scene);
			primaryStage.show();
	
			lastPosition = null;
			counterCheck = 0;
			maxCheck = 15;
	
			gameplay = new Gameplay();
			rounds = gameplay.getCurrentRound();
			// Create a timeline for updating the background
			timeline = new Timeline(new KeyFrame(Duration.millis(300), event -> updateBackground()));
			timeline.setCycleCount(Timeline.INDEFINITE); // Continuous update loop
			timeline.play();
	
			// Initialize checkEndTurn but don't start it yet
			checkEndTurn = new Timeline(new KeyFrame(Duration.millis(200), event -> checkTurn()));
			checkEndTurn.setCycleCount(Timeline.INDEFINITE);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in UIMenu", e);
		}
	}

	/**
	 * Creates and configures the main game interface components.
	 * Sets up the camera feed display, score tracking, and game controls.
	 *
	 * @param primaryStage The primary stage for the game window
	 * @return Configured Scene containing all game interface elements
	 */
	public Scene getScene(Stage primaryStage) {
		this.primaryStage = primaryStage;
		 // Load background
		ImageView background = new ImageView(new Image("background.png"));
		background.setFitWidth(1280);
		background.setFitHeight(720);
		background.setPreserveRatio(false);

		 // Load house images
		ImageView maisonLeft = new ImageView(new Image("maison.png"));
		ImageView maisonRight = new ImageView(new Image("maison.png"));
		maisonLeft.setFitWidth(200);
		maisonLeft.setFitHeight(300);
		maisonRight.setFitWidth(200);
		maisonRight.setFitHeight(300);
		maisonRight.setScaleX(-1);

		// Create bottom-right buttons
		Button BtnPause = new Button("Pause");
		Button btnStartGame = new Button("Débuter la partie");
		Button btnParameters = new Button("Étalonnage");
		Button btnQuit = new Button("Retour au Menu");

		// buton sizes
		BtnPause.setPrefSize(300, 75);
		btnStartGame.setPrefSize(300, 75);
		btnParameters.setPrefSize(300, 75);
		btnQuit.setPrefSize(300, 75);

		// button styles
		String buttonStyle = "-fx-background-image: url('bouttonCurling.png');"
				+ "-fx-background-size: cover;"
				+ "-fx-text-fill: white;"
				+ "-fx-font-size: 24px;"
				+ "-fx-background-color: transparent;"
				+ "-fx-background-radius: 60;";
		BtnPause.setStyle(buttonStyle);
		btnStartGame.setStyle(buttonStyle);
		btnParameters.setStyle(buttonStyle);
		btnQuit.setStyle(buttonStyle);
		
		BtnPause.setAlignment(Pos.BOTTOM_CENTER);
		btnStartGame.setAlignment(Pos.BOTTOM_CENTER);
		btnParameters.setAlignment(Pos.BOTTOM_CENTER);
		btnQuit.setAlignment(Pos.BOTTOM_CENTER);

		btnStartGame.setOnAction(e -> startGame(btnStartGame)); 
		btnParameters.setOnAction(e -> openUIEtalonnage(primaryStage));
		btnQuit.setOnAction(e -> openUIMenu(primaryStage));
		BtnPause.setOnAction(e -> togglePause(BtnPause));

		 // Create score text area
		scoreText1 = new Text("Score 1 : 0");
		scoreText1.setStyle("-fx-fill: #FF6961; -fx-font-weight: bold; -fx-font-size: 24px;"); // Red for player 1

		StackPane textScore1 = new StackPane(scoreText1);
		textScore1.setPrefSize(300, 50); 
		textScore1.setStyle(
				"-fx-background-color: #004D2C; -fx-background-radius: 10; -fx-background-radius: 60;  -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");

		// Text area for player 2 score
		scoreText2 = new Text("Score 2 : 0");
		scoreText2.setStyle("-fx-fill: #A9CBFF; -fx-font-weight: bold; -fx-font-size: 24px;"); // Blue for player 2

		StackPane textScore2 = new StackPane(scoreText2);
		textScore2.setPrefSize(300, 50); 
		textScore2.setStyle(
				"-fx-background-color: #004D2C; -fx-background-radius: 10; -fx-background-radius: 60;  -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");

		 // Create instruction text area
		instructionsText = new Text("Placez jeton");
		instructionsText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 24px;");

		StackPane textBackground = new StackPane(instructionsText);
		textBackground.setPrefSize(300, 50);
		textBackground.setStyle(
				"-fx-background-color: #004D2C; -fx-background-radius: 60; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");

		// Contener for the scores
		HBox topArea = new HBox(100, textScore1, textScore2, BtnPause);
		topArea.setAlignment(Pos.CENTER);

		// Central pane
		centralPane = new Pane();
		centralPane.setPrefWidth(900);
		centralPane.setPrefHeight(500);

		 // Create right text area
		scoreTextArea = new TextArea();
		scoreTextArea.setPrefWidth(300); // Text area width
		scoreTextArea.setPrefHeight(500); // Text area height
		scoreTextArea.setStyle("-fx-control-inner-background: #004D2C; -fx-text-fill: white; -fx-font-size: 24px;"); // White text for better visibility

		HBox centerArea = new HBox(10, centralPane, scoreTextArea);
		centerArea.setAlignment(Pos.CENTER); // Alignement des boutons au centre

		// Container for the buttons
		HBox bottomArea = new HBox(10, btnStartGame, btnParameters, btnQuit, textBackground);
		bottomArea.setAlignment(Pos.CENTER); // Center align bottom buttons

		// main container
		VBox centerContent = new VBox(20, topArea, centerArea, bottomArea);
		centerContent.setAlignment(Pos.CENTER);

		StackPane root = new StackPane(background, centerContent);
		StackPane.setAlignment(centerContent, Pos.CENTER);

		// Create a new scene with the root as the main container
		return new Scene(root, 1280, 720);
	}

	/**
	 * Initiates or processes a game turn.
	 * Manages the start/end of turns and updates the instruction text.
	 *
	 * @param btnStartGame The button that triggered the action
	 */
	private void startGame(Button btnStartGame) {
		if (!checkEndTurn.getStatus().equals(Animation.Status.RUNNING)) {
			checkEndTurn.play();
			instructionsText.setText("Placez jeton");
		} else {
			completeTurn();  
		}
	}

	/**
	 * Processes the end of a turn.
	 * Updates game state, refreshes display, and prepares for next turn.
	 */
	private void completeTurn() {  
		gameplay.endTurn();
		if (gameplay.getCurrentRound() != rounds) {
			if (scoreTextArea != null) {
				scoreTextArea.clear();
	    	}
			rounds = gameplay.getCurrentRound(); 
		}
		if (gameplay.getTargetPosition() != null) {
			updateBackground();
			updateTextScore();
			checkEndGame();
			instructionsText.setText("Placez jeton");
		}
	
	}

	/**
	 * Monitors token stability and position.
	 * Checks if a token has remained stationary long enough to be considered placed.
	 */
	private void checkTurn() {
		int[] actualPosition = gameplay.checkToken();
		if (this.lastPosition == null && !endOfTurn) {
			this.lastPosition = actualPosition;
			Mat image = Imgcodecs.imread("./image.jpg");

			gameplay.findTarget(image);    // Changed from trouverCible
			gameplay.findToken(image);     // Changed from trouverJeton
			gameplay.displayTokens();      // Changed from afficherJetons
			instructionsText.setText("Placez jeton");
		}
		if (checkPositionToken(this.lastPosition, actualPosition) && actualPosition != null && !endOfTurn) {
			System.out.println("Check count: " + String.valueOf(this.counterCheck));  // Changed from "Nombre de check"
			if (this.counterCheck == this.maxCheck) {
				endOfTurn = true;
				Mat image = Imgcodecs.imread("./image.jpg");
				gameplay.findTarget(image);    // Changed method names to match updated Gameplay class
				gameplay.findToken(image);
				int turnNumber = gameplay.getCurrentTurn();
				int advantage = gameplay.getAdvantage();
				UpdateTextScoreRound(turnNumber, advantage);
				instructionsText.setText("Retirez jeton");
			} else {
				this.counterCheck += 1;
			}
		} else {
			this.lastPosition = actualPosition;
		}
		if (actualPosition == null && endOfTurn) {
			this.counterCheck = 0;
			this.lastPosition = null;
			endOfTurn = false;
			completeTurn();  
		}
	}

	/**
	 * Validates token position stability.
	 * Compares current and previous positions to determine if token has settled.
	 *
	 * @param lastPosition Previous token position
	 * @param actualPosition Current token position
	 * @return true if token position is stable, false otherwise
	 */
	private boolean checkPositionToken(int[] lastPosition, int[] actualPosition) {
		if (lastPosition == null || actualPosition == null) {
			return false;
		} else {
			if (lastPosition[0] - 4 < actualPosition[0] && actualPosition[0] < lastPosition[0] + 4) {
				if (lastPosition[1] - 4 < actualPosition[1] && actualPosition[1] < lastPosition[1] + 4) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Update the text for Advantage on the right-screen
	 * @param turnNumber Turn Number
	 * @param advantage Player who has the advantage
	 */
	private void UpdateTextScoreRound(int turnNumber, int advantage) {
    if (scoreTextArea != null) {
      String newLine = "Tour " + (turnNumber + 1) + " : J" + (advantage == 0 ? "1 " : "2 ") + " en tête";
      scoreTextArea.appendText(newLine + "\n");
    }
	}

	/**
	 * Checks for game completion conditions.
	 * Triggers winner screen if game is finished.
	 */
	private void checkEndGame() {
		if (gameplay.isGameFinished()) {
			System.gc();

			int[] score_tab = gameplay.getPlayerScoreRound();
			openUIWinner(this.primaryStage, score_tab[0], score_tab[1]);
		}
	}

	/**
	 * Updates score display for both players.
	 * Retrieves current scores from gameplay and updates UI.
	 */
	private void updateTextScore() {
		int[] score_tab = gameplay.getPlayerScoreRound();

		setScoreText1("Score 1 : " + score_tab[0]);
		setScoreText2("Score 2 : " + score_tab[1]);
	}

	/**
	 * Updates the game board visualization.
	 * Processes camera feed and draws current game state including tokens and target.
	 */
	private void updateBackground() {
		Mat image = Imgcodecs.imread("./image.jpg");

		// Check if image is valid and has correct dimensions
		if (image == null || image.empty() || image.width() <= 0 || image.height() <= 0) {
			System.out.println("Invalid image or incorrect dimensions");
			return;
		}

		if (gameplay.getTargetPosition() != null) {
			if (ImageHandler.getTargetStyle() == 2) {
				int[] targetPosition = gameplay.getTargetPosition();
				ImageHandler.drawTarget(image, targetPosition);
			}
			for (int k = 0; k < gameplay.getTokens().length; k++) {
				int[] tokenPosition = gameplay.getTokens()[k].getPosition();
				int player = gameplay.getTokens()[k].getPlayer();

				if (tokenPosition[0] != 0) {
					if (player == 0) {
						if (ImageHandler.getTokenStyle() == 1) {
							ImageHandler.drawTokenPlayer2(image, tokenPosition);
						} else {
							ImageHandler.drawBlueCircle(image, tokenPosition);
						}
					} else {
						if (ImageHandler.getTokenStyle() == 1) {
							ImageHandler.drawTokenPlayer1(image, tokenPosition);
						} else {
							ImageHandler.drawRedCircle(image, tokenPosition);
						}
					}
				}
			}
		}

		// Create WritableImage only if dimensions are valid
		WritableImage writableImage = new WritableImage(image.width(), image.height());
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		for (int y = 0; y < image.height(); y++) {
			for (int x = 0; x < image.width(); x++) {
				double[] data = image.get(y, x);
				Color color = Color.rgb((int) data[2], (int) data[1], (int) data[0]);
				pixelWriter.setColor(x, y, color);
			}
		}
		BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
		BackgroundImage backgroundImage = new BackgroundImage(writableImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		centralPane.setBackground(new Background(backgroundImage));
	}

	/**
	 * Manages game pause functionality.
	 * Toggles pause state for all game timelines and updates button text.
	 *
	 * @param btnPause Button used to toggle pause state
	 */
	private void togglePause(Button btnPause) {
    if (timeline.getStatus() == Animation.Status.RUNNING) {
      timeline.pause();
      checkEndTurn.pause(); // Also pause turn control when needed
      btnPause.setText("Reprendre");
    } else {
      timeline.play();
      checkEndTurn.play();
      btnPause.setText("Pause");
    }
	}

	/**
	 * Checks if the current turn is ending.
	 * Getter for the endOfTurn flag indicating if current turn is complete.
	 *
	 * @return true if turn is ending, false otherwise
	 */
	public boolean isEndOfTurn() {
		return endOfTurn;
	}

	/**
	 * Updates the score display for player 2.
	 * Setter for the score text display in the UI.
	 *
	 * @param score New score text to display
	 */
	public void setScoreText2(String score) {
		this.scoreText2.setText(score);
	}

	/**
	 * Updates the score display for player 1.
	 * Setter for the score text display in the UI.
	 *
	 * @param score New score text to display
	 */
	public void setScoreText1(String score) {
		this.scoreText1.setText(score);
	}

	/**
	 * Returns to the main menu.
	 * Stops all game processes and transitions to menu screen.
	 *
	 * @param primaryStage The primary stage for UI display
	 */
	private void openUIMenu(Stage primaryStage) {
		this.timeline.stop();
		this.checkEndTurn.stop();
		UIMenu uiMenu = new UIMenu();
		uiMenu.start(primaryStage);
	}

	/**
	 * Transitions to the calibration interface.
	 * Stops current game timelines and opens calibration screen.
	 *
	 * @param primaryStage The primary stage for UI display
	 */
	private void openUIEtalonnage(Stage primaryStage) {
		this.timeline.stop();
		this.checkEndTurn.stop();
		UIEtalonnage uiEtalonnage = new UIEtalonnage();
		uiEtalonnage.start(primaryStage);
	}

	/**
	 * Opens the winner screen with final scores.
	 * Cleans up game resources and displays game results.
	 *
	 * @param primaryStage The primary stage for UI display
	 * @param score1 Final score for player 1
	 * @param score2 Final score for player 2
	 */
	private void openUIWinner(Stage primaryStage, int score1, int score2) {
		this.timeline.stop();
		this.checkEndTurn.stop();
		gameplay = null;
		UIWinner uiWinner = new UIWinner(score1, score2);
		uiWinner.start(primaryStage);
	}
}
