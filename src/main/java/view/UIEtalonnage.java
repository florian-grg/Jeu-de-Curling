package view;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.ImageHandler;
import logic.WebcamCapture;

/**
 * Calibration interface for the game.
 * Provides controls for adjusting token and target detection parameters,
 * auto-exposure settings, and visual feedback of the camera feed.
 */
public class UIEtalonnage extends Application {
	private Timeline timeline;
	private VBox imagePane;
	private Text tokenRadiusText;
	private Text targetRadiusText;  // Nouveau texte pour Target Radius

	/**
     * Initializes and displays the calibration interface.
     * Sets up camera feed and calibration controls.
     *
     * @param primaryStage The primary stage for the calibration window
     */
	@Override
	public void start(Stage primaryStage) {
	    Scene scene = getScene(primaryStage);
	    primaryStage.setTitle("Game of Curling - Menu");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}

	/**
     * Creates and configures the calibration interface scene.
     * Sets up all UI controls and camera preview.
     *
     * @param primaryStage The primary stage for the window
     * @return Configured Scene with all calibration controls
     */
	public Scene getScene(Stage primaryStage) {
		// Load background
    	ImageView background = new ImageView(new Image("background.png"));
        background.setFitWidth(1280);
        background.setFitHeight(720);
        background.setPreserveRatio(false);
		
		// Load and configure window title
    	Text title = new Text("Étalonnage");
    	title.setFont(Font.font("Alba Super", 96)); // Increase text size for 1280x720 resolution
    	title.setStyle("-fx-fill: white; -fx-font-weight: bold;");
        
		// Create and configure title container
		StackPane textTitle = new StackPane(title);
		textTitle.setPrefSize(700, 100); // Background dimensions (width x height)
		textTitle.setStyle("-fx-background-color: #004D2C; -fx-background-radius: 60; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 60;");
		textTitle.setAlignment(Pos.CENTER);
		
		 // Container for title area
		HBox topArea = new HBox(10, textTitle);
		topArea.setAlignment(Pos.TOP_CENTER);

		// Create buttons with proportional sizes
		Button btnQuit = new Button("Retour");
		Button autoExpositionButton = new Button("Auto Exposition");
		btnQuit.setOnAction(e -> openUIGame(primaryStage));
		autoExpositionButton.setOnAction(e -> AutoExposition());
		
		// Adjust button sizes
		btnQuit.setPrefWidth(300);
		autoExpositionButton.setPrefWidth(300);
		btnQuit.setPrefHeight(75);
		autoExpositionButton.setPrefHeight(75);

		 // button styles
		String buttonStyle = "-fx-background-image: url('bouttonCurling.png');"
		+ "-fx-background-size: cover;"
		+ "-fx-text-fill: white;"
		+ "-fx-font-size: 24px;"
		+ "-fx-background-color: transparent;"
		+ "-fx-background-radius: 60;";
		btnQuit.setStyle(buttonStyle);
		autoExpositionButton.setStyle(buttonStyle);
		
		autoExpositionButton.setAlignment(Pos.BOTTOM_CENTER);
		btnQuit.setAlignment(Pos.BOTTOM_CENTER);
		
		// Configure token radius controls
        Button tokenRadiusButtonPlus = new Button("+");
        Button tokenRadiusButtonMinus = new Button("-");
        tokenRadiusText = new Text("Rayon Jeton: " + (int)ImageHandler.getTokenRadius());
        styleButton(tokenRadiusButtonPlus);
        styleButton(tokenRadiusButtonMinus);
        styleText(tokenRadiusText);
        
        tokenRadiusButtonPlus.setAlignment(Pos.BOTTOM_CENTER);

        // Button for the target radius
        Button targetRadiusButtonPlus = new Button("+");
        Button targetRadiusButtonMinus = new Button("-");
        targetRadiusText = new Text("Rayon Cible: " + (int)ImageHandler.getTargetRadius());
        styleButton(targetRadiusButtonPlus);
        styleButton(targetRadiusButtonMinus);
        styleText(targetRadiusText);

        // Button actions
        tokenRadiusButtonPlus.setOnAction(e -> updateTokenRadius(1));
        tokenRadiusButtonMinus.setOnAction(e -> updateTokenRadius(-1));
        targetRadiusButtonPlus.setOnAction(e -> updateTargetRadius(1));
        targetRadiusButtonMinus.setOnAction(e -> updateTargetRadius(-1));

        // Layout for radius controls
        HBox tokenRadiusBox = new HBox(10, tokenRadiusButtonMinus, tokenRadiusText, tokenRadiusButtonPlus);
        HBox targetRadiusBox = new HBox(10, targetRadiusButtonMinus, targetRadiusText, targetRadiusButtonPlus);
        tokenRadiusBox.setAlignment(Pos.CENTER);
        targetRadiusBox.setAlignment(Pos.CENTER);

        // Update main container to include both controls
        VBox radiusControls = new VBox(20, tokenRadiusBox, targetRadiusBox);
        radiusControls.setAlignment(Pos.CENTER);

		// Configure button container
		VBox buttonBox = new VBox(25);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(autoExpositionButton, radiusControls, btnQuit);

		// Configure image container
		imagePane = new VBox(5);
		imagePane.setAlignment(Pos.CENTER);

		// Configure main container for image and buttons
		HBox content = new HBox(20);
		content.setAlignment(Pos.CENTER);
		content.setPadding(new Insets(10));
		content.getChildren().addAll(imagePane, buttonBox);
		HBox.setHgrow(imagePane, Priority.ALWAYS);
		HBox.setHgrow(buttonBox, Priority.ALWAYS);
		content.widthProperty().addListener((obs, oldVal, newVal) -> {
			imagePane.setPrefWidth(newVal.doubleValue() * 0.75);
			buttonBox.setPrefWidth(newVal.doubleValue() * 0.25);
		});
		 // Configure global container
		VBox root = new VBox(5);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(10));
		root.getChildren().addAll(title, content);
		root.setStyle("-fx-background-color: transparent;");
		VBox.setVgrow(title, Priority.ALWAYS);
		VBox.setVgrow(content, Priority.ALWAYS);
		title.maxHeight(Double.MAX_VALUE);
		content.setMaxHeight(Double.MAX_VALUE);
		root.heightProperty().addListener((obs, oldVal, newVal) -> {
			title.prefHeight(newVal.doubleValue() * 0.20);
			content.setPrefHeight(newVal.doubleValue() * 0.80);
		});
		StackPane mainContainer = new StackPane(background, root);
		StackPane.setAlignment(root, Pos.CENTER);
		
		// Display image continuously to see changes
		this.timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
			updateImage();
		}));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		// Create scene with 1280x720 dimensions
		return new Scene(mainContainer, 1280, 720);
	}

	/**
     * Updates the token detection radius setting.
     * Validates and applies radius changes for token detection.
     *
     * @param modification Amount to change radius by (+/- 1)
     */
	private void updateTokenRadius(int modification) {
		if (ImageHandler.getTokenRadius() + modification < 0) {
			return;
		}
		ImageHandler.setTokenRadius(ImageHandler.getTokenRadius() + modification);
		tokenRadiusText.setText("Rayon Jeton: " + (int)ImageHandler.getTokenRadius());
	}

	/**
     * Updates the target detection radius setting.
     * Validates and applies radius changes for target detection.
     *
     * @param modification Amount to change radius by (+/- 1)
     */
	private void updateTargetRadius(int modification) {
    double newRadius = ImageHandler.getTargetRadius() + modification;
    // Check if new radius is between 0 and 500
    if (newRadius < 0 || newRadius > 500) {
        return;
    }
    ImageHandler.setTargetRadius(newRadius);
    targetRadiusText.setText("Rayon Cible: " + (int)ImageHandler.getTargetRadius());
}

	/**
     * Returns to the game interface.
     * Stops calibration preview and transitions back to game screen.
     *
     * @param primaryStage The primary stage for UI display
     */
	private void openUIGame(Stage primaryStage) {
		UIGame uiGame = new UIGame();
		this.timeline.stop();
		uiGame.start(primaryStage);
	}

	/**
     * Performs automatic exposure calibration.
     * Tests multiple exposure values to find optimal camera settings.
     */
	private void AutoExposition() {
		WebcamCapture.setExpositionSet(true);
        ImageHandler.setExpositionValue(-3);
        Boolean endCheck = true;
        Integer nbCheck = 0;
        int lastCheck =0;
        Mat image = Imgcodecs.imread("./image.jpg");
        System.out.println("Exposition Value: " + ImageHandler.getExpositionValue());
        while (endCheck) {
            image = Imgcodecs.imread("./image.jpg");
            updateImage();
            ImageHandler.setExpositionValue(ImageHandler.getExpositionValue()+1);
            if (ImageHandler.getTargetPosition(image) !=null) {
                nbCheck++;
                lastCheck = ImageHandler.getExpositionValue();
            }
            if (ImageHandler.getExpositionValue() == 2) {
                endCheck = false;
            }
        }
        if (nbCheck == 0) {
            System.out.println("No Exposition Value found for this target");
        }
        Integer midle = (int) (nbCheck/2);
        
        ImageHandler.setExpositionValue(lastCheck-midle-6);
    }

	/**
     * Updates the camera preview display.
     * Captures current frame and draws detection circles for visual feedback.
     */
	private void updateImage() {
	    Mat image = Imgcodecs.imread("./image.jpg");

	    if (image.empty()) {
	        System.out.println("Calibration image not found");
	        return;
	    }

	    // Draw blue circle for token
	    int tokenRadius = (int) ImageHandler.getTokenRadius();
	    Point tokenCenter = new Point(image.width() / 2, image.height() / 2);
	    Imgproc.circle(image, tokenCenter, tokenRadius, new Scalar(255, 0, 0), 3);

	    // Draw red circle for target
	    int targetRadius = (int) ImageHandler.getTargetRadius();
	    Point targetCenter = new Point(image.width() / 2, image.height() / 2);
	    Imgproc.circle(image, targetCenter, targetRadius, new Scalar(0, 0, 255), 3);

	    // Convert Mat to WritableImage format
	    WritableImage writableImage = new WritableImage(image.width(), image.height());
	    PixelWriter pixelWriter = writableImage.getPixelWriter();

	    for (int y = 0; y < image.height(); y++) {
	        for (int x = 0; x < image.width(); x++) {
	            double[] data = image.get(y, x);
	            Color color = Color.rgb((int) data[2], (int) data[1], (int) data[0]);
	            pixelWriter.setColor(x, y, color);
	        }
	    }

	    // Update background with new image
	    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
	    BackgroundImage backgroundImage = new BackgroundImage(writableImage, BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
	    imagePane.setBackground(new Background(backgroundImage));
	}

	/**
     * Applies consistent button styling.
     * Sets up visual appearance for all control buttons.
     *
     * @param button Button to be styled
     */
	private void styleButton(Button button) {
        button.setPrefWidth(50);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-image: url('bouttonCurling.png');"
                + "-fx-background-size: cover;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 24px;"
                + "-fx-background-color: transparent;"
                + "-fx-background-radius: 60;");
    }

    /**
     * Applies consistent text styling.
     * Sets up font and color for all text elements.
     *
     * @param text Text element to be styled
     */
    private void styleText(Text text) {
        text.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
    }
}