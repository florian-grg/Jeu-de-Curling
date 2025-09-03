package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.GameplayApp;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main menu interface for the game.
 * Provides options to start a new game, access settings, and quit the application.
 * Handles the initial user interaction and navigation to other game interfaces.
 */
public class UIMenu extends Application {
    private static final Logger logger = Logger.getLogger(GameplayApp.class.getName());

    /**
     * Initializes and displays the main menu interface.
     * Sets up the visual components and button actions.
     *
     * @param primaryStage The primary stage for the menu UI
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load and configure background image
            ImageView background = new ImageView(new Image("background.png"));
            background.setFitWidth(1280);
            background.setFitHeight(720);
            background.setPreserveRatio(false);
            
            // load the houses images
            ImageView maisonLeft = new ImageView(new Image("maison.png"));
            ImageView maisonRight = new ImageView(new Image("maison.png"));
            maisonLeft.setFitWidth(500);
            maisonLeft.setFitHeight(500);
            maisonRight.setFitWidth(500);
            maisonRight.setFitHeight(500);
            maisonRight.setScaleX(-1);
            
            // Create and configure title text
            Text title = new Text("Curling Couriot");
            Font.loadFont(getClass().getResourceAsStream("/ALBAS.TTF"), 48);
            title.setFont(Font.font("Alba Super", 96));  // Set text size for 1280x720 resolution
            title.setStyle("-fx-fill: white; -fx-font-weight: bold;");
            
            StackPane textTitle = new StackPane(title);
            textTitle.setPrefSize(700, 100); // Dimensions du fond (largeur x hauteur)
            textTitle.setStyle("-fx-background-color: #004D2C; -fx-background-radius: 60; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");
            textTitle.setAlignment(Pos.CENTER);
            
            // Contain the title
            HBox topArea = new HBox(10, textTitle);
            topArea.setAlignment(Pos.TOP_CENTER);

            // Create navigation buttons with proportional sizes
            Button btnNewGame = new Button("Nouvelle partie");
            Button btnOptions = new Button("Paramètres");
            Button btnClose = new Button("Quitter");
            
            btnNewGame.setAlignment(Pos.BOTTOM_CENTER);
            btnOptions.setAlignment(Pos.BOTTOM_CENTER);
            btnClose.setAlignment(Pos.BOTTOM_CENTER);

            // adjust button sizes
            btnNewGame.setPrefSize(300, 75);
            btnOptions.setPrefSize(300, 75);
            btnClose.setPrefSize(300, 75);
            
            //  button style
            String buttonStyle = "-fx-background-image: url('bouttonCurling.png');"
                    + "-fx-background-size: cover;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 24px;"
                    + "-fx-background-color: transparent;"
                    + "-fx-background-radius: 60;";
            btnNewGame.setStyle(buttonStyle);
            btnOptions.setStyle(buttonStyle);
            btnClose.setStyle(buttonStyle);
            
            btnNewGame.setOnAction(e -> openUIGame(primaryStage));
            btnOptions.setOnAction(e -> openUISettings(primaryStage));
            btnClose.setOnAction(e -> primaryStage.close());
            
            // position house images
            StackPane leftPane = new StackPane(maisonLeft);
            StackPane rightPane = new StackPane(maisonRight);
            leftPane.setAlignment(Pos.BOTTOM_LEFT);
            rightPane.setAlignment(Pos.BOTTOM_RIGHT);

            // main container
            VBox centerContent = new VBox(10, topArea, btnNewGame, btnOptions, btnClose);
            centerContent.setAlignment(Pos.CENTER);

            StackPane root = new StackPane(background, leftPane, rightPane, centerContent);
            StackPane.setAlignment(centerContent, Pos.CENTER);
            StackPane.setAlignment(leftPane, Pos.BOTTOM_LEFT);
            StackPane.setAlignment(rightPane, Pos.BOTTOM_RIGHT);

            // create scene with 1280x720 dimensions
            Scene scene = new Scene(root, 1280, 720);

            // configure window
            primaryStage.setTitle("Game of Curling - Menu");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing menu interface", e);
        }
    }
    
    /**
     * Opens the game interface from the main menu.
     * Creates and displays the game scene.
     *
     * @param primaryStage The primary stage to display the game interface
     */
    private void openUIGame(Stage primaryStage) {
        // Create new scene for UIGame
        UIGame uiGame = new UIGame();
        uiGame.start(primaryStage);
        Scene gameScene = uiGame.getScene(primaryStage);
        primaryStage.setScene(gameScene);
    }
    
    /**
     * Opens the settings interface from the main menu.
     * Creates and displays the settings scene.
     *
     * @param primaryStage The primary stage to display the settings interface
     */
    private void openUISettings(Stage primaryStage) {
        // Create new scene for UISettings
        UISettings uiSettings = new UISettings();
        uiSettings.start(primaryStage);
        Scene settingsScene = uiSettings.getScene(primaryStage);
        primaryStage.setScene(settingsScene);
    }
}
