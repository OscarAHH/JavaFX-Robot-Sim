/**
 * Canvas class serves as the main entry point for the robot simulator application.
 * It handles the graphical user interface (GUI), user interactions, and simulation control.
 */
package robot_simulator;

import java.io.File;
import java.io.Serializable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main Canvas class to handle GUI and simulation logic.
 */
public class Canvas extends Application implements Serializable {

    private static final long serialVersionUID = 1L;
    public Arena myArena; // The Arena object managing the simulation
    protected Group arenaGroup = new Group(); // Group for the arena visuals
    protected VBox uiContainer = new VBox(); // Container for UI elements
    double width; // Width of the arena
    double height; // Height of the arena
    public Stage windowStage; // The primary stage for the application
    Timeline tl = new Timeline(); // Timeline for animation control

    /**
     * Starts the application by prompting the user to set the arena dimensions.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        Label widthLabel = new Label("Enter Width:");
        TextField widthTextField = new TextField();

        Label heightLabel = new Label("Enter Height:");
        TextField heightTextField = new TextField();

        vbox.getChildren().addAll(widthLabel, widthTextField, heightLabel, heightTextField);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            try {
                this.width = Integer.parseInt(widthTextField.getText());
                this.height = Integer.parseInt(heightTextField.getText());
                showWindow(primaryStage, this.width, this.height);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number");
            }
        });

        vbox.getChildren().add(submitButton);
        Scene settingsScene = new Scene(vbox, 300, 200);
        primaryStage.setScene(settingsScene);
        primaryStage.setTitle("Set arena dimensions");
        primaryStage.show();
    }

    /**
     * Sets up the simulation window with the given dimensions.
     *
     * @param primaryStage The primary stage for the simulation.
     * @param width        The width of the arena.
     * @param height       The height of the arena.
     */
    public void showWindow(Stage primaryStage, double width, double height) {
        tl.setCycleCount(Animation.INDEFINITE);
        KeyFrame start = new KeyFrame(Duration.seconds(.0100), (ActionEvent event) -> begin());
        tl.getKeyFrames().add(start);
        tl.pause();

        if (arenaGroup == null) {
            arenaGroup = new Group();
        }
        myArena = new Arena(width, height, arenaGroup);

        Group root = new Group();
        root.getChildren().addAll(arenaGroup, uiContainer);
        Scene scene = new Scene(root, width * 2, height * 1.2);

        setupUI();

        scene.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            if (!myArena.isSimulationRunning()) {
                if (myArena.getSelectedObject() == null) {
                    myArena.handleSelection(mouseX, mouseY, myArena.isSimulationRunning());
                } else {
                    myArena.moveSelectedObject(mouseX, mouseY);
                }
            } else {
                System.out.println("Simulation is running. Cannot select or move objects.");
            }
        });

        windowStage = new Stage();
        windowStage.setScene(scene);
        windowStage.setTitle("Robot Simulation");
        windowStage.show();

        primaryStage.close();
    }

    /**
     * Sets up the user interface with buttons and menu options.
     */
    private void setupUI() {
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button addNormRobotButton = new Button("Add Normal Robot");
        Button addWeakRobotButton = new Button("Add Weak Robot");
        Button addBeamRobotButton = new Button("Add Beam Robot");
        Button addAvoidRobotButton = new Button("Add Avoider Robot");
        Button addObstacleButtonLine = new Button("Add Line Obstacle");
        Button addObstacleButtonCircle = new Button("Add Circle Obstacle");
        Button deleteButton = new Button("Delete Selected");

        startButton.setOnAction(e -> {
            tl.play(); // Start the simulation timeline
            myArena.setSimulationRunning(true); // Mark simulation as running
            System.out.println("Simulation started.");
        });

        stopButton.setOnAction(e -> {
            tl.pause(); // Pause the simulation
            myArena.setSimulationRunning(false); // Mark simulation as not running
        });

        addNormRobotButton.setOnAction(e -> myArena.addRegular());
        addWeakRobotButton.setOnAction(e -> myArena.addWeak());
        addBeamRobotButton.setOnAction(e -> myArena.addBeamRobot());
        addAvoidRobotButton.setOnAction(e -> myArena.addAvoid());
        addObstacleButtonLine.setOnAction(e -> myArena.addObstacle("line"));
        addObstacleButtonCircle.setOnAction(e -> myArena.addObstacle("circle"));
        deleteButton.setOnAction(e -> {
            myArena.deleteSelectedObject();
            drawWorld();
        });

        HBox buttonBox = new HBox(startButton, stopButton, addNormRobotButton, addWeakRobotButton,
                addAvoidRobotButton, addBeamRobotButton, addObstacleButtonLine, addObstacleButtonCircle, deleteButton);
        buttonBox.setSpacing(15);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        Menu menu = new Menu("Menu");
        MenuItem newItem = new MenuItem("New");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem loadItem = new MenuItem("Load");
        MenuItem helpItem = new MenuItem("Help");
        MenuItem aboutItem = new MenuItem("About");

        newItem.setOnAction(e -> {
            myArena.clearArena(); // Clear the arena completely
            drawWorld(); // Redraw the empty arena
            System.out.println("New simulation created.");
        });

        saveItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Simulation");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation Files", "*.dat"));
            File file = fileChooser.showSaveDialog(windowStage);
            if (file != null) {
                int status = myArena.saveFile(file.getAbsolutePath());
                if (status == 0) {
                    System.out.println("Simulation saved to " + file.getAbsolutePath());
                } else {
                    System.out.println("Failed to save simulation.");
                }
            }
        });

        loadItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Simulation");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation Files", "*.dat"));
            File file = fileChooser.showOpenDialog(windowStage);
            if (file != null) {
                Arena loadedArena = Arena.loadFile(file.getAbsolutePath(), new Group());
                if (loadedArena != null) {
                    myArena = loadedArena;
                    myArena.setArenaGroup(arenaGroup); // Reassign the UI group
                    drawWorld();
                    System.out.println("Simulation loaded successfully from " + file.getAbsolutePath());
                } else {
                    System.out.println("Failed to load simulation.");
                }
            }
        });

        helpItem.setOnAction(e -> show_help());
        aboutItem.setOnAction(e -> show_about());

        menu.getItems().addAll(newItem, saveItem, loadItem, helpItem, aboutItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        uiContainer.getChildren().clear();
        uiContainer.getChildren().addAll(menuBar, buttonBox);
    }

    /**
     * Handles the simulation logic and updates during each animation frame.
     */
    public void begin() {
        myArena.checkRobots();
        myArena.adjustRobots();
        drawWorld();
        myArena.obstacleCollision();
        myArena.printRobotLocations();
    }

    /**
     * Redraws the world by rendering the arena and its objects.
     */
    public void drawWorld() {
        myArena.drawArena(); // Simply redraw the arena
    }

    /**
     * Displays a help dialog with instructions on using the simulator.
     */
    public void show_help() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Help");

        Label title = new Label("Robot Simulator:");
        Label line1 = new Label("Use the buttons to add robots and obstacles to the arena.");
        Label line2 = new Label("Use the Start and Stop buttons to control animations.");
        Label line3 = new Label("Click objects to select them and use 'Delete Selected' to remove them.");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        VBox popupLayout = new VBox(10, title, line1, line2, line3, closeButton);
        Scene popupScene = new Scene(popupLayout, 400, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    /**
     * Displays an about dialog with project details.
     */
    public void show_about() {
        Stage popupStage = new Stage();
        popupStage.setTitle("About");

        Label title = new Label("Robot Simulator Project");
        Label line1 = new Label("Java CW 2025");
        Label line2 = new Label("Created by Oscar Hernandez Herrera");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        VBox popupLayout = new VBox(10, title, line1, line2, closeButton);
        Scene popupScene = new Scene(popupLayout, 300, 150);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
