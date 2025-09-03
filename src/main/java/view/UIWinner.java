package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/**
 * Winner announcement interface for the game.
 * Displays the game results and winner, providing options to return to the main menu.
 * Handles the final state of the game with player scores.
 */
public class UIWinner extends Application {
	private int score1;
	private int score2;
	
    /**
     * Creates a new winner screen with final game scores.
     *
     * @param score1 Final score for player 1
     * @param score2 Final score for player 2
     */
	public UIWinner(int score1, int score2) {
		this.score1 = score1;
		this.score2 = score2;
	}
	
    /**
     * Initializes and displays the winner announcement screen.
     * Sets up the visual components and navigation buttons.
     *
     * @param primaryStage The primary stage for the winner UI
     */
	@Override
    public void start(Stage primaryStage) {
    	// Load background
    	ImageView background = new ImageView(new Image("background.png"));
        background.setFitWidth(1280);
        background.setFitHeight(720);
        background.setPreserveRatio(false);
        
        // Load house images
        ImageView maisonLeft = new ImageView(new Image("maison.png"));
        ImageView maisonRight = new ImageView(new Image("maison.png"));
        maisonLeft.setFitWidth(500);
        maisonLeft.setFitHeight(500);
        maisonRight.setFitWidth(500);
        maisonRight.setFitHeight(500);
        maisonRight.setScaleX(-1);
        
        // Create title with proportional font size
        Font.loadFont(getClass().getResourceAsStream("/ALBAS.TTF"), 48);
        Text title = createWinnerText(score1, score2);
        title.setFont(Font.font("Alba Super", 96));  // Increase text size for 1280x720 resolution
        title.setStyle("-fx-fill: white; -fx-font-weight: bold;");
        
		StackPane textTitle = new StackPane(title);
		textTitle.setPrefSize(700, 100); // Background dimensions (width x height)
		textTitle.setStyle("-fx-background-color: #004D2C; -fx-background-radius: 60; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");
		textTitle.setAlignment(Pos.CENTER);
		
		// Container for scores
		HBox topArea = new HBox(10, textTitle);
		topArea.setAlignment(Pos.TOP_CENTER);

        // Create buttons with proportional sizes
		Button btnMenu = new Button("Retour au Menu");
        
        btnMenu.setAlignment(Pos.BOTTOM_CENTER);

        // Adjust button sizes
        btnMenu.setPrefSize(300, 75);
        
        // Button styles
        String buttonStyle = "-fx-background-image: url('bouttonCurling.png');"
                + "-fx-background-size: cover;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 24px;"
                + "-fx-background-color: transparent;"
                + "-fx-background-radius: 60;"
                + "-fx-font-family: 'Alba Super'";
        btnMenu.setStyle(buttonStyle);
        
        btnMenu.setOnAction(e -> openUIMenu(primaryStage));
        
        // Position house images
        StackPane leftPane = new StackPane(maisonLeft);
        StackPane rightPane = new StackPane(maisonRight);
        leftPane.setAlignment(Pos.BOTTOM_LEFT);
        rightPane.setAlignment(Pos.BOTTOM_RIGHT);

        // Main container
        VBox centerContent = new VBox(10, topArea, btnMenu);
        centerContent.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(background, leftPane, rightPane, centerContent);
        StackPane.setAlignment(centerContent, Pos.CENTER);
        StackPane.setAlignment(leftPane, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(rightPane, Pos.BOTTOM_RIGHT);

        // Create scene with 1280x720 dimensions
        Scene scene = new Scene(root, 1280, 720);

        // Configure window
        primaryStage.setTitle("Game of Curling - Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Returns to the main menu interface.
     *
     * @param primaryStage The primary stage to display the menu
     */
	private void openUIMenu(Stage primaryStage) {
		UIMenu uiMenu = new UIMenu();
		uiMenu.start(primaryStage);
	}
	
    /**
     * Creates the winner announcement text based on final scores.
     *
     * @param score1 Final score for player 1
     * @param score2 Final score for player 2
     * @return Text object containing the winner announcement
     */
	private Text createWinnerText(int score1, int score2) {
		Text Winner = new Text();
		
		if (score1 > score2) {
			Winner.setText("Joueur 1 gagne !");
		} else if (score1 < score2) {
			Winner.setText("Joueur 2 gagne !");
		} else {
			Winner.setText("Égalité !");
		}
		
		return Winner;
	}
}
