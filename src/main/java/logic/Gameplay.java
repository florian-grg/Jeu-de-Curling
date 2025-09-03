package logic;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Main game logic controller that manages the game state and rules.
 * Handles turn management, token placement, scoring, and game progression
 * through multiple rounds.
 */
public class Gameplay {
	private static ImageHandler handler;
	private static int NB_TURNS = 8;
	private static int MAX_ROUNDS = 2;

	private int[] targetPosition;
	private Token[] tokens;
	private int currentPlayer;
	private int currentTurn;
	private int currentRound;
	private int playerAdvantage;
	private int[] playerScoreRound;
	private boolean gameFinished;
	private static Gameplay instance;

	/**
	 * Initializes a new game session with default settings.
	 * Creates tokens for both players and sets up initial game state.
	 */
	public Gameplay() {
		if (handler == null) {
			handler = new ImageHandler();
		}
		instance = this;
		tokens = new Token[NB_TURNS];
		for (int i = 0; i < NB_TURNS; i++) {
			if (i % 2 == 0)
				tokens[i] = new Token(new int[] { 0, 0 }, 0, 0);
			else
				tokens[i] = new Token(new int[] { 0, 0 }, 1, 0);
		}
		currentPlayer = 1;
		currentTurn = 0;
		currentRound = 1;
		playerAdvantage = 0;
		setPlayerScoreRound(new int[] { 0, 0 });
	}

	/**
	 * Processes the end of a player's turn.
	 * Updates current player, turn counter, and calculates player advantage.
	 */
	public void endTurn() {
		setPlayerAdvantage(getAdvantage());
		this.setCurrentTurn(this.getCurrentTurn() + 1);
		this.setCurrentPlayer(this.getCurrentPlayer() + 1);

		// Check if we reached the configured number of turns
		if (currentTurn >= NB_TURNS) {
			endRound();
		}
		
	}

	/**
	 * Gets the current player advantage based on token positions.
	 * Determines which player has a token closest to the target.
	 * 
	 * @return 0 for player 1's advantage, 1 for player 2's advantage, -1 if no
	 *         valid tokens
	 */
	public int getAdvantage() {
		int closestTokenIndex = -1;
		double minDistance = Double.MAX_VALUE; // Using Double.MAX_VALUE instead of magic number

		// Find the token closest to target
		if (currentTurn == 0) {
			return 0;
		}
		for (int i = 0; i < currentTurn+1; i++) {
			if (tokens[i] != null && tokens[i].getPosition()[0] != 0) { // Check for valid token
				double distance = tokens[i].getDistanceToTarget();
				if (distance >= 0 && distance < minDistance) { // Check for valid distance
					minDistance = distance;
					closestTokenIndex = i;
				}
			}
		}

		// Log the closest distance found
		if (closestTokenIndex != -1) {
			System.out.println("Closest token distance: " + minDistance);
			return closestTokenIndex % 2; // Return player number (0 or 1)
		}

		return -1; // Return -1 if no valid tokens found
	}

	/**
	 * Handles the end of a game round.
	 * Resets tokens and updates scores if game hasn't finished.
	 */
	public void endRound() {
		System.out.println("Starting new round");
		incrementPlayerScore();
		if (currentRound == MAX_ROUNDS) {
			endOfTheGame();
		} else {
			// Create new token array with current NB_TURNS size
			tokens = new Token[NB_TURNS];
			for (int i = 0; i < NB_TURNS; i++) {
				if (i % 2 == 0)
					tokens[i] = new Token(new int[] { 0, 0 }, 0, 0);
				else
					tokens[i] = new Token(new int[] { 0, 0 }, 1, 0);
			}
			incrementRound();
		}
	}

	/**
	 * Detects token position from the current camera image.
	 *
	 * @return int[] Array containing token coordinates, or null if not found
	 */
	public int[] checkToken() {
		Mat image = Imgcodecs.imread("./image.jpg");
		int[] tokenPosition = handler.getTokenPosition(image);

		return tokenPosition;
	}

	/**
	 * Displays the current positions of all tokens in the game.
	 * Shows coordinates for each player's tokens in console output.
	 */
	public void displayTokens() {
		for (int k = 0; k < tokens.length; k += 2) {
			System.out.println("Tour " + (k + 1) + " (J" + (tokens[k].getPlayer() + 1) + ") : "
					+ tokens[k].getPosition()[0] + ", " + tokens[k].getPosition()[1]);
			System.out.println("Tour " + (k + 2) + " (J" + (tokens[k + 1].getPlayer() + 1) + ") : "
					+ tokens[k + 1].getPosition()[0] + ", " + tokens[k + 1].getPosition()[1]);
		}
	}

	/**
	 * Processes token detection and placement on the game board.
	 * Handles collision detection with existing tokens.
	 *
	 * @param image Mat object containing the current camera frame
	 */
	public void findToken(Mat image) {
		int[] tokenPosition = handler.getTokenPosition(image);
		if (tokenPosition != null) {
			System.out.println("Token found!");
			tokens[currentTurn] = new Token(tokenPosition, currentPlayer, handler.getDistanceToTarget());
		} else {
			System.out.println("Token not found...");
			return;
		}
		tokenPosition = null;

		checkColision(currentTurn);
		displayTokens(); // Display token positions
	}

	/**
	 * Checks for collisions between the current token and previously placed tokens.
	 * Removes colliding tokens from the board.
	 *
	 * @param turn Current turn number to check against previous turns
	 */
	public void checkColision(int turn) {
		for (int i = 0; i < turn; i++) {

			double distance = tokens[turn].getDistanceToToken(tokens[i]);
			boolean colision = distance < 0.8 * Token.getRadius();

			if (colision) {
				tokens[i].getPosition()[0] = 0;
				tokens[i].getPosition()[1] = 0;
			}
		}
	}

	/**
	 * Marks the game as finished.
	 * Sets the gameFinished flag to true.
	 */
	public void endOfTheGame() {
		setGameFinished(true);
	}

	/**
	 * Detects and sets the target position from camera image.
	 * Updates the game state with target location.
	 *
	 * @param image Mat object containing the current camera frame
	 */
	public void findTarget(Mat image) {
		if (targetPosition == null) {
			if (ImageHandler.getTargetStyle() == 2) {
				targetPosition = new int[] { 640, 360 };
			} else {
				System.out.println("Waiting for target...");
				targetPosition = ImageHandler.getTargetPosition(image);
				if (targetPosition == null) {
					System.out.println("Target not found...");
					return;
				} else {
					ImageHandler.setTargetPosition(targetPosition);
					System.out.println("Target found!");
				}
			}
		}
		System.out.println("Target: " + targetPosition[0] + ", " + targetPosition[1]);
	}

	/**
	 * Increments the score for the player who currently has the advantage.
	 */
	public void incrementPlayerScore() {
		this.playerScoreRound[getPlayerAdvantage()] += 1;
	}

	/**
	 * Gets the image handler instance.
	 * Getter for accessing image processing functionalities.
	 *
	 * @return The ImageHandler instance used by this game
	 */
	public ImageHandler getHandler() {
		return handler;
	}

	/**
	 * Sets the image handler instance.
	 * Setter for updating the image processing component.
	 *
	 * @param handler The new ImageHandler instance to use
	 */
	public void setHandler(ImageHandler handler) {
		Gameplay.handler = handler;
	}

	/**
	 * Gets the array of all game tokens.
	 * Getter for accessing all tokens in the current game.
	 *
	 * @return Array of Token objects
	 */
	public Token[] getTokens() {
		return tokens;
	}

	/**
	 * Sets the array of game tokens.
	 * Setter for updating all tokens at once.
	 *
	 * @param tokens New array of Token objects
	 */
	public void setTokens(Token[] tokens) {
		this.tokens = tokens;
	}

	/**
	 * Gets the current player number.
	 * Getter for determining whose turn it is.
	 *
	 * @return Current player number (0 or 1)
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Sets the current player, ensuring value cycles between 0 and 1.
	 *
	 * @param currentPlayer New player number
	 */
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer % 2;
	}

	/**
	 * Gets the number of turns in the game.
	 * Static getter for accessing turn configuration.
	 *
	 * @return Current number of turns configured
	 */
	public static int getNbTurns() {
		return NB_TURNS;
	}

	/**
	 * Sets the number of turns for the game.
	 *
	 * @param nbTurns New number of turns
	 */
	public void setNbTurns(int nbTurns) {
		Gameplay.NB_TURNS = nbTurns;
	}

	/**
	 * Gets the current turn number.
	 *
	 * @return Current turn number
	 */
	public int getCurrentTurn() {
		return currentTurn;
	}

	/**
	 * Sets the current turn number.
	 *
	 * @param currentTurn New turn number
	 */
	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

	/**
	 * Gets the target position coordinates.
	 *
	 * @return Array containing target position [x,y]
	 */
	public int[] getTargetPosition() {
		return targetPosition;
	}

	/**
	 * Sets the target position coordinates.
	 *
	 * @param targetPosition New target position [x,y]
	 */
	public void setTargetPosition(int[] targetPosition) {
		this.targetPosition = targetPosition;
	}

	/**
	 * Static utility methods for turn and round management
	 */
	/**
	 * Increments the number of turns by 2 and resizes the tokens array accordingly.
	 * Creates new tokens for both players in the added turns.
	 */
	public static void incrementNbTurns() {
		NB_TURNS += 2;
		if (instance != null && instance.tokens != null) {
			Token[] newTokens = new Token[NB_TURNS];
			System.arraycopy(instance.tokens, 0, newTokens, 0, instance.tokens.length);
			for (int i = instance.tokens.length; i < NB_TURNS; i++) {
				if (i % 2 == 0)
					newTokens[i] = new Token(new int[] { 0, 0 }, 0, 0);
				else
					newTokens[i] = new Token(new int[] { 0, 0 }, 1, 0);
			}
			instance.tokens = newTokens;
		}
	}

	/**
	 * Decrements the number of turns by 2 if possible (minimum 4 turns).
	 * Resizes the tokens array to match the new number of turns.
	 */
	public static void decrementNbTurns() {
		if (NB_TURNS > 4) {
			NB_TURNS -= 2;
			if (instance != null && instance.tokens != null) {
				Token[] newTokens = new Token[NB_TURNS];
				System.arraycopy(instance.tokens, 0, newTokens, 0, NB_TURNS);
				instance.tokens = newTokens;
			}
		}
	}

	/**
	 * Increments the maximum number of rounds by 1.
	 */
	public static void incrementMaxRounds() {
		MAX_ROUNDS++;
	}

	/**
	 * Decrements the maximum number of rounds by 1 if possible (minimum 1 round).
	 */
	public static void decrementMaxRounds() {
		if (MAX_ROUNDS > 1) {
			MAX_ROUNDS--;
		}
	}

	public static int getMaxRounds() {
		return MAX_ROUNDS;
	}

	/**
	 * Increments the current round and resets turn/player states.
	 * Ends the game if maximum rounds are reached.
	 */
	public void incrementRound() {
		this.currentTurn = 0;
		this.currentPlayer = 1;
		if (currentRound == MAX_ROUNDS) {
			endOfTheGame();
		} else {
			currentRound += 1;
			currentPlayer = 1;
			currentTurn = 0;
		}
	}

	/**
	 * Gets the current round number.
	 *
	 * @return Current round number
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * Sets the current round number.
	 *
	 * @param currentRound New round number
	 */
	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	/**
	 * Gets the maximum number of rounds configured.
	 *
	 * @return Maximum number of rounds
	 */
	public int getMaxRound() {
		return MAX_ROUNDS;
	}

	/**
	 * Sets the maximum number of rounds.
	 *
	 * @param maxRound New maximum number of rounds
	 */
	public void setMaxRound(int maxRound) {
		Gameplay.MAX_ROUNDS = maxRound;
	}

	/**
	 * Gets the current score for both players.
	 *
	 * @return Array containing player scores [player1Score, player2Score]
	 */
	public int[] getPlayerScoreRound() {
		return playerScoreRound;
	}

	/**
	 * Sets the scores for both players.
	 *
	 * @param playerScoreRound Array containing new scores [player1Score,
	 *                         player2Score]
	 */
	public void setPlayerScoreRound(int[] playerScoreRound) {
		this.playerScoreRound = playerScoreRound;
	}

	/**
	 * Gets the player who currently has the advantage.
	 *
	 * @return Player number with advantage (0 or 1)
	 */
	public int getPlayerAdvantage() {
		return playerAdvantage;
	}

	/**
	 * Sets which player has the advantage.
	 *
	 * @param playerAdvantage Player number to receive advantage (0 or 1)
	 */
	public void setPlayerAdvantage(int playerAdvantage) {
		this.playerAdvantage = playerAdvantage;
	}

	/**
	 * Checks if the game has finished.
	 *
	 * @return true if game is finished, false otherwise
	 */
	public boolean isGameFinished() {
		return gameFinished;
	}

	/**
	 * Sets the game's finished state.
	 *
	 * @param gameFinished true to mark game as finished, false otherwise
	 */
	public void setGameFinished(boolean gameFinished) {
		this.gameFinished = gameFinished;
	}
}
