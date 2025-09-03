package logic;

/**
 * Represents a game token with position and player information.
 * Handles token positioning, player assignment, and distance calculations.
 */
public class Token {
    private int[] position;
    private int player;
    private static int radius = 30;
    private double distanceToTarget;

    /**
     * Creates a new token with specified position, player, and target distance.
     *
     * @param position Position coordinates of the token [x,y]
     * @param player Player number who owns this token
     * @param distanceToTarget Distance from this token to the target
     */
    public Token(int[] position, int player, double distanceToTarget) {
        super();
        this.position = position;
        this.player = player;
        this.distanceToTarget = distanceToTarget;
    }

    /**
     * Sets the radius for all tokens.
     *
     * @param radius The new radius value for tokens
     */
    public static void setRadius(int radius) {
        Token.radius = radius;
    }

    /**
     * Returns the position of the token.
     *
     * @return Position coordinates of the token [x,y]
     */
    public int[] getPosition() {
        return position;
    }

    /**
     * Returns the player number who owns this token.
     *
     * @return Player number
     */
    public int getPlayer() {
        return player;
    }

    /**
     * Returns the radius of the tokens.
     *
     * @return Radius of the tokens
     */
    public static int getRadius() {
        return radius;
    }

    /**
     * Returns the distance from this token to the target.
     *
     * @return Distance to the target
     */
    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    /**
     * Calculates the Euclidean distance between this token and another token.
     *
     * @param otherToken The token to calculate distance to
     * @return The distance between the two tokens
     */
    public double getDistanceToToken(Token otherToken) {
        int[] otherPosition = otherToken.getPosition();
        double distance = Math
                .sqrt(Math.pow(position[0] - otherPosition[0], 2) + Math.pow(position[1] - otherPosition[1], 2));
        return distance;
    }

    /**
     * Updates the distance from this token to the target.
     *
     * @param distanceToTarget New distance value to the target
     */
    public void setDistanceToTarget(double distanceToTarget) {
        this.distanceToTarget = distanceToTarget;
    }
}
