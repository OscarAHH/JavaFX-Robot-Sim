/**
 * NormalRobot class represents a standard type of robot in the simulation.
 * Inherits properties and methods from the Robot class and implements Serializable for saving/loading.
 */
package robot_simulator;

import java.io.Serializable;

/**
 * Class for the NormalRobot - inherits from the Robot class.
 */
public class NormalRobot extends Robot implements Serializable {
    private static final long serialVersionUID = 1L; // Serialization ID

    private boolean hitObstacle = false; // Indicates whether the robot has hit an obstacle

    /**
     * Constructs a NormalRobot at a specified position, size, and speed.
     *
     * @param x          The x-coordinate of the robot's position.
     * @param y          The y-coordinate of the robot's position.
     * @param rad        The size (radius) of the robot.
     * @param robotSpeed The speed of the robot.
     */
    public NormalRobot(double x, double y, double rad, double robotSpeed) {
        super(x, y, rad, robotSpeed); // Call the constructor of the parent Robot class
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
     * @return A string indicating the type of the robot ("Regular Robot").
     */
    @Override
    public String getType() {
        return "Regular Robot";
    }
}
