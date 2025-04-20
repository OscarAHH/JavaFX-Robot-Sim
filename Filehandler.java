package robot_simulator;

import java.io.File; // Import the File class to handle file operations
import javafx.stage.FileChooser; // Import FileChooser to enable file selection dialogs
import javafx.stage.Stage; // Import Stage for window handling
import javafx.scene.Group; // Import Group for JavaFX graphical grouping

public class Filehandler {
    public String data = ""; // Holds any relevant data as a string (not used in the current code)
    public FileChooser file_chooser; // FileChooser instance to select files
    private Arena arena; // Arena instance to manage the simulation
    private Canvas canvas; // Canvas instance for the visual display

    // Constructor initializes the Arena and Canvas
    public Filehandler() {
        this.arena = new Arena(0, 0, null); // Create a default Arena with dummy dimensions
        this.canvas = new Canvas(); // Create a new Canvas instance
    }

    /*
     * Opens a file selected by the user and loads the simulation data into the Arena.
     */
    public void open() {
        file_chooser = new FileChooser(); // Create a new FileChooser instance
        File selectedFile = file_chooser.showOpenDialog(null); // Open a dialog to select a file
        if (selectedFile != null) { // If a file is selected
            // Load the file into the Arena, using a new Group for visuals
            Arena loadedArena = Arena.loadFile(selectedFile.getAbsolutePath(), new Group());
            if (loadedArena != null) { // If loading succeeds
                this.arena = loadedArena; // Replace the current arena with the loaded one
                this.arena.setArenaGroup(new Group()); // Ensure transient fields are set properly
                System.out.println("File loaded successfully.");
                try {
                    canvas.drawWorld(); // Redraw the world with the loaded data
                } catch (Exception e) { // Handle potential errors during rendering
                    System.out.println("Error drawing world: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("File cannot be opened."); // Print error if loading fails
            }
        }
    }

    /*
     * Resets the arena by starting a new simulation.
     * @param window The current Stage that will be closed and reopened.
     */
    public void resetArena(Stage window) {
        try {
            New(window); // Call the method to start a new simulation
        } catch (Exception e) { // Handle potential errors
            System.out.println("Error resetting arena: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Creates a new simulation by restarting the Canvas.
     * @param window The current Stage to restart.
     */
    public void New(Stage window) throws Exception {
        window.close(); // Close the current stage
        canvas.start(window); // Restart the Canvas application
    }

    /*
     * Saves the current simulation to a file chosen by the user.
     */
    public void save() throws Exception {
        canvas.stop(); // Stop the simulation before saving
        file_chooser = new FileChooser(); // Create a new FileChooser instance
        File selectedFile = file_chooser.showSaveDialog(null); // Open a dialog to select a save file
        if (selectedFile != null) { // If a file is selected
            // Save the current arena to the selected file
            if (arena.saveFile(selectedFile.getAbsolutePath()) > 0) { 
                System.out.println("Error: Could not save file"); // Print error if saving fails
            } else {
                System.out.println("File saved successfully"); // Confirm successful save
            }
        }
    }
}
