/**
 * Abstract class Robot represents a base for all types of robots in the simulation.
 * It extends the Objects class and implements Serializable for saving/loading.
 * Specific robot types will inherit from this class.
 */
package robot_simulator;

import java.io.Serializable;

/**
 * Abstract class for all robots that different types of robots will inherit.
 */
public abstract class Robot extends Objects implements Serializable {
    private static final long serialVersionUID = 1L; // Serialization ID

    static int RobotCounter = 0; // Counter for the number of robots, also used as a unique ID
    protected Direction direction; // The direction the robot is heading
    protected double RobotSpeed; // The speed at which the robot moves

    /**
     * Constructs a robot at a specified position and size with a given speed.
     *
     * @param x          The x-coordinate (width position) of the robot.
     * @param y          The y-coordinate (height position) of the robot.
     * @param rad        The size (radius) of the robot.
     * @param RobotSpeed The speed of the robot.
     */
    public Robot(double x, double y, double rad, double RobotSpeed) {
        super(x, y, rad); // Call the parent class constructor
        ID = RobotCounter++; // Assign a unique ID based on the counter
        direction = Direction.randomDirection(); // Assign a random initial direction
        this.RobotSpeed = RobotSpeed;
    }

    /**
     * Resets the RobotCounter to zero.
     * This is used to start fresh counting for robot IDs.
     */
    protected static void resetRobotCounter() {
        Robot.RobotCounter = 0;
    }

    /**
     * Checks if the robot at its current position is colliding with another object.
     *
     * @param x   The x-coordinate of the object to check against.
     * @param y   The y-coordinate of the object to check against.
     * @param rad The radius of the object to check against.
     * @return True if the robot is colliding, false otherwise.
     */
    public boolean hitting(double x, double y, double rad) {
        // Add a buffer (200) to prevent overlap before physical contact
        return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) < (rad + this.rad) * (rad + this.rad) + 200;
    }

    /**
     * Returns the direction the robot is currently heading.
     *
     * @return The direction of the robot.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns the speed of the robot.
     *
     * @return The robot's speed.
     */
    public double getRobotSpeed() {
        return RobotSpeed;
    }

    /**
     * Adjusts the robot's direction based on angle and obstructions in the arena.
     *
     * @param myArena The arena where the robot is moving.
     */
    protected void checkRobot(Arena myArena) {
        direction = myArena.CheckRobotAngle(x, y, rad, direction, ID);
    }

    /**
     * Calculates and returns the angle of the robot's direction in radians.
     *
     * @return The angle in radians.
     */
    public double getRadAngle() {
        return direction.getAngle() * Math.PI / 180;
    }

    /**
     * Decrements the RobotCounter by 1.
     * This is used to update the count when a robot is removed.
     */
    public static void delete_robot() {
        RobotCounter = RobotCounter - 1;
    }

    /**
     * Reverses the robot's direction to its opposite.
     * This is typically used when the robot hits an obstacle.
     */
    protected void hit_obstacle() {
        direction = direction.getOpposite();
    }

    /**
     * Moves the robot based on its speed and direction.
     * Updates the x and y coordinates of the robot accordingly.
     */
    protected void adjustRobot() {
        double radAngle = direction.getAngle() * Math.PI / 180; // Convert the angle to radians
        x += RobotSpeed * Math.cos(radAngle); // Update the x-coordinate
        y += RobotSpeed * Math.sin(radAngle); // Update the y-coordinate
    }
}
