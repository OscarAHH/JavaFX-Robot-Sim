package robot_simulator;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Arena implements Serializable {
    private static final long serialVersionUID = 1L;
    protected double x, y; // Width and height of the arena
    private List<Objects> objects; // List for all objects in the arena
    private Objects selectedObject = null; // Track the currently selected object

    private transient List<Circle> circles; // JavaFX objects (not serializable)
    private transient List<Line> wheels;
    private transient List<Line> beams;
    public int circle_count, line_count, beam_count = 0;
    protected Direction direction; // Direction the robot is heading
    public transient Canvas canvas;
    private transient Group arenaGroup; // JavaFX visuals (not serializable)
    private boolean simulationRunning = false; // Tracks if the simulation is running
    private transient Scene scene; // Scene is transient and initialized later

    /**
     * Constructor - setting up the arena
     * 
     * @param x (width)
     * @param y (height)
     * @param arenaGroup (Group for JavaFX visualization)
     */
    public Arena(double x, double y, Group arenaGroup) {
        if (arenaGroup == null) {
            throw new IllegalArgumentException("arenaGroup cannot be null");
        }

        this.x = x;
        this.y = y;
        this.circles = new ArrayList<>();
        this.wheels = new ArrayList<>();
        this.beams = new ArrayList<>();
        this.direction = Direction.randomDirection();
        this.canvas = new Canvas();
        this.objects = new ArrayList<>();
        this.arenaGroup = arenaGroup;
        this.scene = new Scene(arenaGroup, x, y);
    }

    // Setter for transient arenaGroup
    public void setArenaGroup(Group arenaGroup) {
        this.arenaGroup = arenaGroup;
    }

    // Setter for transient scene
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Scene getScene() {
        return this.scene;
    }

    public List<Objects> getObjects() {
        return objects;
    }

    public Group getArenaGroup() {
        return this.arenaGroup;
    }

    public void setSimulationRunning(boolean running) {
        this.simulationRunning = running;
    }

    public boolean isSimulationRunning() {
        return this.simulationRunning;
    }

    public void drawArena() {
        if (arenaGroup == null) {
            throw new IllegalStateException("arenaGroup is not initialized.");
        }

        arenaGroup.getChildren().clear();

        for (Objects object : objects) {
            if (object instanceof BeamRobot) {
                double angle = ((BeamRobot) object).getRadAngle();
                showBeam(object.getX(), object.getY(), object.getRad(), angle);
            } else if (object instanceof Robot) {
                double angle = ((Robot) object).getRadAngle();
                String color = object.getColor();
                showCircle(object.getX(), object.getY(), object.getRad(), color, angle);
            } else if (object instanceof Obstacle) {
                if (object.getRad() == 0) {
                    showObstacle_line(object.getX(), object.getY(), object.getX() + 40, object.getY() + 35, 0);
                } else {
                    showObstacle_circle(object.getX(), object.getY());
                }
            }
        }
    }





    /**
     * Moves the selected object (robot or obstacle) to a new position.
     *
     * @param newX The new x-coordinate.
     * @param newY The new y-coordinate.
     */
    public void moveSelectedObject(double newX, double newY) {
        if (selectedObject != null) {
            selectedObject.setXY(newX, newY); // Update the object's position
            System.out.println("Moved " + selectedObject.getType() + " with ID: " + selectedObject.getObjectID() +
                               " to: (" + newX + ", " + newY + ")");
            drawArena(); // Redraw the arena to reflect the changes
            selectedObject = null; // Deselect the object after moving
        } else {
            System.out.println("No object selected to move.");
        }
    }


    // Getter for selectedObject
    public Objects getSelectedObject() {
        return this.selectedObject;
    }

    // Setter for selectedObject (optional, for setting it externally)
    public void setSelectedObject(Objects selectedObject) {
        this.selectedObject = selectedObject;
    }

  

 // Fix in handleSelection()
    public void handleSelection(double mouseX, double mouseY, boolean simulationRunning) {
        if (simulationRunning) {
            System.out.println("Cannot move robots or obstacles while simulation is running.");
            return;
        }

        for (Objects obj : objects) {
            double distance = Math.sqrt(Math.pow(mouseX - obj.getX(), 2) + Math.pow(mouseY - obj.getY(), 2));
            if (distance <= obj.getRad()) {
                if (obj instanceof Robot) {
                    selectedObject = obj;
                    System.out.println("Selected Robot: " + obj.getType() + " with ID: " + obj.getObjectID());
                    drawArena(); // Fixed: Removed argument
                    return;
                } else if (obj instanceof Obstacle && obj.getRad() > 0) { // Circle obstacle
                    selectedObject = obj;
                    System.out.println("Selected Circle Obstacle with ID: " + obj.getObjectID());
                    drawArena(); // Fixed: Removed argument
                    return;
                }
            }
        }

        // No object selected
        selectedObject = null;
        System.out.println("No robot or obstacle selected.");
    }

    // Fix in deleteSelectedObject()
    public void deleteSelectedObject() {
        if (selectedObject != null) {
            System.out.println("Deleted: " + selectedObject.getType() + " with ID: " + selectedObject.getObjectID());

            // Remove the object from the objects list
            objects.remove(selectedObject);

            // Remove from the visual representation
            arenaGroup.getChildren().clear(); // Clear existing visuals
            drawArena(); // Fixed: Removed argument

            // Clear the selection
            selectedObject = null;
        } else {
            System.out.println("No object to delete.");
        }
    }

    // Fix in addRegular()
    public void addRegular() {
        Random random = new Random();
        double x = random.nextDouble(5, this.x - 75);
        double y = random.nextDouble(5, this.y - 20);
        NormalRobot robot = new NormalRobot(x, y, 10, 2);
        robot.setColor("BLACK"); // Regular robot color
        objects.add(robot);
        drawArena(); // Fixed: Removed argument
    }

    // Fix in addWeak()
    public void addWeak() {
        Random random = new Random();
        double x = random.nextDouble(5, this.x - 75);
        double y = random.nextDouble(5, this.y - 20);
        WeakRobot robot = new WeakRobot(x, y, 10, 3);
        robot.setColor("GREY"); // Weak robot color
        objects.add(robot);
        drawArena(); // Fixed: Removed argument
    }

    // Fix in addAvoid()
    public void addAvoid() {
        Random random = new Random();
        double x = random.nextDouble(5, this.x - 75);
        double y = random.nextDouble(5, this.y - 20);
        AvoiderRobot robot = new AvoiderRobot(x, y, 10, 3);
        robot.setColor("BLUE"); // Avoider robot color
        objects.add(robot);
        drawArena(); // Fixed: Removed argument
    }

    // Fix in addBeamRobot()
    public void addBeamRobot() {
        Random random = new Random();
        double x = random.nextInt((int) this.x - 100) + 50;
        double y = random.nextInt((int) this.y - 100) + 50;
        BeamRobot robot = new BeamRobot(x, y, 10, 0.75);
        robot.setColor("CORAL"); // Beam robot color
        objects.add(robot);
        drawArena(); // Fixed: Removed argument
    }

    // Fix in addObstacle()
    public void addObstacle(String type) {
        Random random = new Random();
        // Generate random coordinates within the map boundaries
        int x = random.nextInt((int) this.x - 100) + 50;
        int y = random.nextInt((int) this.y - 100) + 50;

        if (type.equals("line")) {
            int rotation = random.nextInt(0, 360); // Generate random rotation

            // Create and store a LineObstacle
            LineObstacle lineObstacle = new LineObstacle(x, y, x + 40, y + 35); // Example line length
            objects.add(lineObstacle);

            // Render the line obstacle with rotation
            showObstacle_line(x, y, x + 40, y + 35, rotation);
        } else if (type.equals("circle")) {
            // Add the circle obstacle to the list
            Obstacle circleObstacle = new Obstacle(x, y, 30); // Set appropriate radius for a circle
            objects.add(circleObstacle);

            // Render the circle obstacle directly
            showObstacle_circle(x, y);
        }
        drawArena(); // Redraw the arena
    }





    /**
     * Clears all the objects in the arena
     */
    public void clearArena() {
        objects.clear();             // Clear the list of objects
        arenaGroup.getChildren().clear(); // Clear all visuals in the arena
        circle_count = 0;            // Reset counters for circles
        line_count = 0;              // Reset counters for lines
        beam_count = 0;              // Reset counters for beams
        System.out.println("Arena cleared.");
    }




    /*
     * displays the beam robot
     * @param x width position
     * @param y height position
     * @param rad the size
     * @param angle of robot
     */
    public double[] showBeam(double x, double y, double rad, double angle) {
        double[] ends = new double[2];

        // Create and configure the circle
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(rad);
        circle.setRotate(angle);
        circle.setFill(Color.CORAL);

        circles.add(circle);  // Add to circles list


        // Create and configure the beam
        Line beam_line = new Line();
        beam_line.setStartX(x);
        beam_line.setStartY(y);
        beam_line.setEndX(x + rad + 5);
        beam_line.setEndY(y + rad + 5);
        beam_line.setStrokeWidth(5);

        beams.add(beam_line); // Add to beams list


        // Add visuals to arenaGroup
        arenaGroup.getChildren().addAll(circle, beam_line);

        return ends; // Return end coordinates
    }


  
    /**
     * checks all robots to see if it needs to change the angle of the robot
     */
    public void checkRobots() {
        // Temporary list to store objects to be removed
        List<Objects> toRemove = new ArrayList<>();

        // Use an iterator to safely iterate through the list
        for (Objects obj : new ArrayList<>(objects)) { // Iterate over a copy to avoid concurrent modification
            if (obj instanceof Robot) { // Only the robots need to change angles
                if (obj instanceof BeamRobot) {
                    // Check beam robot-specific logic
                    ((BeamRobot) obj).check_BeamRobot(this, objects, ((BeamRobot) obj).getObjectID());
                } else {
                    // Check other robots' position
                    ((Robot) obj).checkRobot(this);
                }

                // Handle weak robot collisions
                if (obj instanceof WeakRobot) {
                    WeakRobot weakRobot = (WeakRobot) obj;
                    if (weakRobot.isHitObstacle()) { // Example collision logic
                        toRemove.add(weakRobot); // Mark for removal
                    }
                }
            }
        }

        // Remove objects after iteration
        objects.removeAll(toRemove);

        // Reset the robot counter if needed
        WeakRobot.resetRobotCounter(); // Ensure Weak_robot count is reset
    }





    /**
     * adjust all the moving objects
     */
    public void adjustRobots() {
        for (Objects robot : objects) { // Loops through the list
            if (robot instanceof Robot) { // Only moves robots
                if (robot instanceof BeamRobot) {
                    ((BeamRobot) robot).adjustRobot(this, objects); // Pass arena and objects list
                } else {
                    ((Robot) robot).adjustRobot();
                }
            }
        }
    }

    

    /**
     * checks the angle the robot is at and if it hits any object, robot or wall, it bounces the opposite way
     * If the robot is a weak robot, it will destroy it
     * @param x (the x position of the robot)
     * @param y (the y position of the robot)
     * @param rad (the size of the robot)
     * @param direction (the direction that the robot is heading )
     * @param notID (the ID of the robot not to be checked)
     * @return returns the new direction
     */
    public Direction CheckRobotAngle(double x, double y, double rad, Direction direction, int notID){
        Direction answer = direction; // to stores the answer for the return value
        // check if the robot hits a wall
        if (x < rad){// check to see if the robot hit the left wall
            answer = direction.goEast(); // change direction to east
        }

        if(x > this.x - rad){ // check to see if the robot hit the right wall
            answer = direction.goWest(); // Change the direction to west
        }

        if (y < rad + 65 ){ // check to see if the robot hit the top wall
            answer = direction.goSouth(); // Change the direction to south
        }

        if(y > this.y - rad){// check to see if the robot hit the bottom wall
            answer = direction.goNorth();// Change the direction to north
        }
     // Create an iterator to safely remove elements from the list
        Iterator<Objects> iterator = objects.iterator();

        // Check if the robot hit another robot
        while (iterator.hasNext()) {
            Objects object = iterator.next();
            if (object instanceof Robot) {
                // Check all the robots except the one with the given ID
                if (object.getObjectID() != notID && ((Robot) object).hitting(x, y, rad)) {
                    // If it hits another robot, delete the Evil Robot, otherwise update the direction
                    if ("Weak Robot".equals(object.getType())) {
                        iterator.remove(); // Safely remove the Evil Robot
                        Robot.delete_robot(); // decrement robot counter
                    } else {
                        answer = direction.getOpposite();  // Change the direction to opposite of original
                    }
                }
            }
        }

        return answer; // Returns the new direction of the robot
    }



    /**
     * add a obstacle to the window using random coordinates
     * @param String type - type of obstacle
     */


    /**
     * counts the amount of objects in the list and returns the value
     * @return number of obstacles (int)
     */
    public int getNumOfRegularObstacles(){
        int counter = 0;
        for (Objects object : objects){
            // the objects needs to be Regular Obstacle but not a Bird object in order to count only the regular obstacles
            if (object instanceof Obstacle){
                counter++;
            }
        }
        return counter;
    }



    /**
     * Separates the list of objects into robots and obstacles
     * checks  whether the current robot is hitting the current object
     * here we use an iterator to safely remove the weak robot if it's hit
     */
    public void obstacleCollision() {
        // Split the Robots and the obstacles from the objects
        List<Robot> robots = new ArrayList<>();
        List<Obstacle> obstacles = new ArrayList<>();
        
        // Separate robots and obstacles
        for (Objects obj : objects) {
            if (obj instanceof Robot) {
                robots.add((Robot) obj);
            } else if (obj instanceof Obstacle) {
                obstacles.add((Obstacle) obj);
            }
        }

        // Iterate over robots and obstacles to check for collisions
        for (Robot robot : robots) {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.getRad() == 0) { // Line obstacle
                    // Define line endpoints based on the obstacle's position
                    double x1 = obstacle.getX();
                    double y1 = obstacle.getY();
                    double x2 = obstacle.getX() + 40; // Adjust length as needed
                    double y2 = obstacle.getY() + 35; // Adjust length as needed

                    // Check collision with the line obstacle
                    if (isRobotCollidingWithLine(robot.getX(), robot.getY(), robot.getRad(), x1, y1, x2, y2)) {
                        robot.hit_obstacle(); // Make the robot bounce
                    }
                } else { // Circle obstacle
                    // Check collision with a circular obstacle
                    if (robot.hitting(obstacle.getX(), obstacle.getY(), obstacle.getRad())) {
                        robot.hit_obstacle();
                    }
                }
            }
        }
    }



    /**
     * @param x coordinate (double)
     * @param y coordinate (double)
     * @param size of robot (double)
     * @param colour of robot (String)
     */


    public void showCircle(double x, double y, double rad, String colour, double angle) {
    	// Set up the circle with random x and y coords and size
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(rad);
        // Set rotation to the same as direction

        circle.setRotate(angle);

        // Set up two new lines to represent wheels
        Line wheel_1 = new Line();
        Line wheel_2 = new Line();

        // Set the initial coordinates for the wheels tangent and perpendicular to the circle
        double wheelStartX = x - rad;
        double wheelStartY = y - rad;
        double wheelEndX = x + rad;
        double wheelEndY = y - rad;

        // Rotate the coordinates based on the robot's direction
        double wheelStartX_rotated = rotateX(wheelStartX, wheelStartY, x, y, angle);
        double wheelStartY_rotated = rotateY(wheelStartX, wheelStartY, x, y, angle);
        double wheelEndX_rotated = rotateX(wheelEndX, wheelEndY, x, y, angle);
        double wheelEndY_rotated = rotateY(wheelEndX, wheelEndY, x, y, angle);
        // Set position of wheel 1
        wheel_1.setStartX(wheelStartX_rotated);
        wheel_1.setStartY(wheelStartY_rotated);
        wheel_1.setEndX(wheelEndX_rotated);
        wheel_1.setEndY(wheelEndY_rotated);
        wheel_1.setStrokeWidth(3);
        // set position of wheel 2
        wheelStartX = x - rad;
        wheelStartY = y + rad;
        wheelEndX = x + rad;
        wheelEndY = y + rad;
        // set rotation of wheel 2
        wheelStartX_rotated = rotateX(wheelStartX, wheelStartY, x, y, angle);
        wheelStartY_rotated = rotateY(wheelStartX, wheelStartY, x, y, angle);
        wheelEndX_rotated = rotateX(wheelEndX, wheelEndY, x, y, angle);
        wheelEndY_rotated = rotateY(wheelEndX, wheelEndY, x, y, angle);

        wheel_2.setStartX(wheelStartX_rotated);
        wheel_2.setStartY(wheelStartY_rotated);
        wheel_2.setEndX(wheelEndX_rotated);
        wheel_2.setEndY(wheelEndY_rotated);
        wheel_2.setStrokeWidth(3);

        // Add wheels to the wheels array
        wheels.add(wheel_1);

        line_count=line_count+1;
        wheels.add(wheel_2);

        line_count=line_count+1;

        if ("BLACK".equals(colour)) {
            circle.setFill(Color.BLACK);  // set colour to black
        }
        else if ("GREY".equals(colour)) {
            circle.setFill(Color.GREY);  // set colour to grey
        }
        else if("BLUE".equals(colour)) {
        	circle.setFill(Color.BLUE);
        }


        circles.add(circle);  // Add to circles list


        arenaGroup.getChildren().addAll(circle, wheel_1, wheel_2);
 // add the wheels and circle to the window to represent a robot
    }
    /*
     * displays the beam robot
     * @param x width position
     * @param y height position
     * @param rad the size
     * @param angle of robot
     */


    /**
     * Displays the line obstacle to the window.
     * @param x coordinate (double)
     * @param y coordinate (double)
     * @param rotation (int)
     */
    public void showObstacle_line(double x1, double y1, double x2, double y2, int rotation) {
        // Check for duplicate lines in arenaGroup
        for (javafx.scene.Node node : arenaGroup.getChildren()) {
            if (node instanceof Line) {
                Line existingLine = (Line) node;
                if (existingLine.getStartX() == x1 && existingLine.getStartY() == y1 &&
                    existingLine.getEndX() == x2 && existingLine.getEndY() == y2 &&
                    existingLine.getRotate() == rotation) {
                    return; // Line already exists; no need to add it again
                }
            }
        }

        // Create and add the new line
        Line line = new Line();
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
        line.setRotate(rotation); // Apply rotation to the line
        line.setStrokeWidth(5);
        line.setStroke(Color.BLACK);

        arenaGroup.getChildren().add(line); // Add the line to arenaGroup
    }






	/**
	 * Displays a circle obstacle in the arena.
	 * @param x The x-coordinate of the circle.
	 * @param y The y-coordinate of the circle.
	 */
	public void showObstacle_circle(double x, double y) {
	    Circle circle = new Circle();
	    circle.setCenterX(x);
	    circle.setCenterY(y);
	    circle.setRadius(30); // Set appropriate radius for the circle obstacle
	    circle.setFill(Color.RED); // Set the color of the circle obstacle

	    arenaGroup.getChildren().add(circle); // Add to arenaGroup
	}



	/*
	 * Rotates the wheels to match the direction of the robot
	 * @param x coordinate (double)
	 * @param y coordinate (double)
	 * @param centreX coordinate (double)
	 * @param centreY coordinate (double)
	 * @param radian angle of circle (double)
	 * @return rotation of x coordinate
	 */
	private double rotateX(double x, double y, double centerX, double centerY, double angle) {
	    return (x - centerX) * Math.cos(angle) - (y - centerY) * Math.sin(angle) + centerX;
	}

	/*
	 * Rotates the wheels to match the direction of the robot
	 * @param x coordinate (double)
	 * @param y coordinate (double)
	 * @param centreX coordinate (double)
	 * @param centreY coordinate (double)
	 * @param radian angle of circle (double)
	 * @return rotation of y coordinate
	 */
	private double rotateY(double x, double y, double centerX, double centerY, double angle) {
	    return (x - centerX) * Math.sin(angle) + (y - centerY) * Math.cos(angle) + centerY;
	}

	/**
	 * Checks if a robot collides with a line obstacle.
	 * 
	 * @param cx The x-coordinate of the robot's center.
	 * @param cy The y-coordinate of the robot's center.
	 * @param radius The radius of the robot.
	 * @param x1 The x-coordinate of the line's start point.
	 * @param y1 The y-coordinate of the line's start point.
	 * @param x2 The x-coordinate of the line's end point.
	 * @param y2 The y-coordinate of the line's end point.
	 * @return true if the robot collides with the line, false otherwise.
	 */
	private boolean isRobotCollidingWithLine(double cx, double cy, double radius, 
	                                         double x1, double y1, double x2, double y2) {
	    // Compute the projection of the robot's center onto the line segment
	    double lineLengthSquared = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
	    double t = ((cx - x1) * (x2 - x1) + (cy - y1) * (y2 - y1)) / lineLengthSquared;

	    // Clamp t to the range [0, 1] to ensure the projection lies on the line segment
	    t = Math.max(0, Math.min(1, t));

	    // Compute the closest point on the line segment
	    double closestX = x1 + t * (x2 - x1);
	    double closestY = y1 + t * (y2 - y1);

	    // Compute the distance between the robot's center and the closest point
	    double distanceSquared = Math.pow(closestX - cx, 2) + Math.pow(closestY - cy, 2);

	    // Check if the distance is less than or equal to the robot's radius
	    return distanceSquared <= Math.pow(radius, 2);
	}

	/*
	 * prints out all current objects with their coordinates to the window
	 */
	public void printRobotLocations() {
        Group root = (Group) arenaGroup;
        root.getChildren().removeIf(node -> node instanceof Text);  // remove text
        int i = 0;
        for (Objects objects : objects) {  // loop through all objects

            Text text = new Text(objects.getType() + objects.getObjectID() + ": (" + Math.round(objects.getX()) +
                    ", " + Math.round(objects.getY()) + ")"); // set up text
            // set the location of the text
            text.setX(x+150);
            text.setY(100 + i * 20);  // y coordinate dependent on i
            root.getChildren().add(text);  // add the text to the window
            i++;
        }
    }

	public int saveFile(String fname) {
        int status = 0;
        try (FileOutputStream fileOutputStream = new FileOutputStream(fname);
                ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream)) {

            // Temporarily nullify transient fields
            Group tempGroup = this.arenaGroup;
            this.arenaGroup = null;

            Scene tempScene = this.scene;
            this.scene = null;

            outputStream.writeObject(this);

            // Restore transient fields
            this.arenaGroup = tempGroup;
            this.scene = tempScene;

            System.out.println("Simulation saved successfully to " + fname);

        } catch (IOException e) {
            System.err.println("IOException occurred during save: " + e.getMessage());
            e.printStackTrace();
            status = 1;
        }
        return status;
	}

	public static Arena loadFile(String fname, Group newArenaGroup) {
        try (FileInputStream fileInputStream = new FileInputStream(fname);
                ObjectInputStream inputStream = new ObjectInputStream(fileInputStream)) {

            Arena loadedArena = (Arena) inputStream.readObject();

            // Reinitialize transient fields
            loadedArena.setArenaGroup(newArenaGroup);
            loadedArena.scene = new Scene(newArenaGroup, loadedArena.x, loadedArena.y);
            loadedArena.circles = new ArrayList<>();
            loadedArena.wheels = new ArrayList<>();
            loadedArena.beams = new ArrayList<>();

            loadedArena.drawArena();
            System.out.println("Simulation loaded successfully from " + fname);
            return loadedArena;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error occurred during load: " + e.getMessage());
            e.printStackTrace();
            return null;
	}





	


}
}	
