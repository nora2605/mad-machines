package mm.model.GameObjects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Plank extends PhysicalGameObject {
    private static final float WIDTH = 1.0f;
    private static final float HEIGHT = 0.1f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Plank.png").toExternalForm());
    private final BodyDef bodyDef = new BodyDef();
    private final PolygonShape shape;

    /**
     * Constructor for Plank object
     */
    public Plank() {
        bodyDef.type = BodyType.STATIC;
        shape = new PolygonShape();
        shape.setAsBox(WIDTH / 2, HEIGHT / 2);
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(bodyDef);
        physicsObject.createFixture(shape, 0.0f);
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 position, float rotation) {
        float s = Math.abs((float)Math.sin(rotation));
        float c = Math.abs((float)Math.cos(rotation));
        float cx = (WIDTH * c + HEIGHT * s) / 2;
        float cy = (WIDTH * s + HEIGHT * c) / 2;
        return new Rectangle2D(position.x - cx, position.y - cy, 2 * cx, 2 * cy);
    }

    @Override
    public void render(GraphicsContext ctx, Vec2 position, float rotation) {
        float x = position.x;
        float y = position.y;

        ctx.setFill(Color.BROWN);

        ctx.translate(x, y);
        ctx.rotate(rotation);

        ctx.drawImage(sprite, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
    
        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }
}
