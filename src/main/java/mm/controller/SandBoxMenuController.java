package mm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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

public class SandBoxMenuController implements Initializable {
    @FXML
    VBox levelSelect_levelsSection;
    @FXML
    MediaPlayer bgm;

    private String levelToLoad;
    private File[] levels;
    private HBox levelSelected;
    private File[] levelsFiles;
    private File[] sandboxFiles;
    private ArrayList<HBox> standardLevels = new ArrayList<>();
    private static final Image bg = new Image(ResourceLoader.getResource("/sprites/Background.jpeg").toExternalForm());
    
    public SandBoxMenuController() {
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

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        // load saves (levels rn) dynamically
        updateList();
        createLastListEntry();
    }

    private void createLastListEntry() {
        HBox createLevel = new HBox();
        Label levelName = new Label();
        createLevel.setId("blankCanvas");
        createLevel.setOnMouseClicked(this::selectLevel);
        levelName.setText("CREATE LEVEL");
        createLevel.getChildren().addAll(levelName);
        levelSelect_levelsSection.getChildren().addAll(createLevel);
    }

    private void updateList() {
        try {
            URL levelsUrl = ResourceLoader.getResource("/levels");
            File sandboxDir = new File("sandbox");
            File levelsDir = new File(levelsUrl.toURI());

            int savesLength = 0;
            if (sandboxDir.exists()) {
                sandboxFiles = sandboxDir.listFiles();
                savesLength = sandboxFiles.length;
            }
            
            // combine all levels
            levelsFiles = levelsDir.listFiles();
            int levelsLength = levelsFiles.length;
            File[] allFiles = new File[levelsLength + savesLength];
            System.arraycopy(levelsFiles, 0, allFiles, 0, levelsLength);

            if (sandboxFiles != null) {
                System.arraycopy(sandboxFiles, 0, allFiles, levelsLength, savesLength);
            }
            levels = allFiles;

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
                levelEntry.setId(levelToLoad);
                // keep the onMouseClicked Action
                levelEntry.setOnMouseClicked(this::selectLevel);
                
                levelPreview.setWidth(160.0);
                levelPreview.setHeight(120.0);
                
                GameLevel gameLevel;
                String path = "";
                InputStream is;
                File fileToLoad;
                for (File file : levelsFiles) {
                    if (file.getName().equals(level.getName())) {
                        path = "/levels/";
                        break;
                    }
                    path = "sandbox/";
                }

                // new level loader to accomodate the fact that sandbox levels cant be in ressources (cant use RessourceLoader)
                if (path.equals("/levels/")) {
                    // all /levels/ are standard 
                    standardLevels.add(levelEntry);
                    fileToLoad = new File("src/main/resources/levels", level.getName());
                } else {
                    fileToLoad = new File("sandbox", level.getName());
                }
                //set the HBox id to the level path
                levelEntry.setId(path + level.getName());

                try {
                    is = new FileInputStream(fileToLoad);
                    gameLevel = GameLevel.fromJSON(new String(is.readAllBytes()));
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
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

    @FXML
    private void selectLevel(MouseEvent event) {
        HBox level = (HBox) event.getSource();

        if (levelSelected == null) {
            level.setStyle("-fx-border-color: #42A5F5; -fx-border-width: 5px;");
        } else {
            levelSelected.setStyle("");
            level.setStyle("-fx-border-color: #42A5F5; -fx-border-width: 5px;");
        }
        System.out.println("Level Selected! " + level.getId());
        levelSelected = level;
        String levelID = level.getId();
        
        if (levelID.equals("blankCanvas")) {
            LevelCreator.setLevel(levelID);
            return;
        }
        
        if (!levelID.equals("blankCanvas")) {
            if (levelID.substring(0, 7).equals("sandbox")) {
                System.out.println("Wir haben ein sandboxlvl");
                generateSave(levelID);
                return;
            }
            GameWindow.setLevel(levelID);
        } 

        LevelCreator.setLevel(levelID);
    }

    private void generateSave(String levelId) {

        GameLevel save;
        InputStream stream;
        try {
            File saveToLoad = new File(levelId);
            stream = new FileInputStream(saveToLoad);
            save = GameLevel.fromJSON(new String(stream.readAllBytes()));
            stream.close();
            GameWindow.setLevel(save);
            LevelCreator.setLevel(save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void editLevel() throws IOException {
        if (levelSelected == null) {
            System.out.println("No level selected");
            alertNoLevel();
            return; // or throw an exception if you want to be more explicit
        }
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
        }
        App.setRoot("LevelCreator");
    }

    @FXML 
    private void playLevel() throws IOException {
        if (levelSelected == null || levelSelected.getId().equals("blankCanvas")) {
            alertNoLevel();
            System.out.println("No level selected");
            return; 
        }
        if (bgm != null) {
            try {
                bgm.stop();
                bgm.dispose();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        GameWindow.setPreview(true);
        App.setRoot("GameWindow");
    }

    private void alertNoLevel() {
        Alert alertNoLevelSelected = new Alert(Alert.AlertType.CONFIRMATION);
        alertNoLevelSelected.initOwner(App.getStage());
        alertNoLevelSelected.setTitle("Oops!");
        alertNoLevelSelected.setHeaderText("You didn't select a level!");
        alertNoLevelSelected.setContentText("Select a playable level or create a new one!");
        ButtonType accept = new ButtonType("OK!");
        alertNoLevelSelected.getButtonTypes().setAll(accept);
        alertNoLevelSelected.showAndWait();
        return;
    }

    @FXML
    private void deleteLevel() throws IOException {
        System.out.println("in delete levels");
        if (levelSelected == null) {
            alertNoLevel();
            return;
        }
        if (levelSelected.getId() != null && !levelSelected.getId().equals("blankCanvas")) {
            try {
                for (HBox box : standardLevels) {
                    if (levelSelected.getId().equals(box.getId())) {
                        System.out.println("Dont delete!");
                        // disallow deleting base game levels
                        Alert alertDeleteStandardLevel = new Alert(Alert.AlertType.CONFIRMATION);
                        alertDeleteStandardLevel.initOwner(App.getStage());
                        alertDeleteStandardLevel.setTitle("Oops!");
                        alertDeleteStandardLevel.setHeaderText("You can't delete a base-game level!");
                        alertDeleteStandardLevel.setContentText("Select a custom level or create a new one!");
                        ButtonType accept = new ButtonType("OK!");
                        alertDeleteStandardLevel.getButtonTypes().setAll(accept);
                        alertDeleteStandardLevel.showAndWait();
                                
                        System.out.println("No level selected");
                        return;
                    }
                }
                File levelToDelete = new File(levelSelected.getId());
        
                if (levelToDelete.exists()) {
                    System.out.println("it exists!");
                    if (levelToDelete.delete()) {
                        System.out.println("Level: " + levelSelected.getId() + " deleted!");
                        // Update the UI
                        levelSelect_levelsSection.getChildren().clear();
                        standardLevels.clear();
                        levelSelected = null;
                        levelsFiles = null;
                        sandboxFiles = null;
                        updateList();
                        createLastListEntry();
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        } else {
            alertNoLevel();
            System.out.println("No level selected");
        }
    }
}
