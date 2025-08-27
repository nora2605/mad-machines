package mm.model.SpecialObjects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.RopeJointDef;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.model.GameObject;
import mm.model.SpecialObject;
import mm.view.ResourceLoader;

public class Rope implements SpecialObject {
    static final int SEGMENTS = 20;
    float segmentLength = 0.1f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Rope.png").toExternalForm());
    Body[] physicsObjects;
    
    @Override
    public void remove(World world) {
        for (Body body : physicsObjects) {
            world.destroyBody(body);
            body = null;
        }
    }

    @Override
    public void instantiate(World world, GameObject obA, GameObject obB) {
        physicsObjects = new Body[SEGMENTS];

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        bodyDef.type = BodyType.DYNAMIC;

        Vec2 direction = obB.getPosition().sub(obA.getPosition()).mul(1.0f / SEGMENTS);
        segmentLength = direction.length();

        shape.setAsBox(0.02f, 1.2f * segmentLength / 2.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.01f;
        fixtureDef.isSensor = true;

        float rot = (float) Math.atan2(direction.y, direction.x);
        bodyDef.angle = rot;

        bodyDef.position = obA.getPosition().add(direction.mul(0.5f));
        physicsObjects[0] = world.createBody(bodyDef);
        physicsObjects[0].createFixture(fixtureDef);
        physicsObjects[0].getFixtureList().setSensor(true);
        
        RevoluteJointDef fjointDef = new RevoluteJointDef();
        fjointDef.bodyA = obA.getPhysicsObject();
        fjointDef.bodyB = physicsObjects[0];
        fjointDef.collideConnected = false;
        fjointDef.localAnchorA.set(0, 0);
        fjointDef.localAnchorB.set(-segmentLength / 2.0f, 0);
        world.createJoint(fjointDef);

        for (int i = 1; i < SEGMENTS; i++) {
            bodyDef.position = bodyDef.position.add(direction);
            physicsObjects[i] = world.createBody(bodyDef);
            physicsObjects[i].createFixture(fixtureDef);
            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.bodyA = physicsObjects[i - 1];
            jointDef.bodyB = physicsObjects[i];
            jointDef.collideConnected = false;
            jointDef.localAnchorA.set(segmentLength / 2.0f, 0);
            jointDef.localAnchorB.set(-segmentLength / 2.0f, 0);
            world.createJoint(jointDef);
        }
        RevoluteJointDef ljointDef = new RevoluteJointDef();
        ljointDef.bodyA = physicsObjects[SEGMENTS - 1];
        ljointDef.bodyB = obB.getPhysicsObject();
        ljointDef.collideConnected = false;
        ljointDef.localAnchorA.set(segmentLength / 2.0f, 0);
        ljointDef.localAnchorB.set(0, 0);
        world.createJoint(ljointDef);

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = obA.getPhysicsObject();
        ropeJointDef.bodyB = obB.getPhysicsObject();
        ropeJointDef.maxLength = segmentLength * (SEGMENTS + 1);
        ropeJointDef.collideConnected = true;
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        world.createJoint(ropeJointDef);
    }

    @Override
    public void render(GraphicsContext ctx) {
        for (Body body : physicsObjects) {
            ctx.translate(body.getPosition().x, body.getPosition().y);
            ctx.rotate(body.getAngle() * (180.0 / Math.PI));
            
            ctx.drawImage(sprite, -1.2f * segmentLength / 2.0f, -0.02f, 1.2f * segmentLength, 0.04f);

            ctx.rotate(-body.getAngle() * (180.0 / Math.PI));
            ctx.translate(-body.getPosition().x, -body.getPosition().y);
        }
    }

    @Override
    public void renderPreview(GraphicsContext ctx, Vec2 posA, Vec2 posB) {
        Vec2 pos = posA;
        Vec2 direction = posB.sub(posA).mul(1.0f / SEGMENTS);
        segmentLength = direction.length();
        float rot = (float) Math.atan2(direction.y, direction.x);

        for (int i = 0; i < SEGMENTS; i++) {
            ctx.translate(pos.x, pos.y);
            ctx.rotate(rot * (180.0 / Math.PI));
            ctx.drawImage(sprite, -1.2f * segmentLength / 2.0f, -0.02f, 1.2f * segmentLength, 0.04f);
            ctx.rotate(-rot * (180.0 / Math.PI));
            ctx.translate(-pos.x, -pos.y);
            pos = pos.add(direction);
        }
    }

    @Override
    public Rectangle2D getBoundingBox() {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Body body : physicsObjects) {
            Vec2 pos = body.getPosition();
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
            maxX = Math.max(maxX, pos.x);
            maxY = Math.max(maxY, pos.y);
        }

        return new Rectangle2D(minX - 0.05, minY - 0.05, maxX - minX + 0.1, maxY - minY + 0.1);
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 posA, Vec2 posB) {
        float minX = Math.min(posA.x, posB.x);
        float minY = Math.min(posA.y, posB.y);
        float maxX = Math.max(posA.x, posB.x);
        float maxY = Math.max(posA.y, posB.y);

        return new Rectangle2D(minX - 0.05, minY - 0.05, maxX - minX + 0.1, maxY - minY + 0.1);
    }
}
