package mm.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import mm.level.LevelObject;

public class EditorObject {
    //should be encapsulated, is not for time reasons
    public boolean selected;
    public LevelObject prototype;

    /**
     * Constructor for EditorObject
     * @param object
     */
    public EditorObject(LevelObject object) {
        this.prototype = object;
        this.selected = false;
    }

    //render doesnt need testing
    /**
     * Renders the EditorObject on the given GraphicsContext
     * @param ctx
     */
    public void render(GraphicsContext ctx) {
        //render doesnt need testing
        prototype.renderPreview(ctx);
        if (selected) {
            ctx.setStroke(javafx.scene.paint.Color.WHITE);
            Rectangle2D box = prototype.getBoundingBox();
            ctx.strokeRect(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
        }
    }
}
