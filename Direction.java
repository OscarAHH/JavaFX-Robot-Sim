/**
 * Enum Direction represents the eight cardinal and intercardinal directions.
 * Provides utilities to calculate angles, choose random directions, and determine opposite directions.
 */
package robot_simulator;

import java.util.Random;

/**
 * Enum for setting direction.
 */
public enum Direction {
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST,
    NORTH,
    NORTHEAST;

    private static Random random = new Random(); // Random instance for generating random directions

    /**
     * Returns a random direction.
     *
     * @return A random direction from the enum values.
     */
    public static Direction randomDirection() {
        return values()[random.nextInt(values().length)]; // Use the Random class to get a random direction
    }

    /**
     * Calculates the angle in degrees for the direction.
     * Each direction's angle is its ordinal value multiplied by 45.
     *
     * @return The angle in degrees.
     */
    public int getAngle() {
        return ordinal() * 45;
    }

    /**
     * Converts the angle of the direction to radians.
     *
     * @return The angle in radians.
     */
    public double toRadians() {
        return Math.toRadians(getAngle());
    }

    /**
     * Returns a random eastward direction (NORTHEAST, EAST, or SOUTHEAST).
     *
     * @return A random eastward direction.
     */
    public Direction goEast() {
        int num = random.nextInt(3); // Generate 0, 1, or 2
        if (num == 0) {
            return NORTHEAST; // If 0, return NORTHEAST
        } else if (num == 1) {
            return EAST; // If 1, return EAST
        } else {
            return SOUTHEAST; // If 2, return SOUTHEAST
        }
    }

    /**
     * Returns a random southward direction (SOUTHEAST, SOUTH, or SOUTHWEST).
     *
     * @return A random southward direction.
     */
    public Direction goSouth() {
        return values()[random.nextInt(3) + 1]; // Generate a value between 1 and 3
    }

    /**
     * Returns a random westward direction (SOUTHWEST, WEST, or NORTHWEST).
     *
     * @return A random westward direction.
     */
    public Direction goWest() {
        return values()[random.nextInt(3) + 3]; // Generate a value between 3 and 5
    }

    /**
     * Returns a random northward direction (NORTHWEST, NORTH, or NORTHEAST).
     *
     * @return A random northward direction.
     */
    public Direction goNorth() {
        return values()[random.nextInt(3) + 5]; // Generate a value between 5 and 7
    }

    /**
     * Returns the opposite direction of the current one.
     * Opposites are calculated by shifting 4 positions in the enum values.
     *
     * @return The opposite direction.
     */
    public Direction getOpposite() {
        if (ordinal() >= 4) {
            return values()[ordinal() - 4]; // If in the second half, subtract 4
        }
        return values()[ordinal() + 4]; // If in the first half, add 4
    }

    /**
     * Adds an offset to the current direction and wraps around if necessary.
     *
     * @param num The offset to add to the direction.
     * @return The resulting direction after adding the offset.
     */
    public Direction add(int num) {
        if (ordinal() + num > 7) {
            return values()[ordinal() + num - 8]; // Wrap around if exceeding upper bound
        } else if (ordinal() + num < 0) {
            return values()[8 + ordinal() + num]; // Wrap around if below lower bound
        }
        return values()[ordinal() + num]; // Return the direction with the added value
    }
}
