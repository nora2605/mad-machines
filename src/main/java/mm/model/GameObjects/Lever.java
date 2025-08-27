package mm.model.GameObjects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.GearJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.view.ResourceLoader;

public class Lever extends Gear {
    private static final float WIDTH = 0.7f;
    private static final float HEIGHT = 0.1f;
    private static final float G_RADIUS = 0.2f;

    private static final Image g_sprite = new Image(ResourceLoader.getResource("/sprites/SmallGear.png").toExternalForm());
    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Lever.png").toExternalForm());
    private final BodyDef lever_body = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();

    private Body anchor;
    private Joint anchorJoint;

    public Lever() {
        lever_body.type = BodyType.DYNAMIC;
    }

    @Override
    public void instantiate(World world) {
        throw new UnsupportedOperationException("Use instantiate(World, Vec2, float) instead.");
    }

    @Override
    public void instantiate(World world, Vec2 position, float rotation) {
        physicsObject = world.createBody(lever_body);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH / 2, HEIGHT / 2);
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.density = 0.3f;
        physicsObject.createFixture(fixtureDef);
        
        // Anchor (plane behind it to support off-center rotation)
        BodyDef anchorDef = new BodyDef();
        anchorDef.type = BodyType.STATIC;
        anchor = world.createBody(anchorDef);
        PolygonShape anchorShape = new PolygonShape();
        anchorShape.setAsBox(WIDTH / 2, WIDTH / 2);
        FixtureDef aFixDef = new FixtureDef();
        aFixDef.shape = anchorShape;
        aFixDef.isSensor = true;
        anchor.createFixture(aFixDef);
        
        setPosition(position.x, position.y);
        setRotation(rotation);

        RevoluteJointDef aJointDef = new RevoluteJointDef();
        aJointDef.bodyA = anchor;
        aJointDef.bodyB = physicsObject;
        aJointDef.localAnchorB.set(WIDTH / 2 - G_RADIUS / 2, 0);
        aJointDef.localAnchorA.set(
            (float)Math.cos(rotation * Math.PI / 180) * (WIDTH / 2 - G_RADIUS / 2),
            (float)Math.sin(rotation * Math.PI / 180) * (WIDTH / 2 - G_RADIUS / 2)
        );
        aJointDef.collideConnected = false;
        aJointDef.enableMotor = true;
        aJointDef.maxMotorTorque = G_RADIUS * 0.1f;
        aJointDef.motorSpeed = 0f;

        anchorJoint = world.createJoint(aJointDef);
        
        // pretend you're a gear
        physicsObject.setTransform(getGearPosition(position), rotation * (float)Math.PI / 180);
        Body bodyList = world.getBodyList();
        while (bodyList != null) {
            if (bodyList.getUserData() instanceof Gear) {
                Gear o_gear = (Gear) bodyList.getUserData();
                if (o_gear.areWeConnected(this)) {
                    System.out.println("Connecting to " + o_gear.getClass().getSimpleName());
                    GearJointDef jointDef = new GearJointDef();
                    jointDef.bodyA = physicsObject;
                    jointDef.bodyB = o_gear.getPhysicsObject();
                    jointDef.joint1 = anchorJoint;
                    jointDef.joint2 = o_gear.getAnchorJoint();
                    jointDef.ratio = o_gear.getRadius() / getRadius();
                    world.createJoint(jointDef);
                }
            }
            bodyList = bodyList.getNext();
        }
        setPosition(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        anchor.setTransform(getPosition(), anchor.getAngle());
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
        
        ctx.drawImage(g_sprite, WIDTH / 2 - 3 * G_RADIUS / 2, -G_RADIUS, G_RADIUS * 2, G_RADIUS * 2);
        ctx.drawImage(sprite, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);

        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }

    @Override
    public void remove(World world) {
        if (anchorJoint != null) {
            world.destroyJoint(anchorJoint);
            anchorJoint = null;
        }
        if (physicsObject != null) {
            world.destroyBody(physicsObject);
            physicsObject = null;
        }
        if (anchor != null) {
            world.destroyBody(anchor);
            anchor = null;
        }
    }

    @Override
    public float getRadius() {
        return G_RADIUS;
    }

    @Override
    public Joint getAnchorJoint() {
        return anchorJoint;
    }

    private Vec2 getGearPosition(Vec2 position) {
        return new Vec2(
            position.x + (WIDTH / 2 - G_RADIUS / 2) * (float)Math.cos(getRotation() * Math.PI / 180),
            position.y + (WIDTH / 2 - G_RADIUS / 2) * (float)Math.sin(getRotation() * Math.PI / 180)
        );
    }

    @Override
    public boolean areWeConnected(Gear friend) {
        if (friend == this) {
            return false; // cannot connect to itself
        }
        float distance = getGearPosition(getPosition()).sub(friend.getPosition()).length();
        return distance <= (getRadius() + friend.getRadius());
    }
}
