package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.Gameplay;
import logic.ImageHandler;

/**
 * Settings interface for the game configuration.
 * Manages game parameters like number of turns, rounds, and token appearance.
 * Provides visual controls for adjusting game settings.
 */
public class UISettings extends Application {
    private Text nbTurnsText;
    private Text nbRoundsText;

    /**
     * Initializes and displays the settings interface.
     * Sets up all configuration controls and layout.
     *
     * @param primaryStage The primary stage for the settings window
     */
    @Override
    public void start(Stage primaryStage) {
        Scene scene = getScene(primaryStage);
        primaryStage.setTitle("Game of Curling - Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates and configures the settings interface scene.
     * Sets up turn counters, round controls, and token style options.
     *
     * @param primaryStage The primary stage for component layout
     * @return Complete scene with all settings controls
     */
    public Scene getScene(Stage primaryStage) {
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

        // Create and configure title
        Text title = new Text("Paramètres");
        title.setFont(Font.font("Alba Super", 96)); // Augmenter la taille du texte pour la résolution 1280x720
        title.setStyle("-fx-fill: white; -fx-font-weight: bold;");

        StackPane textTitle = new StackPane(title);
        textTitle.setPrefSize(700, 100); // Dimensions du fond (largeur x hauteur)
        textTitle.setStyle(
                "-fx-background-color: #004D2C; -fx-background-radius: 60; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");
        textTitle.setAlignment(Pos.CENTER);

        // contain the title
        HBox topArea = new HBox(10, textTitle);
        topArea.setAlignment(Pos.TOP_CENTER);

        // Configure token style controls
        Text tokenTypeText = new Text("Jeton : " + (ImageHandler.getTokenStyle() == 1 ? "Image" : "Cercle"));
        styleText(tokenTypeText);

        Button btnPrevType = new Button("<");
        Button btnNextType = new Button(">");
        styleButton(btnPrevType);
        styleButton(btnNextType);

        btnPrevType.setOnAction(e -> {
            changeTokenStyle(-1);
            tokenTypeText.setText("Jeton : " + (ImageHandler.getTokenStyle() == 1 ? "Image" : "Cercle"));
        });
        btnNextType.setOnAction(e -> {
            changeTokenStyle(1);
            tokenTypeText.setText("Jeton : " + (ImageHandler.getTokenStyle() == 1 ? "Image" : "Cercle"));
        });
        
        Text targetTypeText = new Text("Cible : " + (ImageHandler.getTargetStyle() == 1 ? "Réelle" : "Virtuelle"));
        styleText(targetTypeText);

        Button btnPrevTypeTarget = new Button("<");
        Button btnNextTypeTarget = new Button(">");
        styleButton(btnPrevTypeTarget);
        styleButton(btnNextTypeTarget);

        btnPrevTypeTarget.setOnAction(e -> {
            changeTargetStyle(-1);
            targetTypeText.setText("Cible : " + (ImageHandler.getTargetStyle() == 1 ? "Réelle" : "Virtuelle"));
        });
        btnNextTypeTarget.setOnAction(e -> {
            changeTargetStyle(1);
            targetTypeText.setText("Cible : " + (ImageHandler.getTargetStyle() == 1 ? "Réelle" : "Virtuelle"));
        });

        HBox tokenTypeControl = new HBox(10, btnPrevType, tokenTypeText, btnNextType);
        tokenTypeControl.setAlignment(Pos.CENTER);

        HBox targetTypeControl = new HBox(10, btnPrevTypeTarget, targetTypeText, btnNextTypeTarget);
        targetTypeControl.setAlignment(Pos.CENTER);
        
        Button btnQuit = new Button("Retour");

        // Ajustement des tailles des boutons
        btnQuit.setPrefSize(300, 75);

        // Styles des boutons
        String buttonStyle = "-fx-background-image: url('bouttonCurling.png');"
                + "-fx-background-size: cover;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 24px;"
                + "-fx-background-color: transparent;"
                + "-fx-background-radius: 60;";
        btnQuit.setStyle(buttonStyle);

        btnQuit.setOnAction(e -> openUIMenu(primaryStage));

        HBox button3 = new HBox(10, btnQuit);
        button3.setAlignment(Pos.CENTER);

        // Configure turn controls
        Button btnDecreaseTurns = new Button("-");
        Button btnIncreaseTurns = new Button("+");
        nbTurnsText = new Text("Tours: " + Gameplay.getNbTurns());
        styleButton(btnDecreaseTurns);
        styleButton(btnIncreaseTurns);
        styleText(nbTurnsText);

        // Configure round controls
        Button btnDecreaseRounds = new Button("-");
        Button btnIncreaseRounds = new Button("+");
        nbRoundsText = new Text("Manches: " + Gameplay.getMaxRounds());
        styleButton(btnDecreaseRounds);
        styleButton(btnIncreaseRounds);
        styleText(nbRoundsText);

        // Configuration des actions
        btnDecreaseTurns.setOnAction(e -> {
            Gameplay.decrementNbTurns();
            updateTexts();
        });
        btnIncreaseTurns.setOnAction(e -> {
            Gameplay.incrementNbTurns();
            updateTexts();
        });
        btnDecreaseRounds.setOnAction(e -> {
            Gameplay.decrementMaxRounds();
            updateTexts();
        });
        btnIncreaseRounds.setOnAction(e -> {
            Gameplay.incrementMaxRounds();
            updateTexts();
        });

        // Create and configure layout for controls
        HBox turnsControl = new HBox(10, btnDecreaseTurns, nbTurnsText, btnIncreaseTurns);
        HBox roundsControl = new HBox(10, btnDecreaseRounds, nbRoundsText, btnIncreaseRounds);
        turnsControl.setAlignment(Pos.CENTER);
        roundsControl.setAlignment(Pos.CENTER);

        // house images positioning
        StackPane leftPane = new StackPane(maisonLeft);
        StackPane rightPane = new StackPane(maisonRight);
        leftPane.setAlignment(Pos.BOTTOM_LEFT);
        rightPane.setAlignment(Pos.BOTTOM_RIGHT);
        // main container
        VBox centerContent = new VBox(20, topArea, turnsControl, roundsControl, tokenTypeControl, targetTypeControl, button3);
        centerContent.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(background, leftPane, rightPane, centerContent);
        StackPane.setAlignment(centerContent, Pos.CENTER);
        StackPane.setAlignment(leftPane, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(rightPane, Pos.BOTTOM_RIGHT);

        // create scene with 1280x720 dimensions
        return new Scene(root, 1280, 720);
    }

    /**
     * Updates the display text for turns and rounds.
     * Refreshes UI with current game configuration values.
     */
    private void updateTexts() {
        nbTurnsText.setText("Tours: " + Gameplay.getNbTurns());
        nbRoundsText.setText("Rounds: " + Gameplay.getMaxRounds());
    }

    /**
     * Applies consistent styling to buttons.
     * Sets up standard appearance for all control buttons.
     *
     * @param btn Button to be styled
     */
    private void styleButton(Button btn) {
        btn.setPrefSize(75, 75);
        btn.setStyle("-fx-background-image: url('bouttonCurling.png');" +
                "-fx-background-size: cover;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 24px;" +
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 60;");
    }

    /**
     * Applies consistent styling to text elements.
     * Sets up standard appearance for all text displays.
     *
     * @param text Text element to be styled
     */
    private void styleText(Text text) {
        text.setFill(javafx.scene.paint.Color.WHITE);
        text.setStyle("-fx-font-size: 36px;");
    }

    /**
     * Returns to the main menu interface.
     * Saves current settings and transitions back to menu.
     *
     * @param primaryStage The primary stage for UI display
     */
    private void openUIMenu(Stage primaryStage) {
        UIMenu uiMenu = new UIMenu();
        uiMenu.start(primaryStage);
    }

    /**
     * Changes the token visualization style.
     * Toggles between available token display modes (image/circle).
     * Ensures the style value stays within valid range (1-2).
     *
     * @param direction Direction of change (1 for next, -1 for previous)
     */
    private void changeTokenStyle(int direction) {
        int currentStyle = ImageHandler.getTokenStyle();
        int newStyle = currentStyle + direction;

        if (newStyle < 1)
            newStyle = 2;
        if (newStyle > 2)
            newStyle = 1;

        ImageHandler.setTokenStyle(newStyle);
    }
    
    /**
     * Changes the target visualization style.
     * Toggles between available target display modes (real/virtual).
     * Ensures the style value stays within valid range (1-2).
     *
     * @param direction Direction of change (1 for next, -1 for previous)
     */
    private void changeTargetStyle(int direction) {
        int currentStyle = ImageHandler.getTargetStyle();
        int newStyle = currentStyle + direction;

        if (newStyle < 1)
            newStyle = 2;
        if (newStyle > 2)
            newStyle = 1;

        ImageHandler.setTargetStyle(newStyle);
    }
}