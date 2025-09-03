package logic;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages webcam capture functionality for the game.
 * Handles camera initialization, image capture, and camera settings.
 * Provides methods for capturing and saving images from the webcam.
 */
public class WebcamCapture {
	private static final Logger logger = Logger.getLogger(WebcamCapture.class.getName());
	private static boolean expositionSet = false;
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;
	private static final int EXPOSITION_VALUE = -8;
	private static VideoCapture camera;
	private static final String USB_DEVICE_ID = "USB\\VID_045E&PID_075D&MI_00\\7&BDE31B4&0&0000";

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Captures an image from the webcam with specified parameters.
	 * Handles camera initialization, image capture, and file saving.
	 *
	 * @param filename Name of the file to save the captured image
	 * @param usbDeviceID USB device identifier for the camera
	 * @param file File object for saving the image
	 * @return Mat object containing the captured image, or null if capture fails
	 */
	public static Mat captureImage(String filename, String usbDeviceID, File file) {

		try {
			int cameraIndex = findCameraByUSBID(USB_DEVICE_ID);

			if (cameraIndex == -1) {
				System.out.println("Error: Unable to find webcam with specified USB ID");
				return null;
			}

			camera = new VideoCapture(cameraIndex);

			if (!camera.isOpened()) {
				logger.log(Level.SEVERE, "Error: Failed to initialize camera capture");
				return null;
			}

			// Set optional camera parameters
			camera.set(Videoio.CAP_PROP_FRAME_WIDTH, CAMERA_WIDTH);
			camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, CAMERA_HEIGHT);
			// Set exposure value if specified
			if (expositionSet) {
				camera.set(Videoio.CAP_PROP_EXPOSURE, EXPOSITION_VALUE);
			}

			Mat frame = new Mat();
			if (camera.read(frame)) {
				Imgcodecs.imwrite(filename, frame);
				Thread.sleep(100);
			} else {
				System.out.println("Capture attempt failed, retrying...");
				try {
					Thread.sleep(500); 
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Thread interrupted during capture retry", e);
				}
			}

			camera.release();
			System.out.println("Image capture completed successfully");
			return frame;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error during image capture", e);
			return null;
		}
	}

	/**
	 * Locates the camera device by its USB ID.
	 * Maps the USB device ID to a camera index.
	 *
	 * @param usbDeviceID USB device identifier to search for
	 * @return Camera index if found, -1 if not found
	 */
	private static int findCameraByUSBID(String usbDeviceID) {
        if (usbDeviceID.equals(USB_DEVICE_ID)) {
            return 1; 
        }
        return 0; 
	}
	
	/**
	 * Sets the exposure configuration state.
	 * Controls whether exposure settings should be applied to the camera.
	 *
	 * @param expositionSet True to enable exposure settings, false to disable
	 */
	public static void setExpositionSet(boolean expositionSet) {
		WebcamCapture.expositionSet = expositionSet;
	}
	
}