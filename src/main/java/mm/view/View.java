package mm.view;

import org.jbox2d.common.Vec2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

//all graphics, no testing
public class View {
    private final float left;
    private final float right;
    private final float top;
    private final float bottom;

    /**
     * nullconstructor,
     * left/top = 0,
     * right/bottom = 1
     */
    public View() {
        this.left = 0;
        this.right = 1;
        this.top = 0;
        this.bottom = 1;
    }

    /**
     * constructor
     * @param left
     * @param right
     * @param top
     * @param bottom
     */
    public View(float left, float right, float top, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * construct View from canvas
     * @param canvas existing canvas
     * @return newly constructed View instance
     */
    public static View fromCanvas(Canvas canvas) {
        return new View(0, (float) canvas.getWidth(), 0, (float) canvas.getHeight());
    }

    /**
     * construct View from position and size vectors
     * @param position
     * @param size
     * @return newly constructed View instance
     */
    public static View fromExtent(Vec2 position, Vec2 size) {
        return new View(position.x, position.x + size.x, position.y, position.y + size.y);
    }

    
    /**
     * Transforms the graphics context to match the view
     * @param ctx
     */
    public void transformContext(GraphicsContext ctx) {
        ctx.scale(ctx.getCanvas().getWidth() / getWidth(), ctx.getCanvas().getHeight() / getHeight());
        ctx.translate(-left, -top);
    }

    // getters
    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }

    public float getWidth() {
        return right - left;
    }

    public float getHeight() {
        return bottom - top;
    }

    public float getCenterX() {
        return (left + right) / 2;
    }

    public float getCenterY() {
        return (top + bottom) / 2;
    }
}
