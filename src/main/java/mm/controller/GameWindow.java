package mm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.common.Vec2;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import mm.level.GameLevel;
import mm.level.LevelObject;
import mm.level.LevelSpecialObject;
import mm.model.EditorObject;
import mm.model.GameScene;
import mm.view.ResourceLoader;

public class GameWindow  {

    
    GraphicsContext ctx;
    
    MediaPlayer bgm;
    @FXML
    Button buttonMenu;
    @FXML
    Button buttonPlay;
    @FXML
    Canvas mainCanvas;
    @FXML
    Button buttonClear;
    @FXML
    private VBox gameWindow_VBoxBlocks;
    @FXML
    private VBox gameWindow_VBoxSpecial;

    private static boolean isPreview = false;
    private Stage stage;
    private Scene scene;
    private AnchorPane canvasContainer;
    private static String levelName = null;
    private HashMap<String,Integer> objects;
    private HashMap<String,Integer> objectsBackup;
    private String selectedItem;
    private HBox oldItem;
    private AnimationTimer updateTimer;
    // 0 if not selected, 1 if selected, +1 for every child connected (if thats needed more than once)
    private int specialObjectPlacement = 0;
    private List<Integer> currentSpecialChildren = new ArrayList<>();
    private static GameLevel loadedLevel;
    private final GameScene currentGameScene;
    private ObservableList<Node> allHBoxes = FXCollections.observableArrayList();
    EditorObject dragging = null;

    /**
     * Constructor for Gamewindow,
     * tries to create a Scene from selected GameLevel
     */
    public GameWindow() {
        System.out.println(isPreview);
        if (loadedLevel == null) {
            try {
                loadedLevel = GameLevel.fromJSON(new String(
                    ResourceLoader
                        .getResourceStream(levelName)
                        .readAllBytes()
                ));
                System.out.println("loaded level!");
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        currentGameScene = new GameScene(loadedLevel);
    }

    /**
     * Setup routine for GameWindow (provided by javaFX)
     */
    @FXML
    private void initialize() {
        createMediaPlayer();

        if (isPreview) {
            buttonMenu.setText("Editor");
        }
        
        // holds all available Items of a level inside the .JSON 
        String[] availableObjects = loadedLevel.available;
        // use HashMap to track the amount of items
        objects = new HashMap<>(
            Map.ofEntries(
                Map.entry("BouncyBall", 0),
                Map.entry("Domino", 0),
                Map.entry("Plank", 0),
                Map.entry("Crate", 0),
                Map.entry("Log", 0),
                Map.entry("Rope", 0),
                Map.entry("RubberBand", 0),
                Map.entry("Balloon", 0),
                Map.entry("Paddle", 0),
                Map.entry("Blocker", 0),
                Map.entry("Bucket", 0),
                Map.entry("SmallGear", 0),
                Map.entry("BigGear", 0),
                Map.entry("Motor", 0),
                Map.entry("Lever", 0)
            )
        );

        // load all the available objects into the UI
        for (String item : availableObjects) {
            objects.put(item, objects.get(item) + 1);
        }
        objectsBackup = new HashMap<>(objects);

        allHBoxes.addAll(gameWindow_VBoxBlocks.getChildren());
        allHBoxes.addAll(gameWindow_VBoxSpecial.getChildren());

        updateLabels();
        if (loadedLevel.available.length > 0) {
            selectedItem = loadedLevel.available[0];
        } else {
            selectedItem = "Crate";
        }

        for (Node item : allHBoxes) {
            HBox entry = (HBox) item;

            entry.setOnMousePressed(event -> {
                if (oldItem != null) {
                    oldItem.setStyle("");
                }
                selectedItem = entry.getId();
                if (selectedItem.equals("Rope") || selectedItem.equals("RubberBand")) {
                    specialObjectPlacement = 1;
                } else {
                    specialObjectPlacement = 0;
                }
                currentSpecialChildren.clear();
                entry.setStyle("-fx-border-color: #42A5F5; -fx-border-width: 5px;");
                oldItem = entry;
                System.out.println("Selected: " + selectedItem);
            });
        }

        ctx = mainCanvas.getGraphicsContext2D();

        mainCanvas.setOnMousePressed(event -> {
            Vec2 mousePos = currentGameScene.transformMouse(
                event.getX(),
                event.getY(),
                mainCanvas.getWidth(),
                mainCanvas.getHeight()
            );
            
            if (specialObjectPlacement > 0 && objects.get(selectedItem) > 0) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    int oi = currentGameScene.getObjectIndexAt(mousePos);
                    if (oi != -1 && !currentSpecialChildren.contains(oi)) {
                        currentSpecialChildren.add(oi);
                        specialObjectPlacement++;
                    }
                    // hardcoded for now since only 2 children are intended rn
                    if (specialObjectPlacement == 3) {
                        LevelSpecialObject specialObject = new LevelSpecialObject();
                        specialObject.type = selectedItem;
                        specialObject.children = currentSpecialChildren.stream().mapToInt(i -> i).toArray();
                        
                        currentGameScene.addSpecialObject(specialObject);
                        specialObjectPlacement = 1;
                        currentSpecialChildren.clear();
                        objects.put(selectedItem, objects.get(selectedItem) - 1);
                        updateLabels();
                    }
                    return;
                }
            }

            EditorObject eo = currentGameScene.getEditorObjectAt(mousePos);
            LevelSpecialObject lso = currentGameScene.getSpecialObjectAt(mousePos);
            if (event.getButton() == MouseButton.PRIMARY) {
                if (eo == null && objects.get(selectedItem) > 0 && currentGameScene.isEditable()) {
                    LevelObject n = new LevelObject();
                    n.position = mousePos;
                    n.rotation = 0.0f;
                    n.type = selectedItem;
                    currentGameScene.addEditorObject(n);
                    eo = currentGameScene.getEditorObjectAt(mousePos);
                    objects.put(selectedItem, objects.get(selectedItem) - 1);
                    updateLabels();
                    System.out.println("Put one: " + selectedItem + ". Remaining " + selectedItem + "s: " + objects.get(selectedItem));
                }
                dragging = eo;
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                if (eo != null) {
                    currentGameScene.removeEditorObject(eo);
                    objects.put(eo.prototype.type, objects.get(eo.prototype.type) + 1);
                    updateLabels();
                    System.out.println("Removed one: " + eo.prototype.type + ". Remaining " + eo.prototype.type + "s: " + objects.get(eo.prototype.type));
                }
                else if (lso != null) {
                    currentGameScene.removeSpecialObject(lso);
                    objects.put(lso.type, objects.get(lso.type) + 1);
                    updateLabels();
                    System.out.println("Removed special object: " + lso.type);
                }
            }
        });

        mainCanvas.setOnMouseReleased(event -> {
            if (dragging != null) {
                dragging = null;
            }
        });

        mainCanvas.setOnMouseDragged(event -> {
            if (dragging != null) {
                Vec2 newPos = currentGameScene.transformMouse(
                    event.getX(),
                    event.getY(),
                    mainCanvas.getWidth(),
                    mainCanvas.getHeight()
                );
                currentGameScene.editEditorObject(
                    dragging,
                    newPos,
                    dragging.prototype.rotation
                );
            }
        });

        mainCanvas.setOnScroll(event -> {
            EditorObject eo = currentGameScene.getEditorObjectAt(
                currentGameScene.transformMouse(
                    event.getX(),
                    event.getY(),
                    mainCanvas.getWidth(),
                    mainCanvas.getHeight()
                )
            );
            if (eo != null) {
                float rotation = eo.prototype.rotation + (event.getDeltaY() > 0 ? 5f : -5f);
                if (rotation < 0) rotation += 360.0f;
                if (rotation >= 360.0f) rotation -= 360.0f;
                currentGameScene.editEditorObject(eo, eo.prototype.position, rotation);
            }
            event.consume();
        });
        
        if (mainCanvas.getScene() != null) {
            onSceneReady(mainCanvas.getScene());
        } else {
            mainCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    onSceneReady(newScene);
                }
            });
        }

        //adds canvas resizing to app queue 
        Platform.runLater(() -> {
            
            if (App.getFullscreen() || App.getMaximized()) {
                System.out.println("Initial Resolution: Fullscreen");
                resizeCanvas();
            } else {
                System.out.println("Initial Resolution: 1000x600");
                resizeToOriginal();
            }
        });

        updateTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate <= 16_000_000.0f) return;
                if (currentGameScene != null) {
                    currentGameScene.update((now - lastUpdate) / 1_000_000_000.0f);
                    buttonPlay.setText(currentGameScene.isPlaying() ? "Pause" : "Play");
                    currentGameScene.render(ctx);
                    if (specialObjectPlacement > 0) {
                        currentGameScene.renderJoints(
                            ctx,
                            selectedItem,
                            currentSpecialChildren.stream().mapToInt(i -> i).toArray()
                        );
                    }
                }
                lastUpdate = now;
            }
        };

        // play/pause button
        buttonPlay.setOnAction(event -> {
            if (oldItem != null) {
                oldItem.setStyle("");
            }
            if (!currentGameScene.isPlaying()) {
                if (currentGameScene.isEditable())
                    currentGameScene.play();
                else
                    currentGameScene.setPlaying(true);
            } else {
                currentGameScene.setPlaying(false);
            }
        });

        //start Timer
        updateTimer.start();
        canvasContainer = (AnchorPane) mainCanvas.getParent();

    }

    private void onSceneReady(Scene newScene) {
        scene = newScene;
        
        scene.addEventHandler(KeyEvent.KEY_PRESSED,event -> {
            if (event.getCode() == KeyCode.F11) {
                System.out.println("Resizing to Fullscreen");
                Platform.runLater(() -> {
                    resizeCanvas();
                });
            }
        });
        stage = (Stage) mainCanvas.getScene().getWindow();
        canvasContainer.layoutBoundsProperty().addListener((nobs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (!stage.maximizedProperty().get() && !stage.fullScreenProperty().get()) {
                    System.out.println("Set Resolution: 1000x600");
                    resizeToOriginal();
                } else {
                    System.out.println("Set Resolution: DISPLAY_MAX");
                    resizeCanvas();
                }
            });
        });
    }

    /**
     * Sets the canvas size according to the screen resolution and the level aspect ratio
     */
    private void resizeCanvas() {

        mainCanvas.widthProperty().unbind();
        mainCanvas.heightProperty().unbind();
        double canvasWidth, canvasHeight;

        double viewWidth = loadedLevel.view.getRight();
        double viewHeight = loadedLevel.view.getBottom();
        double viewAspect = viewWidth / viewHeight;

        double containerWidth = canvasContainer.getWidth();
        double containerHeight = canvasContainer.getHeight();
        double containerAspect = containerWidth / containerHeight;
        System.out.println("Container Width: " + containerWidth + ", Container Height: " + containerHeight);

        if (containerAspect > viewAspect) {
            // Fit to height
            canvasHeight = containerHeight;
            canvasWidth = canvasHeight * viewAspect;
        } else {
            // Fit to width
            canvasWidth = containerWidth;
            canvasHeight = canvasWidth / viewAspect;
        }
        mainCanvas.setWidth(canvasWidth);
        mainCanvas.setHeight(canvasHeight);

        double border = (containerWidth - canvasWidth) / 2;
        mainCanvas.setLayoutX(border);
    }

    private void resizeToOriginal() {
        double canvasWidth, canvasHeight;
        canvasWidth = 792.0;
        canvasHeight = 598.0;
        mainCanvas.setWidth(canvasWidth);
        mainCanvas.setHeight(canvasHeight);
        mainCanvas.setLayoutX(0.0);
    }

    /**
     * This updates the "amount" labels of the objects on screen.
     */
    private void updateLabels() {

        for (Node child : allHBoxes) {
            HBox entry = (HBox) child;
            String entryID = entry.getId();
            VBox entryVBox = (VBox) entry.getChildren().get(0); // get VBox inside each Item HBox
            Label label = (Label) entryVBox.getChildren().get(1);
            label.setText(String.valueOf(objects.get(entryID)));
        }
    }

    /**
     * Creates the MediaPlayer outside of initialize(), because Java bitches if its inside there.
     */
    private void createMediaPlayer() {
        try {
            URL mediaURL = ResourceLoader.getResource("/sound/fermi.mp3");
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
     * Rewinds the current level
     * @throws IOException
     */
    @FXML 
    private void rewindLevel() throws IOException{
        currentGameScene.rewind();
        buttonPlay.setText("Play");
    }

    /**
     * set scene back to LevelSelectScreen.fxml
     * @throws IOException
     */
    @FXML 
    private void backToMenu() throws IOException{
        dispose();
        // if we are in a preview the Menu button brings us back to the editor
        if (isPreview) {
            isPreview = false;
            //set editor level
            LevelCreator.setLevel(loadedLevel);
            App.setRoot("LevelCreator");
        } else {
            App.setRoot("LevelSelectScreen");
        }
    }

    /**
     * Clears level from all user-placed objects
     */
    @FXML
    private void clearLevel() {
        try {
            rewindLevel();
            objects = new HashMap<>(objectsBackup);
            currentGameScene.removeAllEditorObjects();
            currentGameScene.removeAllSpecialObjects();
            updateLabels();
            System.out.println("level cleared");
            System.out.println(objects);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles returining to the Sandbox Selection 
     * @throws IOException
     */
    public void dispose() {
        updateTimer.stop();
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
        }
        currentGameScene.setPlaying(false);
        mainCanvas = null;
    }

    /**
     * Sets the level inside the GameWindow given a String which is the path
     * @param level
     */
    public static void setLevel(String level) {
        levelName = level;
    }

    /**
     * Sets the level inside the GameWindow given a GameLevel Object
     * @param level
     */
    public static void setLevel(GameLevel level) {
        loadedLevel = level;
    }

    /**
     * Sets the isPreview value. isPreview is necessary to differentiate whether we are coming into an ordinary level from the level selection screen or from the editor. This affects labels and some functions.
     * @param preview
     */
    public static void setPreview(boolean preview) {
        isPreview = preview;
    }

    /**
     * Returns the current GameLevel
     * @return the currently loaded level inside the GameWindow
     */
    public static GameLevel getLevel() {
        return loadedLevel;
    }

    /**
     * Clears set level attributes.
     */
    public static void  clear() {
        levelName = null;
        loadedLevel = null;
        isPreview = false;
    }
}
