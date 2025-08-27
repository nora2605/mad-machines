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

public class Motor extends PhysicalGameObject {
    private static final float WIDTH = 0.3f;
    private static final float HEIGHT = 0.3f;
    private static final float R_RADIUS = 0.1f;

    private static final Image rumpSprite = new Image(ResourceLoader.getResource("/sprites/Motor.png").toExternalForm());
    private static final Image rotorSprite = new Image(ResourceLoader.getResource("/sprites/Rotor.png").toExternalForm());
    private final BodyDef gdynbody = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();

    private Body rump;

    public Motor() {
        gdynbody.type = BodyType.DYNAMIC;
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(gdynbody);
        CircleShape rotorShape = new CircleShape();
        rotorShape.setRadius(R_RADIUS);
        fixtureDef.shape = rotorShape;
        fixtureDef.friction = 0.01f;
        fixtureDef.density = 0.03f;
        physicsObject.createFixture(fixtureDef);

        BodyDef rumpDef = new BodyDef();
        rumpDef.type = BodyType.DYNAMIC;
        rumpDef.fixedRotation = true;
        rump = world.createBody(rumpDef);
        PolygonShape rumpShape = new PolygonShape();
        Vec2[] vertices = new Vec2[4];
        vertices[0] = new Vec2(-WIDTH / 2, 0);
        vertices[1] = new Vec2(WIDTH / 2, 0);
        vertices[2] = new Vec2(WIDTH / 2, HEIGHT / 2);
        vertices[3] = new Vec2(-WIDTH / 2, HEIGHT / 2);
        rumpShape.set(vertices, 4);
        FixtureDef rumpFixture = new FixtureDef();
        rumpFixture.shape = rumpShape;
        rumpFixture.density = 2f;
        rumpFixture.friction = 0.5f;
        rump.createFixture(rumpFixture);
        
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = rump;
        jointDef.bodyB = physicsObject;
        jointDef.localAnchorA.set(0, 0);
        jointDef.localAnchorB.set(0, 0);
        jointDef.collideConnected = false;
        jointDef.enableMotor = true;
        jointDef.maxMotorTorque = 30f;
        jointDef.motorSpeed = 1f;
        world.createJoint(jointDef);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        rump.setTransform(getPosition(), y);
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 position, float rotation) {
        return new Rectangle2D(position.x - WIDTH / 2, position.y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    @Override
    public void render(GraphicsContext ctx, Vec2 position, float rotation) {
        float x = position.x;
        float y = position.y;

        ctx.translate(x, y);
        ctx.drawImage(rumpSprite, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
        ctx.rotate(rotation);
        ctx.drawImage(rotorSprite, -R_RADIUS, -R_RADIUS, 2 * R_RADIUS, 2 * R_RADIUS);
        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }

    @Override
    public void remove(World world) {
        if (physicsObject != null) {
            world.destroyBody(physicsObject);
            physicsObject = null;
        }
        if (rump != null) {
            world.destroyBody(rump);
            rump = null;
        }
    }
}

