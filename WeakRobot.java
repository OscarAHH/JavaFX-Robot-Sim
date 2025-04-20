/**
 * WeakRobot class represents a type of robot in the simulation that is vulnerable to obstacles.
 * Inherits properties and methods from the Robot class and implements Serializable for saving/loading.
 */
package robot_simulator;

import java.io.Serializable;

/**
 * Class for the WeakRobot - inherits from the Robot class.
 */
public class WeakRobot extends Robot implements Serializable {
    private static final long serialVersionUID = 1L; // Serialization ID
    private static int weakRobotCounter = 0; // Tracks the number of WeakRobot instances
    private boolean hitObstacle = false; // Indicates whether the robot has hit an obstacle

    /**
     * Constructs a WeakRobot at a specified position, size, and speed.
     *
     * @param x          The x-coordinate of the robot's position.
     * @param y          The y-coordinate of the robot's position.
     * @param rad        The size (radius) of the robot.
     * @param robotSpeed The speed of the robot.
     */
    public WeakRobot(double x, double y, double rad, double robotSpeed) {
        super(x, y, rad, robotSpeed); // Call the constructor of the parent Robot class
        weakRobotCounter++; // Increment the counter for WeakRobot instances
    }

    /**
     * Resets the counter for WeakRobot instances to zero.
     */
    public static void resetRobotCounter() {
        weakRobotCounter = 0; // Reset the WeakRobot counter
    }

    /**
     * Returns the current count of WeakRobot instances.
     *
     * @return The number of WeakRobot instances.
     */
    public static int getWeakRobotCount() {
        return weakRobotCounter;
    }

    /**
     * Checks if the robot has hit an obstacle.
     *
     * @return True if the robot is hitting an obstacle, otherwise false.
     */
    public boolean isHitObstacle() {
        return hitObstacle;
    }

    /**
     * Sets the hitObstacle status for the robot.
     *
     * @param hitObstacle True if the robot is hitting an obstacle, otherwise false.
     */
    public void setHitObstacle(boolean hitObstacle) {
        this.hitObstacle = hitObstacle;
    }

    /**
     * Returns the type of the robot as a string.
     *
     * @return A string indicating the type of the robot ("Weak Robot").
     */
    @Override
    public String getType() {
        return "Weak Robot";
    }
}
