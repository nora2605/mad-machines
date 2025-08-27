package mm.model.GameObjects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Paddle extends PhysicalGameObject {
    private static final float WIDTH = 0.8f;
    private static final float HEIGHT = 0.1f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Paddle.png").toExternalForm());
    private static final Image spriteVertical = new Image(ResourceLoader.getResource("/sprites/Paddle_vertical.png").toExternalForm());
    private final BodyDef paddle = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();

    private Body anchor;

    public Paddle() {
        paddle.type = BodyType.DYNAMIC;

        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(paddle);
        PolygonShape shapeH = new PolygonShape();
        shapeH.setAsBox(WIDTH / 2, HEIGHT / 2);
        fixtureDef.shape = shapeH;        
        physicsObject.createFixture(fixtureDef);
        PolygonShape shapeV = new PolygonShape();
        shapeV.setAsBox(HEIGHT / 2, WIDTH / 2);
        fixtureDef.shape = shapeV; 
        physicsObject.createFixture(fixtureDef);

        // Anchor
        anchor = world.createBody(new BodyDef());
        CircleShape anchorShape = new CircleShape();
        anchorShape.setRadius(HEIGHT);
        FixtureDef anchorFixture = new FixtureDef();
        anchorFixture.shape = anchorShape;
        
        RevoluteJointDef anchorJoint = new RevoluteJointDef();
        anchorJoint.bodyA = anchor;
        anchorJoint.bodyB = physicsObject;
        anchorJoint.localAnchorA.set(0, 0);
        anchorJoint.localAnchorB.set(0, 0);
        anchorJoint.collideConnected = false;
        anchorJoint.enableMotor = true;
        anchorJoint.maxMotorTorque = 0.02f;
        anchorJoint.motorSpeed = 0f; // simulates friction
        world.createJoint(anchorJoint);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        anchor.setTransform(getPosition(), anchor.getAngle());
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 position, float rotation) {
        // since width is max(width, height)
        return new Rectangle2D(position.x - WIDTH / 2, position.y - WIDTH / 2, WIDTH, WIDTH);
    }

    @Override
    public void render(GraphicsContext ctx, Vec2 position, float rotation) {
        float x = position.x;
        float y = position.y;

        ctx.translate(x, y);
        ctx.rotate(rotation);

        ctx.drawImage(sprite, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
        ctx.drawImage(spriteVertical, -HEIGHT / 2, -WIDTH / 2, HEIGHT, WIDTH);

        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }

    @Override
    public void remove(World world) {
        if (physicsObject != null) {
            world.destroyBody(physicsObject);
            physicsObject = null;
        }
        if (anchor != null) {
            world.destroyBody(anchor);
            anchor = null;
        }
    }
}

