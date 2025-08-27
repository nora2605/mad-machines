package mm.level;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import mm.model.GameObject;
import mm.model.SpecialObject;
import mm.model.SpecialObjects.Rope;
import mm.model.SpecialObjects.RubberBand;

public class LevelSpecialObject {
    public String type;
    public int[] children;

    //render doesnt need testing
    /**
     * Renders preview for level selection menu
     * @param parent
     * @param ctx
     */
    public void renderPreview(GameLevel parent, GraphicsContext ctx) {
        SpecialObject so = this.realize();
        so.renderPreview(ctx, parent.objects[children[0]].position, parent.objects[children[1]].position);
    }

    /**
     * returns bounding box of special game object
     * @param parent
     * @return
     */
    public Rectangle2D getBoundingBox(GameLevel parent) {
        SpecialObject so = this.realize();
        return so.getBoundingBox(parent.objects[children[0]].position, parent.objects[children[1]].position);
    }

    /**
     * realizes Special Objects. Important: regular objects are not realized here, but in LevelObject
     * @return
     */
    public SpecialObject realize() {
        switch (type) {
            case "Rope":
                return new Rope();
            case "RubberBand":
                return new RubberBand();
        }
        return null;
    }
}
