package mm.model.GameObjects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Domino extends PhysicalGameObject {
    // physics parameters
    private static final float WIDTH = 0.05f;
    private static final float HEIGHT = 0.38f;
    private static final float DENSITY = 3.0f;
    private static final float FRICTION = 0.3f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Domino.png").toExternalForm());
    private final BodyDef bodyDef = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();

    /**
     * Constructor for Domino object
     */
    public Domino() {
        bodyDef.type = BodyType.DYNAMIC;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH / 2, HEIGHT / 2);

        fixtureDef.shape = shape;
        fixtureDef.density = DENSITY;
        fixtureDef.friction = FRICTION;
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(bodyDef);
        physicsObject.createFixture(fixtureDef);
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

        ctx.setFill(Color.WHITE);
        
        ctx.translate(x, y);
        ctx.rotate(rotation);

        ctx.drawImage(sprite, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
    
        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }
}
