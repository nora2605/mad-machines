package mm.model;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import mm.controller.App;
import mm.level.GameLevel;
import mm.level.LevelObject;
import mm.level.LevelSpecialObject;
import mm.level.WinCondition;
import mm.model.SpecialObjects.WinSensor;
import mm.view.ResourceLoader;
import mm.view.View;

public final class GameScene {
    /**
     * stores whether the physics engine is updated
    */
    boolean isPlaying = false;

    /**
     * returns whether the scene is currently playing
     * @return true if playing, false otherwise
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * sets whether the scene is currently playing
     * @param playing true if playing, false otherwise
     */
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    /**
     * stores whether editing is enabled.
     * controlled internal state (get-only)
     */
    boolean editable = true;

    /**
     * returns whether the scene is currently editable
     * @return true if editable, false otherwise
     */
    public boolean isEditable() {
        return editable;
    }

    boolean inBuilder = false;
    
    private GameLevel baseLevel;

    private final List<GameObject> gameObjects;
    private final List<SpecialObject> specialObjects;
    private final List<EditorObject> placed;
    private final List<LevelSpecialObject> placedSpecial;
    final WinSensor winSensor;
    private final World world;
    Vec2 winConditionPosition;
    private WinCondition winCon;
    private final View worldView;

    private final AudioClip bump;
    private final AudioClip win;

    private static final Image bg = new Image(ResourceLoader.getResource("/sprites/Background.jpeg").toExternalForm());
    private static final Color transparentRed = Color.RED.deriveColor(0, 1, 1, 0.25);

    /**
     * constructs GameScene from GameLevel,
     * then calls initialize
     * @param level
     */
    public GameScene(GameLevel level) {
        bump = new AudioClip(ResourceLoader.getResource("/sound/bump.wav").toExternalForm());
        bump.setVolume(0.3);
        win = new AudioClip(ResourceLoader.getResource("/sound/win.wav").toExternalForm());
        win.setVolume(0.6);

        world = new World(new Vec2(0.0f, 9.81f));
        world.setContactListener(new GameSceneContactListener(this));
        gameObjects = new ArrayList<>();
        specialObjects = new ArrayList<>();
        placed = new ArrayList<>();
        placedSpecial = new ArrayList<>();

        baseLevel = level;
        worldView = level.view;

        winSensor = level.winCondition.instantiate(world);
        winConditionPosition = level.winCondition.position;
        winSensor.setPosition(winConditionPosition);

        initialize();
    }

    /**
     * overloaded constructor for editor mode
     * @param level
     * @param inBuilder
     */
    public GameScene(GameLevel level, boolean inBuilder) {
        this(level);
        for (LevelObject obj : level.objects) {
            this.addEditorObject(obj);
        }
        for (LevelSpecialObject sp0 : level.special) {
            this.addSpecialObject(sp0);
        }
        gameObjects.clear();
        specialObjects.clear();
        this.inBuilder = inBuilder;
    }

    /**
     * Adds a new editable object to the scene
     * @param object a levelobject prototype
     */
    public void addEditorObject(LevelObject object) {
        if (!editable) return;
        EditorObject editorObject = new EditorObject(object);
        placed.add(editorObject);
    }

    public void addSpecialObject(LevelSpecialObject object) {
        if (!editable) return;
        placedSpecial.add(object);
    }

    /**
     * gets the editable object at the given position, if any
     * @param position position to check
     * @return NULLABLE editor object at given position
     */
    public EditorObject getEditorObjectAt(Vec2 position) {
        // iterate in reversed order to select the most recently placed one (on top in render)
        for (int i = placed.size() - 1; i >= 0; i--) {
            EditorObject eo = placed.get(i);
            if (eo.prototype.getBoundingBox().contains(position.x, position.y)) {
                return eo; 
            }
        }
        return null;
    }

    public WinCondition getWinConditionAt(Vec2 position) {
        if (baseLevel.winCondition.getBoundingBox().contains(position.x, position.y)) {
            return baseLevel.winCondition; 
        } 
        return null;
    }

    public void editWinConditionAt(Vec2 position) {
        if (!editable) return;
        baseLevel.winCondition.position = position;
        winSensor.setPosition(position);
    }

    public WinCondition getWinCondition() {
        return baseLevel.winCondition;
    }

    /**
     * gets the editable object at the given position, if any
     * @param position position to check
     * @return NULLABLE editor object at given position
     */
    public LevelSpecialObject getSpecialObjectAt(Vec2 position) {
        for (int i = placedSpecial.size() - 1; i >= 0; i--) {
            LevelSpecialObject lso = placedSpecial.get(i);
            if (lso.realize().getBoundingBox(
                getPositionByIndex(lso.children[0]),
                getPositionByIndex(lso.children[1])
            ).contains(position.x, position.y)) {
                return lso;
            }
        }
        return null;
    }

    /**
     * Find the GameObject which BoundingBox contains a position 
     * @param position the coordinates to check for
     * @return the index of the corresponding object, -1 otherwise
     */
    public int getObjectIndexAt(Vec2 position) {
        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            GameObject go = gameObjects.get(i);
            if (go.getBoundingBox().contains(position.x, position.y)) {
                return i;
            }
        }
        for (int i = placed.size() - 1; i >= 0; i--) {
            EditorObject eo = placed.get(i);
            if (eo.prototype.getBoundingBox().contains(position.x, position.y)) {
                return gameObjects.size() + i;
            }
        }
        return -1;
    }

    /**
     * Check if bounding boxes intersect
     * @param eo object to place
     * @return true if builder mode, true if no intersections with any
     */
    public boolean isLegalPosition(EditorObject eo) {
        if (inBuilder) return true;
        for (EditorObject object : placed) {
            if (object == eo) continue;
            if (object.prototype.getBoundingBox().intersects(eo.prototype.getBoundingBox())) {
                return false;
            }
        }

        for (GameObject gameObject : gameObjects) {
            if (gameObject.getBoundingBox().intersects(eo.prototype.getBoundingBox())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if no bounding boxes intersect for every object
     * (or builder mode is active)
     * @return true if builder mode, otherwise false if any intersections exist
     */
    private boolean isLegalScene() {
        if (inBuilder) return true;
        for (EditorObject object : placed) {
            if (!isLegalPosition(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * edits the given editor object
     * @param object the object to edit
     * @param position the new position
     * @param rotation the new rotation
     */
    public void editEditorObject(EditorObject object, Vec2 position, float rotation) {
        if (!editable) return;
        object.prototype.position = position;
        object.prototype.rotation = rotation;
    }

    /**
     * removes the given editor object from the scene
     * @param object the object to remove
     */
    public void removeEditorObject(EditorObject object) {
        // recalculate indices
        int oi = getObjectIndex(object);
        placedSpecial.removeIf(special -> {
            for (int ci : special.children) {
                if (ci == oi) {
                    return true;
                }
            }
            return false;
        });
        for (var special : placedSpecial) {
            for (int i = 0; i < special.children.length; i++) {
                if (special.children[i] > oi) {
                    special.children[i]--;
                }
            }
        }
        placed.remove(object);
    }

    /**
     * removes the given special object from the scene
     * @param object the object to remove
     */
    public void removeSpecialObject(LevelSpecialObject object) {
        placedSpecial.remove(object);
    }

    /**
     * Instantiates all placed editable gameobjects to real gameobjects and starts playing
     */
    public void play() {
        if (!isLegalScene()) return;

        isPlaying = true;
        editable = false;
        for (EditorObject object : placed) {
            GameObject go = object.prototype.realize();
            go.instantiate(world, object.prototype.position, object.prototype.rotation);
            go.getPhysicsObject().setUserData(go);

            gameObjects.add(go);
        }
        for (LevelSpecialObject special : placedSpecial) {
            SpecialObject so = special.realize();
            GameObject obA = gameObjects.get(special.children[0]);
            GameObject obB = gameObjects.get(special.children[1]);
            so.instantiate(world, obA, obB);

            specialObjects.add(so);
        }
    }

    /**
     * rewinds the scene to the last prePlay() call and stops playing
     */
    public void rewind() {
        isPlaying = false;
        editable = true;
        for (SpecialObject so : specialObjects) {
            so.remove(world);
        }
        specialObjects.clear();
        for (GameObject go : gameObjects) {
            go.remove(world);
        }
        gameObjects.clear();
        initialize();
    }

    /**
     * removes all placed editable gameobjects
     */
    public void removeAllEditorObjects() {
        if (!editable) return;
        placed.clear();
    }

    /**
     * removes all placed special gameobjects
     */
    public void removeAllSpecialObjects() {
        if (!editable) return;
        placedSpecial.clear();
    }

    /**
     * Instantiates all level gameobjects
     */
    private void initialize() {
        for (LevelObject object : baseLevel.objects) {
            GameObject go = object.realize();
            go.instantiate(world, object.position, object.rotation);
            
            go.getPhysicsObject().setUserData(go);

            gameObjects.add(go);
        }
        for (LevelSpecialObject special : baseLevel.special) {
            SpecialObject so = special.realize();
            GameObject obA = gameObjects.get(special.children[0]);
            GameObject obB = gameObjects.get(special.children[1]);
            so.instantiate(world, obA, obB);
            specialObjects.add(so);
        }
    }

    /**
     * call time step function,
     * manage collisions
     * update GameObjects
     * remove out-of-bounds GameObjects 
     * @param deltaTime tick speed in ns
     */
    public void update(float deltaTime) {
        if (!isPlaying) return;

        world.step(deltaTime, 14, 10);

        // update objects
        for (GameObject gameObject : gameObjects) {
            gameObject.update(deltaTime);
        }
        // update special objects
        for (SpecialObject specialObject : specialObjects) {
            specialObject.update(deltaTime);
        }
    }

    //render doesnt need testing
    /**
     * render background,
     * transform View,
     * render objects,
     * reset View transform
     * @param ctx GraphicsContext for draw calls
     */
    public void render(GraphicsContext ctx) {
        // view transform
        worldView.transformContext(ctx);

        ctx.drawImage(bg, worldView.getLeft(), worldView.getTop(), worldView.getWidth(), worldView.getHeight());

        for (var gameObject : gameObjects) {
            gameObject.render(ctx);
        }

        for (var specialObject : specialObjects) {
            specialObject.render(ctx);
        }

        winSensor.render(ctx);

        if (editable) {
            // Render bounding boxes

            for (var placedObject : placed) {
                placedObject.render(ctx);
                if (inBuilder)
                    ctx.setStroke(Color.BLUE);
                else if (isLegalPosition(placedObject))
                    ctx.setStroke(Color.GREEN);
                else
                    ctx.setStroke(Color.RED);
                Rectangle2D boundingBox = placedObject.prototype.getBoundingBox();
                ctx.strokeRect(
                    boundingBox.getMinX(),
                    boundingBox.getMinY(),
                    boundingBox.getWidth(),
                    boundingBox.getHeight()
                );
            }

            for (var placedSpecialObject : placedSpecial) {
                placedSpecialObject.realize().renderPreview(ctx,
                    getPositionByIndex(placedSpecialObject.children[0]),
                    getPositionByIndex(placedSpecialObject.children[1])
                );
                ctx.setStroke(Color.GREEN);
                Rectangle2D boundingBox = placedSpecialObject.realize().getBoundingBox(
                    getPositionByIndex(placedSpecialObject.children[0]),
                    getPositionByIndex(placedSpecialObject.children[1])
                );
                ctx.strokeRect(
                    boundingBox.getMinX(),
                    boundingBox.getMinY(),
                    boundingBox.getWidth(),
                    boundingBox.getHeight()
                );
            }

            for (var gameObject : gameObjects) {
                Rectangle2D boundingBox = gameObject.getBoundingBox();
                ctx.setFill(transparentRed);
                ctx.fillRect(
                    boundingBox.getMinX(),
                    boundingBox.getMinY(),
                    boundingBox.getWidth(),
                    boundingBox.getHeight()
                );
            }
        }

        // reset view transform
        ctx.setTransform(1, 0, 0, 1, 0, 0);
    }

    /**
     * Allows getting the scenes object index of a GameObject
     * @param object
     * @return
     */
    public int getObjectIndex(GameObject object) {
        return gameObjects.indexOf(object);
    }

    /**
     * Allows getting the scenes object index of an EditorObject
     * @param object
     * @return
     */
    public int getObjectIndex(EditorObject object) {
        return placed.indexOf(object) + gameObjects.size();
    }

    /**
     * Allows getting an object by its index to avoid unnecessary polymorphism in the interface
     * between GameObject and EditorObject while supporting special objects to interlink
     * @param index
     * @return
     */
    public Object getObjectByIndex(int index) {
        if (index < gameObjects.size()) {
            return gameObjects.get(index);
        } else if (index < gameObjects.size() + placed.size()) {
            return placed.get(index - gameObjects.size());
        } else {
            return null; // oob
        }
    }

    /**
     * Allows getting the position of a GameObject by its index
     * @param index
     * @return
     */
    public Vec2 getPositionByIndex(int index) {
        if (index < gameObjects.size()) {
            return gameObjects.get(index).getPosition();
        } else if (index < gameObjects.size() + placed.size()) {
            return placed.get(index - gameObjects.size()).prototype.position;
        } else {
            return null; // oob
        }
    }

    /**
     * Renders available joints for the given selected (special) item
     * @param ctx
     * @param selectedItem
     * @param children
     */
    public void renderJoints(GraphicsContext ctx, String selectedItem, int[] children) {
        if (!editable) return;

        worldView.transformContext(ctx);
        
        for (GameObject go : gameObjects) {
            boolean isChild = false;
            for (int child : children) {
                if (getObjectIndex(go) == child) {
                    isChild = true;
                    break;
                }
            }
            ctx.setStroke(isChild ? Color.GREEN : Color.BLUE);
            ctx.strokeOval(
                go.getPosition().x - 0.025f,
                go.getPosition().y - 0.025f,
                0.05f,
                0.05f
            );
        }
        for (EditorObject eo : placed) {
            boolean isChild = false;
            for (int child : children) {
                if (getObjectIndex(eo) == child) {
                    isChild = true;
                    break;
                }
            }
            ctx.setStroke(isChild ? Color.GREEN : Color.BLUE);
            ctx.strokeOval(
                eo.prototype.position.x - 0.025f,
                eo.prototype.position.y - 0.025f,
                0.05f,
                0.05f
            );
        }
        ctx.setTransform(1, 0, 0, 1, 0, 0);
    }

    /**
     * handles collision between two game objects
     * @param a first game object
     * @param b second game object
     * @param impulse the impulse of the collision
     */
    void OnCollision(GameObject a, GameObject b, float impulse) {
        a.onCollision(b);
        b.onCollision(a);
        bump.play(Math.min(Math.max(impulse * 3, 0.05f), 1.0f));
    }

    /**
     * win logic
     */
    void win()
    {
        win.play();
        isPlaying = false;
        alertWinCondition();
    }
    /**
     * WindCondition PopUp.
     */
    private void alertWinCondition() {
        Platform.runLater(() -> {
            Alert wonLevel = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            wonLevel.initOwner(App.getStage());
            wonLevel.setTitle("Congratulations!");
            wonLevel.setHeaderText("You finished the level!");
            wonLevel.setContentText("Either leave, redo the level or let it play out!");
            ButtonType accept = new ButtonType("OK!");
            wonLevel.getButtonTypes().setAll(accept);
            wonLevel.showAndWait();
        });
    }

    /**
     * transforms mouse coordinates to world coordinates
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     * @param w width of the canvas
     * @param h height of the canvas
     * @return transformed Vec2 in world coordinates
     */
    public Vec2 transformMouse(double x, double y, double w, double h) {
        float worldX = (float) ((x / w) * worldView.getWidth()) + worldView.getLeft();
        float worldY = (float) ((y / h) * worldView.getHeight()) + worldView.getTop();
        return new Vec2(worldX, worldY);
    }

    /**
     * Exports the current level to a JSON string
     * @param name the name of the level
     * @param description the description of the level
     * @param winCondition the win condition of the level
     * @param worldView the view of the world
     * @return JSON string representation of the level
     */
    public String exportLevel(String name, String description, WinCondition winCondition, View worldView) {
        GameLevel exported = new GameLevel();
        ArrayList<LevelObject> objects = new ArrayList<>();
        // objects.addAll(Arrays.asList(baseLevel.objects)); This son of a bitch made me act up in ways you cant imagine
        for (EditorObject object : placed) {
            objects.add(object.prototype);
        }
        exported.objects = objects.toArray(LevelObject[]::new);
        // And the indices should line up! :D
        ArrayList<LevelSpecialObject> nSpecial = new ArrayList<>();
        // nSpecial.addAll(Arrays.asList(baseLevel.special));
        for (LevelSpecialObject special : placedSpecial) {
            nSpecial.add(special);
        }
        exported.special = nSpecial.toArray(LevelSpecialObject[]::new);

        exported.name = name;
        exported.description = description;
        exported.winCondition = winCondition;
        exported.view = worldView;

        return exported.toJSON();
    }

    public GameLevel getGameLevel() {
        return baseLevel;
    }

    public void setWinConditionPosition(Vec2 pos) {
        winConditionPosition = pos;
        winSensor.setPosition(pos);
    }
}

class GameSceneContactListener implements ContactListener {
    private final GameScene scene;

    public GameSceneContactListener(GameScene scene) {
        this.scene = scene;
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getBody() == scene.winSensor.body ||
            contact.getFixtureB().getBody() == scene.winSensor.body) {
            if (contact.isTouching()) {
                GameObject a = (GameObject) contact.getFixtureA().getBody().getUserData();
                GameObject b = (GameObject) contact.getFixtureB().getBody().getUserData();
                if (a == null && b == null)
                    return;
                GameObject target = a == null ? b : a;
                if (target.getClass().getSimpleName().equals(scene.winSensor.target)) {
                    scene.win();
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) { }
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        GameObject a = (GameObject) contact.getFixtureA().getBody().getUserData();
        GameObject b = (GameObject) contact.getFixtureB().getBody().getUserData();
        if (a == null || b == null) {
            return;
        }
        float maxImpulse = 0;
        int maxImpulseIndex = 0;
        for (int i = 0; i < impulse.normalImpulses.length; i++) {
            if (Math.abs(impulse.normalImpulses[i]) > maxImpulse) {
                maxImpulse = Math.abs(impulse.normalImpulses[i]);
                maxImpulseIndex = i;
            }
        }
        // 15 mNs and 30 cm/s in normal direction
        if (maxImpulse >= 0.015) {
            WorldManifold worldManifold = new WorldManifold();
            contact.getWorldManifold(worldManifold);
            Vec2 vel1 = a.getPhysicsObject().getLinearVelocityFromWorldPoint(worldManifold.points[maxImpulseIndex]);
            Vec2 vel2 = b.getPhysicsObject().getLinearVelocityFromWorldPoint(worldManifold.points[maxImpulseIndex]);
            float relVel = Math.abs(Vec2.dot(worldManifold.normal, vel1.sub(vel2)));
            
            if (relVel >= 0.3)
                scene.OnCollision(a, b, maxImpulse);
        }
    }
}