package logic;

import java.io.File;

import org.opencv.core.Core;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import view.UIMenu;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Main application class that handles the gameplay interface and webcam capture.
 * This class initializes the OpenCV library, manages continuous image capture from webcam,
 * and coordinates the user interface components.
 */
public class GameplayApp extends Application {
    private static final Logger logger = Logger.getLogger(GameplayApp.class.getName());

    static {
        // Set the path to the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Initializes and starts the application components.
     * Sets up continuous webcam capture in a separate thread and launches the UI interface.
     *
     * @param primaryStage The primary stage for the application UI
     */
    @Override
    public void start(Stage primaryStage) {
        configureLogger();
        String filename = "image.jpg";
        File file = new File(filename);
        String usbDeviceID = "USB\\VID_045E&PID_075D&MI_00\\7&BDE31B4&0&0000";
        
        // Create a thread that continuously captures images until program termination
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                    WebcamCapture.captureImage(filename, usbDeviceID, file);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Exception in UIMenu", e);
                }
            }
        }).start();
        
        UIMenu UI = new UIMenu();

        // Start a thread for the user interface
        new Thread(() -> {
            Platform.runLater(() -> UI.start(primaryStage));
        }).start();
    }

    /**
     * Configures the application logger with file output and exception handling.
     * Sets up logging to write to 'application.log' and establishes uncaught exception handling.
     */
    private void configureLogger() {
        try {
            // Create a FileHandler to write logs to a file
            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Set logging level
            logger.setLevel(Level.ALL);

            // Add handler for uncaught exceptions
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                logger.log(Level.SEVERE, "Uncaught exception in thread " + thread.getName(), throwable);
            });

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to configure logger", e);
        }
    }

    /**
     * Application entry point.
     * Launches the JavaFX application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
