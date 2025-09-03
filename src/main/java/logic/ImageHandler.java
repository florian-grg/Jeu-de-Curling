package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

/**
 * Handles image processing and detection for the game.
 * Manages token and target detection, position tracking, and visualization.
 * Provides methods for image capture, processing, and drawing game elements.
 */
public class ImageHandler {
	private static int[] tokensPosition;
	private static int[] targetPosition;
	private static int[] tokenPosition;
	private static int distanceToTarget;
	private static boolean newTurn;
	private static double tokenRadius;
	private static double targetRadius;
	private static int expositionValue = -8;
	private static int cameraWidth = 1280;
	private static int cameraHeight = 720;
	private static int tokenStyle = 1; // 1 for images (curlingJ1/2.png), 2 for circles
	private static int targetStyle = 1; // 1 for real target , 2 for virtual target

	static {
		// Load the OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Main method for testing image processing functionality.
	 * Demonstrates target and token detection capabilities.
	 *
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
		// Load the image
		String path = "imagesTest/";
		Mat imageClean1 = Imgcodecs.imread(path + "image_clean_1.jpg");
		Mat imageClean2 = Imgcodecs.imread(path + "image_clean_6.jpg");
		ImageHandler handler = new ImageHandler();

		// Get positions
		int[] targetPosition = ImageHandler.getTargetPosition(imageClean1);
		if (targetPosition != null) {
			drawCross(imageClean1, targetPosition, new Scalar(255, 0, 0)); // Blue cross for the target
		}
		afficherImage(imageClean1);
		int[] tokenPosition = handler.getTokenPosition(imageClean2);
		int distance = handler.getDistanceToTarget();

		// Display results
		System.out.println("Target position: " + Arrays.toString(targetPosition));
		System.out.println("Token position: " + Arrays.toString(tokenPosition));
		System.out.println("Distance: " + distance);

		// Draw crosses on the image
		if (targetPosition != null) {
			drawCross(imageClean2, targetPosition, new Scalar(255, 0, 0)); // Blue cross for the target
		}
		if (tokenPosition != null) {
			drawCross(imageClean2, tokenPosition, new Scalar(0, 0, 255)); // Red cross for the token
		}
		afficherResultat(targetPosition, distance);
		afficherImage(imageClean2);
	}

	/**
	 * Draws a cross marker at the specified position.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y]
	 * @param color    Color of the cross marker
	 */
	private static void drawCross(Mat image, int[] position, Scalar color) {
		int crossSize = 4;
		Point center = new Point(position[0], position[1]);

		// Horizontal line
		Imgproc.line(
				image,
				new Point(center.x - crossSize, center.y),
				new Point(center.x + crossSize, center.y),
				color,
				2);

		// Vertical line
		Imgproc.line(
				image,
				new Point(center.x, center.y - crossSize),
				new Point(center.x, center.y + crossSize),
				color,
				2);
	}

	/**
	 * Gets the target position stored in memory.
	 * Getter for the static targetPosition field.
	 *
	 * @return Array containing target coordinates [x,y]
	 */
	public static int[] getTargetPosition() {
		return targetPosition;
	}

	/**
	 * Sets a new target position in memory.
	 * Setter for the static targetPosition field.
	 *
	 * @param newTargetPosition New position coordinates to set
	 */
	public static void setTargetPosition(int[] newTargetPosition) {
		ImageHandler.targetPosition = newTargetPosition;
	}

	/**
	 * Gets the current token radius value.
	 * Getter for the static tokenRadius field used for token detection and drawing.
	 *
	 * @return Current token radius in pixels
	 */
	public static double getTokenRadius() {
		return tokenRadius;
	}

	/**
	 * Sets a new token radius value.
	 * Setter for the static tokenRadius field used for token detection and drawing.
	 *
	 * @param tokenRadius New radius value in pixels
	 */
	public static void setTokenRadius(double tokenRadius) {
		System.out.println("Token radius set to " + tokenRadius);
		ImageHandler.tokenRadius = tokenRadius;
	}

	/**
	 * Gets the current target radius value.
	 * Getter for the static targetRadius field used for target detection and
	 * drawing.
	 *
	 * @return Current target radius in pixels
	 */
	public static double getTargetRadius() {
		return targetRadius;
	}

	/**
	 * Sets a new target radius value.
	 * Setter for the static targetRadius field used for target detection and
	 * drawing.
	 *
	 * @param radius New radius value in pixels
	 */
	public static void setTargetRadius(double radius) {
		System.out.println("Target radius set to " + radius);
		ImageHandler.targetRadius = radius;
	}

	/**
	 * Gets the current camera exposure value.
	 * Getter for the static expositionValue field used for camera settings.
	 *
	 * @return Current exposure setting
	 */
	public static int getExpositionValue() {
		return expositionValue;
	}

	/**
	 * Gets all token positions.
	 * Getter for the static tokensPosition array storing all token coordinates.
	 *
	 * @return Array containing all token positions
	 */
	public static int[] getTokens() {
		return tokensPosition;
	}

	/**
	 * Sets positions for all tokens at once.
	 * Setter for the static tokensPosition array.
	 *
	 * @param tokensPosition New array containing all token positions
	 */
	public static void setTokensPosition(int[] tokensPosition) {
		ImageHandler.tokensPosition = tokensPosition;
	}

	/**
	 * Gets the current token positions array.
	 * Getter providing access to the tokens position data.
	 *
	 * @return Array containing token positions
	 */
	public int[] getTokensPosition() {
		return tokensPosition;
	}

	/**
	 * Gets the current token visualization style.
	 * Getter for the static tokenStyle field defining how tokens are displayed.
	 *
	 * @return 1 for image-based tokens, 2 for circle-based tokens
	 */
	public static int getTokenStyle() {
		return tokenStyle;
	}

	/**
	 * Sets the token visualization style.
	 * Setter for the static tokenStyle field controlling token appearance.
	 *
	 * @param style 1 for image-based tokens, 2 for circle-based tokens
	 */
	public static void setTokenStyle(int style) {
		if (style >= 1 && style <= 2) {
			tokenStyle = style;
			System.out.println("Token style updated: " + style);
		}
	}

	/**
	 * Sets the camera exposure value.
	 * Updates the exposure setting and logs the new value.
	 *
	 * @param expositionValue New exposure value to set
	 */
	public static void setExpositionValue(int expositionValue) {
		ImageHandler.expositionValue = expositionValue;
		System.out.println("Exposition value set to " + ImageHandler.expositionValue);
	}

	/**
	 * Gets the current camera width value.
	 * Getter for the static cameraWidth field used for camera settings.
	 *
	 * @return Current camera width setting
	 */
	public static int getCameraWidth() {
		return cameraWidth;
	}

	/**
	 * Sets the camera width value.
	 * Updates the camera width setting.
	 *
	 * @param cameraWidth New camera width value to set
	 */
	public static void setCameraWidth(int cameraWidth) {
		ImageHandler.cameraWidth = cameraWidth;
	}

	/**
	 * Gets the current camera height value.
	 * Getter for the static cameraHeight field used for camera settings.
	 *
	 * @return Current camera height setting
	 */
	public static int getCameraHeight() {
		return cameraHeight;
	}

	/**
	 * Sets the camera height value.
	 * Updates the camera height setting.
	 *
	 * @param cameraHeight New camera height value to set
	 */
	public static void setCameraHeight(int cameraHeight) {
		ImageHandler.cameraHeight = cameraHeight;
	}

	/**
	 * Initializes a new ImageHandler instance.
	 * Sets default values for token and target parameters.
	 */
	public ImageHandler() {
		targetPosition = null;
		tokenPosition = null;
		distanceToTarget = -1;
		newTurn = false;
		tokenRadius = 34;
		targetRadius = 205;
	}

	/**
	 * Displays the results of token and target detection.
	 *
	 * @param targetPosition Coordinates of the detected target
	 * @param distance       Distance between token and target
	 */
	public static void afficherResultat(int[] targetPosition, int distance) {
		System.out.println("Target position: " + targetPosition[0] + ", " + targetPosition[1]);
		if (tokenPosition == null) {
			System.out.println("Token position: null");
		} else {
			System.out.println("Token position: " + tokenPosition[0] + ", " + tokenPosition[1]);
		}
		System.out.println("Distance to target: " + distance);
	}

	/**
	 * Displays the image with detected tokens and targets.
	 * Shows visual feedback of detection results.
	 *
	 * @param image Mat object to display
	 */
	public static void afficherImage(Mat image) {
		if (tokenPosition != null) {
			drawToken(image, tokenPosition);
		}
		System.out.println("Target position: " + Arrays.toString(targetPosition));
		if (targetPosition != null) {
			drawTarget(image, targetPosition);
		}
		HighGui.imshow("Image", image);
		HighGui.waitKey();
	}

	/**
	 * Draws a token marker at the specified position.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y]
	 */
	public static void drawToken(Mat image, int[] position) {
		if (position[0] != 0) {
			Imgproc.circle(image, new Point(tokenPosition[0], tokenPosition[1]), (int) tokenRadius,
					new Scalar(0, 0, 255), 3);
		}
	}

	/**
	 * Draws a target marker at the specified position.
	 * Uses either real or virtual target style based on targetStyle setting.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y]
	 */
	public static void drawTarget(Mat image, int[] position) {
		if (position[0] != 0) {
			if (targetStyle == 1) {
				// Draw real target (simple circle)
				Imgproc.circle(
					image,
					new Point(position[0], position[1]),
					(int) targetRadius,
					new Scalar(255, 0, 0),
					3);
			} else {
				// Draw virtual target with effects
				drawVirtualTarget(image, position);
				targetPosition = position;
			}
		}
	}

	/**
	 * Draws a virtual target with concentric circles and transparency effects.
	 * Creates a target visualization with a semi-transparent blue outer circle
	 * and a red inner circle.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y] for the target
	 */
	private static void drawVirtualTarget(Mat image, int[] position) {
		if (position[0] != 0) {
			Point center = new Point(position[0], position[1]);
			
			// Draw semi-transparent blue outer circle
			Mat overlay = image.clone();
			Imgproc.circle(overlay, center, (int)targetRadius, new Scalar(255, 0, 0), -1);
			Core.addWeighted(overlay, 0.2, image, 0.8, 0, image);
			
			// Draw concentric circles: blue outer and red inner
			int[] radii = {(int)targetRadius, (int)(targetRadius*0.5)};
			Scalar[] colors = {
				new Scalar(255, 0, 0),    // Blue
				new Scalar(0, 0, 255)     // Red
			};
			
			// Draw the circles with defined thickness
			for (int i = 0; i < radii.length; i++) {
				Imgproc.circle(image, center, radii[i], colors[i], 2);
			}
			
			// Draw small red center point
			Imgproc.circle(image, center, 5, new Scalar(0, 0, 255), -1);
		}
	}

	/**
	 * Draws a token for player 1 at the specified position.
	 * Uses either image overlay or circle based on style setting.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y]
	 */
	private static void drawTokenPlayer(Mat image, int[] position, String imagePath) {
		if (position[0] != 0) {
			// Load the overlay image with -1 to preserve the alpha channel
			Mat overlay = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_UNCHANGED);

			// Resize the overlay based on the tokenRadius
			int newSize = (int) (tokenRadius * 4);
			Mat resizedOverlay = new Mat();
			Imgproc.resize(overlay, resizedOverlay, new Size(newSize, newSize));

			// Check if the image has 4 channels (BGRA)
			if (resizedOverlay.channels() != 4) {
				System.out.println("The image must have an alpha channel");
				return;
			}

			// Define the region of interest (ROI)
			int x = position[0] - resizedOverlay.cols() / 2;
			int y = position[1] - resizedOverlay.rows() / 2;
			Rect roi = new Rect(x, y, resizedOverlay.cols(), resizedOverlay.rows());

			if (x >= 0 && y >= 0 && x + resizedOverlay.cols() <= image.cols()
					&& y + resizedOverlay.rows() <= image.rows()) {
				Mat imageROI = image.submat(roi);

				// Split the channels
				List<Mat> overlayChannels = new ArrayList<>();
				Core.split(resizedOverlay, overlayChannels);

				// Normalize the alpha channel
				Mat alpha = overlayChannels.get(3);
				alpha.convertTo(alpha, CvType.CV_32F, 1.0 / 255.0);

				// Create the image without the alpha channel
				Mat overlayColor = new Mat();
				Core.merge(overlayChannels.subList(0, 3), overlayColor);

				// Apply alpha blending
				Mat foreground = new Mat();
				Mat background = new Mat();
				overlayColor.convertTo(foreground, CvType.CV_32F);
				imageROI.convertTo(background, CvType.CV_32F);

				// For each pixel: result = alpha * foreground + (1-alpha) * background
				for (int i = 0; i < foreground.rows(); i++) {
					for (int j = 0; j < foreground.cols(); j++) {
						double[] fg = foreground.get(i, j);
						double[] bg = background.get(i, j);
						double a = alpha.get(i, j)[0];

						double[] result = new double[3];
						for (int c = 0; c < 3; c++) {
							result[c] = a * fg[c] + (1 - a) * bg[c];
						}
						background.put(i, j, result);
					}
				}

				background.convertTo(imageROI, CvType.CV_8U);
			}
		}
	}

	/**
	 * Draws a token for player 1 using specified image.
	 * Wrapper method for drawTokenPlayer with player 1's image.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y]
	 */
	public static void drawTokenPlayer1(Mat image, int[] position) {
		drawTokenPlayer(image, position, "curlingJ1.png");
	}

	/**
	 * Draws a token for player 2 using specified image.
	 * Wrapper method for drawTokenPlayer with player 2's image.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y]
	 */
	public static void drawTokenPlayer2(Mat image, int[] position) {
		drawTokenPlayer(image, position, "curlingJ2.png");
	}

	/**
	 * Detects the target position in the provided image.
	 * Uses circle detection algorithms to locate the target area.
	 *
	 * @param image Input image to process
	 * @return int[] array containing target coordinates [x,y], or null if not found
	 */
	public static int[] getTargetPosition(Mat image) {
		// Convert to grayscale for better efficiency
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
		// Edge detection with Canny - lower thresholds for imperfect circles
		Imgproc.Canny(grayImage, grayImage, 30, 100); // Lower thresholds

		// Larger morphological closing
		Mat strel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));
		Imgproc.morphologyEx(grayImage, grayImage, Imgproc.MORPH_CLOSE, strel);

		Mat circles = new Mat();
		int minRadius, maxRadiusSearch;

		minRadius = (int) (targetRadius - 8); // Increase search range
		maxRadiusSearch = (int) (targetRadius + 8);

		// HoughCircles method for circle detection
		Imgproc.HoughCircles(
				grayImage,
				circles,
				Imgproc.HOUGH_GRADIENT,
				1.0,
				1,
				15,
				5,
				minRadius,
				maxRadiusSearch);
		System.out.println("Circles found: " + circles.cols());

		// For robustness: take the average of the circle centers
		double sumX = 0;
		double sumY = 0;
		int[] position = null;
		int nbCircles = circles.cols(); // Total number of detected circles

		// First pass: calculate averages
		for (int i = 0; i < nbCircles; i++) {
			double[] circle = circles.get(0, i);
			sumX += circle[0];
			sumY += circle[1];
		}

		// If circles were found, calculate the average
		if (nbCircles > 0) {
			position = new int[] {
					(int) (sumX / nbCircles),
					(int) (sumY / nbCircles)
			};
		}
		;

		ImageHandler.targetPosition = position;

		return position;
	}

	/**
	 * Detects the token position in the provided image.
	 * Uses color detection and circle detection algorithms.
	 *
	 * @param image Input image to process
	 * @return int[] array containing token coordinates [x,y], or null if not found
	 */
	public int[] getTokenPosition(Mat image) {
		// Convert to HSV color space
		Mat hsvImage = new Mat();
		Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

		// Define color ranges
		// Red HSV
		Scalar lowerRed1 = new Scalar(0, 100, 100);
		Scalar upperRed1 = new Scalar(10, 255, 255);
		Scalar lowerRed2 = new Scalar(160, 100, 100);
		Scalar upperRed2 = new Scalar(179, 255, 255);

		// Adjusted white HSV
		// Hue can be any value as saturation is very low
		// Saturation must be very low (close to 0)
		// Value must be very high (close to 255)
		Scalar lowerWhite = new Scalar(0, 0, 130); // H:0-180, S:0-20, V:180-255
		Scalar upperWhite = new Scalar(360, 130, 255); // Covers the entire hue spectrum

		// Blue HSV for target detection
		Scalar lowerBlue = new Scalar(75, 70, 70); // Blue-green hues
		Scalar upperBlue = new Scalar(140, 255, 255); // Dark blue

		// Create masks
		Mat redMask1 = new Mat();
		Mat redMask2 = new Mat();
		Mat blueMask = new Mat(); // Add blue mask
		Mat whiteMask = new Mat();

		// Detect colors
		Core.inRange(hsvImage, lowerRed1, upperRed1, redMask1);
		Core.inRange(hsvImage, lowerRed2, upperRed2, redMask2);
		Core.inRange(hsvImage, lowerBlue, upperBlue, blueMask); // Detect blue
		Core.inRange(hsvImage, lowerWhite, upperWhite, whiteMask);

		// afficherImage(blueMask); // Display blue mask for debugging

		// Combine red masks
		Mat redMask = new Mat();
		Core.add(redMask1, redMask2, redMask);

		// Convert the image to grayscale
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
		// Binary thresholding
		Imgproc.threshold(grayImage, grayImage, 185, 255, Imgproc.THRESH_BINARY_INV);
		// Remove red, blue, and white areas
		Core.subtract(grayImage, redMask, grayImage);
		Core.subtract(grayImage, blueMask, grayImage); // Subtract blue mask
		Core.subtract(grayImage, whiteMask, grayImage);

		// Clean the image with morphology
		Mat strel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
				new Size((int) tokenRadius / 4, (int) tokenRadius / 4));
		Imgproc.morphologyEx(grayImage, grayImage, Imgproc.MORPH_OPEN, strel);

		// Morphological closing
		strel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
				new Size((int) tokenRadius / 2, (int) tokenRadius / 2));
		Imgproc.morphologyEx(grayImage, grayImage, Imgproc.MORPH_CLOSE, strel);
		// afficherImage(grayImage);
		// Circle detection
		Mat circles = new Mat();
		int minRadius = (int) (tokenRadius - 5);
		int maxRadius = (int) (tokenRadius + 5);

		// Optimized HoughCircles parameters
		Imgproc.HoughCircles(
				grayImage,
				circles,
				Imgproc.HOUGH_GRADIENT,
				1.0,
				1, // minimum distance between centers
				30, // upper threshold for Canny
				10, // accumulator threshold
				minRadius,
				maxRadius);

		for (int i = 0; i < circles.cols(); i++) {
			double[] circle = circles.get(0, i);
			Point center = new Point(circle[0], circle[1]);
			int radius = (int) circle[2];
			// Draw the center
			Imgproc.circle(grayImage, center, 3, new Scalar(0, 255, 0), -1);
			// Draw the circle
			Imgproc.circle(grayImage, center, radius, new Scalar(0, 0, 255), 2);
		}

		// Calculate the average positions as for the target
		double sumX = 0;
		double sumY = 0;
		double sumRadius = 0;
		int[] position = null;
		int nbCircles = circles.cols();

		for (int i = 0; i < nbCircles; i++) {
			double[] circle = circles.get(0, i);
			sumX += circle[0];
			sumY += circle[1];
			sumRadius += circle[2];
		}

		if (nbCircles > 0) {
			position = new int[] {
					(int) (sumX / nbCircles),
					(int) (sumY / nbCircles)
			};
			if (tokenRadius < 0) {
				tokenRadius = sumRadius / nbCircles;
			}
		}

		tokenPosition = position;

		return position;
	}

	/**
	 * Calculates distance between token and target.
	 *
	 * @return Distance in pixels, or -1 if either position is unknown
	 */
	public int getDistanceToTarget() {
		if (ImageHandler.targetPosition == null || ImageHandler.tokenPosition == null) {
			return -1; // Indicates that one of the positions is missing
		}

		int dx = tokenPosition[0] - targetPosition[0];
		int dy = tokenPosition[1] - targetPosition[1];
		distanceToTarget = (int) Math.sqrt(dx * dx + dy * dy);

		return distanceToTarget;
	}

	/**
	 * Detects if a turn change has occurred based on token position.
	 *
	 * @return true if turn should change, false otherwise
	 */
	public boolean detectTurnChange() {
		// Simple logic: if distance is below threshold, consider turn complete
		int threshold = 20; // Adjust based on desired precision
		if (distanceToTarget > 0 && distanceToTarget < threshold) {
			try {
				System.out.println("Please remove the token...");
				Thread.sleep(5000); // Pause for 5 seconds
			} catch (InterruptedException e) {
				System.out.println("Sleep interrupted");
			}
			newTurn = true;
		} else {
			newTurn = false;
		}
		return newTurn;
	}

	/**
	 * Captures and returns a new image from the webcam.
	 *
	 * @return Mat object containing the captured image
	 */
	public Mat getImage() {
		String filename = "image.jpg";
		File file = new File(filename);
		String usbDeviceID = "USB\\VID_045E&PID_075D&MI_00\\7&BDE31B4&0&0000";
		Mat image = WebcamCapture.captureImage(filename, usbDeviceID, file);
		return image;
	}

	/**
	 * Adds a new token to the tokens position array.
	 * Stores the token's position in the first available slot.
	 *
	 * @param token Token object to add
	 */
	public void addToken(Token token) {
		if (tokensPosition == null) {
			tokensPosition = new int[128];
		}
		for (int i = 0; i < tokensPosition.length; i += 2) {
			if (tokensPosition[i] == 0) {
				if (tokensPosition[i + 1] == 0) {
					tokensPosition[i] = token.getPosition()[0];
					tokensPosition[i + 1] = token.getPosition()[1];
					break;
				}
			}
		}
	}

	/**
	 * Gets the current target visualization style.
	 * Getter for the static targetStyle field defining how the target is displayed.
	 *
	 * @return 1 for real target, 2 for virtual target visualization
	 */
	public static int getTargetStyle() {
		return targetStyle;
	}

	/**
	 * Sets the target visualization style.
	 * Setter for the static targetStyle field controlling target appearance.
	 *
	 * @param style 1 for real target, 2 for virtual target visualization
	 */
	public static void setTargetStyle(int style) {
		if (style >= 1 && style <= 2) {
			targetStyle = style;
			System.out.println("Target style mis à jour : " + style);
		}
	}

	/**
	 * Draws a red circle representing player 1's token.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y] for the circle
	 */
	public static void drawRedCircle(Mat image, int[] position) {
		if (position[0] != 0) {
			Scalar color = new Scalar(0, 0, 255); // BGR: Red
			Imgproc.circle(
					image,
					new Point(position[0], position[1]),
					(int) tokenRadius,
					color,
					3);
		}
	}

	/**
	 * Draws a blue circle representing player 2's token.
	 *
	 * @param image    Image to draw on
	 * @param position Position coordinates [x,y] for the circle
	 */
	public static void drawBlueCircle(Mat image, int[] position) {
		if (position[0] != 0) {
			Scalar color = new Scalar(255, 0, 0); // BGR: Blue
			Imgproc.circle(
					image,
					new Point(position[0], position[1]),
					(int) tokenRadius,
					color,
					3);
		}
	}
}