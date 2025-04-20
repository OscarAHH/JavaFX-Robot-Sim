/**
 * BeamRobot class represents a type of robot in the simulation that emits a beam.
 * Inherits properties and methods from the Robot class and implements Serializable for saving/loading.
 */
package robot_simulator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Class for the BeamRobot - inherits from the Robot class.
 */
public class BeamRobot extends Robot implements Serializable {
    private static final long serialVersionUID = 1L; // Serialization ID

    private boolean hitObstacle = false; // Indicates whether the robot is hitting an obstacle
    private double beamX; // X-coordinate of the beam's endpoint
    private double beamY; // Y-coordinate of the beam's endpoint
    private double beam_angle; // Angle in radians for the beam direction

    /**
     * Constructs a BeamRobot at a specified position, size, and speed.
     *
     * @param x          The x-coordinate of the robot's position.
     * @param y          The y-coordinate of the robot's position.
     * @param rad        The size (radius) of the robot.
     * @param robotSpeed The speed of the robot.
     */
    public BeamRobot(double x, double y, double rad, double robotSpeed) {
        super(x, y, rad, robotSpeed); // Call the constructor of the parent Robot class
        this.beam_angle = Math.toRadians(direction.getAngle()); // Calculate the initial beam angle in radians
    }

    /**
     * Updates the beam's endpoint coordinates.
     *
     * @param beam_x The new beam X-coordinate.
     * @param beam_y The new beam Y-coordinate.
     */
    public void set_beam(double beam_x, double beam_y) {
        this.beamX = beam_x;
        this.beamY = beam_y;
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
     * Sets the hitObstacle status for the robot.
     *
     * @param hitObstacle True if the robot is hitting an obstacle, otherwise false.
     */
    public void setHitObstacle(boolean hitObstacle) {
        this.hitObstacle = hitObstacle;
    }

    /**
     * Adjusts the movement of the BeamRobot, updating its position and beam visuals.
     *
     * @param myArena The arena in which the robot moves.
     * @param objects The list of all objects in the arena.
     */
    public void adjustRobot(Arena myArena, List<Objects> objects) {
        // Move the BeamRobot
        x += RobotSpeed * Math.cos(beam_angle); // Update X position
        y += RobotSpeed * Math.sin(beam_angle); // Update Y position

        // Bounce off walls
        if (x < rad || x > myArena.getX() - rad) {
            beam_angle = Math.PI - beam_angle; // Reflect horizontally
        }
        if (y < rad + 65 || y > myArena.getY() - rad) {
            beam_angle = -beam_angle; // Reflect vertically
        }

        // Update the beam visuals
        double beamEndX = x + rad * 2 * Math.cos(beam_angle);
        double beamEndY = y + rad * 2 * Math.sin(beam_angle);
        set_beam(beamEndX, beamEndY);
        myArena.showBeam(x, y, rad, beam_angle);
    }

    /**
     * Checks the position of the BeamRobot and handles collisions with walls or other robots.
     *
     * @param myArena The arena in which the robot moves.
     * @param objects The list of all objects in the arena.
     * @param notID   The ID of the current robot (used to ignore itself).
     */
    public void check_BeamRobot(Arena myArena, List<Objects> objects, int notID) {
        // Check if the beam hits a wall
        if (beamX < rad || beamX > myArena.getX() - rad || beamY < rad + 65 || beamY > myArena.getY() - rad) {
            beam_angle += 0.2; // Slightly adjust the angle to prevent sticking to walls
        }

        // Check collisions with other robots
        Iterator<Objects> iterator = objects.iterator();
        while (iterator.hasNext()) {
            Objects object = iterator.next();
            if (object instanceof Robot) {
                // Skip the current robot
                if (object.getObjectID() != notID && ((Robot) object).hitting(x, y, rad)) {
                    // Handle collision with another robot
                    if ("Weak Robot".equals(object.getType())) {
                        iterator.remove(); // Safely remove the weak robot
                        Robot.delete_robot(); // Decrement the robot counter
                    } else {
                        beam_angle += 0.2; // Adjust the angle for collision
                    }
                }
            }
        }
    }

    /**
     * Returns the type of the robot as a string.
     *
     * @return A string indicating the type of the robot ("Beam Robot").
     */
    @Override
    public String getType() {
        return "Beam Robot";
    }
}
