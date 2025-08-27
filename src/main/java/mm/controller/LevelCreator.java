package mm.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.jbox2d.common.Vec2;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import mm.level.GameLevel;
import mm.level.LevelObject;
import mm.level.LevelSpecialObject;
import mm.level.WinCondition;
import mm.model.EditorObject;
import mm.model.GameScene;
import mm.view.ResourceLoader;


public class LevelCreator {
    @FXML
    Canvas mainCanvas;
    @FXML
    private AnchorPane canvasContainer;
    @FXML
    Button buttonClear;
    @FXML
    private VBox levelCreator_VBoxBlocks;
    @FXML
    private VBox levelCreator_VBoxSpecial;
    @FXML
    private AnchorPane levelCreator_scrollPane;

    private CodeArea levelCreator_levelPreview;

    AnimationTimer updateTimer;

    private static GameLevel originalLevel;
    private GameLevel newLevel;
    private Stage stage;
    private Scene scene;
    private GraphicsContext ctx;
    private static String levelPath = null;
    private static GameLevel loadedLevel;
    private boolean editingText = false;
    private GameScene currentGameScene;
    private MediaPlayer bgm;
    private String selectedItem;
    private HBox oldItem = null;
    private int specialObjectPlacement = 0;
    private List<Integer> currentSpecialChildren = new ArrayList<>();
    private HashMap<String,Integer> availableObjects;
    private static Map<String, Integer> availableBackup = null;
    private ObservableList<Node> allHBoxes = FXCollections.observableArrayList();
    private ObservableList<Spinner<Integer>> allSpinners = FXCollections.observableArrayList();
    private EditorObject dragging = null;
    private WinCondition draggingWc = null;
    private PauseTransition debouncePause = new PauseTransition(Duration.millis(3000));

    /**
     * Constructor, similar to GameWindow Constructor
     * takes selected GameLevel and tries to generate a Scene 
     */
    public LevelCreator() {
        if (levelPath != null && levelPath.equals("blankCanvas")) {
            levelPath = "/blankLevel.json";
        }
        if (loadedLevel == null) {
            try {
                loadedLevel = GameLevel.fromJSON(new String(
                    ResourceLoader
                        .getResourceStream(levelPath)
                        .readAllBytes()
                ));
                originalLevel = loadedLevel;
                System.out.println("loaded level!");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        currentGameScene = new GameScene(loadedLevel, true);
    }

    @FXML 
    public void initialize() {
        createMediaPlayer();

        levelCreator_levelPreview = new CodeArea();
        levelCreator_levelPreview.setStyle("-fx-font-size: 7pt; ");
        levelCreator_levelPreview.setParagraphGraphicFactory(LineNumberFactory.get(levelCreator_levelPreview));
        levelCreator_levelPreview.setWrapText(true);
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(levelCreator_levelPreview);
        levelCreator_scrollPane.getChildren().add(vsPane);

        // Anchor all sides to fill the parent AnchorPane
        AnchorPane.setTopAnchor(vsPane, 0.0);
        AnchorPane.setBottomAnchor(vsPane, 0.0);
        AnchorPane.setLeftAnchor(vsPane, 0.0);
        AnchorPane.setRightAnchor(vsPane, 0.0);


        availableObjects = new HashMap<>(
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

        allHBoxes.addAll(levelCreator_VBoxBlocks.getChildren());
        allHBoxes.addAll(levelCreator_VBoxSpecial.getChildren());

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

        // tracks all spinners, for item input
        for (Node item : allHBoxes) {
            HBox entry = (HBox) item;
            for (Node entryChild : entry.getChildren()) {
                if (entryChild instanceof VBox) {
                    VBox entryVBox = (VBox) entryChild;
                    for (Node vBoxChild : entryVBox.getChildren()) {
                        if (vBoxChild instanceof Spinner) {
                            allSpinners.add((Spinner<Integer>) vBoxChild);
                        }
                    }
                }
            }
        }

        if (availableBackup != null) {
            availableObjects = new HashMap<>(availableBackup);
            availableBackup = null; // optional, je nach Use Case
            updateSpinners();
        }

        for (Spinner<Integer> spinner : allSpinners) {
            // filters all symbols except numbers in the spinner input
            spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    spinner.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            // saves the new spinner values for the puzzle mode
            spinner.valueProperty().addListener((obs, oldV, newV) -> {
                String[] idComp = spinner.getId().split("_");
                String spinnerID = idComp[0];
                availableObjects.put(spinnerID, newV);
            });
        }

        ctx = mainCanvas.getGraphicsContext2D();

        // handles the textArea to Canvas conversion using a timer as a buffer 
        levelCreator_levelPreview.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            debouncePause.stop();
            editingText = true;
            debouncePause.setOnFinished(ev -> {
                try {
                    newLevel = GameLevel.fromJSONforLevelCreator(levelCreator_levelPreview.getText());
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initOwner(App.getStage());
                        alert.setTitle("Oops: Faulty JSON!");
                        alert.setHeaderText("Your JSON seems to have an error. Sucks to suck.");
                        alert.setContentText(e.getMessage());

                        // futher delay showAndWait
                        Platform.runLater(alert::showAndWait);
                    });
                }
                Platform.runLater(() -> {
                    if (newLevel != null) {
                        currentGameScene = new GameScene(newLevel, true);
                        loadedLevel = newLevel;
                        availableObjects.replaceAll((key, val) -> 0);
                        for (String obj : newLevel.available) {
                            availableObjects.put(obj, availableObjects.get(obj) + 1);
                        }
                        updateSpinners();
                        updateJSON(); // this prevents JSON delay funkyness
                        availableBackup = new HashMap<>(availableObjects);
                    }
                });
                editingText = false;
            });
            debouncePause.playFromStart();
        });

        levelCreator_levelPreview.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            debouncePause.playFromStart();
        });

        levelCreator_levelPreview.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            debouncePause.playFromStart();
        });

        mainCanvas.setOnMousePressed(event -> {
            Vec2 mousePos = currentGameScene.transformMouse(
                event.getX(),
                event.getY(),
                mainCanvas.getWidth(),
                mainCanvas.getHeight()
            );
            if (specialObjectPlacement > 0 ) {
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
                    }
                    return;
                }
            }
            EditorObject eo = currentGameScene.getEditorObjectAt(mousePos);
            LevelSpecialObject lso = currentGameScene.getSpecialObjectAt(mousePos);
            WinCondition wC = currentGameScene.getWinConditionAt(mousePos);
            System.out.println("Win Condition found!");
            if (event.getButton() == MouseButton.PRIMARY) {
                if (eo == null && wC == null) {
                    LevelObject n = new LevelObject();
                    n.position = mousePos;
                    n.rotation = 0.0f;
                    n.type = selectedItem == null ? "Crate" : selectedItem;
                    currentGameScene.addEditorObject(n);
                    eo = currentGameScene.getEditorObjectAt(mousePos);
                }

                if (wC != null) {
                    draggingWc = wC;
                }
                dragging = eo;
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                if (eo != null) {
                    currentGameScene.removeEditorObject(eo);
                    System.out.println("Object removed");
                } else if (lso != null) {
                    currentGameScene.removeSpecialObject(lso);
                    System.out.println("Removed special object: " + lso.type);
                }
            }
        });

        // handles dragging by releasing or "freeing" it from the drag
        mainCanvas.setOnMouseReleased(event -> {
            if (dragging != null) {
                dragging = null;
            }
            if (draggingWc != null) {
                draggingWc = null;
            }
        });

        // drags objects
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
            } else if (draggingWc != null) {
                Vec2 newPos = currentGameScene.transformMouse(
                    event.getX(),
                    event.getY(),
                    mainCanvas.getWidth(),
                    mainCanvas.getHeight()
                );
                currentGameScene.editWinConditionAt(newPos);
            }
        });

        // rotates objects
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

        // refreshes the JSON and the scene
        updateTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            private long frameCounter = 0;
            @Override
            public void handle(long now) {
                frameCounter++;
                if (now - lastUpdate <= 16_000_000.0f) return;
                if (currentGameScene != null) {
                    currentGameScene.update((now - lastUpdate) / 1_000_000_000.0f);
                    currentGameScene.render(ctx);
                    if (specialObjectPlacement > 0) {
                        currentGameScene.renderJoints(
                            ctx,
                            selectedItem,
                            currentSpecialChildren.stream().mapToInt(i -> i).toArray()
                        );
                    }
                }
                if (frameCounter >= 100 && !editingText) {
                    updateJSON();
                    frameCounter = 0;
                }
                lastUpdate = now;
            }
        };

        updateTimer.start();
        canvasContainer = (AnchorPane) mainCanvas.getParent();
    }

    /**
     * Updates the .JSON in the preview based on the current state of the scene. This is called periodically every 100 Frames.
     */
    private void updateJSON() {
        ArrayList<String> newAvailableObjects = new ArrayList<>();
        for (String object : availableObjects.keySet()) {
            int amount = availableObjects.get(object);
            for (int i = amount; i > 0; i--) {
                newAvailableObjects.add(object);
            }
        }
        availableBackup = new HashMap<>(availableObjects);
        String[] availableObjString = newAvailableObjects.toArray(String[]::new);
        if (loadedLevel != null) {
            GameLevel newLevel = GameLevel.fromJSON(currentGameScene.exportLevel("Save", loadedLevel.description, currentGameScene.getWinCondition(), loadedLevel.view));
            newLevel.available = availableObjString;
            loadedLevel = newLevel;
            levelCreator_levelPreview.replaceText(loadedLevel.toJSON());
        }
    }

    /**
     * Updates all the Spinner Values.
     */
    private void updateSpinners() {

        for (Spinner<Integer> spinner : allSpinners) {
            String[] idComp = spinner.getId().split("_");
            String spinnerID = idComp[0]; // e.g. "Plank"
            if (availableObjects.containsKey(spinnerID)) {
                int value = availableObjects.get(spinnerID);
                spinner.getValueFactory().setValue(value);
            }
        }
    }

    /**
     * Creates the game scene once it is fully loaded, as this is sometimes slower on some OS's. Also handles the resizing of the stage as the game scene needs to be fully initialized first.
     * @param newScene
     */
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
     * helper method to resize the canvas (as the name implies)
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

    /**
     * simplified resizeCanvas() with hardcoded values
     */
    private void resizeToOriginal() {
        double canvasWidth, canvasHeight;
        canvasWidth = 792.0;
        canvasHeight = 598.0;
        mainCanvas.setWidth(canvasWidth);
        mainCanvas.setHeight(canvasHeight);
        mainCanvas.setLayoutX(0.0);
    }

    /**
     * Creates media player for Soundtrack
     * (which is self composed, btw!!)
     */
    private void createMediaPlayer() {
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
     * Handles returining to the Sandbox Selection 
     * @throws IOException
     */
    @FXML 
    private void backToMenu() throws IOException{
        loadedLevel = null;
        if (availableBackup != null) {
            availableBackup.clear();
        }
        dispose();
        App.setRoot("SandBoxModeMenu");
        GameWindow.clear();
    }
    /**
     * Releases resources like MediaPlayer, AnimationTimer and clears the game scene.
     */
    public void dispose() {
        updateTimer.stop();
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
        }
        currentGameScene.setPlaying(false);
        currentGameScene = null;
        mainCanvas = null;
    }

    @FXML
    private void switchPuzzleMode() throws IOException {
        if (oldItem != null) {
            oldItem.setStyle("");
        }
        updateJSON();
        GameWindow.setPreview(true);
        GameWindow.setLevel(loadedLevel);
        System.out.println("Switching to TestMode");

        dispose();

        App.setRoot("GameWindow");
    }

    String levelName = "";
    @FXML
    private void saveLevel() throws IOException {

        TextInputDialog nameDialogue = new TextInputDialog();
        nameDialogue.setTitle("Name your Level");
        nameDialogue.setHeaderText("Give your Level a name!");
        nameDialogue.setContentText("Enter:");

        Optional<String> result = nameDialogue.showAndWait();
        result.ifPresent(input -> {
            levelName = input;
            try {
                File sandboxDir = new File("sandbox");
                if (!sandboxDir.exists()) {
                    sandboxDir.mkdirs();
                }
                File[] saves = sandboxDir.listFiles();
                int count = sandboxDir.listFiles() != null ? saves.length : 0;
                loadedLevel.name = levelName;
    
                File file = new File(sandboxDir, count + "-CustomLevel-" + levelName + ".json"); 
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(loadedLevel.toJSON());
                    System.out.println("Level saved to: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void clearLevel() throws IOException {
        loadedLevel = originalLevel;
        currentGameScene = new GameScene(originalLevel, true);
        for (String key : availableObjects.keySet()) {
            availableObjects.put(key, 0);
        }
        availableBackup = null;
        updateJSON();
        updateSpinners();
    }
    /**
     * Sets the level inside the Level Editor via level Path
     * @param level
     */
    public static void setLevel(String level) {
        levelPath = level;
        System.out.println("Level Path: "  + levelPath);
    }

    /**
     * Sets the level inside the Level Editor via a GameLevel object.
     * @param level
     */
    public static void setLevel(GameLevel level) {
        loadedLevel = level;
    }

}