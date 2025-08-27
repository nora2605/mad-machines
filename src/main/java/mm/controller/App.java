package mm.controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import mm.view.ResourceLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class App extends Application {

    @FXML
    private static Scene scene;
    @FXML
    private static MediaPlayer bgm;
    @FXML
    private static double lastVolume = 0.5;
    @FXML
    private static Stage stage;

    private static boolean maximized = false;
    boolean remaxed = false;
    private static boolean fullscreen = false;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        scene = new Scene(loadFXML("StartScreen"));
        
        // Set the app's icon
        primaryStage.getIcons().add(new Image(ResourceLoader.getResource("/icon.png").toExternalForm()));

        // Set title
        primaryStage.setTitle("Mad Machines");
        // Sets the Styling via CSS
        scene.getStylesheets().add(ResourceLoader.getResource("/app.css").toExternalForm());
        // Set scene
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);  
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        playMusic();

        // in javafx you leave fullscreen automatically with ESC, this changes the internal variable onClick as well
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fullscreen = false;
            }
        });

        // custom: F11 to enter fullscreen
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                fullscreen = true;
                Platform.runLater(() -> {
                    stage.setFullScreen(fullscreen);
                });
            }
        });

        // when you enter/leave fullscreen the original resolution should be restored
        stage.fullScreenProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                primaryStage.setWidth(1000);
                primaryStage.setHeight(600);
                primaryStage.setMaximized(primaryStage.isMaximized());
            }
        });

        // logic for maximizing, removes the limits and rerenders because else it wont display at full resolution
        stage.maximizedProperty().addListener((obs, oldV, newV) -> {
            maximized = newV;
            if (!newV) {
                primaryStage.setWidth(1000);
                primaryStage.setHeight(600);
            }
        });
    }

    /**
     * launches application
     * @param args command line parameters, get via getParameters()
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * uses javaFX FXMLLoader to load object hierarchy 
     * @param fxml file name in /resources/fxml
     * @return loaded object hierarchy
     * @throws IOException
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Define root of scene graph
     * @param fxml file name to call {@code loadFXML(fxml)} with
     * @throws IOException
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Stops the Music in the MediaPlayer and discards all media.
     */
    public static void stopMusic() {
        if (bgm == null) {
            System.err.println("Error, MediaPlayer doesn't exist!");
            return;
        }
        bgm.stop();
        bgm.dispose();
    }

    /**
     * Plays the music of the Start Screen. The volume, which is set in the options screen, is saved here, so we dont loose the setting globally.
     */
    public static void playMusic() {
        try {
            Media bgmStream = new Media(ResourceLoader.getResource("/sound/dante.mp3").toExternalForm());
            bgm = new MediaPlayer(bgmStream);
            bgm.setCycleCount(-1);
            bgm.setVolume(lastVolume);
            bgm.play();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Sets the volume of the Music MediaPlayer
     * @param volume the volume which is used to set. A value between 0 and 1.
     */
    public static void setVolume(double volume) {
        if (bgm == null) {
            System.err.println("Error, MediaPlayer doesnt exist!");
            return;
        }
        lastVolume = volume;
        bgm.setVolume(lastVolume);
    }

    /**
     * Return the current set volume value.
     * @return Volume value of the Music Player.
     */
    public static double getVolume() {
        if (bgm == null) {
            System.err.println("Error, MediaPlayer doesn't exist. Set base value to 0");
            return 0.0;
        }
        return bgm.getVolume();
    }

    /**
     * Scale to Fullscreen if
     * @param checkboxValue is true
     */
    public static void setFullscreen(boolean checkboxValue) {
        fullscreen = checkboxValue;
        if (!fullscreen) {
            stage.setWidth(1000);
            stage.setHeight(600);
        }
        // Linux shenanigans
        Platform.runLater(() -> stage.setFullScreen(checkboxValue));
    }

    //getters
    
    public static boolean getFullscreen() {
        return fullscreen;
    }

    public static boolean getMaximized() {
        return maximized;
    }

    public static Stage getStage() {
        return stage;
    }
}
