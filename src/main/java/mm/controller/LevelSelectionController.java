package mm.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mm.level.GameLevel;
import mm.view.ResourceLoader;

public class LevelSelectionController implements Initializable{
    @FXML
    VBox levelSelect_levelsSection;
    @FXML
    MediaPlayer bgm;

    private String levelToLoad;
    private File[] levels;
    private HBox levelSelected;
    private static final Image bg = new Image(ResourceLoader.getResource("/sprites/Background.jpeg").toExternalForm());
    
    @Override
    public void initialize(URL url, ResourceBundle resource) {

        // load saves (levels rn) dynamically
        try {
            URL levelsUrl = ResourceLoader.getResource("/levels");
            File levelsDir = new File(levelsUrl.toURI());
    
            levels = levelsDir.listFiles();
            if (levels == null) {
                System.out.println("Keine Level gefunden!");
            } else {
                System.out.println("Level geladen:");
                for (File level : levels) {
                    System.out.println(level.getName());
                }
            }

            /*
             * Loads the levels from the "/levels" Folder and displays them dynamically in the Selection Screen
             */
            for (File level : levels) {
                HBox levelEntry = new HBox();
                VBox levelEntryContainer = new VBox();
                Label levelName = new Label();
                Canvas levelPreview = new Canvas();
                TextArea levelDesc = new TextArea();
                levelDesc.setEditable(false);
                
                // set the name of each entry to the .json name
                levelToLoad = level.getName();
                levelEntry.setId("/levels/" + levelToLoad);
                // keep the onMouseClicked Action
                levelEntry.setOnMouseClicked(this::selectLevel);
                
                levelPreview.setWidth(160.0);
                levelPreview.setHeight(120.0);
                
                GameLevel gameLevel;
                try {
                    gameLevel = GameLevel.fromJSON(new String(
                        ResourceLoader
                            .getResourceStream("/levels/" + level.getName())
                            .readAllBytes()
                    ));
                } catch (IOException e) {
                    continue; // skip this level if it can't be loaded
                }

                levelDesc.setText(gameLevel.description);
                GraphicsContext ctx = levelPreview.getGraphicsContext2D();

                ctx.fillRect(0, 0, levelPreview.getWidth(), levelPreview.getHeight());
                gameLevel.view.transformContext(ctx);
                ctx.drawImage(bg, gameLevel.view.getLeft(), gameLevel.view.getTop(), gameLevel.view.getWidth(), gameLevel.view.getHeight());
                gameLevel.renderPreview(ctx);
                ctx.setTransform(1, 0, 0, 1, 0, 0);

                // set VBox inside HBox
                levelEntryContainer.setId("levelContainer_" + level.getName());
                levelName.setText(gameLevel.name);
                levelName.setStyle("-fx-text-fill: white; -fx-font-size: 24.0; -fx-font-weight: bold;-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0.4, 0.0, 0.0);");

                levelEntryContainer.getChildren().add(levelName);
                levelEntryContainer.getChildren().add(levelPreview);

                levelEntry.getChildren().add(levelEntryContainer);
                levelEntry.getChildren().add(levelDesc);

                levelSelect_levelsSection.getChildren().add(levelEntry);
            }
        } catch (URISyntaxException e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Constructor for LevelSelectionController
     * Initializes the background music for the level selection screen
     */
    public LevelSelectionController() {
        try {
            URL mediaURL = ResourceLoader.getResource("/sound/luciano.mp3");
            Media bgmStream = new Media(mediaURL.toExternalForm());
            bgm = new MediaPlayer(bgmStream);
            bgm.setCycleCount(-1);
            bgm.setVolume(App.getVolume());
            bgm.play();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * selects Level in LevelSelectScreen
     * @param event MouseEvent 
     */
    @FXML
    private void selectLevel(MouseEvent event) {
        HBox level = (HBox) event.getSource();

        if (levelSelected == null) {
            level.setStyle("-fx-border-color: ff2222; -fx-border-width: 2px;");
        } else {
            levelSelected.setStyle("");
            level.setStyle("-fx-border-color: ff2222; -fx-border-width: 2px;");
        }
        System.out.println("Level Selected! " + level.getId());
        levelSelected = level;
    }

    /**
     * if a level is selected, set scene to selected level
     * @throws IOException
     */
    @FXML
    private void loadLevel() throws IOException {
        if (levelSelected == null) {
            System.out.println("No level selected");
            Alert alertNoLevelSelected = new Alert(Alert.AlertType.CONFIRMATION);
            alertNoLevelSelected.initOwner(App.getStage());
            alertNoLevelSelected.setTitle("Oops!");
            alertNoLevelSelected.setHeaderText("You didn't select a level!");
            alertNoLevelSelected.setContentText("Select a level first by clicking on it!");
            ButtonType accept = new ButtonType("OK!");
            alertNoLevelSelected.getButtonTypes().setAll(accept);
            alertNoLevelSelected.showAndWait();
            return; 
        }
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
        }
        System.out.println("Loading Level... " + levelSelected.getId());
        GameWindow.clear();
        GameWindow.setLevel(levelSelected.getId());
        App.setRoot("GameWindow");
    }

    /**
     * Returns to the main menu screen.
     * stops music, if any
     * @throws IOException
     */
    @FXML 
    private void backToMenu() throws IOException {
        if (bgm == null) {
            App.setRoot("StartScreen");
            return;
        }
        bgm.stop();
        bgm.dispose();
        App.setRoot("StartScreen");
        App.playMusic();
    }
}
