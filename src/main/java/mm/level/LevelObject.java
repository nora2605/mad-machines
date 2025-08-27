package mm.level;

import org.jbox2d.common.Vec2;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import mm.model.GameObject;
import mm.model.GameObjects.Balloon;
import mm.model.GameObjects.BigGear;
import mm.model.GameObjects.Blocker;
import mm.model.GameObjects.BouncyBall;
import mm.model.GameObjects.Bucket;
import mm.model.GameObjects.Crate;
import mm.model.GameObjects.Domino;
import mm.model.GameObjects.Lever;
import mm.model.GameObjects.Log;
import mm.model.GameObjects.Motor;
import mm.model.GameObjects.Paddle;
import mm.model.GameObjects.Plank;
import mm.model.GameObjects.SmallGear;

public class LevelObject {
    public String type;
    public Vec2 position;
    public float rotation;

    /**
     * dummy constructor
     */
    public LevelObject() {
        this.type = null;
        this.position = new Vec2(0, 0);
        this.rotation = 0;
    }

    //render doesnt need testing
    /**
     * Renders preview for level selection menu
     * @param ctx
     */
    public void renderPreview(GraphicsContext ctx) {
        GameObject go = this.realize();
        go.render(ctx, position, rotation);
    }

    /**
     * returns bounding box of game object
     * @return
     */
    @JsonIgnore
    public Rectangle2D getBoundingBox() {
        GameObject go = this.realize();
        return go.getBoundingBox(position, rotation * (float) Math.PI / 180.0f);
    }

    /**
     * Converts the Level description of an object to an instance of a GameObject.
     * keep in mind, some objects are realized as Special Objects  
     * </p>
     * Has
     * <ul>
     * <li> Plank 
     * <li> BouncyBall
     * <li> Domino
     * <li> Balloon 
     * <li> Log
     * <li> Crate
     * </ul>
     * @return new instance of the selected GameObject
     */
    public GameObject realize() {
        switch (type) {
            case "Plank":
                return new Plank();
            case "BouncyBall":
                return new BouncyBall();
            case "Domino":
                return new Domino();
            case "Balloon":
                return new Balloon();
            case "Log":
                return new Log();
            case "Crate":
                return new Crate();
            case "Paddle":
                return new Paddle();
            case "Blocker":
                return new Blocker();
            case "Bucket":
                return new Bucket();
            case "SmallGear":
                return new SmallGear();
            case "BigGear":
                return new BigGear();
            case "Motor":
                return new Motor();
            case "Lever":
                return new Lever();
            default:
                throw new IllegalArgumentException("Unknown object type: " + type);
        }
    }

        public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
