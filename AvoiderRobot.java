/**
 * AvoiderRobot class represents a type of robot in the simulation designed to avoid obstacles.
 * Inherits properties and methods from the Robot class and implements Serializable for saving/loading.
 */
package robot_simulator;

import java.io.Serializable;

/**
 * Class for the AvoiderRobot - inherits from the Robot class.
 */
public class AvoiderRobot extends Robot implements Serializable {
    private static final long serialVersionUID = 1L; // Serialization ID

    private boolean hitObstacle = false; // Indicates whether the robot is hitting an obstacle

    /**
     * Constructs an AvoiderRobot at a specified position, size, and speed.
     *
     * @param x          The x-coordinate of the robot's position.
     * @param y          The y-coordinate of the robot's position.
     * @param rad        The size (radius) of the robot.
     * @param robotSpeed The speed of the robot.
     */
    public AvoiderRobot(double x, double y, double rad, double robotSpeed) {
        super(x, y, rad, robotSpeed); // Call the constructor of the parent Robot class
    }

    /**
     * Checks if the robot is hitting an obstacle.
     *
     * @return True if the robot is hitting an obstacle, otherwise false.
     */
    public boolean isHitObstacle() {
        return hitObstacle;
    }

    /**
     * Adjusts the robot's direction to avoid an obstacle.
     *
     * @param obstacle The obstacle to avoid.
     */
    public void avoid(Obstacle obstacle) {
        if (direction.ordinal() % 2 != 0) { // If the robot is moving diagonally, adjust to move straight
            direction = direction.add(1);
            return; // Exit to allow further adjustment
        }

        int next = 0; // Determine the direction adjustment
        if (!hitObstacle) {
            if (direction.ordinal() == 2 || direction.ordinal() == 6) { // Robot hits from top or bottom
                if ((x < obstacle.getX() && y > obstacle.getY()) || (x > obstacle.getX() && y < obstacle.getY())) {
                    next = -2;
                } else {
                    next = 2;
                }
            } else { // Robot hits from left or right
                if ((x < obstacle.getX() && y < obstacle.getY()) || (x > obstacle.getX() && y > obstacle.getY())) {
                    next = -2;
                } else {
                    next = 2;
                }
            }
        }

        int finalNext = next;
        new Thread(() -> {
            setHitObstacle(true); // Mark that the robot is avoiding an obstacle
            direction = direction.add(finalNext); // Adjust the direction

            while (hitting(obstacle.getX(), obstacle.getY(), obstacle.getRad())) {
                adjustRobot(); // Continue moving until the robot is no longer hitting the obstacle
            }

            try {
                Thread.sleep(50); // Pause for a short duration to simulate natural movement
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            direction = direction.add(-finalNext); // Restore the original direction
            setHitObstacle(false); // Reset the hitObstacle flag
        }).start();
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
     * @return A string indicating the type of the robot ("Avoider Robot").
     */
    @Override
    public String getType() {
        return "Avoider Robot";
    }
}
