package mm.model.GameObjects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Bucket extends PhysicalGameObject {
    private static final float WIDTH = 0.35f;
    private static final float HEIGHT = 0.5f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Bucket.png").toExternalForm());

    /**
     * Constructor for Bucket object
     */
    public Bucket() {
        
    }

    @Override
    public void instantiate(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        physicsObject = world.createBody(bodyDef);
        
        PolygonShape groundShape = new PolygonShape();
        Vec2[] vertices = new Vec2[4];
        vertices[0] = new Vec2(-WIDTH / 2, HEIGHT / 3);
        vertices[1] = new Vec2(WIDTH / 2, HEIGHT / 3);
        vertices[2] = new Vec2(WIDTH / 2, HEIGHT / 2);
        vertices[3] = new Vec2(-WIDTH / 2, HEIGHT / 2);
        groundShape.set(vertices, 4);
        
        FixtureDef ground = new FixtureDef();
        ground.shape = groundShape;
        ground.density = 1.0f;
        ground.friction = 0.5f;
        physicsObject.createFixture(ground);

        PolygonShape sideShapeA = new PolygonShape();
        vertices[0] = new Vec2(-WIDTH / 2, 0);
        vertices[1] = new Vec2(-WIDTH / 3, 0);
        vertices[2] = new Vec2(-WIDTH / 3, HEIGHT / 2);
        vertices[3] = new Vec2(-WIDTH / 2, HEIGHT / 2);
        sideShapeA.set(vertices, 4);

        FixtureDef sideA = new FixtureDef();
        sideA.shape = sideShapeA;
        sideA.density = 1.0f;
        sideA.friction = 0.5f;
        physicsObject.createFixture(sideA);

        PolygonShape sideShapeB = new PolygonShape();
        vertices[0] = new Vec2(WIDTH / 2, 0);
        vertices[1] = new Vec2(WIDTH / 3, 0);
        vertices[2] = new Vec2(WIDTH / 3, HEIGHT / 2);
        vertices[3] = new Vec2(WIDTH / 2, HEIGHT / 2);
        sideShapeB.set(vertices, 4);

        FixtureDef sideB = new FixtureDef();
        sideB.shape = sideShapeB;
        sideB.density = 1.0f;
        sideB.friction = 0.5f;
        physicsObject.createFixture(sideB);

        FixtureDef hook = new FixtureDef();
        CircleShape hookShape = new CircleShape();
        hookShape.setRadius(WIDTH / 6);
        hook.shape = hookShape;
        hook.density = 0.0f;
        hook.isSensor = true;
        physicsObject.createFixture(hook);
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

        ctx.translate(x, y);
        ctx.rotate(rotation);

        ctx.drawImage(sprite, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
    
        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }
}
