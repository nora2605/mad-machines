package mm.model.GameObjects;

import org.jbox2d.collision.shapes.CircleShape;
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

public class SmallGear extends Gear {
    private static final float RADIUS = 0.2f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/SmallGear.png").toExternalForm());
    private final BodyDef gear = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();

    private Body anchor;
    private Joint anchorJoint;

    public SmallGear() {
        gear.type = BodyType.DYNAMIC;
    }

    @Override
    public void instantiate(World world) {
        throw new UnsupportedOperationException("Use instantiate(World, Vec2, float) instead.");
    }

    @Override
    public void instantiate(World world, Vec2 position, float rotation) {
        physicsObject = world.createBody(gear);
        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.1f;
        fixtureDef.density = 0.05f;
        physicsObject.createFixture(fixtureDef);
        
        
        // Anchor
        anchor = world.createBody(new BodyDef());
        CircleShape anchorShape = new CircleShape();
        anchorShape.setRadius(RADIUS / 2);
        FixtureDef anchorFixture = new FixtureDef();
        anchorFixture.shape = anchorShape;
        
        RevoluteJointDef aJointDef = new RevoluteJointDef();
        aJointDef.bodyA = anchor;
        aJointDef.bodyB = physicsObject;
        aJointDef.localAnchorA.set(0, 0);
        aJointDef.localAnchorB.set(0, 0);
        aJointDef.collideConnected = false;
        aJointDef.enableMotor = true;
        aJointDef.maxMotorTorque = RADIUS * 0.1f;
        aJointDef.motorSpeed = 0f;

        anchorJoint = world.createJoint(aJointDef);
        
        // done before discovering other gears
        setPosition(position.x, position.y);
        setRotation(rotation);
        
        Body bodyList = world.getBodyList();
        while (bodyList != null) {
            if (bodyList.getUserData() instanceof Gear) {
                Gear o_gear = (Gear) bodyList.getUserData();
                if (o_gear.areWeConnected(this)) {
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
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        anchor.setTransform(getPosition(), anchor.getAngle());
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 position, float rotation) {
        return new Rectangle2D(position.x - 0.8f * RADIUS, position.y - 0.8f * RADIUS, RADIUS * 1.6f, RADIUS * 1.6f);
    }

    @Override
    public void render(GraphicsContext ctx, Vec2 position, float rotation) {
        float x = position.x;
        float y = position.y;

        ctx.translate(x, y);
        ctx.rotate(rotation);

        ctx.drawImage(sprite, -RADIUS, -RADIUS, RADIUS * 2, RADIUS * 2);

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
        return RADIUS;
    }

    @Override
    public Joint getAnchorJoint() {
        return anchorJoint;
    }

    @Override
    public boolean areWeConnected(Gear friend) {
        if (friend == this) {
            return false; // cannot connect to itself
        }
        float distance = getPosition().sub(friend.getPosition()).length();
        return distance <= (getRadius() + friend.getRadius());
    }
}
